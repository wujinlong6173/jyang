package wjl.yang.compile;

import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.UiGraphSort;
import wjl.yang.utils.YangKeyword;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 解析 typedef 语句，确定每个 type 引用哪个定义。
 */
class TypedefCompiler extends DefineAndUseCompiler {
    private final static Set<String> INTERNAL_TYPES;
    private UiGraph<YangStmt, Void> typedefDepends;

    static {
        INTERNAL_TYPES = new HashSet<>();
        // 整型 int8  int16  int32  int64  uint8  uint16  uint32  uint64
        INTERNAL_TYPES.add("int8");
        INTERNAL_TYPES.add("int16");
        INTERNAL_TYPES.add("int32");
        INTERNAL_TYPES.add("int64");
        INTERNAL_TYPES.add("uint8");
        INTERNAL_TYPES.add("uint16");
        INTERNAL_TYPES.add("uint32");
        INTERNAL_TYPES.add("uint64");
        // 实数decimal64
        INTERNAL_TYPES.add("decimal64");
        // 字符串string
        INTERNAL_TYPES.add("string");
        // 布尔类型boolean
        INTERNAL_TYPES.add("boolean");
        // 枚举类型enumeration
        INTERNAL_TYPES.add("enumeration");
        // 一组按位使用的数据bits
        INTERNAL_TYPES.add("bits");
        // 二进制流binary
        INTERNAL_TYPES.add("binary");
        // XML的空元素empty
        INTERNAL_TYPES.add("empty");
        // 联合体union
        INTERNAL_TYPES.add("union");
        // 对叶节点的引用leafref，用path子句指定引用哪个叶节点。
        INTERNAL_TYPES.add("leafref");
        // 对已有标识的引用identityref
        INTERNAL_TYPES.add("identityref");
        // 对数据节点的引用instance-identifier，类似于XPath
        INTERNAL_TYPES.add("instance-identifier");
    }

    TypedefCompiler() {
        super(YangKeyword.TYPEDEF, YangKeyword.TYPE);
    }

    /**
     *
     * @param modules 主模块和子模块。
     */
    void compile(List<YangModule> modules) {
        typedefDepends = new UiGraph<>();
        match(modules);
        List<YangStmt> sortedTypedefs = UiGraphSort.sortReverse(typedefDepends, null);
        if (!typedefDepends.isEmpty()) {
            Set<YangStmt> errList = typedefDepends.copyNodes();
            for (YangStmt err : errList) {
                err.reportError("circular dependency");
            }
        }
    }

    @Override
    protected boolean checkInternalDefine(YangStmt parentStmt, YangStmt stmt) {
        String id = stmt.getValue();
        return INTERNAL_TYPES.contains(id);
    }

    @Override
    protected void onMatch(YangStmt parentDefine, YangStmt parentStmt, YangStmt use, YangStmt targetDefine) {
        if (parentDefine != null) {
            typedefDepends.addEdge(parentDefine, targetDefine, null);
        }
    }
}
