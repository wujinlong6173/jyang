package wjl.yang.compile;

import wjl.yang.model.ModuleAndIdentify;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 搜索模型节点。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
class SchemaNodeSeeker {
    private static final Set<String> SCHEMA_NODE_KEYS;

    static {
        // 只搜索模型节点，不能被其它语句干扰
        SCHEMA_NODE_KEYS = new HashSet<>();
        SCHEMA_NODE_KEYS.add(YangKeyword.CONTAINER);
        SCHEMA_NODE_KEYS.add(YangKeyword.LIST);
        SCHEMA_NODE_KEYS.add(YangKeyword.LEAF);
        SCHEMA_NODE_KEYS.add(YangKeyword.LEAF_LIST);
        SCHEMA_NODE_KEYS.add(YangKeyword.ANYDATA);
        SCHEMA_NODE_KEYS.add(YangKeyword.ANYXML);
        SCHEMA_NODE_KEYS.add(YangKeyword.CHOICE);
        SCHEMA_NODE_KEYS.add(YangKeyword.CASE);
    }

    /**
     * 在grouping的复制体中找模型节点
     *
     * @param pos 报告错误的位置
     * @param cloneSubs
     * @param path 路径
     * @return
     */
    static YangStmt searchInGrouping(YangStmt pos, List<YangStmt> cloneSubs, List<String> path) {
        Iterator<String> it = path.iterator();
        if (!it.hasNext()) {
            pos.reportError(" path is empty.");
            return null;
        }

        YangModule oriModule = pos.getOriModule();
        String hop = it.next();
        ModuleAndIdentify mi = oriModule.separate(hop, true);
        if (mi == null) {
            pos.reportError(hop + " invalid prefix.");
            return null;
        }

        YangStmt ret = search(cloneSubs, mi.getModule(), mi.getIdentify());
        while (ret != null && it.hasNext()) {
            hop = it.next();
            mi = oriModule.separate(hop, true);
            if (mi == null) {
                pos.reportError(hop + " invalid prefix.");
                return null;
            }

            ret = search(ret.getSubStatements(), mi.getModule(), mi.getIdentify());
        }

        if (ret == null) {
            pos.reportError(hop + " is not found.");
        }
        return ret;
    }

    /**
     * 在模块体重找模型节点
     *
     * @param pos 报告错误的位置
     * @param path 搜索的路径
     * @return 找到的模型节点，返回空时肯定会填错误信息
     */
    static YangStmt searchInModule(YangStmt pos, List<String> path) {
        Iterator<String> it = path.iterator();
        if (!it.hasNext()) {
            return null;
        }

        YangModule oriModule = pos.getOriModule();
        String hop = it.next();
        ModuleAndIdentify mi = oriModule.separate(hop, true);
        if (mi == null) {
            pos.reportError(hop + " invalid prefix.");
            return null;
        }

        YangStmt ret = search(mi.getModule().getStmt().getSubStatements(), mi.getModule(), mi.getIdentify());
        while (ret != null && it.hasNext()) {
            hop = it.next();
            mi = oriModule.separate(hop, true);
            if (mi == null) {
                pos.reportError(hop + " invalid prefix.");
                return null;
            }

            ret = search(ret.getSubStatements(), mi.getModule(), mi.getIdentify());
        }

        if (ret == null) {
            pos.reportError(hop + " is not found.");
        }
        return ret;
    }

    private static YangStmt search(List<YangStmt> stmtList, YangModule module, String id) {
        if (stmtList == null) {
            return null;
        }

        YangMainModule mainModule = module.getMainModule();
        for (YangStmt stmt : stmtList) {
            if (Objects.equals(id, stmt.getValue())
                && mainModule == stmt.getSchemaModule()
                && SCHEMA_NODE_KEYS.contains(stmt.getKey())) {
                return stmt;
            }
        }
        return null;
    }
}
