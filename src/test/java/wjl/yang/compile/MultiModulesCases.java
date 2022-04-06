package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class MultiModulesCases {
    private void check(MultiModulesComparator comparator) {
        if (comparator.getOutputStr() != null) {
            Assert.assertEquals(comparator.getOutputStr(), comparator.getResultStr());
        }
        Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
    }

    @Test
    public void testAugment() {
        MultiModulesComparator comparator = new MultiModulesComparator("augment");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testGrouping() {
        MultiModulesComparator comparator = new MultiModulesComparator("grouping");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testRefine() {
        MultiModulesComparator comparator = new MultiModulesComparator("refine");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testTypedef() {
        MultiModulesComparator comparator = new MultiModulesComparator("typedef");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testIdentity() {
        MultiModulesComparator comparator = new MultiModulesComparator("identity");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testExtension() {
        MultiModulesComparator comparator = new MultiModulesComparator("extension");
        comparator.compare();
        check(comparator);
    }

    @Test
    public void testRpc() {
        MultiModulesComparator comparator = new MultiModulesComparator("rpc");
        comparator.compare();
        check(comparator);
    }
}
