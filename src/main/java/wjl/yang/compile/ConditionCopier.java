package wjl.yang.compile;

import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.List;

class ConditionCopier {
    static void copyConditions(YangStmt from, List<YangStmt> cloneSubs) {
        if (from == null || cloneSubs == null || from.getSubStatements() == null) {
            return;
        }

        List<YangStmt> conditions = new ArrayList<>();
        for (YangStmt sub : from.getSubStatements()) {
            if (YangKeyword.WHEN.equals(sub.getKey()) || YangKeyword.IF_FEATURE.equals(sub.getKey())) {
                conditions.add(sub);
            }
        }

        if (conditions.isEmpty()) {
            return;
        }

        for (YangStmt cloneSub : cloneSubs) {
            cloneSub.addSubStatements(conditions);
        }
    }
}
