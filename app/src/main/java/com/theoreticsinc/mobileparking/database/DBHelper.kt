package com.theoreticsinc.mobileparking.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import com.theoreticsinc.mobileparking.database.model.*
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by ravi on 15/03/18.
 */

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //init {
    //    val db = this.readableDatabase
    //    db.execSQL(ExitTrans.CREATE_TABLE)
    //}

    /// get time and date
    private val df = SimpleDateFormat("h:mm a")
    private val c = Calendar.getInstance()
    private val dt = SimpleDateFormat("yyyy-MM-dd")
    private var time: String? = null
    private var date: String? = null
    // use in hold response from server
    private var `is`: InputStream? = null
    private var result: String? = null
    private var line: String? = null
    private var code: Int = 0
    // Select All Query
    // looping through all rows and adding to list
    // close db connection
    // return notes list
    val allNotes: List<Note>
        get() {
            val notes = ArrayList<Note>()
            val selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                    Note.COLUMN_TIMESTAMP + " DESC"

            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val note = Note()
                    note.id = cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID))
                    note.note = cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE))
                    note.timestamp = cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
                    notes.add(note)
                } while (cursor.moveToNext())
            }
            db.close()
            return notes
        }

    // return count
    val notesCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + Note.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)

            val count = cursor.count
            cursor.close()
            return count
        }

    // return lastDate
    val netManagerLastDate: String
        get() {
            val lastDateQuery = "SELECT  * FROM " + Netmanager.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(lastDateQuery, null)
            var timedate: String = ""
            if (cursor.moveToFirst()) {
                do {
                    timedate = cursor.getString(cursor.getColumnIndex(Netmanager.LASTTIME))
                } while (cursor.moveToNext())
            }
            db.close()
            return timedate
        }


    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ExitTrans.CREATE_TABLE)
        db.execSQL(Colltrain.CREATE_TABLE)
        db.execSQL(Netmanager.CREATE_TABLE)
        db.execSQL(CrdPlt.CREATE_TABLE)
        this.initNetManager(db)
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ExitTrans.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + Colltrain.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + Netmanager.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + CrdPlt.TABLE_NAME)
        // Create tables again
        onCreate(db)
    }

    fun insertExitTrans(receiptNumber: String, cashierName: String, entranceID: String, exitID: String, cardNumber: String,
                        plateNumber: String, parkerType: String, amount: String, vatamount: String, discountamount: String, dateTimeIn: String, dateTimeOut: String, hoursParked: String, minsParked: String, settlementRef: String): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase

        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(ExitTrans.RECEIPTNUMBER, receiptNumber)
        values.put(ExitTrans.CASHIERNAME, cashierName)
        values.put(ExitTrans.ENTRANCEID, entranceID)
        values.put(ExitTrans.EXITID, exitID)
        values.put(ExitTrans.CARDNUMBER, cardNumber)
        values.put(ExitTrans.PLATENUMBER, plateNumber)
        values.put(ExitTrans.PARKERTYPE, parkerType)
        values.put(ExitTrans.AMOUNT, amount)
        values.put(ExitTrans.VATAMOUNT, vatamount)
        values.put(ExitTrans.DISCOUNTAMOUNT, discountamount)
        values.put(ExitTrans.DATETIMEIN, dateTimeIn)
        values.put(ExitTrans.DATETIMEOUT, dateTimeOut)
        values.put(ExitTrans.HOURSPARKED, hoursParked)
        values.put(ExitTrans.MINUTESPARKED, minsParked)
        values.put(ExitTrans.SETTLEMENTREF, settlementRef)

        //values.put(ExitTrans.
        // insert row
        val id = db.insert(ExitTrans.TABLE_NAME, null, values)

        // close db connection
        db.close()

        // return newly inserted row id

        return id
    }

    fun insertLogin2colltrain(UID: String, logID: String, loginStamp: String): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase

        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Colltrain.SENTINELID, "EX04")
        values.put(Colltrain.USERCODE, UID)
        values.put(Colltrain.USERNAME, UID)
        values.put(Colltrain.LOGINID, logID)
        values.put(Colltrain.LOGINSTAMP, loginStamp)

        // insert row
        val id = db.insert(Colltrain.TABLE_NAME, null, values)

        // close db connection
        db.close()

        // return newly inserted row id
        return id
    }

    fun getNetManagerLastDate(tableName: String): String {
        val lastDateQuery = "SELECT  * FROM " + Netmanager.TABLE_NAME + " WHERE " + Netmanager.TABLENAME + " = '" + tableName + "'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(lastDateQuery, null)
        var timedate: String = ""
        if (cursor.moveToFirst()) {
            do {
                timedate = cursor.getString(cursor.getColumnIndex(Netmanager.LASTTIME))
            } while (cursor.moveToNext())
        }
        db.close()
        return timedate
    }

    fun saveCrdPlt(cardCode: String): Long {
        val db = this.writableDatabase
        var id = 0L
        try {
            val values = ContentValues()

            // `id` and `timestamp` will be inserted automatically.
            // no need to add them

            values.put(CrdPlt.CARDCODE, cardCode)
            values.put(CrdPlt.VEHICLE, "CAR")
            values.put(CrdPlt.PLATE, "Empty")
            values.put(CrdPlt.TIMEIN, "2019-01-01 01:00:00")
            values.put(CrdPlt.OPERATOR, "CAR")
            values.put(CrdPlt.PC, "Entry Zone 2")
            values.put(CrdPlt.PIC, "CAR")
            values.put(CrdPlt.PIC2, "CAR")
            values.put(CrdPlt.LANE, "LANE")

            // insert row
            id = db.insert(CrdPlt.TABLE_NAME, null, values)
        } catch (e: Exception) {

        }
        // close db connection
        db.close()

        // return newly inserted row id
        return id
    }

    fun updateNetManager(tableName: String, cardNumber: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Netmanager.LASTTIME, tableName)
        // insert row
        val id = db.update(Netmanager.TABLE_NAME, values, Netmanager.LASTTIME + " = ?", arrayOf(tableName))

        // close db connection
        db.close()

        // return newly inserted row id
        return id
    }

    fun deleteNetManager(tableName: String, cardCode: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(CrdPlt.TABLE_NAME, tableName)
        values.put(CrdPlt.CARDCODE, cardCode)
        // insert row
        db.delete(CrdPlt.TABLE_NAME, CrdPlt.CARDCODE + " = ?", arrayOf(cardCode))
        // close db connection
        db.close()

        // return newly inserted row id
        return
    }


    fun initNetManager(db: SQLiteDatabase): Long {
        val values = ContentValues()

        values.put(Netmanager.LASTTIME, "2019-01-01 01:00:00")
        // insert row
        var id: Long = 0L
        values.put(Netmanager.TABLENAME, "crdplt")
        id = db.insert(Netmanager.TABLE_NAME, null, values)
        values.put(Netmanager.TABLENAME, "exit_trans")
        id = db.insert(Netmanager.TABLE_NAME, null, values)
        values.put(Netmanager.TABLENAME, "colltrain")
        id = db.insert(Netmanager.TABLE_NAME, null, values)
        values.put(Netmanager.TABLENAME, "zread")
        id = db.insert(Netmanager.TABLE_NAME, null, values)

        // close db connection
        //db.close()

        // return newly inserted row id
        return id
    }

    inner class sendUserDetailTOOnlineServer : AsyncTask<ArrayList<String>, Void, String>() {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //private val dialog = ProgressDialog(this@DBHelper)

        override fun onPreExecute() {
            //    this.dialog.setMessage("Please wait")
            //    this.dialog.show()
        }

        override fun doInBackground(vararg alldata: ArrayList<String>): String? {

            val passed = alldata[0] //get passed arraylist

            val usnm = passed[0]
            val pass = passed[1]
            val phone = passed[2]
            val hintq = passed[3]
            val hingas = passed[4]

            // current time
            time = df.format(Calendar.getInstance().time)
            date = dt.format(c.getTime())
            try {

                // Log.e(" setup Activity ", "  user detail to server " + " "+ SendName+" "+Sendmobile+" "+Checkgender);

                val nameValuePairs = ArrayList<NameValuePair>()

                nameValuePairs.add(BasicNameValuePair("username", usnm))
                nameValuePairs.add(BasicNameValuePair("password", pass))
                nameValuePairs.add(BasicNameValuePair("phonenumber", phone))
                nameValuePairs.add(BasicNameValuePair("hintq", hintq))
                nameValuePairs.add(BasicNameValuePair("hintanswer", hingas))
                nameValuePairs.add(BasicNameValuePair("date", date))
                nameValuePairs.add(BasicNameValuePair("time", time))

                val httpclient = DefaultHttpClient()
                val httppost = HttpPost("http://blueappsoftware.in/android/tutorial/insertUserDetail.php")
                httppost.entity = UrlEncodedFormEntity(nameValuePairs, "UTF-8") // UTF-8  support multi language
                val response = httpclient.execute(httppost)
                val entity = response.entity
                `is` = entity.content
                //     Log.e("pass 1", "connection success ");
            } catch (e: Exception) {
                //  Log.e("Fail 1", e.toString());
                //  Log.d("setup Activity ","  fail 1  "+e.toString());
            }


            return result
        }

        override fun onPostExecute(result: String) {
            //  Log.e(" setup acc ","  signup result  " + result);
            //if (dialog.isShowing) {
            //    dialog.dismiss()
            //}

            try {
                val json_data = JSONObject(result)
                code = json_data.getInt("Tripcode")
                //   Log.d("pass 3", "connection success " + result);
                if (code == 1) {
                    try {
                        val Res_username = json_data.getString("Res_username")
                        val Res_password = json_data.getString("Res_password")

                        //val nextScreen = Intent(getApplicationContext(), HomeScreen::class.java)
                        //nextScreen.putExtra("username", Res_username)
                        //nextScreen.putExtra("password", Res_password)
                        //Sending data to another Activity
                        //startActivity(nextScreen)

                    } catch (e: Exception) {

                    }

                }


            } catch (e: Exception) {
                //LoginError(" Network Problem , Please try again")
                //    Log.e("Fail 3 main result ", e.toString());
                // Log.d(" setup Activity "," fail 3 error - "+ result );
            }

        }

    }


    fun updateColltrain(colltrain: Colltrain): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Colltrain.CARSERVED, colltrain.carServed)
        values.put(Colltrain.TOTALAMOUNT, colltrain.totalAmount)
        values.put(Colltrain.REGULARCOUNT, colltrain.regularCount)
        values.put(Colltrain.REGULARAMOUNT, colltrain.regularAmount)
        values.put(Colltrain.GRACEPERIODCOUNT, colltrain.gracePeriodCount)
        values.put(Colltrain.GRACEPERIODAMOUNT, colltrain.gracePeriodAmount)
        values.put(Colltrain.VIPCOUNT, colltrain.vipCount)
        values.put(Colltrain.VIPAMOUNT, colltrain.vipAmount)
        values.put(Colltrain.MOTORCYCLECOUNT, colltrain.motorcycleCount)
        values.put(Colltrain.MOTORCYCLEAMOUNT, colltrain.motorcycleAmount)
        values.put(Colltrain.SENIORCOUNT, colltrain.seniorCount)
        values.put(Colltrain.SENIORAMOUNT, colltrain.seniorAmount)
        values.put(Colltrain.DISCOUNTCOUNT, colltrain.discountCount)
        values.put(Colltrain.DISCOUNTAMOUNT, colltrain.discountAmount)
        // updating row
        return db.update(Colltrain.TABLE_NAME, values, Colltrain.LOGINID + " = ?",
                arrayOf(colltrain.logINID.toString()))
    }

    fun getAllCrdPlt(): ArrayList<CrdPlt> {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM " + CrdPlt.TABLE_NAME

        val cursor = db.rawQuery(selectQuery, null)


        cursor?.moveToFirst()
        val allCrdPlt = ArrayList<CrdPlt>()

        // prepare note object
        if (cursor.moveToFirst()) {
            do {
                val crdPlt = CrdPlt()
                crdPlt.CardCode = cursor.getString(cursor.getColumnIndex(CrdPlt.CARDCODE))
                crdPlt.Vehicle = cursor.getString(cursor.getColumnIndex(CrdPlt.VEHICLE))
                crdPlt.Plate = cursor.getString(cursor.getColumnIndex(CrdPlt.PLATE))
                crdPlt.Timein = cursor.getString(cursor.getColumnIndex(CrdPlt.TIMEIN))
                crdPlt.Operator = cursor.getString(cursor.getColumnIndex(CrdPlt.OPERATOR))
                crdPlt.PC = cursor.getString(cursor.getColumnIndex(CrdPlt.PC))
                crdPlt.PIC = cursor.getString(cursor.getColumnIndex(CrdPlt.PIC))
                crdPlt.PIC2 = cursor.getString(cursor.getColumnIndex(CrdPlt.PIC2))
                crdPlt.Lane = cursor.getString(cursor.getColumnIndex(CrdPlt.LANE))
                allCrdPlt.add(crdPlt)
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return allCrdPlt
    }

    fun getExitTransList(dtOut: String): ArrayList<ExitTrans> {
        val all = ArrayList<ExitTrans>()
        val db = this.readableDatabase

        val cursor = db.query(ExitTrans.TABLE_NAME,
                arrayOf(ExitTrans.RECEIPTNUMBER, ExitTrans.CASHIERNAME, ExitTrans.ENTRANCEID, ExitTrans.EXITID, ExitTrans.CARDNUMBER, ExitTrans.PLATENUMBER, ExitTrans.PARKERTYPE,
                        ExitTrans.AMOUNT, ExitTrans.DATETIMEIN, ExitTrans.DATETIMEOUT, ExitTrans.HOURSPARKED, ExitTrans.MINUTESPARKED, ExitTrans.SETTLEMENTREF),
                ExitTrans.DATETIMEOUT + "<=?",
                arrayOf(dtOut), null, null, null, null)

        cursor?.moveToFirst()
        val exitTrans = ExitTrans()
        // prepare note object
        if (cursor.moveToFirst()) {
            do {
                val exitTrans = ExitTrans()
                exitTrans.setReceiptNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.RECEIPTNUMBER)))
                exitTrans.setCashierName(cursor.getString(cursor.getColumnIndex(ExitTrans.CASHIERNAME)))
                exitTrans.setEntranceId(cursor.getString(cursor.getColumnIndex(ExitTrans.ENTRANCEID)))
                exitTrans.setExitId(cursor.getString(cursor.getColumnIndex(ExitTrans.EXITID)))
                exitTrans.setCardNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.CARDNUMBER)))
                exitTrans.setPlateNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.PLATENUMBER)))
                exitTrans.setParkerType(cursor.getString(cursor.getColumnIndex(ExitTrans.PARKERTYPE)))
                exitTrans.setAmount(cursor.getFloat(cursor.getColumnIndex(ExitTrans.AMOUNT)))
                exitTrans.setDateTimeIn(cursor.getString(cursor.getColumnIndex(ExitTrans.DATETIMEIN)))
                exitTrans.setDateTimeOut(cursor.getString(cursor.getColumnIndex(ExitTrans.DATETIMEOUT)))
                exitTrans.setHoursParked(cursor.getInt(cursor.getColumnIndex(ExitTrans.HOURSPARKED)))
                exitTrans.setMinutesParked(cursor.getInt(cursor.getColumnIndex(ExitTrans.MINUTESPARKED)))
                exitTrans.setSettlementRef(cursor.getString(cursor.getColumnIndex(ExitTrans.SETTLEMENTREF)))

                all.add(exitTrans)
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return all
    }


    fun getExitTrans(dtOut: String): ExitTrans {
        // get readable database as we are not inserting anything
        val db = this.readableDatabase

        val cursor = db.query(ExitTrans.TABLE_NAME,
                arrayOf(ExitTrans.RECEIPTNUMBER, ExitTrans.CASHIERNAME, ExitTrans.ENTRANCEID, ExitTrans.EXITID, ExitTrans.CARDNUMBER, ExitTrans.PLATENUMBER, ExitTrans.PARKERTYPE,
                        ExitTrans.AMOUNT, ExitTrans.DATETIMEIN, ExitTrans.DATETIMEOUT, ExitTrans.HOURSPARKED, ExitTrans.MINUTESPARKED, ExitTrans.SETTLEMENTREF),
                ExitTrans.DATETIMEOUT + "<=?",
                arrayOf(dtOut), null, null, null, null)

        cursor?.moveToFirst()
        val exitTrans = ExitTrans()
        // prepare note object
        if (cursor.moveToFirst()) {
            do {
                val exitTrans = ExitTrans()
                exitTrans.setReceiptNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.RECEIPTNUMBER)))
                exitTrans.setCashierName(cursor.getString(cursor.getColumnIndex(ExitTrans.CASHIERNAME)))
                exitTrans.setEntranceId(cursor.getString(cursor.getColumnIndex(ExitTrans.ENTRANCEID)))
                exitTrans.setExitId(cursor.getString(cursor.getColumnIndex(ExitTrans.EXITID)))
                exitTrans.setCardNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.CARDNUMBER)))
                exitTrans.setPlateNumber(cursor.getString(cursor.getColumnIndex(ExitTrans.PLATENUMBER)))
                exitTrans.setParkerType(cursor.getString(cursor.getColumnIndex(ExitTrans.PARKERTYPE)))
                exitTrans.setAmount(cursor.getFloat(cursor.getColumnIndex(ExitTrans.AMOUNT)))
                exitTrans.setDateTimeIn(cursor.getString(cursor.getColumnIndex(ExitTrans.DATETIMEIN)))
                exitTrans.setDateTimeOut(cursor.getString(cursor.getColumnIndex(ExitTrans.DATETIMEOUT)))
                exitTrans.setHoursParked(cursor.getInt(cursor.getColumnIndex(ExitTrans.HOURSPARKED)))
                exitTrans.setMinutesParked(cursor.getInt(cursor.getColumnIndex(ExitTrans.MINUTESPARKED)))
                exitTrans.setSettlementRef(cursor.getString(cursor.getColumnIndex(ExitTrans.SETTLEMENTREF)))

                //notes.add(note)
            } while (cursor.moveToNext())
        }
        db.close()
        //val exitTrans = ExitTrans()
        //        cursor!!.getString(cursor.getColumnIndex(ExitTrans.RECEIPTNUMBER)),
        //        cursor.getString(cursor.getColumnIndex(ExitTrans.CASHIERNAME)),
        //        cursor.getString(cursor.getColumnIndex(ExitTrans.ENTRANCEID)))

        // close the db connection
        cursor.close()

        return exitTrans
    }


    fun getColltrain(logINID: String): Colltrain {
        // get readable database as we are not inserting anything
        val db = this.readableDatabase

        val cursor = db.query(Colltrain.TABLE_NAME,
                arrayOf(Colltrain.LOGINID, Colltrain.USERCODE, Colltrain.USERNAME, Colltrain.CARSERVED,
                        Colltrain.LOGINSTAMP, Colltrain.LOGOUTSTAMP, Colltrain.TOTALAMOUNT,
                        Colltrain.REGULARCOUNT, Colltrain.REGULARAMOUNT, Colltrain.GRACEPERIODCOUNT, Colltrain.GRACEPERIODAMOUNT, Colltrain.VIPCOUNT, Colltrain.VIPAMOUNT,
                        Colltrain.MOTORCYCLECOUNT, Colltrain.MOTORCYCLEAMOUNT, Colltrain.SENIORCOUNT, Colltrain.SENIORAMOUNT,
                        Colltrain.DISCOUNTCOUNT, Colltrain.DISCOUNTAMOUNT),
                Colltrain.LOGINID + "=?",
                arrayOf(logINID.toString()), null, null, null, null)

        cursor?.moveToFirst()

        // prepare note object
        var coll = Colltrain()
        coll.Colltrain(cursor!!.getString(cursor.getColumnIndex(Colltrain.LOGINID)),
                "EX04", cursor.getString(cursor.getColumnIndex(Colltrain.USERCODE)), cursor.getString(cursor.getColumnIndex(Colltrain.USERNAME)),
                cursor.getString(cursor.getColumnIndex(Colltrain.LOGINSTAMP)), cursor.getString(cursor.getColumnIndex(Colltrain.LOGOUTSTAMP)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.CARSERVED)), cursor.getDouble(cursor.getColumnIndex(Colltrain.TOTALAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.REGULARCOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.REGULARAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.GRACEPERIODCOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.GRACEPERIODAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.VIPCOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.VIPAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.MOTORCYCLECOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.MOTORCYCLEAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.SENIORCOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.SENIORAMOUNT)),
                cursor.getInt(cursor.getColumnIndex(Colltrain.DISCOUNTCOUNT)), cursor.getDouble(cursor.getColumnIndex(Colltrain.DISCOUNTAMOUNT))
        )
        // close the db connection
        cursor.close()

        return coll
    }


    fun getNote(id: Long): Note {
        // get readable database as we are not inserting anything
        val db = this.readableDatabase

        val cursor = db.query(Note.TABLE_NAME,
                arrayOf(Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP),
                Note.COLUMN_ID + "=?",
                arrayOf(id.toString()), null, null, null, null)

        cursor?.moveToFirst()

        // prepare note object
        val note = Note(
                cursor!!.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)))

        // close the db connection
        cursor.close()

        return note
    }

    fun updateNote(note: Note): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Note.COLUMN_NOTE, note.note)

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                arrayOf(note.id.toString()))
    }

    fun deleteNote(note: Note) {
        val db = this.writableDatabase
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                arrayOf(note.id.toString()))
        db.close()
    }

    fun insertLoginUser(userID: String, password: String): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase

        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LogUser.COLUMN_ID, userID)

        // insert row
//        val id = db.insert(LogUser.TABLE_NAME, null, values)
        val id = 0L
        // close db connection
        db.close()

        // return newly inserted row id
        return id
    }

    fun deleteCrdPlt(all: ArrayList<CrdPlt>) {
        for (i in all) {
            val db = this.writableDatabase
            db.delete(CrdPlt.TABLE_NAME, CrdPlt.CARDCODE + " = ?",
                    arrayOf(i.CardCode))
            db.close()
            println(i.CardCode)
        }
    }

    companion object {

        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "carpark_db"
    }
}
