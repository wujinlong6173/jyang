package wjl.yang.compile;

import wjl.yang.model.YangStmt;
import wjl.yang.parser.SchemaNodeIdParser;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 执行 uses > refine 语句。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
class RefineCopier {
    static void usesRefine(YangStmt refine, List<YangStmt> cloneSubs) {
        if (refine == null || cloneSubs == null) {
            return;
        }

        try {
            List<String> path = SchemaNodeIdParser.parse(refine.getValue(), false);
            YangStmt target = SchemaNodeSeeker.searchInGrouping(refine, cloneSubs, path);
            if (target != null && refine.getSubStatements() != null) {
                for (YangStmt ref : refine.getSubStatements()) {
                    refine(ref, target);
                }
            }
        } catch (IOException | YangParseException err ) {
            refine.reportError(err.getMessage());
        }
    }

    private static void refine(YangStmt ref, YangStmt target) {
        RefineRule rule = REFINE_RULES.get(ref.getKey());
        if (rule == null) {
            ref.reportError("unknown refine key.");
        } else if (rule.supportKeys.contains(target.getKey())) {
            if (rule.merge) {
                target.addSubStatement(ref);
            } else {
                target.replaceStmt(ref);
            }
        } else {
            ref.reportError("can not apply to " + target.toString());
        }
    }

    static class RefineRule {
        final boolean merge;
        final Set<String> supportKeys;

        RefineRule(boolean merge, String...keys) {
            this.merge = merge;
            this.supportKeys = new HashSet<>();
            Collections.addAll(supportKeys, keys);
        }
    }

    private static final Map<String, RefineRule> REFINE_RULES;

    static {
        REFINE_RULES = new HashMap<>();
        REFINE_RULES.put(YangKeyword.DESCRIPTION, new RefineRule(false,
            YangKeyword.CONTAINER, YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.LIST,
            YangKeyword.CHOICE, YangKeyword.CASE, YangKeyword.ANYDATA, YangKeyword.ANYDATA));
        REFINE_RULES.put(YangKeyword.REFERENCE, new RefineRule(false,
            YangKeyword.CONTAINER, YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.LIST,
            YangKeyword.CHOICE, YangKeyword.CASE, YangKeyword.ANYDATA, YangKeyword.ANYDATA));
        REFINE_RULES.put(YangKeyword.CONFIG, new RefineRule(false,
            YangKeyword.CONTAINER, YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.LIST,
            YangKeyword.CHOICE, YangKeyword.ANYDATA, YangKeyword.ANYDATA));
        REFINE_RULES.put(YangKeyword.PRESENCE, new RefineRule(false,
            YangKeyword.CONTAINER));
        REFINE_RULES.put(YangKeyword.MUST, new RefineRule(true,
            YangKeyword.CONTAINER, YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.LIST,
            YangKeyword.ANYDATA, YangKeyword.ANYDATA));
        REFINE_RULES.put(YangKeyword.DEFAULT, new RefineRule(false,
            YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.CHOICE));
        REFINE_RULES.put(YangKeyword.MANDATORY, new RefineRule(false,
            YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.ANYXML, YangKeyword.ANYDATA));
        REFINE_RULES.put(YangKeyword.MIN_ELEMENTS, new RefineRule(false,
            YangKeyword.LEAF_LIST, YangKeyword.LIST));
        REFINE_RULES.put(YangKeyword.MAX_ELEMENTS, new RefineRule(false,
            YangKeyword.LEAF_LIST, YangKeyword.LIST));
        REFINE_RULES.put(YangKeyword.IF_FEATURE, new RefineRule(true,
            YangKeyword.CONTAINER, YangKeyword.LEAF, YangKeyword.LEAF_LIST, YangKeyword.LIST,
            YangKeyword.CHOICE, YangKeyword.CASE, YangKeyword.ANYDATA, YangKeyword.ANYDATA));
    }
}
