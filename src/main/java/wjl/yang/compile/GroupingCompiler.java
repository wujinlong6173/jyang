package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.List;
import java.util.Map;

/**
 * 处理 grouping 和 uses 语句，将 uses 语句替换成 grouping 中的内容。
 */
class GroupingCompiler {
    private Map<YangModule, Map<String, YangStmt>> moduleToGroupings;

    /**
     *
     * @param modules 主模块和子模块。
     */
    void expandGrouping(List<YangModule> modules) {
        moduleToGroupings = CompileUtil.collectSpecialStmt(modules, YangKeyword.GROUPING);
    }
}
