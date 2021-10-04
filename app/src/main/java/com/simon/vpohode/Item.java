package com.simon.vpohode;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.simon.vpohode.database.DBFields;
import com.simon.vpohode.database.DatabaseHelper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Data
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

    public Item cursorToItem(Cursor cursors){
        return new Item(cursors.getInt(cursors.getColumnIndex("_id")),
                cursors.getString(cursors.getColumnIndex("name")),
                cursors.getInt(cursors.getColumnIndex("style")),
                cursors.getInt(cursors.getColumnIndex("istop")),
                cursors.getDouble(cursors.getColumnIndex("termindex")),
                cursors.getInt(cursors.getColumnIndex("layer")),
                cursors.getInt(cursors.getColumnIndex("color")),
                cursors.getString(cursors.getColumnIndex("foto")),
                cursors.getInt(cursors.getColumnIndex("used")),
                cursors.getString(cursors.getColumnIndex("created")),
                cursors.getInt(cursors.getColumnIndex("inwash")) > 0,
                cursors.getString(cursors.getColumnIndex("brand")));
    }

    public Item() {
    }

    public Item getItemById(int id, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DBFields.ID.toFieldName() + " = " + id, null);
        System.out.println("getItemById \nid" + id +
                "\nCursor size " + cursor.getCount());
        cursor.moveToFirst();
        return cursorToItem(cursor);
    }

    @NonNull
    @Override
    public String toString() {
        return "id - " + id + " name - " + name + " style - " + style + " termind - " + termid +
                " fotoUri - " + foto + " top - " + top + " color - " + color + " layer - " + layer +
                " used - " + used + " created - " + created + " inwash - " + inwash + " brand - " + brand;
    }
}
