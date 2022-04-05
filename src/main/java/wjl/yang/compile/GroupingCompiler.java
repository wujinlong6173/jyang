package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.UiGraphSort;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理 grouping 和 uses 语句，将 uses 语句替换成 grouping 中的内容。
 * 计算grouping的依赖关系时，将uses>augment 后面的内容看成一个匿名的grouping。
 */
class GroupingCompiler extends DefineAndUseCompiler {
    private final List<UsesToGrouping> usesToGroupings = new ArrayList<>();
    private List<YangStmt> sortedUses;

    GroupingCompiler() {
        super(YangKeyword.GROUPING, YangKeyword.USES);
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void expandGrouping(List<YangModule> modules) {
        searchDefineInModules(modules);
        searchUseInModules(modules, true);

        // 将uses、grouping、uses>augment三种语句放在一个有向图中排序，得到uses语句的执行顺序
        UiGraph<YangStmt, UsesToGrouping> groupDepends = buildGroupDepends();
        sortedUses = UiGraphSort.sortReverse(groupDepends,
            (node) -> node != null && YangKeyword.USES.equals(node.getKey()));

        if (!groupDepends.isEmpty()) {
            Set<YangStmt> errList = groupDepends.copyNodes();
            for (YangStmt err : errList) {
                err.reportError("circular dependency");
            }
        }

        // 方便根据uses语句查找相关信息
        Map<YangStmt, UsesToGrouping> usesToGroupingMap = new HashMap<>();
        for (UsesToGrouping utg : usesToGroupings) {
            if (utg.parentStmt != null) {
                usesToGroupingMap.put(utg.uses, utg);
            }
        }

        // 按顺序展开所有uses语句
        GroupingCopier copier = new GroupingCopier();
        for (YangStmt uses : sortedUses) {
            UsesToGrouping utg = usesToGroupingMap.get(uses);
            copier.copy(utg.parentStmt, utg.uses, utg.targetGrouping);
        }
    }

    private UiGraph<YangStmt, UsesToGrouping> buildGroupDepends() {
        UiGraph<YangStmt, UsesToGrouping> graph = new UiGraph<>();
        for (UsesToGrouping val : usesToGroupings) {
            if (val.parentGrouping != null) {
                graph.addEdge(val.parentGrouping, val.uses, null);
            }
            graph.addEdge(val.uses, val.targetGrouping, val);
        }
        return graph;
    }

    /**
     * 让测试用例检查中间结果，只检查最终结果很难保证算法没问题
     *
     * @return 排好序的所有uses语句
     */
    List<YangStmt> getSortedUses() {
        return sortedUses;
    }

    @Override
    protected void onUse(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeDefines) {
        YangStmt targetDefine = findTargetDefine(stmt, scopeDefines);
        if (targetDefine != null) {
            usesToGroupings.add(new UsesToGrouping(parentDefine, parentStmt, stmt, targetDefine));
        }
    }

    @Override
    protected boolean checkHiddenDefine(YangStmt parentDefine, YangStmt parentStmt, YangStmt stmt) {
        if (YangKeyword.AUGMENT.equals(stmt.getKey()) && YangKeyword.USES.equals(parentStmt.getKey())) {
            usesToGroupings.add(new UsesToGrouping(parentDefine, null, parentStmt, stmt));
            return true;
        } else {
            return false;
        }
    }

    static class UsesToGrouping {
        // 从uses语句往上找到的grouping语句，可能为空；或者是uses>augment语句
        final YangStmt parentGrouping;
        // 从uses语句往上找到的语句；目标为匿名grouping时填空
        final YangStmt parentStmt;
        // uses语句本身
        final YangStmt uses;
        // uses语句引用的grouping语句；或者是uses>augment语句
        final YangStmt targetGrouping;

        UsesToGrouping(YangStmt parentGrouping, YangStmt parentStmt, YangStmt uses, YangStmt targetGrouping) {
            this.parentGrouping = parentGrouping;
            this.parentStmt = parentStmt;
            this.uses = uses;
            this.targetGrouping = targetGrouping;
        }
    }
}
