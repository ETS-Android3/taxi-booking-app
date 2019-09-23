package main.com.jjtaxidriver.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import main.com.jjtaxidriver.MainActivity;
import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.app.Config;
import main.com.jjtaxidriver.constant.BaseUrl;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.utils.NotificationUtils;

public class PaymentAct extends AppCompatActivity {

    private ProgressBar progressbar;
    private RelativeLayout exit_app_but;
    private TextView discount_type,paymentmessage,time_tv,date_tv, total_fare, payment_type, carcharge, cullectpayment, basefare, timefare, distancefare, distance;
    private String comment_str = "",total_amount_input_str="",tip_amount_str = "0",amount_str = "", car_charge_str = "",discount_str="0";
    float rating = 0;
    private EditText comment_et,total_fare_et,user_secret_pin;
    private RatingBar ratingbar;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    public String pay_status = "",payment_type_str="";
    String time_zone = "";
    ACProgressFlower ac_dialog;
    MyLanguageSession myLanguageSession;
    private String language = "",amount_str_main="";
    private boolean isVisible =true;
    String user_pin_code, user_pin_code1;
    TextView pin_textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_payment);
//        pin_textView = this.findViewById(R.id.user_pin_code);
//        pin_textView.setVisibility(View.GONE);
        ac_dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        idinit();
        clickevent();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push notification: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your payment is successfull")) {
                            pay_status = "Processing";
                          //  PaymentSendByDriver(data.getString("payment_type").trim());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new GetPayment().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        LocalBroadcastManager.getInstance(PaymentAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(PaymentAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(PaymentAct.this.getApplicationContext());
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(PaymentAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cullectpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingbar.getRating();
                comment_str = comment_et.getText().toString();
                if (rating == 0) {
                    Toast.makeText(PaymentAct.this, "Please give rating", Toast.LENGTH_LONG).show();
                } else {
                    Calendar c = Calendar.getInstance();
                    TimeZone tz = c.getTimeZone();

                    time_zone = tz.getID();

                      if (payment_type_str.equalsIgnoreCase("Cash")){
                          total_amount_input_str = total_fare_et.getText().toString();
                          //if (total_amount_input_str==null||total_amount_input_str.equalsIgnoreCase("")){
                            //  Toast.makeText(PaymentAct.this,getResources().getString(R.string.enteramountthatmarkedbymeter),Toast.LENGTH_LONG).show();
                          //}
                          //else {
                              new SubmitPayment().execute();
                          //}
                      }
                      else if(payment_type_str.equalsIgnoreCase("Corporate")) {
                            total_amount_input_str = total_fare_et.getText().toString();
                            user_pin_code1 = user_secret_pin.getText().toString();
                            amount_str = total_fare_et.getText().toString();
                            if (!user_pin_code1.equalsIgnoreCase(user_pin_code) ) {
                                pincode_errorAlert();
                            }
                            else {
                                new SubmitPayment().execute();
                            }
                      }
                      else  {

                          new SubmitPayment().execute();
                      }



                   /* if (pay_status.equalsIgnoreCase("not_pay")) {
                        Toast.makeText(PaymentAct.this, getResources().getString(R.string.usernotpaymentyet), Toast.LENGTH_LONG).show();
                    } else {
                        new ResponseToRequest().execute();
                    }*/

                }


            }
        });
    }
    private void pincode_errorAlert(){
        final Dialog dialogSts = new Dialog(PaymentAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.pincode_error_alert);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.yes_tv1);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

            }
        });
        dialogSts.show();
    }
    private class SubmitPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
