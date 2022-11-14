package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YangLexTest {
    @Test
    public void testComment() {
        assertTokens("abc/**/123", "abc", "123");
        assertTokens("abc/****/123", "abc", "123");
        assertTokens("abc/***/123", "abc", "123");
        assertTokens("abc/* *\r\n */123", "abc", "123");
        assertTokens("abc/*******\n\n", "abc");
    }

    private void assertTokens(String tpl, String ... tokens) {
        Reader reader = new StringReader(tpl);
        YangLex lex = new YangLex(reader);

        try {
            List<String> real = new ArrayList<>();
            int token = lex.yylex();
            while (token != -1) {
                real.add(lex.getString());
                token = lex.yylex();
            }
            List<String> exp = Arrays.asList(tokens);
            Assert.assertEquals(tpl, exp, real);
        } catch (IOException | YangParseException err) {
            Assert.fail(err.getMessage());
        }
    }
}
