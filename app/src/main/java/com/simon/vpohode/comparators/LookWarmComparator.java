package com.simon.vpohode.comparators;

import com.simon.vpohode.Look;

import java.util.Comparator;

public class LookWarmComparator implements Comparator<Look> {
    @Override
    public int compare(Look o1, Look o2) {
        return Double.compare((o1.getMin()+o1.getMax())/2,(o2.getMin()+o2.getMax())/2);
    }
}
