package wjl.yang.compile;

import wjl.yang.model.YangStmt;
import wjl.yang.utils.UiGraph;
import wjl.yang.utils.UiGraphSort;
import wjl.yang.utils.YangKeyword;

import java.util.List;
import java.util.Set;

final class CompileUtil {
    static String version(YangStmt stmt) {
        YangStmt version = stmt.searchOne(YangKeyword.REVISION);
        return version != null ? version.getValue() : null;
    }

    static String prefix(YangStmt stmt) {
        YangStmt prefix = stmt.searchOne(YangKeyword.PREFIX);
        return prefix != null ? prefix.getValue() : null;
    }

    static String revisionDate(YangStmt stmt) {
        YangStmt ver = stmt.searchOne(YangKeyword.REVISION_DATE);
        return ver != null ? ver.getValue() : null;
    }

    static void reportCircularDependency(UiGraph<YangStmt, Void> dependGraph) {
        UiGraphSort.sortReverse(dependGraph, null);
        if (!dependGraph.isEmpty()) {
            Set<YangStmt> errList = dependGraph.copyNodes();
            for (YangStmt err : errList) {
                err.reportError("circular dependency");
            }
        }
    }
}
