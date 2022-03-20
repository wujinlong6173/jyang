package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理 grouping 和 uses 语句，将 uses 语句替换成 grouping 中的内容。
 */
class GroupingCompiler {
    private Map<YangModule, Map<String, YangStmt>> moduleToGroupings;
    private final Map<YangStmt, UsesToGrouping> usesToGroupingMap = new HashMap<>();

    /**
     *
     * @param modules 主模块和子模块。
     */
    void expandGrouping(List<YangModule> modules) {
        moduleToGroupings = CompileUtil.collectSpecialStmt(modules, YangKeyword.GROUPING);
        // 先展开各模块顶层定义的groupings，要注意循环引用。
        searchUsesStmt(modules);
    }

    private void searchUsesStmt(List<YangModule> modules) {
        for (YangModule module : modules) {
            YangStmt stmtModule = module.getStmt();
            if (stmtModule.getSubStatements() == null) {
                continue;
            }

            Map<String, YangStmt> moduleGroupings = moduleToGroupings.get(module);
            Map<String, YangStmt> scopeGroupings = moduleGroupings == null ? new HashMap<>() : new HashMap<>(moduleGroupings);

            for (YangStmt topStmt : stmtModule.getSubStatements()) {
                searchUsesStmt(null, stmtModule, topStmt, scopeGroupings);
            }
        }
    }

    private void searchUsesStmt(YangStmt parentGrouping, YangStmt parentStmt, YangStmt stmt,
        Map<String, YangStmt> scopeGroupings) {
        if (YangKeyword.USES.equals(stmt.getKey())) {
            YangStmt targetGrouping = findTargetGrouping(stmt, scopeGroupings);
            usesToGroupingMap.put(stmt, new UsesToGrouping(parentGrouping, parentStmt, targetGrouping));
        } else if (YangKeyword.GROUPING.endsWith(stmt.getKey())) {
            parentGrouping = stmt;
        }

        if (stmt.getSubStatements() == null) {
            return;
        }

        // 先收集本语句定义的grouping，再递归处理子语句
        Set<String> localGroupings = null;
        for (YangStmt group : stmt.getSubStatements()) {
            if (YangKeyword.GROUPING.equals(group.getKey())) {
                String name = group.getValue();
                YangStmt exist = scopeGroupings.get(name);
                if (exist != null) {
                    stmt.getOriModule().addError(group, String.format(" is already defined in %s", exist.toString()));
                } else {
                    scopeGroupings.put(name, group);
                    if (localGroupings == null) {
                        localGroupings = new HashSet<>();
                    }
                    localGroupings.add(name);
                }
            }
        }

        for (YangStmt sub : stmt.getSubStatements()) {
            searchUsesStmt(parentGrouping, stmt, sub, scopeGroupings);
        }

        if (localGroupings != null) {
            for (String local : localGroupings) {
                scopeGroupings.remove(local);
            }
        }
    }

    private YangStmt findTargetGrouping(YangStmt uses, Map<String, YangStmt> scopeGroupings) {
        YangModule oriModule = uses.getOriModule();
        YangStmt targetGrouping = null;
        ModuleAndIdentify mi = oriModule.separate(uses.getValue(), false);
        if (mi == null) {
            oriModule.addError(uses, "invalid prefix.");
            return null;
        } else if (mi.getModule() == null) {
            targetGrouping = scopeGroupings.get(mi.getIdentify());
        } else {
            Map<String, YangStmt> moduleGroupings = moduleToGroupings.get(mi.getModule());
            if (moduleGroupings != null) {
                targetGrouping = moduleGroupings.get(mi.getIdentify());
            }
        }

        if (targetGrouping == null) {
            oriModule.addError(uses, "undefined grouping.");
        }
        return targetGrouping;
    }

    static class UsesToGrouping {
        // 从uses语句往上找到的grouping语句，可能为空
        final YangStmt parentGrouping;
        // 从uses语句往上找到的语句
        final YangStmt parentStmt;
        // uses语句引用的grouping语句
        final YangStmt targetGrouping;

        UsesToGrouping(YangStmt parentGrouping, YangStmt parentStmt, YangStmt targetGrouping) {
            this.parentGrouping = parentGrouping;
            this.parentStmt = parentStmt;
            this.targetGrouping = targetGrouping;
        }
    }
}
