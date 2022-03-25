package wjl.yang.model;

/**
 * 展开 uses 语句时，复制 grouping 中定义的语句。
 * oriModule 填 uses 语句所在的模块或子模块，line 填零。
 */
public class YangStmtClone extends YangStmt {
    /**
     * 引用被复制的语句，仅用于构造错误提示信息。
     */
    private YangStmt source;

    /**
     * 引用 uses 语句本身，仅用于构造错误提示信息。
     */
    private YangStmt uses;

    public void setSource(YangStmt source) {
        this.source = source;
    }

    public void setUses(YangStmt uses) {
        this.uses = uses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (uses != null) {
            sb.append(uses.toString()).append("> ");
        }
        if (source != null) {
            sb.append(source.toString());
        }
        return sb.toString();
    }
}
