package com.simon.vpohode;

public enum Templates {
    NONE("Выбери шаблон"),
    TSHIRT("Футболка"),
    SHIRT("Рубашка"),
    SWEATER("Кофта"),
    PANTS("Штаны"),
    PROMO1("Джинсы Moms Straight Cut"),
    JACKET("Осенняя куртка"),
    COAT("Пальто"),
    KALSONY("Кальсоны"),
    SNIKERS("Кроссовки");

    private String templates;

    Templates (String nTemplates){
        templates = nTemplates;
    }

    @Override
    public String toString() {
        return templates;
    }

    public static Item fillTemplate2 (String input){
        switch (input){
            case "Футболка":
                return new Item(0,input,Styles.CASUAL.toString(),0,1d,1,0,"",0,"");
            case "Рубашка":
                return new Item(0,input,Styles.BUSINESS.toString(),0,2d,1,0,"",0,"");
            case "Кофта":
                return new Item(0,input,Styles.CASUAL.toString(),0,2d,2,0,"",0,"");
            case "Штаны":
                return new Item(0,input,Styles.CASUAL.toString(),1,3d,2,0,"",0,"");
            case "Осенняя куртка":
                return new Item(0,input,Styles.CASUAL.toString(),0,1d,3,0,"",0,"");
            case "Пальто":
                return new Item(0,input,Styles.ELEGANT.toString(),0,2d,3,0,"",0,"");
            case "Кальсоны":
                return new Item(0,input,Styles.HOME.toString(),1,1d,1,0,"",0,"");
            case "Кроссовки":
                return new Item(0,input,Styles.SPORT.toString(),1,2d,3,0,"",0,"");
            case "Джинсы Moms Straight Cut":
                return new Item(0,input,Styles.CASUAL.toString(),1,2d,2,-12961222,"https://thumb.tildacdn.com/tild6137-3566-4031-a636-663934313261/-/format/webp/2330_grey_5.jpg",0,"");
            // Шаблоны можно добавить тут + добавить имя в spinnerTemplate - --- - - - - --
        }
        return null;
    }

}
