package com.theoreticsinc.mobileparking.database.model

import java.util.*

public class LoginUser {
    val TABLE_NAME = "loginuser"
    val PKID = "pkid"
    val CASHIERID = "CashierID"
    val CASHIERNAME = "CashierName"
    val LOGID = "LogID"
    val LOGINTIME = "logintime"
    val LOGINDATE = "logindate"

    private var id: Int = 0
    private var cashierID: String? = null
    private var cashierName: String? = null
    private var logID: Date? = null
    private var logintime: Date? = null
    private var logindate: Date? = null
    private var loginStamp: Date? = null

    // Create table SQL query
    val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
                    + PKID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CASHIERID + " VARCHAR(128),"
                    + CASHIERNAME + " VARCHAR(15),"
                    + LOGID + " DATETIME,"
                    + LOGINTIME + " DATETIME,"
                    + LOGINDATE + " DATETIME,"
                    + LOGID + " DATETIME,"
                    + ")")

    fun getCashierID(): String? {
        return cashierID

    }

    fun getCashierName(): String? {
        return cashierName
    }

    fun getLogID(): Date? {
        return logID
    }

    fun setLogID(logID: Date) {
        this.logID = logID
    }

    fun getLogintime(): Date? {
        return logintime
    }

    fun setLogintime(logintime: Date) {
        this.logintime = logintime
    }

    fun getLogindate(): Date? {
        return logindate
    }

    fun setLogindate(logindate: Date) {
        this.logindate = logindate
    }

    fun getLoginStamp(): Date? {
        return loginStamp
    }

    fun setLoginStamp(loginStamp: Date) {
        this.loginStamp = loginStamp
    }
}