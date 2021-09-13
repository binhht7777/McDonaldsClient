package com.example.mcdonalds.Model;

public class Table {
    String tableid, tablename;

    public Table(String tableId, String tableName) {
        tableid = tableId;
        tablename = tableName;
    }

    public String getTableId() {
        return tableid;
    }

    public void setTableId(String tableId) {
        tableid = tableId;
    }

    public String getTableName() {
        return tablename;
    }

    public void setTableName(String tableName) {
        tablename = tableName;
    }
}
