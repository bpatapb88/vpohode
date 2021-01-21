package com.simon.vpohode;

public class Item {
    private int id;
    private String name;
    private String style;
    private Double termid;
    private String foto;
    private int top;
    private int color;
    private int layer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Double getTermid() {
        return termid;
    }

    public void setTermid(Double termid) {
        this.termid = termid;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Item(int id, String name, String style, int top, Double termid, int layer, int color, String foto) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.termid = termid;
        this.foto = foto;
        this.top = top;
        this.color = color;
        this.layer = layer;
    }

    public Item() {
    }
}
