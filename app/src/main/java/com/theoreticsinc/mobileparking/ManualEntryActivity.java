package com.theoreticsinc.mobileparking;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.theoreticsinc.mobileparking.database.DBHelper;
import com.theoreticsinc.mobileparking.database.model.CrdPlt;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ManualEntryActivity extends AppCompatActivity implements TimePickerFragment.OnTimePickedListener, DatePickerFragment.OnDatePickedListener {

    public String server = "http://192.168.1.240/";
    DBHelper db = null;
    private Date d2IN;
    /// get time and date
    private DateFormat df = new SimpleDateFormat("h:mm a");
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm");
    private String time, date;

    private TextView cardNo;
    private TextView entryTime;
    private TextView entryDate;
    private TextView sysMessages;

    // use in hold response from server
    private InputStream is = null;
    private String result = null;
    private String line = null;
    private int code;
    private String CardNumber = "";
    private String timeIN, dateIN;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
/// get time and date
        DateFormat df = new SimpleDateFormat("h:mm a");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm");


        cardNo = findViewById(R.id.cardNoDisplay);
        Button setTimeBtn = findViewById(R.id.setTimeBtn);
        Button setDateBtn = findViewById(R.id.setDateBtn);
        entryTime = findViewById(R.id.entryTime);
        entryDate = findViewById(R.id.entryDate);
        sysMessages = findViewById(R.id.sysMessages1);


        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
                //entryTime.setText(((TimePickerFragment) newFragment).message);
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
                //entryTime.setText(((TimePickerFragment) newFragment).message);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scan Card or Press Back Button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendData2Server();
            }
        });

        Intent intent = getIntent();
        CardNumber = intent.getStringExtra("cardNumber");
        cardNo.setText(CardNumber);
    }


    private void sendData2Server() {
        try {
            CardNumber = cardNo.getText().toString();
            //String CardNumber = "TEST241";
            db = new DBHelper(this);
            d2IN = new Date();
            db.saveCrdPlt(CardNumber);
            //String date2Retrieve = db.getNetManagerLastDate("crdplt");
            ArrayList<CrdPlt> all = db.getAllCrdPlt();
            //ArrayList<CrdPlt> all = db.getExitTransList(date2Retrieve);
            ArrayList<String> cards = new ArrayList<>();
            cards.add(CardNumber);
            new sendUserTOServer().execute(cards);
            String currdate = dateTimeFormat.format(d2IN);
            System.out.println("***currdate***" + currdate);
            db.deleteCrdPlt(all);
            //db.deleteNetManager("crdplt", CardNumber);
            //db.saveCard2Server(CardNumber);
            //super.onBackPressed();
        } catch (Exception e) {
            Log.e("Fail Sending Data", e.toString());
        }
    }

    @Override
    public void onTimePicked(int textId, int hour, int minute) {
        entryTime.setText(hour + ":" + minute);
        timeIN = hour + ":" + minute;
    }

    @Override
    public void onDatePicked(Integer mLayoutId, int year, int month, int date) {
        month++;
        entryDate.setText(year + "-" + month + "-" + date);
        dateIN = year + "-" + month + "-" + date;
    }

    private class sendUserTOServer extends AsyncTask<ArrayList<String>, Void, String> {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        private ProgressDialog dialog = new ProgressDialog(ManualEntryActivity.this);

        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Saving Card to Server");
                this.dialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("window is closed", "connection is closed");
            }

        }

        protected String doInBackground(ArrayList<String>... alldata) {

            ArrayList<String> passed = alldata[0]; //get passed arraylist

            String usnm = passed.get(0);

            // current time
            //time = df.format(Calendar.getInstance().getTime());
            //date = dt.format(c.getTime());
            try {

                // Log.e(" setup Activity ", "  user detail to server " + " "+ SendName+" "+Sendmobile+" "+Checkgender);

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                CardNumber = cardNo.getText().toString();
                nameValuePairs.add(new BasicNameValuePair("ritc", CardNumber));
                nameValuePairs.add(new BasicNameValuePair("dateIn", dateIN + " " + timeIN));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(server + "comms/makeManualEntry.php");
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
                this.dialog.setMessage("Card : " + CardNumber + " is Accepted!");
                this.dialog.show();
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
                //code=(json_data.getInt("Tripcode"));
                //   Log.d("pass 3", "connection success " + result);

                try {
                    String Res_username = json_data.getString("Res_username");
                    String Res_password = json_data.getString("Res_password");

                    Toast.makeText(getBaseContext(), Res_username + "  " + Res_password, Toast.LENGTH_SHORT);
                    if (null == result || result.compareTo("[]\n") == 0) {
                        sysMessages.setText("EMPTY CARD");
                    } else {
                        sysMessages.setText(Res_password);
                    }
                } catch (Exception e) {
                    Log.e("Fail process Result ", e.toString());
                }


            } catch (Exception e) {
                //LoginError(" Network Problem , Please try again");
                Log.e("Fail 3 main result ", e.toString());
                // Log.d(" setup Activity "," fail 3 error - "+ result );
            }
        }

    }


}
