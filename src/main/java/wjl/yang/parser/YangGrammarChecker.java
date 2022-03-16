package wjl.yang.parser;

import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.util.*;

/**
 * 校验单个YANG文件是否满足格式要求。
 */
public class YangGrammarChecker {
    private static final String MUST_DEFINE_MODULE = "must define module or submodule.";
    private static final String NOT_SUPPORTED_STMT = "not supported statement.";
    private final List<YangError> errors = new ArrayList<>();

    public boolean check(YangStmt root) {
        errors.clear();
        if (Objects.equals(YangKeyword.MODULE, root.getKey())) {
            check(root, YangGrammar.MODULE);
        } else if (Objects.equals(YangKeyword.SUBMODULE, root.getKey())) {
            check(root, YangGrammar.SUB_MODULE);
        } else {
            addError(root, MUST_DEFINE_MODULE);
        }
        return errors.isEmpty();
    }

    public List<YangError> getErrors() {
        return errors;
    }

    private void check(YangStmt stmt, YangGrammar.Stmt gram) {
        // 调用者保证键值匹配
        String err = gram.checkToken(stmt.getValueToken());
        if (err != null) {
            addError(stmt, err);
            return;
        }

        err = gram.checkValue(stmt.getValue());
        if (err != null) {
            addError(stmt, err);
            return;
        }

        checkSubStatements(stmt, gram);
    }

    private void checkSubStatements(YangStmt stmt, YangGrammar.Stmt gram) {
        Map<String, YangGrammar.SubStmt> subGramMap = gram.getSubStatements();
        if (subGramMap.isEmpty()) {
            if (stmt.getSubStatements() != null) {
                for (YangStmt sub : stmt.getSubStatements()) {
                    addError(sub, NOT_SUPPORTED_STMT);
                }
            }
        } else if (stmt.getSubStatements() == null) {
            for (Map.Entry<String, YangGrammar.SubStmt> entry : subGramMap.entrySet()) {
                if (entry.getValue().getMin() > 0) {
                    addError(stmt, String.format(Locale.ENGLISH, "require sub statement %s.", entry.getKey()));
                }
            }
        } else {
            int[] count = new int[gram.getSubStatementCount()];
            for (YangStmt sub : stmt.getSubStatements()) {
                YangGrammar.SubStmt subGram = subGramMap.get(sub.getKey());
                if (subGram == null) {
                    addError(sub, NOT_SUPPORTED_STMT);
                } else {
                    check(sub, subGram.getStmt());
                    count[subGram.getIndex()] ++;
                }
            }
            for (Map.Entry<String, YangGrammar.SubStmt> entry : subGramMap.entrySet()) {
                YangGrammar.SubStmt subGram = entry.getValue();
                if (count[subGram.getIndex()] < subGram.getMin()) {
                    addError(stmt, String.format(Locale.ENGLISH, "require sub statement %s.", entry.getKey()));
                }
                if (subGram.getMax() > 0 && subGram.getMax() < count[subGram.getIndex()]) {
                    addError(stmt, String.format(Locale.ENGLISH, "too many sub statement %s.", entry.getKey()));
                }
            }
        }
    }

    private void addError(YangStmt stmt, String err) {
        errors.add(new YangError(stmt, err));
    }

}
