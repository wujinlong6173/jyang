package wjl.yang.grammar;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.YangLex;
import wjl.yang.parser.YangParseException;
import wjl.yang.parser.YangParser;
import wjl.yang.utils.YangError;

import java.io.IOException;
import java.io.StringReader;

public class YangGrammarTest {
    private YangParser parser = new YangParser();
    private YangGrammarChecker checker = new YangGrammarChecker();

    private void assertError(String model, String err) {
        String wrap = String.format("module test { %s }", model);
        YangLex lex = new YangLex(new StringReader(wrap));
        try {
            YangStmt stmt = parser.parse(lex);
            checker.check(stmt);
            if (checker.getErrors() != null) {
                YangError item = checker.getErrors().get(0);
                String realErr = item.toString();
                if (!realErr.contains(err)) {
                    Assert.fail("should report error : " + err + ", not " + realErr);
                }
            } else {
                Assert.fail("should report error : " + err);
            }
        } catch (IOException | YangParseException exp) {
            Assert.fail(exp.getMessage());
        }
    }

    @Test
    public void checkKey() {
        assertError("description \"test\" { description ;} ", "not supported statement");
        assertError("leaf x;", "require sub statement type");
        assertError("leaf x { default 0; }", "require sub statement type");
        assertError("leaf x { type string; type int32; }", "too many sub statement type");
        assertError("container root { default 0; }", "not supported statement");
    }

    @Test
    public void checkTokenValue() {
        assertError("yang-version 0.3;", "require 1.1");
        assertError("yang-version abc;", "require decimal, but is identity");
        assertError("container root {config abc;}", "require true false");
        assertError("rpc active { input x {}}", "does not need value");
    }
}