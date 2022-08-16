package wjl.yang.compile;

import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.List;

/**
 * 浅复制条件语句。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
class ConditionCopier {
    /**
     * 将语句 from 中的 when/if_feature 浅复制到 cloneSubs
     */
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
