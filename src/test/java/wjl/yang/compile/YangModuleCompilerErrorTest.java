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

    @Test
    public void testIdentity() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "identity");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testTypedef() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "typedef");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testGrouping() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "grouping");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testType() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "type");
        comparator.compare();
        check(comparator);
    }
}
