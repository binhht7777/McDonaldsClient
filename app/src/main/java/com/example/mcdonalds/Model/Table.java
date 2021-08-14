package com.example.mcdonalds.Model;

public class Table {
    String TableId, TableName;

    public Table(String tableId, String tableName) {
        TableId = tableId;
        TableName = tableName;
    }

    public String getTableId() {
        return TableId;
    }

    public void setTableId(String tableId) {
        TableId = tableId;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }
}
