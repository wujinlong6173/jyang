package wjl.yang.utils;

import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 拓扑排序算法。
 */
public class UiGraphSort {
    /**
     * 反向拓扑排序，正常情况会清空输入的图数据，如果没有清空表示有环。
     *
     * @param graph 此参数会被修改
     * @param filter 将节点塞入返回值前进行过滤
     * @param <N> 节点类型
     * @param <E> 边类型
     * @return 反向排序的节点
     */
    public static <N,E> List<N> sortReverse(UiGraph<N, E> graph, Predicate<N> filter) {
        List<N> ret = new ArrayList<>();
        Set<N> nodes = graph.copyNodes();
        Set<N> next = new HashSet<>();
        while (!nodes.isEmpty()) {
            for (N node : nodes) {
                Map<N, E> suc = graph.successors(node);
                Map<N, E> pre = graph.predecessors(node);
                if (suc == null && pre == null) {
                    continue; // node is removed
                }

                if (suc != null && !suc.isEmpty()) {
                    continue;
                }

                if (pre != null) {
                    for (Map.Entry<N, E> pn : pre.entrySet()) {
                        next.add(pn.getKey());
                    }
                }
                if (filter == null || filter.test(node)) {
                    ret.add(node);
                }
                graph.delNode(node);
            }
            Set<N> temp = nodes;
            nodes = next;
            next = temp;
            next.clear();
        }

        return ret;
    }
}
