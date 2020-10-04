package com.simon.vpohode;

import android.util.Log;

public enum Styles {
    NONE("Стиль не выбран"),
    CASUAL("Кэжуал"),
    BUSINESS("Бизнес"),
    ELEGANT("Элегантный"),
    SPORT("Спорт"),
    HOME("Домашнее");

    private String Styles;

    Styles (String nStyles) {
        Styles = nStyles;
    }

    @Override
    public String toString() {
        return Styles;
    }

}
