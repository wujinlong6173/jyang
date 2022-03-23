package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class SchemaNodeIdParserTest {
    private SchemaNodeIdParser parser = new SchemaNodeIdParser();

    @Test
    public void schemaNodeId() {
        assertOk("order/items/name", "order", "items", "name");
        assertOk("/order/ss:items/name", "order", "ss:items", "name");

        assertError("", "require node identifier");
        assertError("order/items/", "require node identifier");
        assertError("order//items", "require node identifier");
        assertError("a:b:c", "unmatched Input line 1:4");
    }

    private void assertOk(String str, String...result) {
        YangLex lex = new YangLex(new StringReader(str));
        try {
            parser.parse(lex);
        } catch (IOException | YangParseException err ) {
            Assert.fail(err.getMessage());
        }
        Assert.assertEquals(Arrays.asList(result), parser.getStack());
    }

    private void assertError(String str, String msg) {
        YangLex lex = new YangLex(new StringReader(str));
        try {
            parser.parse(lex);
        } catch (IOException | YangParseException err) {
            if (!err.getMessage().contains(msg)) {
                Assert.fail("should report error : " + msg);
            }
        }
    }
}