package wjl.yang.compile;

import wjl.yang.model.ModuleNameVersion;
import wjl.yang.model.YangContext;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangSubModule;
import wjl.yang.parser.YangGrammarChecker;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;
import wjl.yang.parser.YangStmt;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class YangModuleCompiler {
    private final List<String> errors = new ArrayList<>();
    private final YangContext context = new YangContext();

    public void compile(List<String> filenames) {
        YangGrammarChecker checker = new YangGrammarChecker();

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
                    context.addMainModule(mainModule(stmt));
                } else {
                    context.addSubModule(subModule(stmt));
                }
            } catch (IOException | YangParseException err) {
                errors.add(filename + " : " + err.getMessage());
            }
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    private YangMainModule mainModule(YangStmt stmt) {
        String pre = prefix(stmt);
        YangMainModule ret = new YangMainModule(stmt.getValue(), pre, version(stmt));
        ret.addPrefix(pre, new ModuleNameVersion(stmt.getValue(), null));
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

        YangSubModule ret = new YangSubModule(stmt.getValue(), version(stmt), pre, parent);
        ret.addPrefix(pre, new ModuleNameVersion(stmt.getValue(), null));
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

    private void imports(YangStmt stmt, YangModule module) {
        stmt.forEach(YangKeyword.IMPORT, (sub) -> {
            String name = sub.getValue();
            if (!Objects.equals(name, stmt.getValue())) {
                String pre = prefix(sub);
                if (module.getPrefix(pre) != null) {
                    module.addError(sub, "prefix conflict.");
                } else {
                    String ver = revisionDate(sub);
                    module.addPrefix(pre, new ModuleNameVersion(name, ver));
                }
            }
        });
    }
}
