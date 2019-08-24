package main.com.jjtaxiuser.activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.ACProgressCustom;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.paymentclasses.ConfirmPayment;
import main.com.jjtaxiuser.utils.NotificationUtils;

public class FeedbackUs extends AppCompatActivity {

    private TextView date_tv, totalamount, pickuplocation, droplocation, drivername;
    private TextView carcharge, distancefare,timefare,time_tv,distance;
    private ProgressBar progressbar;
    private ImageView driver_img,remove_tips;
    private RelativeLayout exit_app_but;
    private RatingBar ratingbar;
    private EditText comment_et;
    private Button submit;
    private String req_datetime = "",car_charge_str="",driver_id="",amount_str_main="", amount_str = "", request_id = "", comment_str = "", user_log_data = "", user_id = "";
    private float rating = 0;
    MySession mySession;
    RadioGroup paymentGroup;
    String payment_type_str = "Cash";
    float mywalletamount, rideamount;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog waitDialog;
    String time_zone="";
    ACProgressCustom ac_dialog;
    private TextView paymet_type,apply_tv,tipsamt_tv,discount_type;
    private CheckBox tips_check;
    private LinearLayout tipslay,tipsshowlay,tips_check_lay;
    String tips_amount_str = "0";
    private EditText tips_amount;
    MyLanguageSession myLanguageSession;
    private String language = "";

    //paypal code

