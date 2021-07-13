package com.simon.vpohode;

import android.content.res.Resources;

import java.util.stream.IntStream;

public enum Styles {
    NONE(R.string.none),
    CASUAL(R.string.casual),
    BUSINESS(R.string.business),
    ELEGANT(R.string.elegant),
    SPORT(R.string.sport),
    HOME(R.string.home);

    private int styles;

    Styles (int nStyles) {
        styles = nStyles;
    }

    public int toInt() {
        return styles;
    }

    public static int stringToResource(Resources resource, String style){
        for(Styles x : Styles.values()){
            if(resource.getString(x.toInt()).equals(style)){
                return x.toInt();
            }
        }
        return -1;
    }

    public static int getOrdinalByString (int input){
        for (Styles s : Styles.values()){
            if (s.styles == input){
                return s.ordinal();
            }
        }
        return 0;
    }

}
