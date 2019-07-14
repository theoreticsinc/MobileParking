package com.theoreticsinc.mobileparking.database.model;

/**
 * Created by ravi on 20/02/18.
 */

public class ProductItem {
    public static final String TABLE_NAME = "productitem";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String code;
    private String name;
    private String desc;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CODE + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_DESC + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public ProductItem() {
    }

    public ProductItem(int id, String code, String name, String desc, String timestamp) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
