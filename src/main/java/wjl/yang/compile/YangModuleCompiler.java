package wjl.yang.compile;

import wjl.yang.model.YangContext;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangSubModule;
import wjl.yang.parser.YangGrammarChecker;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class YangModuleCompiler {
    private final List<String> errors = new ArrayList<>();
    private final YangContext context = new YangContext();

    public void compile(List<String> filenames) {
        errors.clear();
        YangGrammarChecker checker = new YangGrammarChecker();
        List<YangMainModule> addModules = new ArrayList<>();

        for (String filename : filenames) {
            try (InputStream fin = new FileInputStream(filename)) {
                YangLex lex = new YangLex(fin);
                YangParser parser = new YangParser();
                YangStmt stmt = parser.parse(lex);
                if (!checker.check(stmt)) {
                    errors.add(filename + " has errors:");
                    for (YangError err : checker.getErrors()) {
                        errors.add("  " + err.toString());
                    }
                } else if (Objects.equals(YangKeyword.MODULE, stmt.getKey())) {
                    YangMainModule module = mainModule(stmt);
                    addModules.add(module);
                    context.addMainModule(module);
                } else {
                    context.addSubModule(subModule(stmt));
                }
            } catch (IOException | YangParseException err) {
                errors.add(filename + " : " + err.getMessage());
            }
        }

        imports(addModules);
        includeInMain(addModules);
        importAndIncludeInSub(addModules);
        setOriModule(addModules);
    }

    public List<String> getErrors() {
        return errors;
    }

    private YangMainModule mainModule(YangStmt stmt) {
        String pre = prefix(stmt);
        YangMainModule ret = new YangMainModule(stmt.getValue(), pre, version(stmt));
        ret.addPrefix(pre, ret);
        ret.setStmt(stmt);
        return ret;
    }

    private YangSubModule subModule(YangStmt stmt) {
        String parent = null;
        String pre = null;
        YangStmt belongsTo = stmt.searchOne(YangKeyword.BELONGS_TO);
        if (belongsTo != null) {
            parent = belongsTo.getValue();
            pre = prefix(belongsTo);
        }

        YangSubModule ret = new YangSubModule(stmt.getValue(), pre, version(stmt), parent);
        // TODO ret.addPrefix(pre, new ModuleNameVersion(stmt.getValue(), null));
        ret.setStmt(stmt);
        return ret;
    }

    private String version(YangStmt stmt) {
        YangStmt version = stmt.searchOne(YangKeyword.REVISION);
        return version != null ? version.getValue() : null;
    }

    private String prefix(YangStmt stmt) {
        YangStmt prefix = stmt.searchOne(YangKeyword.PREFIX);
        return prefix != null ? prefix.getValue() : null;
    }

    private String revisionDate(YangStmt stmt) {
        YangStmt ver = stmt.searchOne(YangKeyword.REVISION_DATE);
        return ver != null ? ver.getValue() : null;
    }

    // 处理import子句
    private void imports(List<? extends YangModule> modules) {
        for (YangModule module : modules) {
            YangStmt stmt = module.getStmt();

            stmt.forEach(YangKeyword.IMPORT, (sub) -> {
                String name = sub.getValue();
                if (!Objects.equals(name, stmt.getValue())) {
                    String pre = prefix(sub);
                    if (module.getPrefix(pre) != null) {
                        module.addError(sub, "prefix conflict.");
                    } else {
                        String ver = revisionDate(sub);
                        YangMainModule target = context.matchMainModule(name, ver);
                        if (target == null) {
                            module.addError(sub, "no matched module.");
                        } else {
                            module.addPrefix(pre, target);
                        }
                    }
                }
            });
        }
    }

    private void includeInMain(List<YangMainModule> modules) {
        for (YangMainModule module : modules) {
            YangStmt stmt = module.getStmt();
            Set<String> included = new HashSet<>();
            stmt.forEach(YangKeyword.INCLUDE, (sub) -> {
                String name = sub.getValue();
                String ver = revisionDate(sub);
                YangSubModule target = context.matchSubModule(name, ver);
                if (target == null) {
                    module.addError(sub, "no matched submodule.");
                } else if (!Objects.equals(module.getName(), target.getBelongTo())) {
                    module.addError(sub, "submodule not belongs to this module.");
                } else if (included.contains(name)) {
                    module.addError(sub, "can not include two submodule with same name.");
                } else {
                    included.add(name);
                    target.setIncludedByMain();
                    target.addPrefix(target.getPrefix(), module);
                    module.addSubModule(target);
                }
            });
        }
    }

    private void importAndIncludeInSub(List<YangMainModule> modules) {
        for (YangMainModule main : modules) {
            List<YangSubModule> subModules = main.getSubModules();
            imports(subModules);
            for (YangSubModule module : subModules) {
                YangStmt stmt = module.getStmt();
                Set<String> included = new HashSet<>();
                stmt.forEach(YangKeyword.INCLUDE, (sub) -> {
                    String name = sub.getValue();
                    String ver = revisionDate(sub);
                    YangSubModule target = context.matchSubModule(name, ver);
                    if (target == null) {
                        module.addError(sub, "no matched submodule.");
                    } else if (!Objects.equals(module.getBelongTo(), target.getBelongTo())) {
                        module.addError(sub, "submodule not belongs to same module.");
                    } else if (included.contains(name)) {
                        module.addError(sub, "can not include two submodule with same name.");
                    } else if (!target.isIncludedByMain()) {
                        module.addError(sub, "submodule should be included by main module.");
                    } else {
                        included.add(name);
                        module.addSubModule(target);
                    }
                });
            }
        }
    }

    private void setOriModule(List<YangMainModule> modules) {
        for (YangMainModule main : modules) {
            main.getStmt().iterateAll((stmt) -> {
                stmt.setOriModule(main);
            });

            List<YangSubModule> subModules = main.getSubModules();
            for (YangSubModule sub : subModules) {
                sub.getStmt().iterateAll((stmt) -> {
                    stmt.setOriModule(sub);
                });
            }
        }
    }
}
