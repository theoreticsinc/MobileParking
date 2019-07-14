package com.theoreticsinc.mobileparking.nfcdemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theoreticsinc.mobileparking.LoginActivity;
import com.theoreticsinc.mobileparking.ManualEntryActivity;
import com.theoreticsinc.mobileparking.R;
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
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
@SuppressLint("NewApi")
public class BaseEntryActivity extends Activity {

    public DBHelper db = null;
    public String server = "http://192.168.1.240/";
    public static String[][] TECHLISTS;
    @SuppressLint("NewApi")
    public static IntentFilter[] FILTERS;
    private String version = "5";
    public Float amount = 0f;
    public Float amountDue = 0f;
    public Float discount = 0f;
    public Integer NUMOFDAYS = 0;
    public Integer NUMOFHOURS = 0;
    public Integer NUMOFMINS = 0;
    private String trtype = "R";
    private String entranceID = "E1";
    private String TERMINALID = "EX04";

    static {
        try {
            TECHLISTS = new String[][]{
                    {IsoDep.class.getName()},
                    {NfcV.class.getName()}, {NfcF.class.getName()},
            };

            FILTERS = new IntentFilter[]{
                    new IntentFilter(
                            NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")
            };
        } catch (Exception e) {
        }
    }

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    protected EditText cardCodeEditText;
    protected EditText contentEditText;
    public EditText contentDuration;
    protected EditText contentAmount;
    protected EditText contentPlate;
    protected ImageButton privateCarBtn;
    protected ImageButton vipBtn;
    protected ImageButton motorBtn;
    //protected ImageButton qcBtn;
    protected ImageButton seniorBtn;
    protected MifareClassic mifareClassic;
    private Date d2IN;
    /// get time and date
    private DateFormat df = new SimpleDateFormat("h:mm a");
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd h:mm");
    private String time, date;

    // use in hold response from server
    private InputStream is = null;
    private String result = null;
    private String line = null;
    private int code;
    private String CardNumber = "";
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /*
        qcBtn = (ImageButton) findViewById(R.id.qcBtn);
        vipBtn = (ImageButton) findViewById(R.id.vipBtn);
        seniorBtn = (ImageButton) findViewById(R.id.seniorBtn);
        */
        initListener();

        //sendData2Server();

/*
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
            // do your work
            Random r = new Random();
            long ritcid = r.nextLong();
            ArrayList<String> mylist = new ArrayList<String>();

            mylist.add("AB" + ritcid + "CD" + r.nextLong());

            new sendUserDetailTOServer().execute(mylist);
            handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);
        */
        SharedPreferences sharedPref = getSharedPreferences(
                "userPref", Context.MODE_PRIVATE);
        String currentUser = sharedPref.getString(getString(R.string.uid_key), "");
        if (currentUser.toString().compareTo("") == 0) {
            startActivity(new Intent(BaseEntryActivity.this, LoginActivity.class));
            this.finish();
        }
        System.out.println(currentUser + " userid is");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:
                System.out.println("***F1 BUTTON***");
                CardNumber = cardCodeEditText.getText().toString();
                Intent i = new Intent(BaseEntryActivity.this, ManualEntryActivity.class);
                i.putExtra("cardNumber", CardNumber);
                startActivity(i);
                this.finish();
                return true;
            case 301:
                //P1E1#R1498312668
                String content = "";
                System.out.println("SEE CODE version:" + version);
                MifareClassicCard mifareClassicCard = new MifareClassicCard(
                        mifareClassic);
                int block = 4;

                Date now = new Date();
                long newTimeIn = (now.getTime()) + (28800 * 1000);   // Because of TIMEZONE +8Hrs default
                sendData2Server();
                //String result = mifareClassicCard.writeCarCode("P1E5#R" + String.valueOf(newTimeIn), block, "");
                System.out.println("SEE CODE result:" + String.valueOf(newTimeIn));
                //content = mifareClassicCard.readCarCode(block, "");
                System.out.println("SEE CODE content:" + content);
                contentEditText.setText("Card Entry:" + now.toString());
                contentDuration.setText("CARD was saved to Server!");

                return true;
            case 302:
            default:
                return super.onKeyUp(keyCode, event);
        }

    }

    private void sendData2Server() {
        try {
            CardNumber = cardCodeEditText.getText().toString();
            db = new DBHelper(this);
            d2IN = new Date();
            db.saveCrdPlt(CardNumber);
            //String date2Retrieve = db.getNetManagerLastDate("crdplt");
            ArrayList<CrdPlt> all = db.getAllCrdPlt();
            //ArrayList<CrdPlt> all = db.getExitTransList(date2Retrieve);
            new sendUserDetailTOServer().execute(all);
            String currdate = dateTimeFormat.format(d2IN);
            System.out.println("***currdate***" + currdate);
            db.deleteCrdPlt(all);
            //db.deleteNetManager("crdplt", CardNumber);
            //db.saveCard2Server(CardNumber);
        } catch (Exception e) {
            Log.e("Fail Sending Data", e.toString());
        }
    }

    private class sendUserDetailTOServer extends AsyncTask<ArrayList<CrdPlt>, Void, String> {

        //  final SharedPreferences mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        private ProgressDialog dialog = new ProgressDialog(BaseEntryActivity.this);

        @Override
        protected void onPreExecute() {
            try {
                this.dialog.setMessage("Saving Card to Server");
                this.dialog.show();
            } catch (WindowManager.BadTokenException e) {
                Log.e("window is closed", "connection is closed");
            }

        }

        protected String doInBackground(ArrayList<CrdPlt>... alldata) {

            ArrayList<CrdPlt> passed = alldata[0]; //get passed arraylist

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
                HttpPost httppost = new HttpPost(server + "comms/copyExitData.php");
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
                code = 1;
                //code=(json_data.getInt("Tripcode"));
                //   Log.d("pass 3", "connection success " + result);
                if (code == 1) {
                    try {
                        String Res_username = json_data.getString("Res_username");
                        String Res_password = json_data.getString("Res_password");

                        Toast.makeText(getBaseContext(), Res_username + "  " + Res_password, Toast.LENGTH_SHORT);
                        if (null == result || result.compareTo("[]\n") == 0) {
                            contentEditText.setText("EMPTY CARD");
                        } else {
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

    private void initListener() {

    }

    public void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            } else {
                ret = "0";
            }
        } catch (Exception e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            ret = "0";
        }

        return ret;
    }

    // converts byte arrays to string
    protected String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"
        };
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    @SuppressLint("NewApi")
    protected void checkNfc() {
        if (!nfcAdapter.isEnabled()) {

            startActivity(new Intent(
                    android.provider.Settings.ACTION_NFC_SETTINGS));
        }
    }

    protected void showView(int type) {
        switch (type) {
            case 1:

                break;

            default:
                break;
        }
    }

    protected void hint() {
        //typeEditText.setText("");
        cardCodeEditText.setText("");
        contentEditText.setText("");
    }

    protected void setHintToContentEd(String msg) {
        contentEditText.setText("");
        contentEditText.setHint(msg);
        contentEditText.setHintTextColor(Color.RED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        db = new DBHelper(this);
        MifareClassicCard mifareClassicCard = new MifareClassicCard(
                mifareClassic);
        int block = 4;

        String result = mifareClassicCard.writeCarCode("", block, "");
        String content = mifareClassicCard.readCarCode(block, "");
        System.out.println("SEE CODE onActivityResult content:" + content);


    }
}
