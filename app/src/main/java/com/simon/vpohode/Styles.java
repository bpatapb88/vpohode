package com.simon.vpohode;

public enum Styles {
    NONE("Стиль не выбран"),
    CASUAL("Кэжуал"),
    BUSINESS("Бизнес"),
    ELEGANT("Элегантный"),
    SPORT("Спорт"),
    HOME("Домашнее");

    private String styles;

    Styles (String nStyles) {
        styles = nStyles;
    }

    @Override
    public String toString() {
        return styles;
    }

    public static int getOrdinalByString (String input){
        for (Styles s : Styles.values()){
            if (s.styles.equals(input)){
                return s.ordinal();
            }
        }
        return 0;
    }

}
