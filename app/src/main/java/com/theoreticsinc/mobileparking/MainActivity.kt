package com.theoreticsinc.mobileparking

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.theoreticsinc.mobileparking.barcode.BarcodeCaptureActivity
import com.theoreticsinc.mobileparking.database.DBHelper
import com.theoreticsinc.mobileparking.sdk.BluetoothService

class MainActivity : AppCompatActivity() {
    // Debugging
    private val TAG = "MainActivity"
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
    private lateinit var mloginButton: Button
    private lateinit var mTestButton: Button
    private lateinit var mLogoutButton: Button

    var ordersList: MutableList<String> = mutableListOf()
    private var db: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this@MainActivity, "Please SCAN Card to Begin", Toast.LENGTH_LONG).show()
        db = DBHelper(applicationContext)
        mResultTextView = findViewById(R.id.result_textview)
        mTestButton = findViewById(R.id.printTestButton)
        mloginButton = findViewById(R.id.loginButton)
        mLogoutButton = findViewById(R.id.logoutButton)
        if (DEBUG) {
            mTestButton.visibility = View.VISIBLE
            val intent = Intent(applicationContext, ExitActivity::class.java)
            startActivity(intent)
        } else {
            mTestButton.visibility = View.GONE
        }
        mTestButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(applicationContext, BluetoothActivity::class.java)
                intent.putExtra("commandName", "")
                startActivity(intent)
            }
        })

        mloginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        })


        mLogoutButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val sharedPref = getSharedPreferences(
                        "userPref", Context.MODE_PRIVATE)
                val editor = sharedPref!!.edit()
                editor.putString(getString(R.string.uid_key), "")
                editor.apply()
                val bgColor = sharedPref!!.getString(getString(R.string.uid_key), "")
                val intent = Intent(applicationContext, BluetoothActivity::class.java)
                intent.putExtra("commandName", "printXRead")
                startActivity(intent)
            }
        })

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        //Toast.makeText(this@MainActivity, "Version number:$versionCode name:$versionName", Toast.LENGTH_SHORT).show()
/*
        if (mService == null)
            mService = BluetoothService(this, mHandler)

        if (mService!!.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show()
            return
        }
*/


        val sharedPref = this?.getSharedPreferences(
                "userPref", Context.MODE_PRIVATE)

        val currentUser = sharedPref!!.getString(getString(R.string.uid_key), "")
        if (currentUser.toString().compareTo("") == 0) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        System.out.println(currentUser + " userid is")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val p = barcode.cornerPoints
                    //mResultTextView.text = barcode.displayValue
                    ordersList.add(barcode.displayValue + "\n")
                } else
                    mResultTextView.setText(R.string.no_barcode_captured)
            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_F1) {
            mResultTextView.setText("keyDown")
        } else if (keyCode == KeyEvent.KEYCODE_F2) {
            mResultTextView.setText("keyDown")
        }
        mResultTextView.setText(keyCode.toString())
        return super.onKeyDown(keyCode, event)
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
            //val intent = Intent(applicationContext, BluetoothActivity::class.java)
            startActivity(intent)
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
    public val mHandler = object : Handler() {
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
                    Toast.makeText(applicationContext, "...",
                            Toast.LENGTH_SHORT).show()
                }
                MESSAGE_UNABLE_CONNECT
                -> Toast.makeText(applicationContext, "...",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

}