    //end paypal code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_feedback_us);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

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
                        Log.e("KEY ACCEPT REJ", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Finish")) {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                                receivedConfirmPayment();

                            }
                        }
                        if (keyMessage.equalsIgnoreCase("your payment is denied")) {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                              //  driverDeniedPayment();

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        new GetCurrentBooking().execute();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(FeedbackUs.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();


    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(FeedbackUs.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(FeedbackUs.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(FeedbackUs.this.getApplicationContext());
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingbar.getRating();
                comment_str = comment_et.getText().toString();
                if (rating == 0) {
                    Toast.makeText(FeedbackUs.this, getResources().getString(R.string.plsgiverating), Toast.LENGTH_LONG).show();
                } /*else if (payment_type_str == null || payment_type_str.equalsIgnoreCase("")) {
                    Toast.makeText(FeedbackUs.this, "Please Select Payment Type", Toast.LENGTH_LONG).show();
                } else if (payment_type_str.equalsIgnoreCase("CreditCard")) {
                    if (amount_str!=null){
                      //  beginPayment();
                    }

                }*/ else {
                    Calendar c = Calendar.getInstance();
                    TimeZone tz = c.getTimeZone();
                    Log.e("TIME ZONE >>",tz.getDisplayName());
                    Log.e("TIME ZONE ID>>",tz.getID());
                    time_zone = tz.getID();


                    if (payment_type_str.equalsIgnoreCase("Wallet")) {

                        if (MainActivity.amount == null || MainActivity.amount.equalsIgnoreCase("")) {
                            Toast.makeText(FeedbackUs.this, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();

                        } else {
                            mywalletamount = Float.parseFloat(MainActivity.amount);
                        }
                        if (amount_str == null || amount_str.equalsIgnoreCase("")) {
                            Toast.makeText(FeedbackUs.this, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();

                        } else {
                            rideamount = Float.parseFloat(amount_str);
                        }
                        if (rideamount < mywalletamount) {

                           // new SubmitPayment().execute();
                            new GiveReviewRating().execute();
                        } else {
                            Toast.makeText(FeedbackUs.this, getResources().getString(R.string.something), Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (payment_type_str.equalsIgnoreCase("Card")){
                        if (tips_amount_str!=null&&!tips_amount_str.equalsIgnoreCase("")&&!tips_amount_str.equalsIgnoreCase("0")){
                         double amt= Double.parseDouble(amount_str);
                         double tip= Double.parseDouble(tips_amount_str);
                         double total=amt+tip;
                            amount_str = String.valueOf(total);
                        }
                        Intent i = new Intent(FeedbackUs.this,ConfirmPayment.class);
                        i.putExtra("amount_str",amount_str);
                        i.putExtra("request_id",request_id);
                        i.putExtra("car_charge_str",car_charge_str);
                        i.putExtra("rating",rating+"");
                        i.putExtra("tips_amount_str",tips_amount_str);
                        i.putExtra("comment_str",comment_str);
                        i.putExtra("time_zone",time_zone);
                        i.putExtra("transaction_type","ride_payment");
                        startActivity(i);
                    }else {
                       // new SubmitPayment().execute();
                        new GiveReviewRating().execute();
                    }

                }
            }
        });
    }

    private void idinit() {
     //   paymentGroup = (RadioGroup) findViewById(R.id.paymentGroup);
        timefare = (TextView) findViewById(R.id.timefare);
        distance = (TextView) findViewById(R.id.distance);
        time_tv = (TextView) findViewById(R.id.time_tv);
        distancefare = (TextView) findViewById(R.id.distancefare);
        carcharge = (TextView) findViewById(R.id.carcharge);

        discount_type = findViewById(R.id.discount_type);
        tipsshowlay = findViewById(R.id.tipsshowlay);
        tips_amount = findViewById(R.id.tips_amount);
        tipsamt_tv = findViewById(R.id.tipsamt_tv);
        tips_check_lay = findViewById(R.id.tips_check_lay);
        apply_tv = findViewById(R.id.apply_tv);
        remove_tips = findViewById(R.id.remove_tips);
        tips_check = findViewById(R.id.tips_check);
        tipslay = findViewById(R.id.tipslay);
        submit = (Button) findViewById(R.id.submit);
        paymet_type = (TextView) findViewById(R.id.paymet_type);
        comment_et = (EditText) findViewById(R.id.comment_et);
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        driver_img = (ImageView) findViewById(R.id.driver_img);
        date_tv = (TextView) findViewById(R.id.date_tv);
        droplocation = (TextView) findViewById(R.id.droplocation);
        drivername = (TextView) findViewById(R.id.drivername);
        pickuplocation = (TextView) findViewById(R.id.pickuplocation);
        totalamount = (TextView) findViewById(R.id.totalamount);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        tips_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tipslay.setVisibility(View.VISIBLE);
                } else {
                    tipslay.setVisibility(View.GONE);
                }
            }
        });
        remove_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips_amount_str = "0";
                tipsshowlay.setVisibility(View.GONE);
                tips_check.setChecked(false);
                tips_check_lay.setVisibility(View.VISIBLE);
                tipslay.setVisibility(View.GONE);
                amount_str=amount_str_main;
            }
        });
        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips_amount_str = tips_amount.getText().toString();
                if (tips_amount_str == null || tips_amount_str.equalsIgnoreCase("")) {

                } else {
                    tipsamt_tv.setText("$" + tips_amount_str);
                    tipsshowlay.setVisibility(View.VISIBLE);
                    tips_check_lay.setVisibility(View.GONE);

                }
            }
        });
    }

    private class GetPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
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
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_payment?request_id=1
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            amount_str = jsonObject1.getString("total");
                            amount_str_main = jsonObject1.getString("total");
                            car_charge_str = String.valueOf(jsonObject1.getString("car_charge"));
                            totalamount.setText("Total :" + "$ " + jsonObject1.getString("total"));
                            discount_type.setText(getResources().getString(R.string.discountapplied) + "$ " + jsonObject1.getString("discount"));
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");
                            pickuplocation.setText("" + jsonObject2.getString("picuplocation"));
                            droplocation.setText("" + jsonObject2.getString("dropofflocation"));
                            timefare.setText("$" + jsonObject1.getString("per_min_charge"));
                            // basefare.setText("" + "$ " + jsonObject1.getInt("base_fare"));
                            carcharge.setText("" + "$ " + jsonObject1.getInt("car_charge"));
                            distance.setText(getResources().getString(R.string.distance)+"(" + jsonObject1.getString("miles") + " km)");
                            time_tv.setText(getResources().getString(R.string.time1)+"(" + jsonObject1.getString("perMin") + " min)");
                            distancefare.setText("$" + jsonObject1.getString("per_miles_charge"));



                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private class GiveReviewRating extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
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
            try {
//  hitchride.net/webservice/add_rating_review?


                String postReceiverUrl = BaseUrl.baseurl + "add_rating_review?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                params.put("user_id", user_id);
                params.put("driver_id", driver_id);
                params.put("rating", rating);

                params.put("timezone", time_zone);
                if (comment_str == null) {
                    params.put("review", "");
                } else {
                    params.put("review", comment_str);
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
            // progressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                // waitDriverAccept();
                //   finish();
                Intent i = new Intent(FeedbackUs.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);


            }
        }
    }




    private class SubmitPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
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
            try {
//http://hitchride.net/webservice/add_payment?request_id=44&amount=13.55&car_charge=5&rating=4&review=nice
                String postReceiverUrl = BaseUrl.baseurl + "add_payment?";
                Log.e("ADD PAYMENT"," .. "+postReceiverUrl+"request_id="+request_id+"&amount="+amount_str+"&car_charge="+car_charge_str+"&rating="+rating+"&tip=0&timezone="+time_zone+"&review="+comment_str);
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                params.put("amount", amount_str);
                params.put("car_charge", car_charge_str);
                params.put("rating", rating);
                params.put("tip", tips_amount_str);
                params.put("timezone", time_zone);
                if (comment_str == null) {
                    params.put("review", "");
                } else {
                    params.put("review", comment_str);
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
            // progressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
               // waitDriverAccept();
                //   finish();
                Intent i = new Intent(FeedbackUs.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);


            }
        }
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   progressbar.setVisibility(View.VISIBLE);
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
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_current_booking?user_id=1
                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("User id,", "" + user_id);
                params.put("user_id", user_id);
                params.put("timezone", time_zone);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("Resposne in my Booking", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            driver_id = jsonObject1.getString("driver_id");
                            req_datetime = jsonObject1.getString("req_datetime");
                            payment_type_str = jsonObject1.getString("payment_type");
                            if (payment_type_str.equalsIgnoreCase("Cash")){
                                paymet_type.setText(getResources().getString(R.string.paytype)+" "+getResources().getString(R.string.cash));
                            }
                            else {
                                paymet_type.setText(getResources().getString(R.string.paytype)+" "+payment_type_str);

                            }

                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(req_datetime);
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                date_tv.setText("" + strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String Status = jsonObject1.getString("status");
                            String pay_status = jsonObject1.getString("pay_status");

                            if (Status.equalsIgnoreCase("End") && pay_status.equalsIgnoreCase("Processing")){
                                waitDriverAccept();

                            }

                            JSONArray jsonArray1 = jsonObject1.getJSONArray("driver_details");
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                drivername.setText("" + jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name"));
                                if (jsonObject2.getString("profile_image") == null || jsonObject2.getString("profile_image").equalsIgnoreCase("") || jsonObject2.getString("profile_image").equalsIgnoreCase("http://mobileappdevelop.co/NAXCAN/uploads/images/")) {

                                } else {
                                  //  Picasso.with(FeedbackUs.this).load(jsonObject2.getString("profile_image")).into(driver_img);

                                }

                            }
                        }

                        new GetPayment().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void waitDriverAccept() {


        //   Log.e("War Msg in dialog", war_msg);
        waitDialog = new Dialog(FeedbackUs.this);
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setCancelable(false);
        waitDialog.setContentView(R.layout.wait_driver_accept);
        waitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        waitDialog.show();


    }

    private void receivedConfirmPayment() {
        final Dialog recdialog = new Dialog(FeedbackUs.this);
        recdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        recdialog.setCancelable(false);
        recdialog.setContentView(R.layout.paymentconfirm_lay);
        recdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes = (TextView) recdialog.findViewById(R.id.yes_tv);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recdialog.dismiss();
                //finish();
                Intent i = new Intent(FeedbackUs.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        recdialog.show();


    }



}
/*
Go Add favourite location screen on click of myjob location marker


*/