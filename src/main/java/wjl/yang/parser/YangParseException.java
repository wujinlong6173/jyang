package wjl.yang.parser;

public class YangParseException extends Exception {
    public YangParseException(String msg) {
        super(msg);
    }

    public YangParseException(String msg, int line, String value) {
        super(String.format("parse error line %d %s, input is %s.", line, msg, value));
    }

    public YangParseException(int line, int col, char value) {
        super(String.format("unmatched Input line %d:%d, input is %d.", line, col, (int)value));
    }
}
