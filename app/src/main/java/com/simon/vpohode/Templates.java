package com.simon.vpohode;

public enum Templates {
    NONE("Выбери шаблон"),
    TSHIRT("Футболка"),
    SHIRT("Рубашка"),
    SWEATER("Кофта"),
    PANTS("Штаны"),
    JEANS("Джинсы"),
    JACKET("Осеняя куртка"),
    COAT("Пальто");

    private String templates;

    Templates (String nTemplates){
        templates = nTemplates;
    }

    @Override
    public String toString() {
        return templates;
    }

    public static Item fillTemplate (int input){
        Item x = new Item();
        switch (input){
            case 1:
                x.setName("Футболка");
                x.setTermid(1d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(0);
                x.setLayer(1);
                break;
            case 2:
                x.setName("Рубашка");
                x.setTermid(2d);
                x.setStyle(Styles.BUSINESS.toString());
                x.setTop(0);
                x.setLayer(1);
                break;
            case 3:
                x.setName("Кофта");
                x.setTermid(4d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(0);
                x.setLayer(2);
                break;
            case 4:
                x.setName("Штаны");
                x.setTermid(2d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(1);
                break;
            case 5:
                x.setName("Джинсы");
                x.setTermid(2d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(1);
                break;
            case 6:
                x.setName("Осеняя куртка");
                x.setTermid(5d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(0);
                x.setLayer(3);
                break;
            case 7:
                x.setName("Пальто");
                x.setTermid(6d);
                x.setStyle(Styles.CASUAL.toString());
                x.setTop(0);
                x.setLayer(3);
                break;
            // Шаблоны можно добавить тут + добавить имя в spinnerTemplate - --- - - - - --
        }
        return x;

    }

}
