package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class MultiModulesCases {
    private static final String BASE_DIR = "src/test/resources/yang/models";

    private void check(MultiModulesComparator comparator) {
        if (comparator.getOutputStr() != null) {
            Assert.assertEquals(comparator.getOutputStr(), comparator.getResultStr());
        }
        Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
    }

    @Test
    public void testAugment() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "augment");
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
    public void testRefine() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "refine");
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
    public void testIdentity() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "identity");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testExtension() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "extension");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testRpc() {
        MultiModulesComparator comparator = new MultiModulesComparator(BASE_DIR, "rpc");
        comparator.compare();
        check(comparator);
    }
}
