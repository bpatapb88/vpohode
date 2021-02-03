package com.simon.vpohode;

public enum Templates {
    NONE("Выбери шаблон"),
    TSHIRT("Футболка"),
    SHIRT("Рубашка"),
    SWEATER("Кофта"),
    PANTS("Штаны"),
    JEANS("Джинсы"),
    JACKET("Осеняя куртка"),
    COAT("Пальто"),
    KALSONY("Kalsony"),
    SNIKERS("Krosovki");

    private String templates;

    Templates (String nTemplates){
        templates = nTemplates;
    }

    @Override
    public String toString() {
        return templates;
    }

    public static Item fillTemplate (int input){
        Item templateOfItem = new Item();
        switch (input){
            case 1:
                templateOfItem.setName("Футболка");
                templateOfItem.setTermid(1d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(0);
                templateOfItem.setLayer(1);
                break;
            case 2:
                templateOfItem.setName("Рубашка");
                templateOfItem.setTermid(2d);
                templateOfItem.setStyle(Styles.BUSINESS.toString());
                templateOfItem.setTop(0);
                templateOfItem.setLayer(1);
                break;
            case 3:
                templateOfItem.setName("Кофта");
                templateOfItem.setTermid(4d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(0);
                templateOfItem.setLayer(2);
                break;
            case 4:
                templateOfItem.setName("Штаны");
                templateOfItem.setTermid(2d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(1);
                templateOfItem.setLayer(2);
                break;
            case 5:
                templateOfItem.setName("Джинсы");
                templateOfItem.setTermid(2d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(1);
                templateOfItem.setLayer(2);
                break;
            case 6:
                templateOfItem.setName("Осеняя куртка");
                templateOfItem.setTermid(6d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(0);
                templateOfItem.setLayer(3);
                break;
            case 7:
                templateOfItem.setName("Пальто");
                templateOfItem.setTermid(5d);
                templateOfItem.setStyle(Styles.CASUAL.toString());
                templateOfItem.setTop(0);
                templateOfItem.setLayer(3);
                break;
            case 8:
                templateOfItem.setName("Kalsony");
                templateOfItem.setTermid(2d);
                templateOfItem.setStyle(Styles.HOME.toString());
                templateOfItem.setTop(1);
                templateOfItem.setLayer(1);
                break;
            case 9:
                templateOfItem.setName("Krosovki");
                templateOfItem.setTermid(2d);
                templateOfItem.setStyle(Styles.SPORT.toString());
                templateOfItem.setTop(1);
                templateOfItem.setLayer(3);
                break;
            // Шаблоны можно добавить тут + добавить имя в spinnerTemplate - --- - - - - --
        }
        return templateOfItem;

    }

}
