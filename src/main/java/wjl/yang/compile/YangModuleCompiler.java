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

    public List<YangModule> compile(List<String> filenames) {
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

        LinkageBuilder.build(context, addModules);
        List<YangModule> allModules = allUsedModules(addModules);
        setOriModule(allModules);
        FeatureCompiler.collectFeatures(allModules);
        FeatureCompiler.checkIfFeatures(allModules);
        GroupingCompiler.collectGroupings(allModules);
        return allModules;
    }

    public List<String> getErrors() {
        return errors;
    }

    private YangMainModule mainModule(YangStmt stmt) {
        String pre = CompileUtil.prefix(stmt);
        YangMainModule ret = new YangMainModule(stmt.getValue(), pre, CompileUtil.version(stmt));
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
            pre = CompileUtil.prefix(belongsTo);
        }

        YangSubModule ret = new YangSubModule(stmt.getValue(), pre, CompileUtil.version(stmt), parent);
        ret.setStmt(stmt);
        return ret;
    }

    private List<YangModule> allUsedModules(List<YangMainModule> modules) {
        List<YangModule> ret = new ArrayList<>(modules);
        for (YangMainModule module : modules) {
            ret.addAll(module.getSubModules());
        }
        return ret;
    }

    private void setOriModule(List<? extends YangModule> modules) {
        for (YangModule main : modules) {
            main.getStmt().iterateAll((stmt) -> {
                stmt.setOriModule(main);
            });
        }
    }
}
