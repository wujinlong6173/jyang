package wjl.yang.compile;

import wjl.yang.model.YangContext;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangSubModule;
import wjl.yang.grammar.YangGrammarChecker;
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
import java.util.List;
import java.util.Objects;

public class YangModuleCompiler {
    private final List<String> errors = new ArrayList<>();
    private final YangContext context = new YangContext();

    public List<YangModule> compileStmtList(List<YangStmt> stmtList) {
        YangGrammarChecker checker = new YangGrammarChecker();
        List<YangMainModule> mainModules = new ArrayList<>();

        for (YangStmt stmt : stmtList) {
            checker.check(stmt);
            if (Objects.equals(YangKeyword.MODULE, stmt.getKey())) {
                YangMainModule module = mainModule(stmt);
                mainModules.add(module);
                context.addMainModule(module);
                copyError(checker.getErrors(), module);
            } else if (Objects.equals(YangKeyword.SUBMODULE, stmt.getKey())) {
                YangSubModule module = subModule(stmt);
                context.addSubModule(module);
                copyError(checker.getErrors(), module);
            } else {
                errors.add(stmt.toString() + " has errors:");
                for (YangError err : checker.getErrors()) {
                    errors.add("  " + err.toString());
                }
            }
        }

        LinkageBuilder.build(context, mainModules);
        List<YangModule> allModules = allUsedModules(mainModules);
        setOriModule(allModules);
        new FeatureCompiler().collectFeatures(allModules);
        new GroupingCompiler().expandGrouping(allModules);
        new AugmentCompiler().expandAugment(allModules);
        new TypedefCompiler().compile(allModules);
        new IdentityCompiler().compile(allModules);
        new DataNodeCompiler().compile(mainModules);
        return allModules;
    }

    public List<YangModule> compileFiles(List<String> filenames) {
        List<YangStmt> stmtList = new ArrayList<>();
        for (String filename : filenames) {
            try (InputStream fin = new FileInputStream(filename)) {
                YangLex lex = new YangLex(fin);
                YangParser parser = new YangParser();
                YangStmt stmt = parser.parse(lex);
                if (stmt != null) {
                    stmtList.add(stmt);
                }
            } catch (IOException | YangParseException err) {
                errors.add(filename + " : " + err.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            return null;
        }

        return compileStmtList(stmtList);
    }

    public void addError(String filename, String msg) {
        errors.add(filename + " : " + msg);
    }

    public void addError(String msg) {
        errors.add(msg);
    }

    public void clearErrors() {
        errors.clear();
    }

    public boolean hasError() {
        return !errors.isEmpty();
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

    private void copyError(List<YangError> errorList, YangModule module) {
        if (errorList != null) {
            for (YangError err : errorList) {
                module.addError(err.getPos(), err.getMsg());
            }
        }
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
