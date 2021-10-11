package com.simon.vpohode.comparators;

import com.simon.vpohode.Look;

import java.util.Comparator;

public class LookIdComparator implements Comparator<Look> {
    @Override
    public int compare(Look o1, Look o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }
}