package com.simon.vpohode;

public enum  DBFields {
    ID("_id", "INTEGER"),
    NAME("name", "TEXT"),
    STYLE( "style","TEXT"),
    ISTOP( "istop","INTEGER"),
    TERMID("termindex","DOUBLE");

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
