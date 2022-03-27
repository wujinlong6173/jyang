package wjl.yang.compile;

import wjl.yang.model.YangStmt;
import wjl.yang.parser.SchemaNodeIdParser;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将augment的子句复制到目标位置。
 */
class AugmentCopier extends ConditionCopier {
    private static final Set<String> TOP_IGNORE_KEYS;
    private static final Set<String> SUPPORT_AUGMENT_KEYS;

    static {
        // 需要忽略的第一级子句
        TOP_IGNORE_KEYS = new HashSet<>();
        TOP_IGNORE_KEYS.add(YangKeyword.STATUS);
        TOP_IGNORE_KEYS.add(YangKeyword.DESCRIPTION);
        TOP_IGNORE_KEYS.add(YangKeyword.REFERENCE);
        TOP_IGNORE_KEYS.add(YangKeyword.WHEN);
        TOP_IGNORE_KEYS.add(YangKeyword.IF_FEATURE);

        // 可以作为augment目标的子句
        SUPPORT_AUGMENT_KEYS = new HashSet<>();
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.CONTAINER);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.LIST);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.CHOICE);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.CASE);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.RPC);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.INPUT);
        SUPPORT_AUGMENT_KEYS.add(YangKeyword.OUTPUT);
    }

    /**
     * 处理uses > augment语句
     *
     * @param augment augment语句本身
     * @param cloneSubs 从目标grouping复制出来的子句
     */
    static void usesAugment(YangStmt augment, List<YangStmt> cloneSubs) {
        if (augment == null || cloneSubs == null) {
            return;
        }

        try {
            List<String> path = SchemaNodeIdParser.parse(augment.getValue(), false);
            YangStmt target = SchemaNodeSeeker.searchInGrouping(augment, cloneSubs, path);
            if (target != null && supportAugment(augment, target)) {
                augment(augment, target);
            }
        } catch (IOException | YangParseException err ) {
            augment.reportError(err.getMessage());
        }
    }

    /**
     * 处理模块体重的 augment 语句
     *
     * @param augment augment语句本身
     */
    static void bodyAugment(YangStmt augment) {
        if (augment == null) {
            return;
        }

        try {
            List<String> path = SchemaNodeIdParser.parse(augment.getValue(), true);
            YangStmt target = SchemaNodeSeeker.searchInModule(augment, path);
            if (target != null && supportAugment(augment, target)) {
                augment(augment, target);
            }
        } catch (IOException | YangParseException err ) {
            augment.reportError(err.getMessage());
        }
    }

    private static boolean supportAugment(YangStmt augment, YangStmt target) {
        if (SUPPORT_AUGMENT_KEYS.contains(target.getKey())) {
            return true;
        } else {
            augment.reportError(target.toString() + " cannot be augmented");
            return false;
        }
    }

    private static void augment(YangStmt augment, YangStmt target) {
        List<YangStmt> cloneSubs = cloneAugment(augment);
        ConditionCopier.copyConditions(augment, cloneSubs);
        target.addSubStatements(cloneSubs);
        augment.clearSubStatements();
    }

    private static List<YangStmt> cloneAugment(YangStmt augment) {
        List<YangStmt> copySubs = new ArrayList<>();
        for (YangStmt sub : augment.getSubStatements()) {
            if (!TOP_IGNORE_KEYS.contains(sub.getKey())) {
                copySubs.add(sub);
            }
        }
        return copySubs;
    }
}
