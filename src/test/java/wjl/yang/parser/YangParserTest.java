package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtExt;

import java.io.IOException;
import java.io.StringReader;

public class YangParserTest {
    private YangParser parser = new YangParser();

    private YangStmt parseString(String model) throws IOException, YangParseException {
        YangLex lex = new YangLex(new StringReader(model));
        return parser.parse(lex);
    }

    private void assertError(String model, String err) {
        try {
            YangLex lex = new YangLex(new StringReader(model));
            parser.parse(lex);
            Assert.fail("should report error : " + err);
        } catch (IOException | YangParseException exp) {
            if (!exp.getMessage().contains(err)) {
                Assert.fail("should report error : " + err + ", not " + exp.getMessage());
            }
        }
    }

    @Test
    public void testStringValue() throws IOException, YangParseException {
        String model = "description \"this\" + \" is \" + \"string\";";
        YangStmt stmt = parseString(model);
        Assert.assertEquals("this is string", stmt.getValue());
    }

    @Test
    public void testExtension() throws IOException, YangParseException {
        String model = "test:error \"does not need value\";";
        YangStmt stmt = parseString(model);
        Assert.assertTrue(stmt instanceof YangStmtExt);
        YangStmtExt ext = (YangStmtExt)stmt;
        Assert.assertEquals("test", ext.getExtensionPrefix());
    }

    @Test
    public void testEmptySub() throws IOException, YangParseException {
        String model = "container root {}";
        YangStmt stmt = parseString(model);
        Assert.assertEquals("container", stmt.getKey());
        Assert.assertEquals("root", stmt.getValue());
        Assert.assertNull(stmt.getSubStatements());
    }

    @Test
    public void testSubStatements() throws IOException, YangParseException {
        String model = "container root { leaf x; leaf y; }";
        YangStmt stmt = parseString(model);
        Assert.assertEquals("x", stmt.getSubStatements().get(0).getValue());
        Assert.assertEquals("y", stmt.getSubStatements().get(1).getValue());
    }

    @Test
    public void testError() {
        assertError("description", "require semicolon or left brace");
        assertError("leaf x {", "require right brace");
        assertError("leaf x; {", "require yang keyword");
        assertError("leaf +; {", "require semicolon or left brace");
    }
}