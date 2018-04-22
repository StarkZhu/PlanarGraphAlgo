package algorithms.SSSP;

import selfdualgraph.*;

import java.util.*;

public class Region implements Comparable<Region> {
    TreeMap<Double, Set<Region>> priorityQueue;     // mimic pq operation by delete and re-insert
    Region parent;
    Dart dart;
    double key;
    double alpha;

    public Region(double al, Dart d) {
        alpha = al;
        dart = d;
        priorityQueue = new TreeMap<>();
        key = Double.POSITIVE_INFINITY;
    }

    @Override
    public int compareTo(Region other) {
        if (this.key - other.key < 0) return -1;
        else if (this.key - other.key > 0) return 1;
        else return 0;
    }

    public boolean isAtomic() {
        if (priorityQueue.size() != 1) return false;
        Set<Region> sub = priorityQueue.firstEntry().getValue();
        if (sub.size() > 1) return false;
        Region r = sub.iterator().next();
        return r.priorityQueue.size() == 0;
    }

    public Region minItem() {
        return priorityQueue.firstEntry().getValue().iterator().next();
    }

    public double minKey() {
        return priorityQueue.firstKey();
    }

    /**
     * delete r, update r's key, re-insert r
     * O(logN) time
     * @param r
     * @param k
     */
    public void updateKey(Region r, double k) {
        Set<Region> set = priorityQueue.get(r.key);
        set.remove(r);
        if (set.size() == 0) priorityQueue.remove(r.key);
        r.key = k;
        set = priorityQueue.getOrDefault(k, new HashSet<>());
        set.add(r);
        priorityQueue.put(k, set);
    }

    /**
     * only for atomic region
     * @return
     */
    public Dart getDart() {
        return dart;
    }

    /**
     * Inf for level 2 (root G)
     * logN for level 1
     * 1 for level 0 (atomic)
     * @return
     */
    public double getAlpha() {
        return alpha;
    }

    public Region getParent() {
        return parent;
    }

    public void setParent(Region parent) {
        this.parent = parent;
    }

    public Set<Region> getAllSubregion() {
        Set<Region> subs = new HashSet<>();
        for (Set<Region> reg : priorityQueue.values()) subs.addAll(reg);
        return subs;
    }

    public void addSubRegion(Region r) {
        Set<Region> set = priorityQueue.getOrDefault(r.key, new HashSet<>());
        set.add(r);
        priorityQueue.put(r.key, set);
        r.setParent(this);
    }

    @Override
    public String toString() {
        return String.format("Region[%s]", dart);
    }
}
