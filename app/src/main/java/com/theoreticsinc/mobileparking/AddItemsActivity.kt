package com.theoreticsinc.mobileparking

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.theoreticsinc.mobileparking.barcode.BarcodeCaptureActivity
import com.theoreticsinc.mobileparking.database.DatabaseHelper
import com.theoreticsinc.mobileparking.sdk.BluetoothService
import kotlinx.android.synthetic.main.activity_add_items.*

class AddItemsActivity : AppCompatActivity() {
    // Debugging
    private val TAG = "AddItemsActivity"
    private val DEBUG = true
    /** */
    // Message types sent from the BluetoothService Handler
    val MESSAGE_STATE_CHANGE = 1
    val MESSAGE_READ = 2
    val MESSAGE_WRITE = 3
    val MESSAGE_DEVICE_NAME = 4
    val MESSAGE_TOAST = 5
    val MESSAGE_CONNECTION_LOST = 6
    val MESSAGE_UNABLE_CONNECT = 7
    // Key names received from the BluetoothService Handler
    val DEVICE_NAME = "device_name"
    val TOAST = "toast"
    /** */
    // Name of the connected device
    private var mConnectedDeviceName: String? = null
    // Local Bluetooth adapter
    private var mBluetoothAdapter: BluetoothAdapter? = null
    // Member object for the services
    private var mService: BluetoothService? = null
    private lateinit var mResultTextView: TextView
    private lateinit var itemname: TextView
    private lateinit var itemdesc: TextView

    var ordersList: MutableList<String> = mutableListOf()
    private var db: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_items)

        itemname = findViewById(R.id.itemname)
        itemdesc = findViewById(R.id.itemdesc)

        mResultTextView = findViewById(R.id.result_code)
        setSupportActionBar(additemtoolbar)

        save_item.setOnClickListener { view ->
            Snackbar.make(view, "Saved to Database", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            db = DatabaseHelper(this)
            db!!.insertProductItem(mResultTextView.text.toString(), itemname.text.toString(), itemdesc.text.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val p = barcode.cornerPoints
                    mResultTextView.text = barcode.displayValue

                } else
                    mResultTextView.setText(R.string.no_barcode_captured)
            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_F1) {
            mResultTextView.setText("F1")
        } else if (keyCode == KeyEvent.KEYCODE_F2) {
            mResultTextView.setText("F2")
        } else if (keyCode == 281) {
            mResultTextView.setText("Lower Power Button")
        } else if (keyCode == 302) {
            mResultTextView.setText("Left Blue Button")
        } else if (keyCode == 303) {
            mResultTextView.setText("Right Blue Button")
        } else if (keyCode == 301) {
            mResultTextView.setText("Center Blue Button")
            //val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            //startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
        } else if (keyCode == 82) {
            mResultTextView.setText("Menu Button")
        } else if (keyCode == 67) {
            mResultTextView.setText("Trash Button")
        } else if (keyCode == 302) {
            mResultTextView.setText("Left Blue Button")
        } else if (keyCode == 4) {
            mResultTextView.setText("Back Button")
        }
        return super.onKeyUp(keyCode, event)
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private val BARCODE_READER_REQUEST_CODE = 1
    }

    /** */
    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_STATE_CHANGE -> {
                    if (DEBUG)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1)
                    when (msg.arg1) {
                        BluetoothService.STATE_CONNECTED -> {

                        }
                        BluetoothService.STATE_CONNECTING -> {
                            mResultTextView.setText(R.string.title_connecting)
                        }
                        BluetoothService.STATE_LISTEN, BluetoothService.STATE_NONE -> {
                            mResultTextView.setText(R.string.title_not_connected)
                        }
                    }
                }
                MESSAGE_WRITE -> {
                }
                MESSAGE_READ -> {
                }
                MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(DEVICE_NAME)
                    Toast.makeText(applicationContext,
                            "Connected to $mConnectedDeviceName",
                            Toast.LENGTH_SHORT).show()
                }
                MESSAGE_TOAST -> Toast.makeText(applicationContext,
                        msg.data.getString(TOAST), Toast.LENGTH_SHORT)
                        .show()
                MESSAGE_CONNECTION_LOST
                -> {
                    Toast.makeText(applicationContext, ". . .",
                            Toast.LENGTH_SHORT).show()
                }
                MESSAGE_UNABLE_CONNECT
                -> Toast.makeText(applicationContext, " . . . ",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

}
