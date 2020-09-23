package com.simon.vpohode;

public class Item {
    private int id,termindex;
    private String name,style;
    private boolean isTop;

    public int getId() {
        return id;
    }

    public int getTermindex() {
        return termindex;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }



    public boolean getTop() {
        return isTop;
    }

    public void setTermindex(int termindex) {
        this.termindex = termindex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }


    public void setTop(boolean top) {
        this.isTop = top;
    }

    public Item(int id) {
        this.id = id;
    }
}
