package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public class IfFeatureExprParserTest {
    private IfFeatureExprParser parser = new IfFeatureExprParser();

    @Test
    public void ifFeatureExpr() throws IOException, YangParseException {
        String expr = "a and (b or not c) or not d or e and f";
        YangLex lex = new YangLex(new StringReader(expr));
        parser.parse(lex);
        String [] exp = new String[] {"a", "b", "c", "!", "v", "^", "d", "!", "v", "e", "f", "^", "v"};
        Assert.assertArrayEquals(exp, parser.getStack().toArray());

        assertError("a:b:c", "unmatched input line 1");
    }

    private void assertError(String expr, String msg) {
        YangLex lex = new YangLex(new StringReader(expr));
        try {
            parser.parse(lex);
        } catch (IOException | YangParseException err) {
            if (!err.getMessage().contains(msg)) {
                Assert.fail("should report error : " + msg);
            }
        }
    }
}