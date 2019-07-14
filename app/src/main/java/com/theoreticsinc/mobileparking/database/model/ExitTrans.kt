package com.theoreticsinc.mobileparking.database.model

public class ExitTrans() {

    private var pkid: Int = 0
    private var receiptNumber: String? = null
    private var cashierName: String? = null
    private var entranceId: String? = null
    private var exitId: String? = null
    private var cardNumber: String? = null
    private var plateNumber: String? = null
    private var parkerType: String? = null
    private var amount: Float? = null
    private var vatamount: Float? = null
    private var discountAmount: Float? = null
    private var dateTimeIn: String? = null
    private var dateTimeOut: String? = null
    private var minutesParked: Int? = null
    private var hoursParked: Int? = null
    private var settlementRef: String? = null

    companion object {
        val TABLE_NAME = "exit_trans"
        val PKID = "pkid"
        var RECEIPTNUMBER: String = "ReceiptNumber"
        val CASHIERNAME = "CashierName"
        var ENTRANCEID = "EntranceID"
        var EXITID = "ExitID"
        var CARDNUMBER = "CardNumber"
        var PLATENUMBER = "PlateNumber"
        var PARKERTYPE = "ParkerType"
        var AMOUNT = "Amount"
        var VATAMOUNT = "VatAmount"
        var DISCOUNTAMOUNT = "DiscountAmount"
        var DATETIMEIN = "DateTimeIN"
        var DATETIMEOUT = "DateTimeOUT"
        var HOURSPARKED = "HoursParked"
        var MINUTESPARKED = "MinutesParked"
        var SETTLEMENTREF = "SettlementRef"
        // Create table SQL query
        var CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "("
                        + PKID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + RECEIPTNUMBER + " VARCHAR(128),"
                        + CASHIERNAME + " VARCHAR(15),"
                        + ENTRANCEID + " VARCHAR(5),"
                        + EXITID + " VARCHAR(4),"
                        + CARDNUMBER + " VARCHAR(12),"
                        + PLATENUMBER + " VARCHAR(8),"
                        + PARKERTYPE + " VARCHAR(2),"
                        + AMOUNT + " FLOAT,"
                        + VATAMOUNT + " FLOAT,"
                        + DISCOUNTAMOUNT + " FLOAT,"
                        + DATETIMEIN + " DATETIME,"
                        + DATETIMEOUT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + HOURSPARKED + " INT(5),"
                        + MINUTESPARKED + " INT(5),"
                        + SETTLEMENTREF + " VARCHAR(128)"
                        + ")")


    }

    //-----------------Getters---------------------------------------------------
    fun getId(): Int {
        return pkid
    }

    fun getReceiptNumber(): String? {
        return receiptNumber
    }

    fun getCashierName(): String? {
        return cashierName
    }

    fun getEntranceId(): String? {
        return entranceId
    }

    fun getExitId(): String? {
        return exitId
    }

    fun getCardNumber(): String? {
        return cardNumber
    }

    fun getPlateNumber(): String? {
        return plateNumber
    }

    fun getParkerType(): String? {
        return parkerType
    }

    fun getAmount(): Float? {
        return amount
    }

    fun getDateTimeIn(): String? {
        return dateTimeIn
    }

    fun getDateTimeOut(): String? {
        return dateTimeOut
    }

    fun getMinutesParked(): Int? {
        return minutesParked
    }

    fun getHoursParked(): Int? {
        return hoursParked
    }

    fun getSettlementRef(): String? {
        return settlementRef
    }


    //-----------------Setters---------------------------------------------------

    fun setReceiptNumber(receiptNumber: String) {
        this.receiptNumber = receiptNumber
    }

    fun setId(pkid: Int) {
        this.pkid = pkid
    }

    fun setCashierName(cashierName: String) {
        this.cashierName = cashierName
    }


    fun setEntranceId(entranceId: String) {
        this.entranceId = entranceId
    }

    fun setExitId(exitId: String) {
        this.exitId = exitId
    }

    fun setCardNumber(cardNumber: String) {
        this.cardNumber = cardNumber
    }

    fun setPlateNumber(plateNumber: String) {
        this.plateNumber = plateNumber
    }

    fun setParkerType(parkerType: String) {
        this.parkerType = parkerType
    }

    fun setAmount(amount: Float) {
        this.amount = amount
    }

    fun setDateTimeIn(dateTimeIn: String) {
        this.dateTimeIn = dateTimeIn
    }

    fun setDateTimeOut(dateTimeOut: String) {
        this.dateTimeOut = dateTimeOut
    }

    fun setMinutesParked(minutesParked: Int) {
        this.minutesParked = minutesParked
    }

    fun setHoursParked(hoursParked: Int) {
        this.hoursParked = hoursParked
    }

    fun setSettlementRef(settlementRef: String) {
        this.settlementRef = settlementRef
    }


    //----------------------------------------------------------------------------
    public fun ExitTrans() {}

    public fun ExitTrans(receiptNumber: String, cashierName: String, entranceId: String, exitId: String, cardNumber: String, plateNumber: String, parkerType: String,
                         amount: Float, dateTimeIn: String, dateTimeOut: String, hoursParked: Int, minutesParked: Int, settlementRef: String) {
        //this.pkid = id
        this.receiptNumber = receiptNumber
        this.cashierName = cashierName
        this.entranceId = entranceId
        this.exitId = exitId
        this.cardNumber = cardNumber
        this.plateNumber = plateNumber
        this.parkerType = parkerType
        this.amount = amount
        this.dateTimeIn = dateTimeIn
        this.dateTimeOut = dateTimeOut
        this.hoursParked = hoursParked
        this.minutesParked = minutesParked
        this.settlementRef = settlementRef

    }

}