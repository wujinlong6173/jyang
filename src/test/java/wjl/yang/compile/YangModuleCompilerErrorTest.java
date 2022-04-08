package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class YangModuleCompilerErrorTest {
    private static final String BASE_DIR = "src/test/resources/yang/errors";

    private void check(MultiModulesComparator comparator) {
        if (comparator.getExpectErr() != null) {
            Assert.assertEquals(comparator.getExpectErr(), comparator.getInputErr());
        } else {
            Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
        }
    }

    @Test
    public void testAugment() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "feature");
        comparator.compare();
        check(comparator);
    }
}
