package com.simon.vpohode;

public class Rules {

    public static double ACCURACY = 0.5;

    public static int getLayersTop(double temp){
        int layers = 0;
        if(temp >= 21){
            layers = 1;
        }else if(temp >= 6){
            layers = 2;
        }else{
            layers = 3;
        }
        return layers;
    }
    public static int getLayersBot(double temp){
        int layers = 0;
        if(temp >= 5){
            layers = 1;
        }else{
            layers = 2;
        }
        return layers;
    }
}
