package wjl.yang.utils;

import wjl.yang.parser.YangStmt;

public class YangError {
    private final YangStmt pos;
    private final String msg;

    public YangError(YangStmt pos, String msg) {
        this.pos = pos;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return pos.toString() + msg;
    }
}
