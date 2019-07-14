package com.theoreticsinc.mobileparking.database.model

public class Netmanager() {

    var tableName: String? = null
    var lastTime: String? = null

    companion object {
        val TABLE_NAME = "netmanager"
        val TABLENAME = "tableName"
        val LASTTIME: String = ""


        // Create table SQL query
        var CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "("
                        + TABLENAME + " VARCHAR(20),"
                        + LASTTIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")")


    }

    //----------------------------------------------------------------------------
    public fun Netmanager() {}

    public fun Netmanager(tableName: String, lastTime: String) {

        this.tableName = tableName
        this.lastTime = lastTime

    }

}