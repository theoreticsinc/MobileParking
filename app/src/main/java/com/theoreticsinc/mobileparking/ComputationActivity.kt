package com.theoreticsinc.mobileparking

import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_computation.*


class ComputationActivity : AppCompatActivity() {
    protected var mifareClassic: MifareClassic? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_computation)
        setSupportActionBar(toolbar)
        var amountText = findViewById<View>(R.id.displayAmount) as EditText
        var daysText = findViewById<View>(R.id.displayDays) as EditText
        var hoursText = findViewById<View>(R.id.displayHours) as EditText
        var minsText = findViewById<View>(R.id.displayMins) as EditText

        fab.setOnClickListener { view ->
            Snackbar.make(view, "TRANSACTION", Snackbar.LENGTH_LONG)
                    .setAction("SAVED") {
                        this.finish()
                    }.show()
            this.finish()
        }
        val intent = intent
        val amountStr = intent.getStringExtra("AMOUNT")
        var numOfDaysStr = intent.getIntExtra("NUMOFDAYS", 0)
        val numOfHoursStr = intent.getStringExtra("NUMOFHOURS")
        val numOfMinsStr = intent.getStringExtra("NUMOFMINS")

        amountText.setText(amountStr)
        daysText.setText(numOfDaysStr.toString())
        hoursText.setText(numOfHoursStr)
        minsText.setText(numOfMinsStr)

        intent.putExtra("days", numOfDaysStr)
        intent.putExtra("hours", numOfHoursStr)
        intent.putExtra("mins", numOfMinsStr)

        setResult(900, intent)
        Toast.makeText(this, "PLEASE PRESS ENTER TO SAVE TICKET", Toast.LENGTH_SHORT)
                .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
