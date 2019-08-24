package main.com.jjtaxiuser.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.paymentclasses.ConfirmPayment;

public class WalletActivity extends AppCompatActivity {
    private TextView walletamt,totalamount, onefifty_but, hundred_but, fifty_but, addmoney;
    private EditText amount_et;
    private RelativeLayout exit_app_but;
    private String amount_str="";
    String request_id="",user_log_data="",user_id="",car_charge_str="",rating="",tips_amount_str="",time_zone="",comment_str="";
    MySession mySession;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_wallet);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        idnit();
        clickevent();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fifty_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("50");
                fifty_but.setBackgroundResource(R.drawable.border_yellowrounddrab);
                hundred_but.setBackgroundResource(R.drawable.border_grey_rec);
                onefifty_but.setBackgroundResource(R.drawable.border_grey_rec);

            }
        });
        hundred_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("100");
                hundred_but.setBackgroundResource(R.drawable.border_yellowrounddrab);
                onefifty_but.setBackgroundResource(R.drawable.border_grey_rec);
                fifty_but.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

        onefifty_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("150");
                onefifty_but.setBackgroundResource(R.drawable.border_yellowrounddrab);
                hundred_but.setBackgroundResource(R.drawable.border_grey_rec);
                fifty_but.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });
        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_str = amount_et.getText().toString();
                if (amount_str==null||amount_str.equalsIgnoreCase("")){
                    Toast.makeText(WalletActivity.this,getResources().getString(R.string.enteramount),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(WalletActivity.this,ConfirmPayment.class);
                    i.putExtra("amount_str",amount_str);
                    i.putExtra("request_id",request_id);
                    i.putExtra("car_charge_str",car_charge_str);
                    i.putExtra("rating",rating);
                    i.putExtra("tips_amount_str",tips_amount_str);
                    i.putExtra("comment_str",comment_str);
                    i.putExtra("time_zone",time_zone);
                    i.putExtra("transaction_type","add_to_wallet");
                    startActivity(i);

                }

            }
        });
    }

    private void idnit() {
        addmoney = findViewById(R.id.addmoney);
        exit_app_but = findViewById(R.id.exit_app_but);
        totalamount = findViewById(R.id.totalamount);
        totalamount.setText("$"+ MainActivity.amount);
        fifty_but = (TextView) findViewById(R.id.fifty_but);
        hundred_but = (TextView) findViewById(R.id.hundred_but);
        onefifty_but = (TextView) findViewById(R.id.onefifty_but);
        amount_et = (EditText) findViewById(R.id.amount_et);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        new GetUserProfile().execute();
    }

    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_driver?driver_id=36
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "USER");


                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                Log.e("Json Driver Profile", ">>>>>>>>>>>>" + response);
                return response;
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String online_status = jsonObject1.getString("online_status");
                        MainActivity.amount = jsonObject1.getString("amount");
                        totalamount.setText("$"+ MainActivity.amount);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

}
