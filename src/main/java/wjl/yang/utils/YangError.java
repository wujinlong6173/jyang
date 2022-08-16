package wjl.yang.utils;

import wjl.yang.model.YangStmt;

/**
 * 记录解析过程中发现的错误。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangError {
    private final YangStmt pos;
    private final String msg;

    public YangError(YangStmt pos, String msg) {
        this.pos = pos;
        this.msg = msg;
    }

    public YangStmt getPos() {
        return pos;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return pos.toString() + " " + msg;
    }
}
