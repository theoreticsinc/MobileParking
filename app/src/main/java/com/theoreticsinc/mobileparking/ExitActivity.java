package com.theoreticsinc.mobileparking;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theoreticsinc.mobileparking.database.DBHelper;
import com.theoreticsinc.mobileparking.database.model.Colltrain;
import com.theoreticsinc.mobileparking.nfcdemo.BaseEntryActivity;
import com.theoreticsinc.mobileparking.nfcdemo.MifareClassicCard;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@SuppressLint("NewApi")
public class ExitActivity extends BaseEntryActivity {
    private String content;

    // use in hold response from server
    private InputStream is = null;
    private String result = null;
    private String line = null;
    private int code;
    private String CardNumber = "";

    private String TAG = "ExitActivity";
    private boolean DEBUG = true;

    /// get time and date
    private DateFormat df = new SimpleDateFormat("h:mm a");
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String time, date;
    private Date d2IN;

    private String trtype = "R";
    private String entranceID = "E1";
    private String TERMINALID = "EX04";
    private Integer daysElapsed;
    private Integer hoursElapsed;
    private Integer minutesElapsed;
    private Integer secondsElapsed;
    private Integer orNum;
    private String cardCode;
    private String plateNum;
    private String vehicle;
    private String timeIn;
    private String timeOut;
    private String duration;
    private String amountComputed;
    private String currentUser;
    private Double vatsale;
    private Double percent12;

    String dupORNumber;
    String dupCardNumber;
    String dupPlateNumber;
    String dupVehicle;
    String dupTimeIn;
    String dupTimeOut;
    Float dupAmount;
    Float dupAmountDue;
    String dupCurrentUser;
    String dupDuration;
    Float dupDiscount;

