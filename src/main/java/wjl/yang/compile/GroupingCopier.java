package wjl.yang.compile;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtClone;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将grouping语句复制到uses语句所在的位置，规则比较复杂。
 * 忽略grouping的status、description、reference子句，忽略所有位置的uses、grouping子句。
 * 可以直接复重用所有位置的description、reference、type、typedef、unique、if-feature等子句。
 * 将uses语句的所有when和if-feature子句复制给grouping中所有的子句。
 */
class GroupingCopier {
    private static final Set<String> TOP_IGNORE_KEYS;
    private static final Set<String> ALL_IGNORE_KEYS;
    private static final Set<String> NO_NEED_COPY_KEYS;

    private YangMainModule schemaModule;
    private YangModule oriModule;

    static {
        // 所有位置都要忽略的子句
        ALL_IGNORE_KEYS = new HashSet<>();
        ALL_IGNORE_KEYS.add(YangKeyword.USES);
        ALL_IGNORE_KEYS.add(YangKeyword.GROUPING);

        // 需要忽略的第一级子句
        TOP_IGNORE_KEYS = new HashSet<>(ALL_IGNORE_KEYS);
        TOP_IGNORE_KEYS.add(YangKeyword.STATUS);
        TOP_IGNORE_KEYS.add(YangKeyword.DESCRIPTION);
        TOP_IGNORE_KEYS.add(YangKeyword.REFERENCE);

        // 不需要复制的子句，因为它们不会被refine和augment修改掉
        NO_NEED_COPY_KEYS = new HashSet<>();
        NO_NEED_COPY_KEYS.add(YangKeyword.DESCRIPTION);
        NO_NEED_COPY_KEYS.add(YangKeyword.REFERENCE);
        NO_NEED_COPY_KEYS.add(YangKeyword.STATUS);
        NO_NEED_COPY_KEYS.add(YangKeyword.UNIQUE);
        NO_NEED_COPY_KEYS.add(YangKeyword.IF_FEATURE);
        NO_NEED_COPY_KEYS.add(YangKeyword.WHEN);
        NO_NEED_COPY_KEYS.add(YangKeyword.MUST);
        NO_NEED_COPY_KEYS.add(YangKeyword.TYPE);
        NO_NEED_COPY_KEYS.add(YangKeyword.BASE);

        // 复制会导致算法错误的语句
        NO_NEED_COPY_KEYS.add(YangKeyword.TYPEDEF);
        NO_NEED_COPY_KEYS.add(YangKeyword.IDENTITY);
    }

    /**
     * 将grouping语句复制到uses语句所在的位置。
     *
     * @param parent uses语句的父
     * @param uses uses语句本身
     * @param grouping 引用的grouping语句
     */
    void copy(YangStmt parent, YangStmt uses, YangStmt grouping) {
        oriModule = uses.getOriModule();
        schemaModule = oriModule.getMainModule();

        List<YangStmt> cloneSubs = cloneGrouping(uses, grouping);
        applyAugment(uses, cloneSubs);
        applyRefine(uses, cloneSubs);
        ConditionCopier.copyConditions(uses, cloneSubs);
        parent.replaceStmt(uses, cloneSubs);
    }

    private List<YangStmt> cloneGrouping(YangStmt uses, YangStmt grouping) {
        List<YangStmt> cloneSubs = new ArrayList<>();
        if (grouping.getSubStatements() != null) {
            for (YangStmt sub : grouping.getSubStatements()) {
                if (!TOP_IGNORE_KEYS.contains(sub.getKey())) {
                    YangStmt cloneSub = cloneStmt(uses, sub);
                    cloneSubs.add(cloneSub);
                }
            }
        }
        return cloneSubs;
    }

    private YangStmt cloneStmt(YangStmt uses, YangStmt source) {
        YangStmtClone clone = new YangStmtClone();
        clone.setSchemaModule(schemaModule);
        clone.setSource(source);
        clone.setUses(uses);
        clone.setOriModule(source.getOriModule());
        clone.setKey(source.getKey());
        clone.setValue(source.getValue());
        clone.setValueToken(source.getValueToken());
        if (source.getSubStatements() != null) {
            for (YangStmt sub : source.getSubStatements()) {
                if (NO_NEED_COPY_KEYS.contains(sub.getKey())) {
                    clone.addSubStatement(sub);
                } else if (!ALL_IGNORE_KEYS.contains(sub.getKey())) {
                    clone.addSubStatement(cloneStmt(uses, sub));
                }
            }
        }
        return clone;
    }

    private void applyAugment(YangStmt uses, List<YangStmt> cloneSubs) {
        uses.forEach(YangKeyword.AUGMENT, (aug) -> {
            AugmentCopier.usesAugment(aug, cloneSubs);
        });
    }

    private void applyRefine(YangStmt uses, List<YangStmt> cloneSubs) {
        uses.forEach(YangKeyword.REFINE, (ref) -> {
            RefineCopier.usesRefine(ref, cloneSubs);
        });
    }
}
