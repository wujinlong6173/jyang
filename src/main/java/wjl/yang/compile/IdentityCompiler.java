package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.UiGraphSort;
import wjl.yang.utils.YangKeyword;

import java.util.List;
import java.util.Set;

/**
 * 处理 identity 和 base 语句。
 */
class IdentityCompiler extends DefineAndUseCompiler {
    private UiGraph<YangStmt, Void> identityDepends;

    IdentityCompiler() {
        super(YangKeyword.IDENTITY, YangKeyword.BASE);
        hasLocalDefine = false;
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void compile(List<YangModule> modules) {
        identityDepends = new UiGraph<>();
        match(modules);
        List<YangStmt> sortedIdentities = UiGraphSort.sortReverse(identityDepends, null);
        if (!identityDepends.isEmpty()) {
            Set<YangStmt> errList = identityDepends.copyNodes();
            for (YangStmt err : errList) {
                err.reportError("circular dependency");
            }
        }
    }

    @Override
    protected void onMatch(YangStmt parentDefine, YangStmt parentStmt, YangStmt use, YangStmt targetDefine) {
        if (parentDefine != null) {
            identityDepends.addEdge(parentDefine, targetDefine, null);
        }
    }
}
