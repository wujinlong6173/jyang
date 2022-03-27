package wjl.yang.compile;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class MultiModulesCases {
    @Test
    public void testAugment() {
        MultiModulesComparator comparator = new MultiModulesComparator("augment");
        if (comparator.compare()) {
            Assert.assertEquals(comparator.getOutputStr(), comparator.getResultStr());
        } else {
            Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
        }
    }

    @Test
    public void testGrouping() {
        MultiModulesComparator comparator = new MultiModulesComparator("grouping");
        if (comparator.compare()) {
            Assert.assertEquals(comparator.getOutputStr(), comparator.getResultStr());
        } else {
            Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
        }
    }

    @Test
    public void testRefine() {
        MultiModulesComparator comparator = new MultiModulesComparator("refine");
        if (comparator.compare()) {
            Assert.assertEquals(comparator.getOutputStr(), comparator.getResultStr());
        } else {
            Assert.assertEquals(Collections.emptyList(), comparator.getErrors());
        }
    }
}
