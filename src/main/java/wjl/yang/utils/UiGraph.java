package wjl.yang.utils;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用哈希表简单地实现单向图。
 */
public class UiGraph<N, E> {
    private final Map<N, Map<N, E>> predecessors = new HashMap<>();
    private final Map<N, Map<N, E>> successors = new HashMap<>();

    public void addNode(N nodeId) {
        predecessors.computeIfAbsent(nodeId, (key) -> new HashMap<>());
        successors.computeIfAbsent(nodeId, (key) -> new HashMap<>());
    }

    public void addEdge(N src, N dst, E edge) {
        Map<N, E> pre = predecessors.computeIfAbsent(dst, (key) -> new HashMap<>());
        Map<N, E> suc = successors.computeIfAbsent(src, (key) -> new HashMap<>());
        pre.put(src, edge);
        suc.put(dst, edge);
    }

    public void delNode(N nodeId) {
        Map<N, E> pre = predecessors.remove(nodeId);
        if (pre != null) {
            for (N pn : pre.keySet()) {
                Map<N, E> pnSuc = successors.get(pn);
                if (pnSuc != null) {
                    pnSuc.remove(nodeId);
                }
            }
        }

        Map<N, E> suc = successors.remove(nodeId);
        if (suc != null) {
            for (N sn : suc.keySet()) {
                Map<N, E> snPre = predecessors.get(sn);
                if (snPre != null) {
                    snPre.remove(nodeId);
                }
            }
        }
    }

    public E delEdge(N src, N dst) {
        Map<N, E> pre = predecessors.get(dst);
        Map<N, E> suc = successors.get(src);
        if (pre != null) {
            pre.remove(src);
        }
        if (suc != null) {
            return suc.remove(dst);
        }
        return null;
    }

    public Set<N> copyNodes() {
        Set<N> ret = new HashSet<>(predecessors.keySet());
        ret.addAll(successors.keySet());
        return ret;
    }

    public Map<N, E> predecessors(N nodeId) {
        return predecessors.get(nodeId);
    }

    public Map<N, E> successors(N nodeId) {
        return successors.get(nodeId);
    }

    public UiGraph<N, E> clone() {
        UiGraph<N, E> copy = new UiGraph<>();
        for (Map.Entry<N, Map<N, E>> pre : predecessors.entrySet()) {
            copy.predecessors.put(pre.getKey(), new HashMap<>(pre.getValue()));
        }
        for (Map.Entry<N, Map<N, E>> suc : successors.entrySet()) {
            copy.successors.put(suc.getKey(), new HashMap<>(suc.getValue()));
        }
        return copy;
    }

    public boolean isEmpty() {
        return predecessors.isEmpty() && successors.isEmpty();
    }
}
