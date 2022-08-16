package wjl.yang.grammar;

/**
 * 定义语句之间包含关系的规则
 *
 * @author wujinlong
 * @since 2022-8-16
 */
class SubStmtGrammar {
    private final StmtGrammar stmt;
    private final int min;
    private final int max;
    private final int order;
    private final int index;

    SubStmtGrammar(StmtGrammar stmt, int min, int max, int order, int index) {
        this.stmt = stmt;
        this.min = min;
        this.max = max;
        this.order = order;
        this.index = index;
    }

    StmtGrammar getStmt() {
        return stmt;
    }

    int getMin() {
        return min;
    }

    int getMax() {
        return max;
    }

    int getIndex() {
        return index;
    }
}

