package com.simon.vpohode;

public class Item {
    private int id;
    private String name;
    private int style;
    private Double termid;
    private String foto;
    private int top;
    private int color;
    private int layer;
    private int used;
    private String created;
    private boolean inwash;
    private String brand;

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

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
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

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isInWash() {
        return inwash;
    }

    public void setInWash(boolean inwash) {
        this.inwash = inwash;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Item(int id, String name, int style, int top, Double termid, int layer, int color, String foto, int used, String created, boolean inwash, String brand) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.termid = termid;
        this.foto = foto;
        this.top = top;
        this.color = color;
        this.layer = layer;
        this.used = used;
        this.created = created;
        this.inwash = inwash;
        this.brand = brand;
    }

    public Item() {
    }
}
