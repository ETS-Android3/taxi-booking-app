package main.com.jjtaxidriver.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.jjtaxidriver.MainActivity;
import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.constant.ACProgressCustom;
import main.com.jjtaxidriver.constant.BaseUrl;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.constant.MySession;
import main.com.jjtaxidriver.utils.NotificationUtils;

public class CashOutAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private CheckBox express_checkout;
    private EditText fullname_et,bankrouting_et,accountnumber,amount_et;
    private String fullname_str="",bankrouting_str="",accountnumber_str="",amount_et_str="",express_status="No";
    private TextView submit_req;
    private double withdraw_amount,my_wallet_amount;
    ACProgressCustom ac_dialog;
    MySession mySession;
    private String user_log_data="",user_id="";
    MyLanguageSession myLanguageSession;
    private String language = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_cash_out);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
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
        idinit();
        clickevent();
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
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname_str = fullname_et.getText().toString();
                bankrouting_str = bankrouting_et.getText().toString();
                accountnumber_str = accountnumber.getText().toString();
                amount_et_str = amount_et.getText().toString();
                if (fullname_str==null||fullname_str.equalsIgnoreCase("")){

                }
                else if (bankrouting_str==null||bankrouting_str.equalsIgnoreCase("")){

                }
                else if (accountnumber_str==null||accountnumber_str.equalsIgnoreCase("")){

                }
                else if (amount_et_str==null||amount_et_str.equalsIgnoreCase("")){

                }
                else {
                    withdraw_amount = Double.parseDouble(amount_et_str);
                    if (MainActivity.amount!=null&&!MainActivity.amount.equalsIgnoreCase("")){
                        my_wallet_amount = Double.parseDouble(MainActivity.amount);
                    }
                    if (withdraw_amount>my_wallet_amount){
                        Toast.makeText(CashOutAct.this,getResources().getString(R.string.withdrawamountisgreater),Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (express_checkout.isChecked()){
                            express_status ="YES";
                            new SendWithdrawRequest().execute();
                        }
                        else {
                            express_status ="NO";
                            new SendWithdrawRequest().execute();
                        }

                    }


                }
            }
        });
    }

    private void idinit() {
        submit_req = findViewById(R.id.submit_req);
        exit_app_but = findViewById(R.id.exit_app_but);
        express_checkout = findViewById(R.id.express_checkout);
        fullname_et = findViewById(R.id.fullname_et);
        bankrouting_et = findViewById(R.id.bankrouting_et);
        accountnumber = findViewById(R.id.accountnumber);
        amount_et = findViewById(R.id.amount_et);

        new GetprocessRequest().execute();
    }


    private class SendWithdrawRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/withdraw_request?user_id=22&amount=12&full_name=0&account_number=0&account_routing_no=0&ifsc_code=0
            try {
                String postReceiverUrl = BaseUrl.baseurl + "withdraw_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("amount", withdraw_amount);
                params.put("full_name", fullname_str);
                params.put("account_number", accountnumber_str);
                params.put("account_routing_no",bankrouting_str );
                params.put("ifsc_code","" );
                params.put("express_checkout",express_status );


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
                Log.e("Withdraw Response", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        requestSendSuccess();
                    }
                    else if (jsonObject.getString("message").equalsIgnoreCase("Already you have requested"))
                    {
                        requestAlreadyInprocess();
                    }                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    private class GetprocessRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
          //  http://hitchride.net/webservice/get_my_withdraw_request?user_id=22
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_my_withdraw_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);

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
                Log.e("Get Withdraw Response", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        //requestSendSuccess();
                        requestAlreadyInprocess();
                    }
                    else if (jsonObject.getString("message").equalsIgnoreCase("Already you have requested"))
                    {
                        requestAlreadyInprocess();
                    }                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void requestAlreadyInprocess() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(CashOutAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        message_tv.setText(""+getResources().getString(R.string.alreadyonerequestisprocessing));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
finish();
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    private void requestSendSuccess() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(CashOutAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        message_tv.setText(""+getResources().getString(R.string.yourrequestsendtoadmin));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
finish();
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

}
