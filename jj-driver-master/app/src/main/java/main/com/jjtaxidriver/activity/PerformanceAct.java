package main.com.jjtaxidriver.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.constant.ACProgressCustom;
import main.com.jjtaxidriver.constant.BaseUrl;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.constant.MySession;

public class PerformanceAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private LinearLayout today_lay,week_lay,month_lay;
    private LinearLayout today_data_lay,week_data_lay,month_data_lay;
    private TextView today_tv,week_tv,month_tv;
    private View todayview,weekview,monthview;
    private TextView month_total_ride_count,month_total_earning_tv,month_acceptride_tv,monthcanceled_ride,week_cancel_ride_tv,week_total_accet_ride,week_total_ear_tv,todaycancelride_tv,todayacceptride_tv,today_total_earning_tv,today_total_ride,weektotal_ride_tv;
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
        setContentView(R.layout.activity_performance);
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


                    // amount = jsonObject1.getString("amount");
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

        today_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                week_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                today_tv.setTextColor(getResources().getColor(R.color.darktextcol));

                todayview.setBackgroundResource(R.color.darktextcol);
                weekview.setBackgroundResource(R.color.darkgrey);
                monthview.setBackgroundResource(R.color.darkgrey);

                today_data_lay.setVisibility(View.VISIBLE);
                week_data_lay.setVisibility(View.GONE);
                month_data_lay.setVisibility(View.GONE);
            }
        });
        week_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                week_tv.setTextColor(getResources().getColor(R.color.darktextcol));
                today_tv.setTextColor(getResources().getColor(R.color.darkgrey));

                todayview.setBackgroundResource(R.color.darkgrey);
                weekview.setBackgroundResource(R.color.darktextcol);
                monthview.setBackgroundResource(R.color.darkgrey);

                today_data_lay.setVisibility(View.GONE);
                week_data_lay.setVisibility(View.VISIBLE);
                month_data_lay.setVisibility(View.GONE);
            }
        });
        month_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month_tv.setTextColor(getResources().getColor(R.color.darktextcol));
                week_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                today_tv.setTextColor(getResources().getColor(R.color.darkgrey));

                todayview.setBackgroundResource(R.color.darkgrey);
                weekview.setBackgroundResource(R.color.darkgrey);
                monthview.setBackgroundResource(R.color.darktextcol);

                today_data_lay.setVisibility(View.GONE);
                week_data_lay.setVisibility(View.GONE);
                month_data_lay.setVisibility(View.VISIBLE);
            }
        });

    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
        today_lay = findViewById(R.id.today_lay);
        week_lay = findViewById(R.id.week_lay);
        month_lay = findViewById(R.id.month_lay);
        month_tv = findViewById(R.id.month_tv);
        week_tv = findViewById(R.id.week_tv);
        today_tv = findViewById(R.id.today_tv);
        todayview = findViewById(R.id.todayview);
        weekview = findViewById(R.id.weekview);
        monthview = findViewById(R.id.monthview);

        todaycancelride_tv = findViewById(R.id.todaycancelride_tv);
        todayacceptride_tv = findViewById(R.id.todayacceptride_tv);
        today_total_earning_tv = findViewById(R.id.today_total_earning_tv);
        today_total_ride = findViewById(R.id.today_total_ride);
        today_data_lay = findViewById(R.id.today_data_lay);

        week_data_lay = findViewById(R.id.week_data_lay);
        month_data_lay = findViewById(R.id.month_data_lay);
        weektotal_ride_tv = findViewById(R.id.weektotal_ride_tv);
        week_total_ear_tv = findViewById(R.id.week_total_ear_tv);
        week_total_accet_ride = findViewById(R.id.week_total_accet_ride);
        week_cancel_ride_tv = findViewById(R.id.week_cancel_ride_tv);

        monthcanceled_ride = findViewById(R.id.monthcanceled_ride);
        month_acceptride_tv = findViewById(R.id.month_acceptride_tv);
        month_total_earning_tv = findViewById(R.id.month_total_earning_tv);
        month_total_ride_count = findViewById(R.id.month_total_ride_count);
        new GetPerformanceAsc().execute();
    }


    private class GetPerformanceAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  prgressbar.setVisibility(View.VISIBLE);
/*
            if (ac_dialog != null) {
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
//http://hitchride.net/webservice/get_driver_statement?user_id=22
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_driver_statement?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
             //   params.put("request_id", strings[0]);


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
                Log.e("RideDetail Response", ">>>>>>>>>>>>" + response);
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
            //  prgressbar.setVisibility(View.GONE);
/*
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }
*/

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("status");
                    if (msg.equalsIgnoreCase("1")) {
                        JSONObject jsonArray = jsonObject.getJSONObject("result");
                        JSONObject dailyobj = jsonArray.getJSONObject("daily");
                        today_total_earning_tv.setText("$ "+dailyobj.getString("earning"));
                        today_total_ride.setText(""+dailyobj.getString("total"));
                        todayacceptride_tv.setText(""+dailyobj.getString("accepted_rides")+" %");
                        todaycancelride_tv.setText(""+dailyobj.getString("cancel_rides")+" %");
                        JSONObject weekobj = jsonArray.getJSONObject("week");
                        week_total_ear_tv.setText("$ "+weekobj.getString("earning"));
                        weektotal_ride_tv.setText(""+weekobj.getString("total"));
                        week_total_accet_ride.setText(""+weekobj.getString("accepted_rides")+" %");
                        week_cancel_ride_tv.setText(""+weekobj.getString("cancel_rides")+" %");

                        JSONObject monthobj = jsonArray.getJSONObject("month");
                        month_total_earning_tv.setText("$ "+monthobj.getString("earning"));
                        month_total_ride_count.setText(""+monthobj.getString("total"));
                        month_acceptride_tv.setText(""+monthobj.getString("accepted_rides")+" %");
                        monthcanceled_ride.setText(""+monthobj.getString("cancel_rides")+" %");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
