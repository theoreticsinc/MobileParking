package com.theoreticsinc.mobileparking.database.model

public class Colltrain() {

    var logINID: String? = null
    var sentinelID: String? = null
    var userCode: String? = null
    var userName: String? = null
    var loginStamp: String? = null
    var logoutStamp: String? = null

    var carServed: Int? = null
    var totalAmount: Double? = null
    var regularCount: Int? = null
    var regularAmount: Double? = null
    var gracePeriodCount: Int? = null
    var gracePeriodAmount: Double? = null
    var vipCount: Int? = null
    var vipAmount: Double? = null
    var motorcycleCount: Int? = null
    var motorcycleAmount: Double? = null
    var seniorCount: Int? = null
    var seniorAmount: Double? = null
    var discountCount: Int? = null
    var discountAmount: Double? = null

    companion object {
        val TABLE_NAME = "colltrain"
        val LOGINID = "logINID"
        val SENTINELID: String = "SentinelID"
        val USERCODE = "userCode"
        val USERNAME = "userName"
        val LOGINSTAMP = "loginStamp"
        val LOGOUTSTAMP = "logoutStamp"
        var CARSERVED = "carServed"
        var TOTALAMOUNT = "totalAmount"
        var REGULARCOUNT = "regularCount"
        var REGULARAMOUNT = "regularAmount"
        var GRACEPERIODCOUNT = "gracePeriodCount"
        var GRACEPERIODAMOUNT = "gracePeriodAmount"
        var VIPCOUNT = "vipCount"
        var VIPAMOUNT = "vipAmount"
        var MOTORCYCLECOUNT = "motorcycleCount"
        var MOTORCYCLEAMOUNT = "motorcycleAmount"
        var SENIORCOUNT = "seniorCount"
        var SENIORAMOUNT = "seniorAmount"
        var DISCOUNTCOUNT = "discountCount"
        var DISCOUNTAMOUNT = "discountAmount"

        // Create table SQL query
        var CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "("
                        + LOGINID + " VARCHAR(50),"
                        + SENTINELID + " VARCHAR(4),"
                        + USERCODE + " VARCHAR(10),"
                        + USERNAME + " VARCHAR(25),"
                        + LOGINSTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + LOGOUTSTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + CARSERVED + " INT(11),"
                        + TOTALAMOUNT + " FLOAT,"
                        + REGULARCOUNT + " INT(5),"
                        + REGULARAMOUNT + " FLOAT,"
                        + GRACEPERIODCOUNT + " INT(5),"
                        + GRACEPERIODAMOUNT + " FLOAT,"
                        + VIPCOUNT + " INT(5),"
                        + VIPAMOUNT + " FLOAT,"
                        + MOTORCYCLECOUNT + " INT(5),"
                        + MOTORCYCLEAMOUNT + " FLOAT,"
                        + SENIORCOUNT + " INT(5),"
                        + SENIORAMOUNT + " FLOAT,"
                        + DISCOUNTCOUNT + " INT(5),"
                        + DISCOUNTAMOUNT + " FLOAT"
                        + ")")


    }

    //----------------------------------------------------------------------------
    public fun Colltrain() {}

    public fun Colltrain(logINID: String, sentinelID: String, userCode: String, userName: String, loginStamp: String, logoutStamp: String, carServed: Int, totalAmount: Double, regularCount: Int,
                         regularAmount: Double, gracePeriodCount: Int, gracePeriodAmount: Double, vipCount: Int, vipAmount: Double, motorcycleCount: Int, motorcycleAmount: Double, seniorCount: Int, seniorAmount: Double, discountCount: Int, discountAmount: Double) {

        this.logINID = logINID

        this.sentinelID = sentinelID
        this.userCode = userCode
        this.userName = userName

        this.loginStamp = loginStamp
        this.logoutStamp = logoutStamp

        this.carServed = carServed
        this.totalAmount = totalAmount

        this.regularCount = regularCount
        this.regularAmount = regularAmount

        this.gracePeriodCount = gracePeriodCount
        this.gracePeriodAmount = gracePeriodAmount

        this.vipCount = vipCount
        this.vipAmount = vipAmount

        this.motorcycleCount = motorcycleCount
        this.motorcycleAmount = motorcycleAmount

        this.seniorCount = seniorCount
        this.seniorAmount = seniorAmount

        this.discountCount = discountCount
        this.discountAmount = discountAmount

    }

}