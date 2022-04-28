package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.YangKeyword;

import java.util.List;
import java.util.Map;

/**
 * 处理 identity 和 base 语句。
 */
class IdentityCompiler extends DefineAndUseCompiler {
    private UiGraph<YangStmt, Void> identityDepends;

    IdentityCompiler() {
        super(YangKeyword.IDENTITY, YangKeyword.BASE);
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void compile(List<YangModule> modules) {
        identityDepends = new UiGraph<>();
        searchDefineInModules(modules);
        searchUseInModules(modules, false);
        CompileUtil.reportCircularDependency(identityDepends);
    }

    @Override
    protected void onUse(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines) {
        // 如果parentDefine为空，parentStmt必定是type identityref语句，
        // 否则，parentStmt必定是identity语句。
        YangStmt targetDefine = findTargetDefine(stmt, scopeDefines);
        if (targetDefine != null && parentDefine != null) {
            identityDepends.addEdge(parentDefine, targetDefine, null);
        }
    }
}
