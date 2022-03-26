package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtClone;
import wjl.yang.parser.SchemaNodeIdParser;
import wjl.yang.parser.YangParseException;
import wjl.yang.utils.YangError;
import wjl.yang.utils.YangKeyword;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CompileUtilTest {
    private CompileTestHelper parser = new CompileTestHelper();

    @Test
    public void testCloneStmt() {
        YangMainModule module = parser.parseMainModule("complex/grouping.yang");
        Assert.assertNull(parser.getErrors());

        YangModule dummy = new YangMainModule("dummy", "dy", "2022-03-25");
        YangStmt uses = new YangStmt();
        uses.setOriModule(dummy);
        uses.setKey(YangKeyword.USES);
        uses.setLine(25);
        uses.setValue("clone");
        YangStmtClone clone = CompileUtil.cloneStmt(uses, module.getStmt());
        YangStmt srcRoot = module.getStmt().searchOne(YangKeyword.CONTAINER);
        YangStmt cloneRoot = clone.searchOne(YangKeyword.CONTAINER);
        Assert.assertEquals("grouping line 44 : container root ", srcRoot.toString());
        Assert.assertEquals("dummy line 25 : uses clone > grouping line 44 : container root ", cloneRoot.toString());
    }

    @Test
    public void testSearchSchemaNode() throws IOException, YangParseException {
        YangMainModule module = parser.parseMainModule("complex/search-schema-node.yang");
        List<String> path = SchemaNodeIdParser.parse("networks/network/s:nodes/s:node", false);
        YangStmt found = CompileUtil.searchSchemaNode(module, module.getStmt(), module.getStmt(), path);
        Assert.assertEquals("search line 13 : list node ", found.toString());

        path = SchemaNodeIdParser.parse("networks/network/id", false);
        found = CompileUtil.searchSchemaNode(module, module.getStmt(), module.getStmt(), path);
        Assert.assertEquals("search line 10 : leaf id ", found.toString());

        assertError(module, "networks/x:network", "search line 1 : module search x:network invalid prefix.");
        assertError(module, "networks/nodes", "search line 1 : module search nodes is not found.");
    }

    private void assertError(YangMainModule module, String pathStr, String msg) throws IOException, YangParseException {
        List<String> path = SchemaNodeIdParser.parse(pathStr, false);
        CompileUtil.searchSchemaNode(module, module.getStmt(), module.getStmt(), path);
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