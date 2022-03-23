package wjl.yang.utils;

import org.junit.Assert;
import org.junit.Test;

public class UiGraphTest {
    @Test
    public void testGraph() {
        UiGraph<Integer, Integer> graph = new UiGraph<>();
        graph.addNode(1);
        graph.addEdge(1, 2, 12);
        graph.addEdge(2, 1, 21);
        graph.addEdge(2, 3, 23);
        graph.addEdge(2, 4, 24);
        graph.addEdge(4, 1, 41);
        Assert.assertEquals(2, graph.predecessors(1).size());
        Assert.assertEquals(1, graph.successors(1).size());
        Assert.assertEquals(1, graph.predecessors(2).size());
        Assert.assertEquals(3, graph.successors(2).size());

        graph.delNode(2);
        Assert.assertEquals(1, graph.predecessors(1).size());
        Assert.assertEquals(0, graph.successors(1).size());
        Assert.assertEquals(0, graph.predecessors(4).size());

        graph.delEdge(4, 1);
        Assert.assertEquals(0, graph.predecessors(1).size());
        Assert.assertEquals(0, graph.successors(4).size());
    }
}