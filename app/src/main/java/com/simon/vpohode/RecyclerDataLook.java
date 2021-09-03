package com.simon.vpohode;

import android.database.Cursor;

public class RecyclerDataLook {
    private int id;
    private String name;
    private double max;
    private double min;
    private String items;


    public RecyclerDataLook(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex("_id"));
        this.name = cursor.getString(cursor.getColumnIndex("name"));
        this.max = cursor.getDouble(cursor.getColumnIndex("max"));
        this.min = cursor.getDouble(cursor.getColumnIndex("min"));;
        this.items = cursor.getString(cursor.getColumnIndex("items"));
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }
}
