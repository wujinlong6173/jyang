package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangToken;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class YangLexTest {
    @Test
    public void testComment() {
        assertTokens("abc/**/123", YangToken.IDENTITY, YangToken.INTEGER);
        assertTokens("abc/****/123", YangToken.IDENTITY, YangToken.INTEGER);
        assertTokens("abc/***/123", YangToken.IDENTITY, YangToken.INTEGER);
        assertTokens("abc/* *\r\n */123", YangToken.IDENTITY, YangToken.INTEGER);
    }

    private void assertTokens(String tpl, int ... tokens) {
        Reader reader = new StringReader(tpl);
        YangLex lex = new YangLex(reader);

        try {
            List<Integer> real = new ArrayList<>();
            int token = lex.yylex();
            while (token != -1) {
                real.add(token);
                token = lex.yylex();
            }
            List<Integer> exp = new ArrayList<>();
            for (int i : tokens) {
                exp.add(i);
            }
            Assert.assertEquals(tpl, exp, real);
        } catch (IOException | YangParseException err) {
            Assert.fail(err.getMessage());
        }
    }
}
