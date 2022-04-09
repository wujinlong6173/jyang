package wjl.yang.grammar;

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

