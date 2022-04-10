package wjl.yang.grammar;

import wjl.yang.model.YangToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class StmtGrammar {
    // 语句的关键字
    private String key;
    // 允许的值类型
    private final Set<Integer> validTokens = new HashSet<>();
    // 枚举所有可能的取值，可选参数
    private String[] validValues;
    private int subStatementCount = 0;
    private final Map<String, SubStmtGrammar> subStatementByName = new HashMap<>();

    StmtGrammar(String key, int...validTokens) {
        this.key = key;
        for (int token : validTokens) {
            this.validTokens.add(token);
        }
    }

    void addSub(int min, int max, int order, StmtGrammar...stmtList) {
        for (StmtGrammar stmt : stmtList) {
            this.subStatementByName.put(stmt.key, new SubStmtGrammar(stmt, min, max, order, subStatementCount++));
        }
    }

    void addSub(StmtGrammar stmt, int min, int max) {
        this.subStatementByName.put(stmt.key, new SubStmtGrammar(stmt, min, max, 0, subStatementCount++));
    }

    void addSub(int min, int max, StmtGrammar...stmtList) {
        // 标准要求多种类型的子句加在一起至少出现一次，还没有实现
        for (StmtGrammar stmt : stmtList) {
            this.subStatementByName.put(stmt.key, new SubStmtGrammar(stmt, 0, max, 0, subStatementCount++));
        }
    }

    void setBooleanValues() {
        this.validValues = new String[] {"true", "false"};
    }

    void setValidValues(String... validValues) {
        this.validValues = validValues;
    }

    String getKey() {
        return key;
    }

    /**
     * 获取支持的子句
     *
     * @param value 语句的值，部分语句需要根据值决定支持哪些子句
     * @return 支持的子句
     */
    Map<String, SubStmtGrammar> getSubStatements(String value) {
        return subStatementByName;
    }

    String checkToken(int token) {
        if (validTokens.isEmpty()) {
            if (token != 0) {
                return "does not need value.";
            }
        } else if (!validTokens.contains(token)) {
            StringBuilder sb = new StringBuilder();
            sb.append("require");
            for (Integer vt : validTokens) {
                sb.append(" ").append(YangToken.getText(vt));
            }
            sb.append(", but is ").append(YangToken.getText(token)).append(".");
            return sb.toString();
        }
        return null;
    }

    String checkValue(String value) {
        if (validValues == null) {
            return null;
        }
        for (String vv : validValues) {
            if (Objects.equals(vv, value)) {
                return null;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("require");
        for (String vv : validValues) {
            sb.append(" ").append(vv);
        }
        sb.append(".");
        return sb.toString();
    }
}