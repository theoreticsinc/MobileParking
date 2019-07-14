package com.theoreticsinc.mobileparking

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_test_nfcexit.*
import java.util.*

class TestNFCExit : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_nfcexit)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val newDate1: Date
        newDate1 = Date()
        val newDate1Long = newDate1.getTime()
        System.out.println("SEE CODE newDate1:" + newDate1 + "getTime:" + newDate1Long)

        //1 year = 31,556,952
        //49 years = 1546290648000L
        //1498312668"));    //06/24/2017 2:00PM
        //P1E1#R1498312668
        var timeIn = "1498312668"

        val newDate2: Date
        newDate2 = Date((timeIn.toLong() * 1000L) - 7200L)
        System.out.println("SEE CODE newDate2:" + newDate2 + "getTime:" + newDate2.getTime())

        printDifference(newDate2, newDate1)

    }

    fun printDifference(startDate: Date, endDate: Date) {

        //milliseconds
        var different = endDate.time - startDate.time

        println("SEE CODE startDate : $startDate")
        println("SEE CODE endDate : $endDate")
        println("SEE CODE different : $different")

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different = different % daysInMilli

        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli

        val elapsedSeconds = different / secondsInMilli

        System.out.printf(
                "SEE CODE %d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds)

    }
}
