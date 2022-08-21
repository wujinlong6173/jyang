package wjl.yang.parser;

/**
 * 解析Yang文件是产生的异常。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class YangParseException extends Exception {
    public YangParseException(String msg) {
        super(msg);
    }

    public YangParseException(String msg, int line, String value) {
        super(String.format("parse error line %d %s, input is %s.", line, msg, value));
    }

    public YangParseException(int line, int col, char value) {
        super(String.format("unmatched input line %d, input is %d.", line, (int)value));
    }
}
