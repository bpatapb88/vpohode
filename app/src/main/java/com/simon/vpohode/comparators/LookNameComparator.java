package com.simon.vpohode.comparators;

import com.simon.vpohode.Look;

import java.util.Comparator;

public class LookNameComparator implements Comparator<Look> {
    @Override
    public int compare(Look o1, Look o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
