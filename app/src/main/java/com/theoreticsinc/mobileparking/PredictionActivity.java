package com.theoreticsinc.mobileparking;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.theoreticsinc.mobileparking.database.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PredictionActivity extends Activity {

    private String URL_NEW_PREDICTION = "http://192.168.1.88/new_predict.php";
    private Button btnAddPrediction;

    String numOfGoal = "1";
    String numOfCard = "1";
    String diffOfPos = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        RadioGroup goal = (RadioGroup) findViewById(R.id.answer1);

        goal.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.answer1A:
                        numOfGoal = "1";
                        break;
                    case R.id.answer1B:
                        numOfGoal = "2";
                        break;
                    case R.id.answer1C:
                        numOfGoal = "3";
                        break;

                }

            }
        });

        RadioGroup card = (RadioGroup) findViewById(R.id.answer2);

        card.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.answer2A:
                        numOfCard = "1";
                        break;
                    case R.id.answer2B:
                        numOfCard = "2";
                        break;
                    case R.id.answer2C:
                        numOfCard = "3";
                        break;

                }
            }
        });

        RadioGroup pos = (RadioGroup) findViewById(R.id.answer3);

        pos.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.answer3A:
                        diffOfPos = "1";
                        break;
                    case R.id.answer3B:
                        diffOfPos = "2";
                        break;
                    case R.id.answer3C:
                        diffOfPos = "3";
                        break;

                }

            }
        });

        btnAddPrediction = (Button) findViewById(R.id.submit);

        btnAddPrediction.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                new AddNewPrediction().execute(numOfGoal, numOfCard, diffOfPos);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class AddNewPrediction extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {
            // TODO Auto-generated method stub
            String goalNo = arg[0];
            String cardNo = arg[1];
            String posDiff = arg[2];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("goalNo", goalNo));
            params.add(new BasicNameValuePair("cardNo", cardNo));
            params.add(new BasicNameValuePair("posDiff", posDiff));

            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(URL_NEW_PREDICTION,
                    ServiceHandler.POST, params);

            Log.d(getString(R.string.Request), "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        Log.e(getString(R.string.InsertionSuccess),
                                "> " + jsonObj.getString("message"));
                    } else {
                        Log.e("Add Prediction Error: ",
                                "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}