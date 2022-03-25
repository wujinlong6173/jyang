package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangMainModule;
import wjl.yang.model.YangModule;
import wjl.yang.model.YangStmt;
import wjl.yang.model.YangStmtClone;
import wjl.yang.utils.YangKeyword;

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
        YangStmtClone clone = CompileUtil.cloneStmt(dummy, uses, module.getStmt());
        YangStmt srcRoot = module.getStmt().searchOne(YangKeyword.CONTAINER);
        YangStmt cloneRoot = clone.searchOne(YangKeyword.CONTAINER);
        Assert.assertEquals("grouping line 44 : container root ", srcRoot.toString());
        Assert.assertEquals("dummy line 25 : uses clone > grouping line 44 : container root ", cloneRoot.toString());
    }
}