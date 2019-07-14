package com.theoreticsinc.mobileparking.database.model

public class CrdPlt() {

    var ID: String? = null
    var CardCode: String? = null
    var Vehicle: String? = null
    var Plate: String? = null
    var Timein: String? = null
    var Operator: String? = null
    var PC: String? = null
    var PIC: String? = null
    var PIC2: String? = null
    var Lane: String? = null

    companion object {
        val TABLE_NAME = "timeindb"
        val ID = "ID"
        val CARDCODE = "CardCode"
        val VEHICLE = "Vehicle"
        val PLATE = "Plate"
        val TIMEIN = "Timein"
        val OPERATOR = "Operator"
        val PC = "PC"
        val PIC = "PIC"
        val PIC2 = "PIC2"
        val LANE = "Lane"

        // Create table SQL query
        var CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CARDCODE + " VARCHAR(10),"
                        + VEHICLE + " VARCHAR(10),"
                        + PLATE + " VARCHAR(8),"
                        + TIMEIN + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + OPERATOR + " VARCHAR(10),"
                        + PC + " VARCHAR(12),"
                        + PIC + " VARCHAR(12),"
                        + PIC2 + " VARCHAR(12),"
                        + LANE + " VARCHAR(12)"
                        + ")")


    }

    //----------------------------------------------------------------------------
    public fun CrdPlt() {}

    //-----------------Setters---------------------------------------------------

}