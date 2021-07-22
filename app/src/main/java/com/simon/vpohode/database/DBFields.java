package com.simon.vpohode.database;

public enum  DBFields {
    ID("_id", "INTEGER"),
    NAME("name", "TEXT"),
    STYLE( "style","INTEGER"),
    ISTOP( "istop","INTEGER"),
    TERMID("termindex","DOUBLE"),
    LAYER("layer", "INTEGER"),
    COLOR("color", "INTEGER"),
    FOTO("foto", "TEXT"),
    USED("used", "INTEGER"),
    CREATED("created", "TEXT"),
    INWASH("inwash", "BOOLEAN"),
    BRAND("brand", "TEXT");
    private String dbField;
    private String dbType;

    DBFields(String dbField, String dbType) {
        this.dbField = dbField;
        this.dbType = dbType;
    }
    public String toFieldName() {
        return dbField;
    }
    public String toType(){
        return dbType;
    }

}
