package wjl.yang.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UiGraphSortTest {
    @Test
    public void testSort() {
        UiGraph<Integer, Integer> graph = new UiGraph<>();
        graph.addEdge(1, 2, 12);
        graph.addEdge(2, 3, 23);
        graph.addEdge(3, 4, 34);
        graph.addEdge(4, 5, 45);
        graph.addEdge(5, 6, 56);
        List<Integer> s1 = UiGraphSort.sort(graph.clone(), null);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), s1);

        graph.addEdge(4, 3, 43);
        List<Integer> s2 = UiGraphSort.sort(graph.clone(), null);
        Assert.assertEquals(Arrays.asList(1, 2), s2);
    }

    @Test
    public void testSortReverse() {
        UiGraph<Integer, Integer> graph = new UiGraph<>();
        graph.addEdge(1, 2, 12);
        graph.addEdge(2, 3, 23);
        graph.addEdge(3, 4, 34);
        graph.addEdge(4, 5, 45);
        graph.addEdge(5, 6, 56);
        List<Integer> s1 = UiGraphSort.sortReverse(graph.clone(), null);
        Assert.assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), s1);

        graph.addEdge(4, 3, 43);
        List<Integer> s2 = UiGraphSort.sortReverse(graph.clone(), null);
        Assert.assertEquals(Arrays.asList(6, 5), s2);
    }
}