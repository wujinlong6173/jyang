package wjl.yang.compile;

import wjl.yang.model.YangContext;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangSubModule;
import wjl.yang.grammar.YangGrammarChecker;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangModuleCompiler {
    private final YangGrammarChecker checker = new YangGrammarChecker();
    private final YangContext context = new YangContext();

    /**
     * 根据词法分析的结果生成模块，检查语法错误。
     *
     * @param stmt 模块的顶层语句
     * @return 主模块、子模块、或空
     */
    protected YangModule addModule(YangStmt stmt) {
        if (stmt == null) {
            return null;
        }

        if (YangKeyword.MODULE.equals(stmt.getKey())) {
            YangMainModule module = mainModule(stmt);
            context.addMainModule(module);
            checker.check(module);
            return module;
        } else if (YangKeyword.SUBMODULE.equals(stmt.getKey())) {
            YangSubModule module = subModule(stmt);
            context.addSubModule(module);
            checker.check(module);
            return module;
        } else {
            return null;
        }
    }

    public List<YangModule> compileStmtList(List<YangStmt> stmtList) {
        List<YangMainModule> mainModules = new ArrayList<>();

        for (YangStmt stmt : stmtList) {
            if (Objects.equals(YangKeyword.MODULE, stmt.getKey())) {
                YangMainModule module = mainModule(stmt);
                mainModules.add(module);
                context.addMainModule(module);
                checker.check(module);
                setOriModule(module);
            } else if (Objects.equals(YangKeyword.SUBMODULE, stmt.getKey())) {
                YangSubModule module = subModule(stmt);
                context.addSubModule(module);
                checker.check(module);
                setOriModule(module);
            }
        }

        LinkageBuilder.build(context, mainModules);
        List<YangModule> allModules = allUsedModules(mainModules);
        new FeatureCompiler().collectFeatures(allModules);
        new GroupingCompiler().expandGrouping(allModules);
        new AugmentCompiler().expandAugment(allModules);
        new TypedefCompiler().compile(allModules);
        new IdentityCompiler().compile(allModules);
        new DataNodeCompiler().compile(mainModules);
        return allModules;
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

    private void setOriModule(YangModule module) {
        module.getStmt().iterateAll((stmt) -> {
            stmt.setOriModule(module);
        });
    }
}
