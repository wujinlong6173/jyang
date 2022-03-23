package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;
import wjl.yang.model.YangMainModule;

import java.util.Collections;

public class GroupingCompilerTest {
    private CompileTestHelper parser = new CompileTestHelper();

    @Test
    public void testGrouping() {
        YangMainModule module = parser.parseMainModule("complex/grouping.yang");
        Assert.assertNull(parser.getErrors());
        GroupingCompiler gc = new GroupingCompiler();
        gc.expandGrouping(Collections.singletonList(module));
    }
}