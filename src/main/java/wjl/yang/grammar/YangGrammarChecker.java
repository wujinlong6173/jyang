package wjl.yang.grammar;

import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangSubModule;

import java.util.Locale;
import java.util.Map;

/**
 * 校验单个YANG文件是否满足格式要求。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangGrammarChecker {
    private static final String NOT_SUPPORTED_STMT = "not supported statement.";

    public void check(YangMainModule module) {
        check(module, module.getStmt(), YangGrammar.MODULE);
    }

    public void check(YangSubModule module) {
        check(module, module.getStmt(), YangGrammar.SUB_MODULE);
    }

    private void check(YangModule module, YangStmt stmt, StmtGrammar gram) {
        // 调用者保证键值匹配
        String err = gram.checkToken(stmt.getValueToken());
        if (err != null) {
            module.addError(stmt, err);
            return;
        }

        err = gram.checkValue(stmt.getValue());
        if (err != null) {
            module.addError(stmt, err);
            return;
        }

        checkSubStatements(module, stmt, gram);
    }

    private void checkSubStatements(YangModule module, YangStmt stmt, StmtGrammar gram) {
        Map<String, SubStmtGrammar> subGramMap = gram.getSubStatements(stmt.getValue());
        if (subGramMap.isEmpty()) {
            if (stmt.getSubStatements() != null) {
                // 关键字不需要子句，却写了字句
                for (YangStmt sub : stmt.getSubStatements()) {
                    if (sub.getExtensionPrefix() == null) {
                        module.addError(sub, NOT_SUPPORTED_STMT);
                    }
                }
            }
        } else if (stmt.getSubStatements() == null) {
            // 没有写字句，检查必须写的字句
            for (Map.Entry<String, SubStmtGrammar> entry : subGramMap.entrySet()) {
                if (entry.getValue().getMin() > 0) {
                    module.addError(stmt, String.format(Locale.ENGLISH, "require sub statement %s.", entry.getKey()));
                }
            }
        } else {
            // 检查
            int[] count = new int[subGramMap.size()];
            for (YangStmt sub : stmt.getSubStatements()) {
                if (sub.getExtensionPrefix() != null) {
                    continue;
                }
                SubStmtGrammar subGram = subGramMap.get(sub.getKey());
                if (subGram == null) {
                    module.addError(sub, NOT_SUPPORTED_STMT);
                } else {
                    check(module, sub, subGram.getStmt());
                    count[subGram.getIndex()] ++;
                }
            }

            // 检查子句的数量是否满足要求
            for (Map.Entry<String, SubStmtGrammar> entry : subGramMap.entrySet()) {
                SubStmtGrammar subGram = entry.getValue();
                if (count[subGram.getIndex()] < subGram.getMin()) {
                    module.addError(stmt, String.format(Locale.ENGLISH, "require sub statement %s.", entry.getKey()));
                }
                if (subGram.getMax() > 0 && subGram.getMax() < count[subGram.getIndex()]) {
                    module.addError(stmt, String.format(Locale.ENGLISH, "too many sub statement %s.", entry.getKey()));
                }
            }
        }
    }
}
