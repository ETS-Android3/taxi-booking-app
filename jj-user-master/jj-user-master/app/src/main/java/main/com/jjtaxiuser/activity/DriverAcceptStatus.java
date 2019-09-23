package main.com.jjtaxiuser.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.ACProgressCustom;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.GPSTracker;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.draglocation.DataParser;
import main.com.jjtaxiuser.multipledroppoint.SelectRouteWithCollectiveRide;
import main.com.jjtaxiuser.utils.NotificationUtils;
import main.com.jjtaxiuser.utils.Pinter;

public class DriverAcceptStatus extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    SupportMapFragment mapFragment;
    private RelativeLayout exit_app_but,updatedestinaton;
    private LinearLayout driver_det_lay;
    private ImageView message_lay;
    private TextView pickuplocation,dropofflocation,drivername,carnumber,carname,carbrand;
    private ImageView carimage;
    private CircleImageView driver_imag;
    LatLng picklatLng, droplatlong;
    ACProgressCustom ac_dialog;
    MySession mySession;
    String user_log_data="",user_id="",request_id="",car_type_id="",mobile="",driver_id="";
    public static double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    LatLng  driverlatlng,old_driver_lat;
    Marker driver_marker;
    final Timer timer = new Timer();
    private TextView streatnumber,addpoint,driver_sts,driver_status_sec,timeaway,rating_tv;
    private String driver_detail_str="",pickup_str="",dropoff_str="",driver_img_str="",driver_name_str="",car_detail_str="";
    public static String fav_status="";
    BroadcastReceiver mRegistrationBroadcastReceiver;
    MarkerOptions pickmarkerpoint;
    private ImageView share_img,calllay;
    private MarkerOptions options = new MarkerOptions();
    Marker marker;
    MyLanguageSession myLanguageSession;
    private String language = "",old_sts="";
    float start_rotation ;
    private boolean IsVisible=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_driver_accept_status);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        idint();
        clickevent();
        checkGps();
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        //(keyMessage.equalsIgnoreCase("new shared user"))
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {

                            request_id = data.getString("request_id");
                            requestCancel();
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Arrived")) {
                           // status_map = "Arrived";

                            request_id = data.getString("request_id");
                            driver_status_sec.setText("" + getResources().getString(R.string.yourdriverarr));
                            driver_sts.setText("" + getResources().getString(R.string.driverarrived));
                            driverisArrived();
                            timeaway.setVisibility(View.GONE);
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Start")) {

                            // status_map = "Start";
                            request_id = data.getString("request_id");
                            //titletext.setText("" + "Trip is Started");
                            driver_status_sec.setText("" + getResources().getString(R.string.enjoyride));
                            driver_sts.setText("" + getResources().getString(R.string.onride));

                            tripStarted();
                            timeaway.setVisibility(View.GONE);
                        } else if (keyMessage.equalsIgnoreCase("your booking request is End")) {

                            Intent i = new Intent(DriverAcceptStatus.this, FeedbackUs.class);
                            startActivity(i);
                            finish();
                        } else if (keyMessage.equalsIgnoreCase("your ride is Finish")) {

                            tripFinish();
                        } else if (keyMessage.equalsIgnoreCase("your booking request is assign to new driver")) {

                            request_id = data.getString("request_id");
                           // requestReassign();
                        }

                        else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {

                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                          //  bookedRequestAlert(picklaterdate,picklatertime);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        IsVisible = true;
        new GetCurrentBooking().execute();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }

        if (UpdateLocation.update_sts==1){
            new GetCurrentBooking().execute();
            UpdateLocation.update_sts=0;
        }
        if (SelectRouteWithCollectiveRide.add_drop_sts==1){
            new GetCurrentBooking().execute();
            SelectRouteWithCollectiveRide.add_drop_sts=0;
        }
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(DriverAcceptStatus.this.getApplicationContext());

    }
    private void tripStarted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay1);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv5);

        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg1);
        bodymsg.setText(""+getResources().getString(R.string.tripstarted));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                new GetCurrentBooking().execute();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }


            }
        });
        canceldialog.show();


    }
    private void tripFinish() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay2);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv6);

        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg2);
        bodymsg.setText(""+getResources().getString(R.string.yourridefinish));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                Intent i = new Intent(DriverAcceptStatus.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });
        canceldialog.show();


    }

    private void driverisArrived() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv4);

        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg);
        bodymsg.setText(""+getResources().getString(R.string.yourdriverarr));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                new GetCurrentBooking().execute();

                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }


            }
        });
        canceldialog.show();


    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        IsVisible = false;
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();


        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }


    }
    private void initilizeMap() {
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(DriverAcceptStatus.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverAcceptStatus.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
/*
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        TripStatusAct.this, R.raw.stylemap_3));
*/
        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng;
        MarkerOptions marker;
        if (latitude == 0.0) {
            latLng = new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str);
            marker = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).flat(true).anchor(0.5f, 0.5f);

        } else {
            latLng = new LatLng(latitude, longitude);
            marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        }
        gMap.addMarker(marker);
        MarkerOptions myloc = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).flat(true).anchor(0.5f, 0.5f);
        MarkerOptions myjob = new MarkerOptions().position(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str)).flat(true).anchor(0.5f, 0.5f);
        CameraUpdate center;
        if (latitude == 0.0) {
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), 14);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.animateCamera(zoom);

        } else {
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.animateCamera(zoom);

        }
        gMap.addMarker(myjob);
        gMap.moveCamera(center);


    }
    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  finish();
                toCancelRide();
            }
        });driver_det_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this,DriverDetailAct.class);
                i.putExtra("driver_detail_str",driver_detail_str);
                i.putExtra("driver_id",driver_id);
                i.putExtra("pickup_str",pickup_str);
                i.putExtra("dropoff_str",dropoff_str);
                i.putExtra("fav_status",fav_status);
                i.putExtra("request_id",request_id);
                startActivity(i);
            }
        });message_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagePop();
                Intent i = new Intent(DriverAcceptStatus.this, ChatingAct.class);
                i.putExtra("receiver_id", driver_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", driver_img_str);
                i.putExtra("receiver_name", driver_name_str);
                i.putExtra("block_status", "");
                startActivity(i);
            }
        });
        updatedestinaton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this, UpdateLocation.class);
                i.putExtra("request_id", request_id);
                startActivity(i);
            }
        });
        addpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this, SelectRouteWithCollectiveRide.class);
                i.putExtra("request_id", request_id);
                startActivity(i);
            }
        });
        share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        calllay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile == null || mobile.equalsIgnoreCase("")) {

                } else {
                    if (ActivityCompat.checkSelfPermission(DriverAcceptStatus.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
                    startActivity(callIntent);

                }
            }
        });
    }
    private void toCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResponseToRequest().execute();
                canceldialog.dismiss();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        canceldialog.show();


    }

    private void idint() {

        streatnumber = findViewById(R.id.streatnumber);
        addpoint = findViewById(R.id.addpoint);
        calllay = findViewById(R.id.calllay);
        share_img = findViewById(R.id.share_img);
        updatedestinaton = findViewById(R.id.updatedestinaton);
        rating_tv = findViewById(R.id.rating_tv);
        driver_status_sec = findViewById(R.id.driver_status_sec);
        driver_sts = findViewById(R.id.driver_sts);
        driver_imag = findViewById(R.id.driver_imag);
        timeaway = findViewById(R.id.timeaway);
        carbrand = findViewById(R.id.carbrand);
        carname = findViewById(R.id.carname);
        carimage = findViewById(R.id.carimage);
        carnumber = findViewById(R.id.carnumber);
        drivername = findViewById(R.id.drivername);
        dropofflocation = findViewById(R.id.dropofflocation);
        pickuplocation = findViewById(R.id.pickuplocation);
        exit_app_but = findViewById(R.id.exit_app_but);
        message_lay = findViewById(R.id.message_lay);
        driver_det_lay = findViewById(R.id.driver_det_lay);
    }
    private void messagePop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(DriverAcceptStatus.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_click_sendmsg);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView reply_tv = (TextView) dialogSts.findViewById(R.id.reply_tv);
        TextView driver_name = (TextView) dialogSts.findViewById(R.id.driver_name);
        TextView car_detial = (TextView) dialogSts.findViewById(R.id.car_detial);
        TextView close_tv = (TextView) dialogSts.findViewById(R.id.close_tv);
        CircleImageView driver_img = (CircleImageView) dialogSts.findViewById(R.id.driver_img);
        driver_name.setText(""+driver_name_str);
        car_detial.setText(""+car_detail_str);
        if (driver_img_str!=null&&!driver_img_str.equalsIgnoreCase("")){
            Picasso.with(DriverAcceptStatus.this).load(driver_img_str).into(driver_img);

        }

        reply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });close_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        dialogSts.show();


    }
    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                if (IsVisible){
                    ac_dialog.show();
                }

            }

            if (gMap!=null){
                gMap.clear();
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

                params.put("user_id", user_id);
                params.put("type", "USER");
                params.put("timezone", MainActivity.time_zone);

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

            if (result == null) {
                if(ac_dialog!=null){
                    if (IsVisible){
                        ac_dialog.dismiss();
                    }

                }

            } else if (result.isEmpty()) {
                if(ac_dialog!=null){
                    if (IsVisible){
                        ac_dialog.dismiss();
                    }

                }

            } else {
                try {
                    if(ac_dialog!=null){
                        if (IsVisible){
                            ac_dialog.dismiss();
                        }

                    }

                    Log.e("Resposne in my Booking", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        fav_status = jsonObject.getString("fav_status");
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            car_type_id = jsonObject1.getString("car_type_id");
                            pickuplocation.setText(""+jsonObject1.getString("picuplocation"));
                            dropofflocation.setText(""+jsonObject1.getString("dropofflocation"));
                            streatnumber.setText("" + jsonObject1.getString("street_no"));

                            pickup_str = jsonObject1.getString("picuplocation");
                            dropoff_str = jsonObject1.getString("dropofflocation");
                            String Status = jsonObject1.getString("status");
                            old_sts = jsonObject1.getString("status");
                            if (Status == null || Status.equalsIgnoreCase("")) {
                            } else {
                            }
                            if (Status.equalsIgnoreCase("Accept")) {
                               // status_map = "Accept";
                            } else if (Status.equalsIgnoreCase("Arrived")) {
                               // status_map = "Arrived";
                                timeaway.setVisibility(View.GONE);
                                driver_status_sec.setText("" + getResources().getString(R.string.ddriverhasarr));
                                driver_sts.setText("" + getResources().getString(R.string.drivarr));                                // driverisArrived();
                            } else if (Status.equalsIgnoreCase("Start")) {
                              //  status_map = "Start";
                                timeaway.setVisibility(View.GONE);
                                driver_status_sec.setText("" + getResources().getString(R.string.enjoyride));
                                driver_sts.setText("" + getResources().getString(R.string.onride));                                //  tripStarted();
                            } else if (Status.equalsIgnoreCase("End")) {
                                Intent l = new Intent(DriverAcceptStatus.this, FeedbackUs.class);
                                startActivity(l);
                                finish();
                            }

                            JSONArray jsonArray1 = jsonObject1.getJSONArray("driver_details");
                            if(jsonArray1!=null||jsonArray1.length()!=0){
                                driver_detail_str  =jsonArray1.toString();
                            }
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(k);

                                driver_name_str=jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                car_detail_str= jsonObject2.getString("car_model") + "\n" + jsonObject2.getString("car_number").trim() + " , " + jsonObject2.getString("car_color");
                                carname.setText("" + jsonObject2.getString("car_model"));
                                drivername.setText("" + jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name"));
                                mobile = jsonObject2.getString("mobile");
                                driver_id = jsonObject2.getString("id");
                                carnumber.setText("" + jsonObject2.getString("car_number"));
                                rating_tv.setText("" + jsonObject2.getString("rating"));

                                if (jsonObject2.getString("lat") == null || jsonObject2.getString("lat").equalsIgnoreCase("")) {

                                } else {
                                    driver_lat = Double.parseDouble(jsonObject2.getString("lat"));
                                    driver_lon = Double.parseDouble(jsonObject2.getString("lon"));
                                    driverlatlng = new LatLng(driver_lat, driver_lon);
                                    old_driver_lat = new LatLng(driver_lat, driver_lon);
                                }
                                //"http://mobileappdevelop.co/NAXCAN/uploads/images/"
                                if (jsonObject2.getString("profile_image") == null || jsonObject2.getString("profile_image").equalsIgnoreCase("") || jsonObject2.getString("profile_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                                } else {
                                   driver_img_str= jsonObject2.getString("profile_image");
                                    Picasso.with(DriverAcceptStatus.this).load(jsonObject2.getString("profile_image")).placeholder(R.drawable.user).into(driver_imag);

                                }
                                if (jsonObject2.getString("car_image") == null || jsonObject2.getString("car_image").equalsIgnoreCase("") || jsonObject2.getString("car_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                                } else {
                                    Picasso.with(DriverAcceptStatus.this).load(jsonObject2.getString("car_image")).into(carimage);

                                }

                            }
                            if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                            } else {

                                pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                if (gMap == null) {
                                    Log.e("Come Map Null", "");
                                } else {
                                    JSONArray jsonArray2 = jsonObject.getJSONArray("booking_dropoff");
                                    for (int ii =0;ii<jsonArray2.length();ii++){
                                        JSONObject jsonObject2 = jsonArray2.getJSONObject(ii);
                                        if (jsonObject2.getString("droplon")!=null&&!jsonObject2.getString("droplon").equalsIgnoreCase("")){
                                            double droppoint_lat = Double.parseDouble(jsonObject2.getString("droplat"));
                                            double droppoint_lon = Double.parseDouble(jsonObject2.getString("droplon"));
                                            LatLng latLng = new LatLng(droppoint_lat, droppoint_lon);
                                            options.position(latLng);
                                            options.title("" + jsonObject2.getString("dropofflocation"));
                                            marker = gMap.addMarker(options);
                                            marker.setIcon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_azul));

                                        }

                                    }


                                   /* MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Pickup"))).flat(true).anchor(0.5f, 0.5f).title("Pickup: "+jsonObject1.getString("picuplocation"));
                                    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Destination"))).flat(true).anchor(0.5f, 0.5f).title("Dropoff: "+jsonObject1.getString("dropofflocation"));
*/
                                    MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_rojo)).flat(true).anchor(0.5f, 0.5f);
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                                  /*  CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                    gMap.animateCamera(zoom);*/
                                    MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).flat(true).anchor(0.5f, 0.5f);

                                    driver_marker = gMap.addMarker(markers1);
                                    driver_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));


                                    gMap.addMarker(markers);

                                    if (jsonObject1.getString("dropofflocation")!=null&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("")&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("null")){
                                        drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                        drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));

                                        MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_azul)).flat(true).anchor(0.5f, 0.5f);
                                        gMap.addMarker(marker2);
                                    }





                                    gMap.moveCamera(center);
                                    timer.schedule(new TimerTask() {
                                        public void run() {
                                            System.out.println("-------------runing-------------");
                                            UpdateDriverLoction task = new UpdateDriverLoction();
                                            task.execute();
                                        }
                                    }, 0, 15000);

                                    // gMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                                    Log.e("Come Map True", "" + pic_lat);
                                    if (Status!=null&&!Status.equalsIgnoreCase("Accept")){
                                        if (jsonObject1.getString("dropofflocation")!=null&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("")&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("null")){
                                            String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                            FetchUrl FetchUrl = new FetchUrl();
                                            FetchUrl.execute(url, "first");
                                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                    .include(new LatLng(pic_lat, pick_lon))
                                                    .include(new LatLng(drop_lat, drop_lon))
                                                    .build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int padding = (int) (width * 0); // offset from edges of the map 12% of screen
                                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                                        }

                                    }
                                    else {
                                        if (driver_lat!=0){
                                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                    .include(new LatLng(driver_lat, driver_lon))
                                                    .include(new LatLng(pic_lat, pick_lon))
                                                    .build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int padding = (int) (width * 0); // offset from edges of the map 12% of screen
                                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                                        }

                                    }

                                    // }
                                }
                            }



                        }


                    }
                    else {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    animation_list.add(position);
                    if (j == 0) {
                        picklatLng = position;
                    }
                    if (j == (path.size() - 1)) {
                        droplatlong = position;
                    }

                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
                MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_rojo)).flat(true).anchor(0.5f, 0.5f);
                MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_azul)).flat(true).anchor(0.5f, 0.5f);
                gMap.addMarker(pick);
                gMap.addMarker(drop);

            } else {

            }
        }
    }

    private class UpdateDriverLoction extends AsyncTask<Void, Void, Void> {
        @TargetApi(Build.VERSION_CODES.FROYO)
        @SuppressWarnings({})
        protected Void doInBackground(Void... params) {

            return null;
        }

        protected void onPostExecute(Void paramVoid) {
            driverlatlng = getDriverLocation(driver_id);
            //driverlatlng = getDriverLocationFromRem(driver_id);
            Log.e("Driver lat user side >",""+driverlatlng);

            if (driver_marker!=null){
                if (driverlatlng!=null){

                    if (!driver_marker.isVisible()){
                        driver_marker.setVisible(true);
                    }

                    if (old_driver_lat!=null){
                        Log.e("OLD LAT",""+old_driver_lat);

                        if (old_driver_lat!=driverlatlng){
                         //   LatLng newlats2 = new LatLng(22.7028, 75.8716);
                          //  LatLng newlats = new LatLng(22.6983, 75.8649);
                           // driver_marker.setPosition(driverlatlng);
                            double distance_difference = distFrom(old_driver_lat.latitude, old_driver_lat.longitude, driverlatlng.latitude, driverlatlng.longitude);
                          //  double distance_difference = distFrom(newlats2.latitude, newlats2.longitude, newlats.latitude, newlats.longitude);
                            Log.e("distance_difference >> "," >>"+distance_difference);

                            if (distance_difference > 0.1) {
                                Log.e("distance_ else>> "," >>"+distance_difference);
                                moveVechile(driver_marker,driverlatlng);
                                rotateMarker(driver_marker, getBearing(old_driver_lat, driverlatlng), start_rotation);
                                gMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                        (new CameraPosition.Builder().target(driverlatlng)
                                                .zoom(16.5f).build()));

                            }

                           /* driver_marker.setAnchor(0.5f, 0.5f);
                            driver_marker.setRotation(getBearing(old_driver_lat, driverlatlng));
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(driverlatlng)
                                            .zoom(15.5f).build()));*/
                        }



                    }

                    old_driver_lat = driverlatlng;


                }

            }

            // animateMarker(driver_marker, driverlatlng, false);
        }

        protected void onPreExecute() {
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public LatLng getDriverLocation(String driver_id) {
        System.out.println("driver_id ::: " + driver_id);
        LatLng latlng = null;
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams category = new RequestParams();
//http://hitchride.net/webservice/get_distance_time?user_id=1&request_id=15
        category.put("user_id", user_id);
      //  category.put("type", "DRIVER");
        category.put("request_id", request_id);
        //

        System.err.println(category);
        client.post(BaseUrl.baseurl + "get_distance_time?", category, new JsonHttpResponseHandler() {

            public void onFailure(int statusCode, PreferenceActivity.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.badconn), Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                System.out.println("response ::: " + response);
                try {
                    if (response.getString("status").equals("1")) {
                      // JSONObject responsed = response.getJSONObject("result");
                        if (response.getString("driver_lat")==null||response.getString("driver_lat")=="null"||response.getString("driver_lon")=="null"||response.getString("driver_lon").equalsIgnoreCase("")){

                        }
                        else {
                            Double lat = Double.parseDouble(response.getString("driver_lat"));
                            Double lng = Double.parseDouble(response.getString("driver_lon"));
                            driverlatlng = new LatLng(lat, lng);

                        }

                        JSONArray jsonArray = response.getJSONArray("result");
                        Log.e("COME IN LOOP DOWN","");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.e("COME IN LOOP","");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);




                            String my_booking = jsonObject1.getString("my_booking");
                            if (my_booking.equalsIgnoreCase("yes")) {
                                String distance = jsonObject1.getString("distance");
                                String minaway = String.valueOf(jsonObject1.getInt("min_way"));
                                //  pickmarkerpoint.icon(BitmapDescriptorFactory.fromBitmap(distanceMarker(minaway+"/n"+"min")));

                                if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("droplat") == null) {

                                } else {
                                    double plat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                    double plon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                    double dlat = Double.parseDouble(jsonObject1.getString("droplat"));
                                    double dlon = Double.parseDouble(jsonObject1.getString("droplon"));
                                    LatLng latLng = new LatLng(plat, plon);
                                    if (gMap == null) {

                                    } else {
                                        try {
                                            timeaway.setText(""+minaway+getResources().getString(R.string.minaway));
                                            pickmarkerpoint = new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(distanceMarker(""+minaway + "\n" + "min")));
                                            gMap.addMarker(pickmarkerpoint);
                                        }catch (Exception e){

                                        }


                                    }
                                }

                                break;
                            }





                        }


                        System.out.println("----------latlong---------------- " + driverlatlng);
                    } else {
                        Log.e("CANCEL COMPLETION : ", response.toString());
//                        Toast.makeText(getApplicationContext(), "Something is Wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return driverlatlng;
    }
    private void requestCancel() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay0);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView done_tv = (TextView) canceldialog.findViewById(R.id.done_tv);
        done_tv.setText(""+getResources().getString(R.string.ok));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                finish();
            }
        });
        canceldialog.show();


    }
    private Bitmap distanceMarker(String s) {
        View markerLayout = getLayoutInflater().inflate(R.layout.distance_marker, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.distancemarker);
        markerRating.setText(s);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }
    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
//http://mobileappdevelop.co/NAXCAN/webservice/users_Cancel_request?request_id=1&status=Cancel
            try {
                String postReceiverUrl = BaseUrl.baseurl + "users_Cancel_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                params.put("status", "Cancel");
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
                Log.e("Json Cnacel By user", ">>>>>>>>>>>>" + response);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                //finish();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")){
                    if (jsonObject.getString("result").equalsIgnoreCase("sorry this ride is ended")){
                        rideCompleted();
                    }
                    }
                    else {
                        Intent i = new Intent(DriverAcceptStatus.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void rideCompleted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverAcceptStatus.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
/*
        canceldialog.setContentView(R.layout.booking_success_lay);
*/
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView done_tv = (TextView) canceldialog.findViewById(R.id.done_tv);
        TextView message = (TextView) canceldialog.findViewById(R.id.message);
        TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        heading_tv.setText(""+getResources().getString(R.string.rideiscompleted));
        message.setText(""+getResources().getString(R.string.youcannotcancelride));
        done_tv.setOnClickListener(new View.OnClickListener() {
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

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }




    public void moveVechile(final Marker myMarker, final LatLng finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }


    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }







    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist * 1.60934;
    }


}
