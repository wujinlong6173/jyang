package wjl.yang.parser;

public class YangParseException extends Exception {
    public YangParseException(String msg, int line, String value) {
        super(String.format("parse error at %d %s, input is %s.", line, msg, value));
    }
}
