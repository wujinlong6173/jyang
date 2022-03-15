package wjl.yang.parser;

public class YangParseException extends Exception {
    public YangParseException(String msg, int line, String value) {
        super(String.format("Parse error at %d : %s\ninput is %s.", line, msg, value));
    }
}
