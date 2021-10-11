package com.simon.vpohode.comparators;

import com.simon.vpohode.Look;

import java.util.Comparator;

public class LookEmanComparator implements Comparator<Look> {
@Override
public int compare(Look o1, Look o2) {
        return o2.getName().compareTo(o1.getName());
        }
}