    private boolean exitPressed = false;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exitcard_check);
        cardCodeEditText = (EditText) findViewById(R.id.edittext_car_code);
        contentEditText = (EditText) findViewById(R.id.edittext_content);
        contentDuration = (EditText) findViewById(R.id.contentDuration);
        contentAmount = (EditText) findViewById(R.id.contentAmount);
        contentPlate = (EditText) findViewById(R.id.contentPlate);
        privateCarBtn = (ImageButton) findViewById(R.id.privateBtn);
        motorBtn = (ImageButton) findViewById(R.id.motorBtn);
        vipBtn = (ImageButton) findViewById(R.id.vipBtn);
        seniorBtn = (ImageButton) findViewById(R.id.seniorBtn);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // check NFC status.
        if (DEBUG == false) {
            checkNfc();
        } else {
        }
        //RESET TO OLD RECEIPT NOS
        //writeToFile("01561847", getApplicationContext(), "trent.box");
        onNewIntent(getIntent());
        initListener();

        SharedPreferences sharedPref = getSharedPreferences(
                "userPref", Context.MODE_PRIVATE);
        String currentUser = sharedPref.getString(getString(R.string.uid_key), "");
        this.currentUser = currentUser;
        if (currentUser.toString().compareTo("") == 0) {
            startActivity(new Intent(ExitActivity.this, LoginActivity.class));
            this.finish();
        }
        System.out.println(currentUser + " userid is");
        contentPlate.setText("");
        //displayProcessor(contentDuration, "Proc: "+readProcessorUsage());
        //displayProcessor(contentPlate, "Memory: "+readMemoryUsage());
    }

    private void initListener() {

        final MifareClassicCard mifareClassicCard = new MifareClassicCard(
                mifareClassic);
        int block = 4;
        //content = mifareClassicCard.readCarCode(block,"");
        if (getResources().getString(R.string.alert_auth_err)
                .equals(content)
                || getResources()
                .getString(R.string.alert_read_err).equals(
                        content)) {
            setHintToContentEd(content);
        } else {
            /*
            if (content.charAt(0) == 0b0) {
                contentEditText.setText("EMPTY CARD");
            } else {
                contentEditText.setText("Card Still Has Valid Data");
            }*/
        }

        privateCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (DEBUG == true) {
                    displayProcessor(contentDuration, "Proc: " + readProcessorUsage());
                    orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                    orNum++;
                    CardNumber = "1CAC9246";
                    plateNum = "BOK728";
                    timeIn = dtf.format(new Date());
                    timeOut = dtf.format(new Date());
                    amountComputed = "40.00";
                    vatsale = 12.5;
                    percent12 = 58.75;
                    trtype = "R";
                    daysElapsed = 0;
                    hoursElapsed = 1;
                    minutesElapsed = 30;
                    if (computeAll_byDB(trtype)) {
                        if (amount == 0) {
                            //send2ReceiptPrinterZero();
                        } else if (amount > 0) {
                            //send2ReceiptPrinter();
                        }
                        new saveEXTransaction().execute();
                        updateRecords();
                    }
                } else {
                    if (exitPressed == false) {
                        Toast.makeText(getBaseContext(), "Processing Card...", Toast.LENGTH_SHORT);
                        exitPressed = true;
                        menuDisable(false);
                        orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                        orNum++;
                        writeToFile(orNum.toString(), getApplicationContext(), "trent.box");
                        trtype = "R";
                        //openComputationDisplay();
                        if (computeAll_byDB(trtype)) {
                            save4DuplicateReceipt();
                            if (amount == 0) {
                                send2ReceiptPrinterZero();
                            } else if (amount > 0) {
                                send2ReceiptPrinter();
                            }
                            new saveEXTransaction().execute();
                            updateRecords();
                        }
                        writeToFile(orNum.toString(), getApplicationContext(), "trent.box");
                        exitPressed = false;
                        menuDisable(true);
                    }
                }
            }
        });
        motorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitPressed == false) {
                    Toast.makeText(getBaseContext(), "Processing Card...", Toast.LENGTH_SHORT);
                    exitPressed = true;
                    menuDisable(false);
                    orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                    orNum++;
                    writeToFile(orNum.toString(), getApplicationContext(), "trent.box");
                    trtype = "M";
                    if (computeAll_byDB(trtype)) {
                        save4DuplicateReceipt();
                        if (amount == 0) {
                            send2ReceiptPrinterZero();
                        } else if (amount > 0) {
                            send2ReceiptPrinter();
                        }
                        new saveEXTransaction().execute();
                        updateRecords();
                    }
                    exitPressed = false;
                    menuDisable(true);
                }
            }
        });
        vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitPressed == false) {
                    Toast.makeText(getBaseContext(), "Processing Card...", Toast.LENGTH_SHORT);
                    exitPressed = true;
                    menuDisable(false);
                    orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                    orNum++;
                    writeToFile(orNum.toString(), getApplicationContext(), "trent.box");
                    trtype = "V";
                    if (computeAll_byDB(trtype)) {
                        save4DuplicateReceipt();
                        if (amount == 0) {
                            send2ReceiptPrinterZero();
                        } else if (amount > 0) {
                            send2ReceiptPrinter();
                        }
                        new saveEXTransaction().execute();
                        updateRecords();
                    }
                    exitPressed = false;
                    menuDisable(true);
                }
            }
        });
        seniorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DEBUG == true) {
                    orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                    orNum++;
                    CardNumber = "1CAC9246";
                    plateNum = "BOK728";
                    timeIn = dtf.format(new Date());
                    timeOut = dtf.format(new Date());
                    amountComputed = "40.00";
                    vatsale = 12.5;
                    percent12 = 58.75;
                    trtype = "S";
                    daysElapsed = 0;
                    hoursElapsed = 1;
                    minutesElapsed = 30;
                    if (computeAll_byDB(trtype)) {
                        save4DuplicateReceipt();
                        if (amount == 0) {
                            send2ReceiptPrinterZero();
                        } else if (amount > 0) {
                            send2ReceiptPrinter();
                        }
                        new saveEXTransaction().execute();
                        updateRecords();
                    }
                } else {
                    if (exitPressed == false) {
                        Toast.makeText(getBaseContext(), "Processing Card...", Toast.LENGTH_SHORT);
                        exitPressed = true;
                        menuDisable(false);
                        orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                        orNum++;
                        writeToFile(orNum.toString(), getApplicationContext(), "trent.box");
                        trtype = "S";
                        if (computeAll_byDB(trtype)) {

                            save4DuplicateReceipt();
                            if (amount == 0) {
                                send2ReceiptPrinterZero();
                            } else if (amount > 0) {
                                send2ReceiptPrinter();
                            }

                            new saveEXTransaction().execute();
                            updateRecords();
                        }
                        exitPressed = false;
                        menuDisable(true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F2:
                send4DuplicateReceiptPrinter();
                return true;
            case 301:
            case 302:
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    private void menuDisable(boolean state) {
        privateCarBtn.setEnabled(state);
        motorBtn.setEnabled(state);
        vipBtn.setEnabled(state);
        seniorBtn.setEnabled(state);
        if (state) {
            privateCarBtn.setVisibility(View.VISIBLE);
            motorBtn.setVisibility(View.VISIBLE);
            vipBtn.setVisibility(View.VISIBLE);
            seniorBtn.setVisibility(View.VISIBLE);
        } else {
            privateCarBtn.setVisibility(View.INVISIBLE);
            motorBtn.setVisibility(View.INVISIBLE);
            vipBtn.setVisibility(View.INVISIBLE);
            seniorBtn.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("NewApi")
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS,
                    TECHLISTS);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

        } else {
            if (tag != null) {

                // Get card type & tag lists.
                String[] techList = tag.getTechList();
                StringBuffer techString = new StringBuffer();
                for (int i = 0; i < techList.length; i++) {
                    techString.append(techList[i]);
                    techString.append(";\n");
                }
                //typeEditText.setText(techString.toString());
                // Get card id.
                byte[] id = tag.getId();
                CardNumber = ByteArrayToHexString(id);
                cardCodeEditText.setText(CardNumber);
                new getUserDetailFromServer().execute();
                //saveTransaction(CardNumber);
                // operate mifare card
                mifareClassic = MifareClassic.get(tag);
                if (mifareClassic != null) {
                    showView(1);
                } else {
                    showView(0);
                }
            }
        }
    }

    private void resetInterface() {
        CardNumber = "";
        cardCodeEditText.setText(CardNumber);
        contentEditText.setText("");
        contentAmount.setText("");
        contentPlate.setText("");
        contentDuration.setText("");
    }

    private String fillZeros(String orig, int zeros, boolean startFromLeft) {
        String out = orig;
        int loop = zeros - orig.length();
        if (orig.length() > zeros) {
            return orig;
        }
        for (int i = 0; i <= loop; i++) {
            if (startFromLeft) {
                out = "0" + out;
            } else {
                out = out + "0";
            }
        }
        return out;
    }


    private void send2ReceiptPrinter() {
        orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
        Intent i = new Intent(getApplicationContext(), BluetoothActivity.class);
        i.putExtra("commandName", "printReceipt");
        i.putExtra("orNumOut", "2-" + fillZeros(orNum.toString(), 8, true));
        i.putExtra("cardCode", CardNumber);
        i.putExtra("plateNum", plateNum);
        i.putExtra("vehicle", vehicle);
        i.putExtra("timeIn", timeIn);
        i.putExtra("timeOut", timeOut);
        i.putExtra("amount", amount);
        i.putExtra("amountDue", amountDue);
        i.putExtra("currentUser", currentUser);
        i.putExtra("duration", duration);
        i.putExtra("discount", discount);

        startActivity(i);

    }

    private void send2ReceiptPrinterZero() {
        orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
        Intent i = new Intent(getApplicationContext(), BluetoothActivity.class);
        i.putExtra("commandName", "printReceiptZero");
        i.putExtra("orNumOut", "2-" + fillZeros(orNum.toString(), 8, true));
        i.putExtra("cardCode", CardNumber);
        i.putExtra("plateNum", plateNum);
        i.putExtra("vehicle", vehicle);
        i.putExtra("timeIn", timeIn);
        i.putExtra("timeOut", timeOut);
        i.putExtra("amount", amount);
        i.putExtra("amountDue", amountDue);
        i.putExtra("currentUser", currentUser);
        i.putExtra("duration", duration);
        i.putExtra("discount", discount);

        startActivity(i);

    }

    private void save4DuplicateReceipt() {
        dupORNumber = orNum.toString();
        dupCardNumber = CardNumber;
        dupPlateNumber = plateNum;
        dupVehicle = vehicle;
        dupTimeIn = timeIn;
        dupTimeOut = timeOut;
        dupAmount = amount;
        dupAmountDue = amountDue;
        dupCurrentUser = currentUser;
        dupDuration = duration;
        dupDiscount = discount;
    }

    private void send4DuplicateReceiptPrinter() {
        if (null != dupCardNumber) {
            Intent i = new Intent(getApplicationContext(), BluetoothActivity.class);
            i.putExtra("commandName", "printDuplicate");
            i.putExtra("orNumOut", "2-" + fillZeros(dupORNumber.toString(), 8, true));
            i.putExtra("cardCode", dupCardNumber);
            i.putExtra("plateNum", dupPlateNumber);
            i.putExtra("vehicle", dupVehicle);
            i.putExtra("timeIn", dupTimeIn);
            i.putExtra("timeOut", dupTimeOut);
            i.putExtra("amount", dupAmount);
            i.putExtra("amountDue", dupAmountDue);
            i.putExtra("currentUser", dupCurrentUser);
            i.putExtra("duration", dupDuration);
            i.putExtra("discount", dupDiscount);
            startActivity(i);
        }
    }

    private boolean computeAll_byDB(String TR) {
        amount = 40.0f;
        int firstHours = 3;
        float succeedingRate = 10f;
        NUMOFDAYS = daysElapsed;
        NUMOFHOURS = hoursElapsed;
        NUMOFMINS = minutesElapsed;

        //NUMOFDAYS--;  //Because Day One does not count as Elapsed Day
        //Default TR = "R"
        if (null == NUMOFHOURS || null == NUMOFDAYS || null == NUMOFMINS) {
            return false;
        }
        if (NUMOFHOURS < 0) {
            return false;
        }

        if (NUMOFHOURS == 0 && NUMOFDAYS == 0 && NUMOFMINS <= 19) {  //0-30
            amount = 0f;
            trtype = "G";
        } else if (NUMOFHOURS == 0 && NUMOFDAYS == 0 && NUMOFMINS > 19) { //30 above
            amount = 40f;
        } else if (NUMOFHOURS >= 1 && NUMOFDAYS == 0) { //1hr above
            amount = 40f;
        }

        //Succeeding Rates
        int succeedingHRS = NUMOFHOURS - firstHours;
        //Fraction of 3 Hours
        if (succeedingHRS == 0 && NUMOFMINS > 0) {
            amount = amount + succeedingRate;
        }
        //4 Hours
        else if (succeedingHRS > 0 && NUMOFMINS == 0) {
            amount = amount + (succeedingHRS * succeedingRate);
        }
        //4 Hours and Fraction of the 4th Hour
        else if (succeedingHRS > 0 && NUMOFMINS >= 0) {
            amount = amount + (succeedingHRS * succeedingRate) + succeedingRate;
        }


        //else if (NUMOFHOURS >= 0 && NUMOFDAYS >= 1) { //1 day exactly incl no hours and no mins
        //  amount = 40f;
        //} else if (NUMOFHOURS >= 1 && NUMOFDAYS >= 1) { //1 day above
        //  amount = 40f;
        //}

        if (TR.compareTo("Q") == 0) {
            amount = 0f;
        } else if (TR.compareTo("NQ") == 0) {
            amount = 24.0f;
        } else if (TR.compareTo("V") == 0) {
            amount = 0f;
        } else if (TR.compareTo("M") == 0) {
            //DO NOT OVERRIDE MOTORCYCLES COMPUTATION
            //amount = 40.0f;
        }

        //Grace Override
        if (NUMOFHOURS == 0 && NUMOFDAYS == 0 && NUMOFMINS <= 19) {  //0-20
            amount = 0f;
        }

        amount = amount + (NUMOFDAYS * 250);

        contentAmount.setText("P " + amount + "0");
        cardCode = CardNumber;
        plateNum = contentPlate.getText().toString();
        duration = hoursElapsed + " Hrs, " + minutesElapsed + "Mins";
        vatsale = amount / 1.12;
        vatsale = Math.round(vatsale * 100.0) / 100.0;
        percent12 = amount - (amount / 1.12);
        percent12 = Math.round(percent12 * 100.0) / 100.0;
        amountDue = amount;
        if (TR.compareToIgnoreCase("R") == 0) {
            vehicle = "REGULAR";
        } else if (TR.compareToIgnoreCase("M") == 0) {
            vehicle = "MOTORCYCLE";
        } else if (TR.compareToIgnoreCase("V") == 0) {
            vehicle = "VIP";
            amount = 0f;
        } else if (TR.compareToIgnoreCase("S") == 0) {
            vehicle = "SENIOR";
            double discounted = vatsale * 0.2;
            discounted = Math.round(discounted * 100.0) / 100.0;
            this.discount = (float) discounted;
            amountDue = vatsale.floatValue() - (float) discounted;
            Double aD = Math.round(amountDue * 100.0) / 100.0;
            amountDue = aD.floatValue();
        }
        return true;
    }


    public void updateRecords() {
        db = new DBHelper(this);
        //Update Grand Total
        SharedPreferences sharedPref = getSharedPreferences(
                "TRENT", Context.MODE_PRIVATE);

        SharedPreferences sharedLoginPref = getSharedPreferences(
                getString(R.string.uid_key), Context.MODE_PRIVATE);
        String tellerCode = sharedPref.getString(getString(R.string.uid_key), "0");

        String gTotal = sharedPref.getString(getString(R.string.grandtotal_key), "0");
        //receipt_key
        orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));

        Float grand = Float.parseFloat(gTotal) + amountDue;
        //Long rNum = Long.parseLong(rNos) + 1;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.grandtotal_key), grand.toString());
        editor.putString(getString(R.string.receipt_key), orNum.toString());
        editor.apply();


        //Update Exit Trans
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Save New Exit Transaction
        long id = db.insertExitTrans(TERMINALID + String.format("%08d", orNum), tellerCode, entranceID,
                TERMINALID, cardCodeEditText.getText().toString(), contentPlate.getText().toString(), trtype, amountDue.toString(),
                vatsale.toString(), this.discount.toString(),
                timeIn, timeOut, hoursElapsed.toString(), minutesElapsed.toString(), "");

        //Update Collection Report
        SharedPreferences logPref = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        String loginID = logPref.getString(getString(R.string.logInid_key), "0");
        Colltrain origColltrain = db.getColltrain(loginID);
        int carServed = origColltrain.getCarServed();
        carServed++;

        double totalAmount = origColltrain.getTotalAmount();
        double temp = Math.round((totalAmount + amountDue) * 100.0) / 100.0;
        totalAmount = temp;

        int regularCount = origColltrain.getRegularCount();
        double regularAmount = origColltrain.getRegularAmount();

        int gracePeriodCount = origColltrain.getGracePeriodCount();
        double gracePeriodAmount = origColltrain.getGracePeriodAmount();

        int vipCount = origColltrain.getVipCount();
        double vipAmount = origColltrain.getVipAmount();

        int motorcycleCount = origColltrain.getMotorcycleCount();
        double motorcycleAmount = origColltrain.getMotorcycleAmount();

        int seniorCount = origColltrain.getSeniorCount();
        double seniorAmount = origColltrain.getSeniorAmount();

        int discountCount = origColltrain.getDiscountCount();
        double discountAmount = origColltrain.getDiscountAmount();

        Colltrain newColltrain = new Colltrain();

        newColltrain.setLogINID(loginID);
        newColltrain.setCarServed(carServed);
        newColltrain.setTotalAmount(totalAmount);

        if (trtype.compareToIgnoreCase("G") == 0) {
            gracePeriodCount++;

        } else if (trtype.compareToIgnoreCase("R") == 0) {
            regularCount++;
            regularAmount = regularAmount + amountDue;
        } else if (trtype.compareToIgnoreCase("V") == 0) {
            vipCount++;

        } else if (trtype.compareToIgnoreCase("M") == 0) {
            motorcycleCount++;
            motorcycleAmount = motorcycleAmount + amountDue;

        } else if (trtype.compareToIgnoreCase("S") == 0) {
            seniorCount++;
            seniorAmount = Math.round((seniorAmount + amountDue) * 100.0) / 100.0;

            //Discounts Recording
            discountCount++;
            discountAmount = Math.round((discountAmount + this.discount) * 100.0) / 100.0;

        }
        newColltrain.setGracePeriodCount(gracePeriodCount);
        newColltrain.setRegularCount(regularCount);
        newColltrain.setVipCount(vipCount);
        newColltrain.setMotorcycleCount(motorcycleCount);
        newColltrain.setSeniorCount(seniorCount);
        newColltrain.setDiscountCount(discountCount);

        newColltrain.setGracePeriodAmount(gracePeriodAmount + 0f);
        newColltrain.setRegularAmount(regularAmount);
        newColltrain.setVipAmount(vipAmount + 0f);
        newColltrain.setMotorcycleAmount(motorcycleAmount);
        newColltrain.setSeniorAmount(seniorAmount);
        newColltrain.setDiscountAmount(discountAmount);
        //UPDATE CASHIERS COLLECTION
        int res = db.updateColltrain(newColltrain);

        //RESET Stuff
        amount = 0f;
        contentEditText.setText("Card Erased");
    }

    private void displayProcessor(final EditText text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private class getUserDetailFromServer extends AsyncTask<String, Void, String> {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        private ProgressDialog dialog = new ProgressDialog(ExitActivity.this);

        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("retrieving from server");
                this.dialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("window is closed", "connection is closed");
            }

        }

        protected String doInBackground(String... alldata) {

            //String usnm = passed.get(0);

            // current time
            time = df.format(Calendar.getInstance().getTime());
            date = dt.format(c.getTime());
            try {

                // Log.e(" setup Activity ", "  user detail to server " + " "+ SendName+" "+Sendmobile+" "+Checkgender);

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                CardNumber = cardCodeEditText.getText().toString();
                nameValuePairs.add(new BasicNameValuePair("ritc", CardNumber));
                nameValuePairs.add(new BasicNameValuePair("date", date));
                nameValuePairs.add(new BasicNameValuePair("time", time));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(server + "comms/getExitData.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8")); // UTF-8  support multi language
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("pass 1", "connection success");
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
                //  Log.d("setup Activity ","  fail 1  "+e.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.d("pass 2", "connection success " + result);

            } catch (Exception e) {
                //  Log.e("Fail 2", e.toString());
                //  Log.e("setup Activity  "," fail  2 "+ e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //  Log.e(" setup acc ","  signup result  " + result);
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                Log.e("window is closed", "connection is closed");
            }
            try {
                JSONObject json_data = new JSONObject(result);
                code = 1;
                //code=(json_data.getInt("Tripcode"));
                //   Log.d("pass 3", "connection success " + result);
                if (code == 1) {
                    try {
                        String timein = json_data.getString("timeIn");
                        timeIn = timein;
                        Date datein;
                        Date datenow = new Date();
                        timeOut = dtf.format(datenow);
                        try {
                            datein = dtf.parse(timein);

                            long diff = datenow.getTime() - datein.getTime();
                            int seconds = (int) diff / 1000;
                            int minutes = seconds / 60;
                            int hours = minutes / 60;
                            int days = hours / 24;

                            daysElapsed = days;
                            hoursElapsed = hours - (days * 24);
                            minutesElapsed = minutes - (hours * 60);
                            secondsElapsed = seconds - (minutesElapsed * 60);
                            contentDuration.setText(days + "days :" + hoursElapsed + "hrs :" + minutesElapsed + "mins");
                            duration = hoursElapsed + " Hrs, " + minutesElapsed + "Min";
                            computeAll_byDB("R");
                        } catch (Exception e) {
                            Log.e("Time In Conversation ", e.toString());
                        }
                        String pc = json_data.getString("pc");

                        Toast.makeText(getBaseContext(), timein + "  " + pc, Toast.LENGTH_SHORT);
                        if (null == result || result.compareTo("[]\n") == 0) {
                            contentEditText.setText("EMPTY CARD");
                        } else {
                            contentEditText.setText(timein + ":" + pc);
                        }
                    } catch (Exception e) {
                        Log.e("Fail process Result ", e.toString());
                    }
                }


            } catch (Exception e) {
                //LoginError(" Network Problem , Please try again");
                Log.e("Fail 3 main result ", e.toString());
                // Log.d(" setup Activity "," fail 3 error - "+ result );
            }
        }
    }


    private class saveEXTransaction extends AsyncTask<String, Void, String> {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        private ProgressDialog dialog = new ProgressDialog(ExitActivity.this);

        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Saving transaction to server");
                this.dialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("window is closed", "connection is closed");
            }

        }

        protected String doInBackground(String... alldata) {

            //String usnm = passed.get(0);

            // current time
            time = df.format(Calendar.getInstance().getTime());
            date = dt.format(c.getTime());
            try {
                orNum = Integer.parseInt(readFromFile(getApplicationContext(), "trent.box"));
                // Log.e(" setup Activity ", "  user detail to server " + " "+ SendName+" "+Sendmobile+" "+Checkgender);
                amountComputed = df2.format(amountDue);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                CardNumber = cardCodeEditText.getText().toString();
                nameValuePairs.add(new BasicNameValuePair("orNum", "2-" + orNum.toString()));
                nameValuePairs.add(new BasicNameValuePair("Cardcode", CardNumber));
                nameValuePairs.add(new BasicNameValuePair("Plate", plateNum));
                nameValuePairs.add(new BasicNameValuePair("Operator", currentUser));
                nameValuePairs.add(new BasicNameValuePair("PC", "POS-2"));
                nameValuePairs.add(new BasicNameValuePair("Timein", timeIn));
                nameValuePairs.add(new BasicNameValuePair("TimeOut", timeOut));
                nameValuePairs.add(new BasicNameValuePair("DiscountAmount", df2.format(discount).toString()));
                nameValuePairs.add(new BasicNameValuePair("Vat", df2.format(vatsale).toString()));
                nameValuePairs.add(new BasicNameValuePair("NonVat", df2.format(percent12).toString()));
                nameValuePairs.add(new BasicNameValuePair("TYPE", vehicle));
                nameValuePairs.add(new BasicNameValuePair("Total", amountComputed));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(server + "comms/saveIncomeData.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8")); // UTF-8  support multi language
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("pass 1", "connection success");
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
                //  Log.d("setup Activity ","  fail 1  "+e.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.d("pass 2", "connection success " + result);

            } catch (Exception e) {
                //  Log.e("Fail 2", e.toString());
                //  Log.e("setup Activity  "," fail  2 "+ e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //  Log.e(" setup acc ","  signup result  " + result);
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                new deleteCard().execute();
            } catch (Exception e) {
                Log.e("window is closed", "connection is closed");
            }
            try {
                JSONObject json_data = new JSONObject(result);
                code = 1;
                if (code == 1) {
                    try {
                        String Res_username = json_data.getString("Res_username");
                        String Res_password = json_data.getString("Res_password");

                        if (null == result || result.compareTo("[]\n") == 0) {
                            contentEditText.setText("No Connection to Server...");
                        } else {
                            contentEditText.setText(Res_password);
                        }
                    } catch (Exception e) {
                        Log.e("Fail process Result ", e.toString());
                    }
                }


            } catch (Exception e) {
                //LoginError(" Network Problem , Please try again");
                Log.e("Fail 3 main result ", e.toString());
                // Log.d(" setup Activity "," fail 3 error - "+ result );
            }
        }
    }


    private class deleteCard extends AsyncTask<String, Void, String> {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        private ProgressDialog dialog = new ProgressDialog(ExitActivity.this);

        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Clearing Card...");
                this.dialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("window is closed", "connection is closed");
            }

        }

        protected String doInBackground(String... alldata) {

            //String usnm = passed.get(0);

            // current time
            time = df.format(Calendar.getInstance().getTime());
            date = dt.format(c.getTime());
            try {

                // Log.e(" setup Activity ", "  user detail to server " + " "+ SendName+" "+Sendmobile+" "+Checkgender);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                CardNumber = cardCodeEditText.getText().toString();
                nameValuePairs.add(new BasicNameValuePair("ritc", CardNumber));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(server + "comms/deleteExitData.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8")); // UTF-8  support multi language
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("pass 1", "connection success");
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
                //  Log.d("setup Activity ","  fail 1  "+e.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.d("pass 2", "connection success " + result);

            } catch (Exception e) {
                //  Log.e("Fail 2", e.toString());
                //  Log.e("setup Activity  "," fail  2 "+ e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //  Log.e(" setup acc ","  signup result  " + result);
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                resetInterface();
            } catch (Exception e) {
                Log.e("window is closed", "connection is closed");
            }
            try {
                JSONObject json_data = new JSONObject(result);
                code = 1;
                if (code == 1) {
                    try {
                        String Res_username = json_data.getString("Res_username");
                        String Res_password = json_data.getString("Res_password");

                        if (null == result || result.compareTo("[]\n") == 0) {
                            contentEditText.setText("No Connection to Server...");
                        } else {
                            contentEditText.setText(Res_password);
                        }
                    } catch (Exception e) {
                        Log.e("Fail process Result ", e.toString());
                    }
                }


            } catch (Exception e) {
                //LoginError(" Network Problem , Please try again");
                Log.e("Fail 3 main result ", e.toString());
                // Log.d(" setup Activity "," fail 3 error - "+ result );
            }
        }
    }


    private float readProcessorUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private float readMemoryUsage() {
        try {

            long totalRamValue = totalRamMemorySize();
            long freeRamValue = freeRamMemorySize();
            long usedRamValue = totalRamValue - freeRamValue;


            return (float) (usedRamValue);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    //MEMORY CHECHER

    private long freeRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

        return availableMegs;
    }

    private long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1048576L;
        return availableMegs;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }
    //##############
}
