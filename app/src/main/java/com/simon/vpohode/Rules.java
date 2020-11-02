package com.simon.vpohode;

public class Rules {
    public static final int MAX_TEMPER = 33;

    //step
    public static final double COEFFICIENT = 4;

    //точность
    public static final double ACCURACY = 0.5;

    public static int getLayers(double temp){
        int layers = 0;
        if(temp >= 20){
            layers = 1;
        }else if(temp >= 9){
            layers = 2;
        }else{
            layers = 3;
        }
        return layers;
    }
}
