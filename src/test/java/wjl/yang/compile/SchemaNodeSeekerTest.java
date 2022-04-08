package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangStmt;
import wjl.yang.parser.SchemaNodeIdParser;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangError;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SchemaNodeSeekerTest {
    @Test
    public void testSearchSchemaNode() throws IOException, YangParseException {
        CompileTestHelper parser = new CompileTestHelper();
        YangMainModule module = parser.parseMainModule("complex/search-schema-node.yang");
        List<String> path = SchemaNodeIdParser.parse("networks/network/s:nodes/s:node", false);
        YangStmt found = SchemaNodeSeeker.searchInModule(module.getStmt(), path);
        Assert.assertEquals("search line 13 : list node", found.toString());

        path = SchemaNodeIdParser.parse("networks/network/id", false);
        found = SchemaNodeSeeker.searchInModule(module.getStmt(), path);
        Assert.assertEquals("search line 10 : leaf id", found.toString());

        assertError(module, "networks/x:network", "search line 1 : module search x:network invalid prefix.");
        assertError(module, "networks/nodes", "search line 1 : module search nodes is not found.");
    }

    private void assertError(YangMainModule module, String pathStr, String msg) throws IOException, YangParseException {
        List<String> path = SchemaNodeIdParser.parse(pathStr, false);
        SchemaNodeSeeker.searchInModule(module.getStmt(), path);
        if (module.getErrors() != null) {
            for (YangError err : module.getErrors()) {
                if (Objects.equals(msg, err.toString())) {
                    return;
                }
            }
            module.getErrors().clear();
            Assert.fail("should report error : " + msg);
        }
    }
}