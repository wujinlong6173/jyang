package wjl.yang.grammar;

import wjl.yang.model.YangStmt;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

    private void check(YangStmt stmt, StmtGrammar gram) {
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

    private void checkSubStatements(YangStmt stmt, StmtGrammar gram) {
        Map<String, SubStmtGrammar> subGramMap = gram.getSubStatements();
        if (subGramMap.isEmpty()) {
            if (stmt.getSubStatements() != null) {
                // 关键字不需要子句，却写了字句
                for (YangStmt sub : stmt.getSubStatements()) {
                    if (sub.getExtensionPrefix() == null) {
                        addError(sub, NOT_SUPPORTED_STMT);
                    }
                }
            }
        } else if (stmt.getSubStatements() == null) {
            // 没有写字句，检查必须写的字句
            for (Map.Entry<String, SubStmtGrammar> entry : subGramMap.entrySet()) {
                if (entry.getValue().getMin() > 0) {
                    addError(stmt, String.format(Locale.ENGLISH, "require sub statement %s.", entry.getKey()));
                }
            }
        } else {
            // 检查
            int[] count = new int[gram.getSubStatementCount()];
            for (YangStmt sub : stmt.getSubStatements()) {
                if (sub.getExtensionPrefix() != null) {
                    continue;
                }
                SubStmtGrammar subGram = subGramMap.get(sub.getKey());
                if (subGram == null) {
                    addError(sub, NOT_SUPPORTED_STMT);
                } else {
                    check(sub, subGram.getStmt());
                    count[subGram.getIndex()] ++;
                }
            }

            // 检查子句的数量是否满足要求
            for (Map.Entry<String, SubStmtGrammar> entry : subGramMap.entrySet()) {
                SubStmtGrammar subGram = entry.getValue();
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