/*
            if(ac_dialog!=null){
                ac_dialog.show();
            }
*/

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://hitchride.net/webservice/add_payment?request_id=44&amount=13.55&car_charge=5&rating=4&review=nice
                String postReceiverUrl = BaseUrl.baseurl + "add_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
                if (payment_type_str.equalsIgnoreCase("Cash")){
                    params.put("amount", total_amount_input_str);
                }
                else {
                    params.put("amount", amount_str);
                }
                params.put("car_charge", car_charge_str);
                params.put("rating", "");
                params.put("discount", discount_str);
                params.put("tip", tip_amount_str);
                params.put("timezone", time_zone);

                if (comment_str == null) {
                    params.put("review", "");
                } else {
                    params.put("review", "");
                }

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
            progressbar.setVisibility(View.GONE);
            Log.e("SUBMIT PAYMENT RESULT", " >> " + result);
/*
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }
*/

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                new ResponseToRequest().execute();
                // waitDriverAccept();
                //finish();


            }
        }
    }


    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
                if (isVisible){
                    ac_dialog.show();
                }

            }


            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/driver_accept_and_Cancel_request?request_id=1&status=Accept
            //http://mobileappdevelop.co/NAXCAN/webservice/
            try {
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
                params.put("status", "Finish");
                params.put("review", comment_str);
                params.put("rating", rating);
                params.put("timezone", time_zone);
                params.put("driver_id", MainActivity.user_id);
                params.put("total_amount", total_amount_input_str);


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
                Log.e("Json Start End", ">>>>>>>>>>>>" + response);
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
                if (ac_dialog!=null){
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

                Toast.makeText(PaymentAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                if (ac_dialog!=null){
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

                Toast.makeText(PaymentAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                if (ac_dialog!=null){
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

                finish();
              /*  Intent i = new Intent(PaymentAct.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);*/
            }


        }
    }

    private void idinit() {
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        comment_et = (EditText) findViewById(R.id.comment_et);
        user_secret_pin = (EditText) findViewById(R.id.user_secret_pin);
        total_fare_et = (EditText) findViewById(R.id.total_fare_et);
        discount_type = (TextView) findViewById(R.id.discount_type);
        carcharge = (TextView) findViewById(R.id.carcharge);
        paymentmessage = (TextView) findViewById(R.id.paymentmessage);
        distance = (TextView) findViewById(R.id.distance);
        basefare = (TextView) findViewById(R.id.basefare);
        timefare = (TextView) findViewById(R.id.timefare);
        time_tv = (TextView) findViewById(R.id.time_tv);
        distancefare = (TextView) findViewById(R.id.distancefare);
        cullectpayment = (TextView) findViewById(R.id.cullectpayment);
        payment_type = (TextView) findViewById(R.id.payment_type);
        total_fare = (TextView) findViewById(R.id.total_fare);
        date_tv = (TextView) findViewById(R.id.date_tv);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
    }

    private class GetPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog!=null){
                if (isVisible){
                    ac_dialog.show();
                }

            }


            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_payment?request_id=1
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
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
           // progressbar.setVisibility(View.GONE);
            if (ac_dialog!=null){
                if (isVisible){
                    ac_dialog.dismiss();
                }

            }


            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("GET PAYMENT RESPONSE", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");

                    if (msg.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray1 = jsonObject.getJSONArray("user_details");
                        JSONObject jsonObject4 = jsonArray1.getJSONObject(0);

                        user_pin_code = jsonObject4.getString("pin");
//                        pin_textView.setText(user_pin_code);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            pay_status = jsonObject1.getString("pay_status");
                            total_fare.setText("Total :" + "$" + jsonObject1.getString("total"));
                            discount_type.setText(getResources().getString(R.string.discountapp) + "$ " + jsonObject1.getString("discount")+" "+getResources().getString(R.string.willadded));
                           /* String dis = jsonObject1.getString("distance");
                            double dis_doub = 0;
                            if (dis == null || dis.equalsIgnoreCase("")) {
                                dis_doub = 1;
                            } else {
                                dis_doub = Double.parseDouble(dis);
                            }


                           // String perkm = jsonObject1.getString("per_km");
                            String perkm = jsonObject1.getString("per_miles_charge");*/
                           /* double per_doub = 0;
                            if (perkm == null || perkm.equalsIgnoreCase("")) {
                                per_doub = 1;
                            } else {
                                per_doub = Double.parseDouble(perkm);
                            }
                            double basefare_dob = dis_doub * per_doub;*/

                            amount_str = jsonObject1.getString("total");
                            amount_str_main = jsonObject1.getString("total");
                            car_charge_str = String.valueOf(jsonObject1.getString("car_charge"));


                            distancefare.setText("$" + jsonObject1.getString("per_miles_charge"));
                            timefare.setText("$" + jsonObject1.getString("per_min_charge"));
                           // basefare.setText("" + "$ " + jsonObject1.getInt("base_fare"));
                            carcharge.setText("" + "$ " + jsonObject1.getInt("car_charge"));
                            distance.setText(getResources().getString(R.string.distance)+"(" + jsonObject1.getString("miles") + " km)");
                            time_tv.setText(getResources().getString(R.string.time1)+"(" + jsonObject1.getString("perMin") + " min)");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");

                            payment_type_str=jsonObject2.getString("payment_type");
                            if (jsonObject2.getString("payment_type").equalsIgnoreCase("Cash")){
                                payment_type.setText(getResources().getString(R.string.paytype)+" "+getResources().getString(R.string.cash));
                                total_fare.setVisibility(View.GONE);
                                total_fare_et.setVisibility(View.VISIBLE);
                            }

                            else if (jsonObject2.getString("payment_type").equalsIgnoreCase("Corporate")){
                                payment_type.setText(getResources().getString(R.string.paytype)+" "+getResources().getString(R.string.corporate));
                                total_fare.setVisibility(View.GONE);
                                total_fare_et.setVisibility(View.VISIBLE);
                                user_secret_pin.setVisibility(View.VISIBLE);
                            }
                            else {
                                total_fare.setVisibility(View.VISIBLE);
                                total_fare_et.setVisibility(View.GONE);
                                payment_type.setText(getResources().getString(R.string.paytype)+" "+jsonObject2.getString("payment_type"));
                            }

                            if (jsonObject2.getString("payment_type").equalsIgnoreCase("Card")){
                               paymentmessage.setText(getResources().getString(R.string.colectmoneyfromrider));
                            }
                            else if (jsonObject2.getString("payment_type").equalsIgnoreCase("Wallet")){
                                paymentmessage.setText(getResources().getString(R.string.amountaddwallet));

                            }
                            else {
                                paymentmessage.setText(getResources().getString(R.string.colectmoneyfromrider));

                            }
                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject2.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                date_tv.setText(""+strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void PaymentSendByDriver1(String payment_typesss) {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(PaymentAct.this);
        alertDialog.setTitle("Payment");
        alertDialog.setMessage("Payment give by passenger");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
            }
        });
        alertDialog.show();
    }

    private void PaymentSendByDriver(String payment_types) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(PaymentAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.payment_recieved_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        TextView message_tv =  canceldialog.findViewById(R.id.message_tv);
        if (payment_type_str==null){
            yes_tv.setText(getResources().getString(R.string.yes));
            message_tv.setText("" + getResources().getString(R.string.recivecash));

        }
        else {
            if (payment_type_str.equalsIgnoreCase("Cash")) {
                yes_tv.setText(getResources().getString(R.string.yes));
                message_tv.setText("" + getResources().getString(R.string.recivecash));

            } else if (payment_type_str.equalsIgnoreCase("Wallet")) {
                message_tv.setText("" + getResources().getString(R.string.givebywallet));
            } else if (payment_type_str.equalsIgnoreCase("CreditCard")) {
                message_tv.setText("" + getResources().getString(R.string.givebycreditcard));
            }
        }

        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
             //   new ConfirmPayment().execute("Processing");
                canceldialog.dismiss();
            }
        });
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
               // new ConfirmPayment().execute("Confirm");
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

}
