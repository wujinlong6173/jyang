package wjl.yang.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class SchemaNodeIdParserTest {
    @Test
    public void schemaNodeId() {
        assertDescendant("order/items/name", "order", "items", "name");
        assertAbsolute("/order/ss:items/name", "order", "ss:items", "name");

        assertError("", "require node identifier");
        assertError("order/items/", "require node identifier");
        assertError("order//items", "require node identifier");
        assertError("a:b:c", "unmatched input line 1");
        assertError("/a/b", "require descendant schema id");
    }

    private void assertAbsolute(String str, String...result) {
        YangLex lex = new YangLex(new StringReader(str));
        try {
            List<String> path = SchemaNodeIdParser.parse(str, true);
            Assert.assertArrayEquals(result, path.toArray());
        } catch (IOException | YangParseException err ) {
            Assert.fail(err.getMessage());
        }
    }

    private void assertDescendant(String str, String...result) {
        try {
            List<String> path = SchemaNodeIdParser.parse(str, false);
            Assert.assertArrayEquals(result, path.toArray());
        } catch (IOException | YangParseException err ) {
            Assert.fail(err.getMessage());
        }
    }

    private void assertError(String str, String msg) {
        try {
            SchemaNodeIdParser.parse(str, false);
        } catch (IOException | YangParseException err) {
            if (!err.getMessage().contains(msg)) {
                Assert.fail("should report error : " + msg);
            }
        }
    }
}