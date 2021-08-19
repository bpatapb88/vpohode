package com.simon.vpohode.database;

public enum DBLooksFields {
    ID("_id", "INTEGER"),
    NAME("name", "TEXT"),
    TERMMAX("max","DOUBLE"),
    TERMMIN("min","DOUBLE"),
    ITEMS("items", "TEXT");

    private String dbField;
    private String dbType;

    DBLooksFields(String dbField, String dbType) {
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
