package main.com.jjtaxidriver.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.jjtaxidriver.MainActivity;
import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.app.Config;
import main.com.jjtaxidriver.constant.ACProgressCustom;
import main.com.jjtaxidriver.constant.BaseUrl;
import main.com.jjtaxidriver.constant.DataParser;
import main.com.jjtaxidriver.constant.GPSTracker;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.constant.MySession;
import main.com.jjtaxidriver.utils.NotificationUtils;

public class TripStatusAct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    Toolbar toolbar;
    private double longitude = 0.0, latitude = 0.0;
    MySession mySession;
    //new code location chekc
    protected static final String TAG = "MainActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Location mCurrentLocation;
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;

    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    Marker drivermarker;
    GPSTracker gps;
    private RelativeLayout exit_app_but;
    private double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    ACProgressCustom ac_dialog;
    private String dropofflocation_str="",Status_Chk = "", usermobile_str = "", userimage_str = "", username_str = "", Status = "", user_log_data = "", user_id = "", time_zone = "", request_id = "";
    private TextView streatnumber,location_tv, sts_text, tripsts_but;
    LatLng startlatlong,old_driver_lat;
    final Timer timer = new Timer();
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private LinearLayout botumlay;
    private MarkerOptions options = new MarkerOptions();
    Marker marker;
    private ImageView navigate;
    MyLanguageSession myLanguageSession;
    private String language = "",booking_user_id="";
    // timer code
    LinearLayout timer_lay;
    TextView timer_time_tv, timer_status;
    ImageView timer_image;
    Handler handler;
    Runnable task;
    String Timer_STS_STR = "STOP",timezone="";
    private long startTime, start_time_mills, end_time_mills, millis, hours, mins, secs;
private boolean isVisible= true;
    //end timer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_trip_status);

        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>", tz.getDisplayName());
        Log.e("TIME ZONE ID>>", tz.getID());
        time_zone = tz.getID();
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        startTime = System.currentTimeMillis();
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySession = new MySession(this);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new Error("Can't find tool bar, did you forget to add it in Activity layout file?");
        }

        setSupportActionBar(toolbar);
      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/
        idinits();
        clcickevent();
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
                        Log.e("KEY MSG =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");
                            MainActivity.request_id = request_id;
                            usercancelRide();
                        }
                        else if (keyMessage.equalsIgnoreCase("your ride is update")) {
                            new GetCurrentBooking().execute();
                        }
                        else if (keyMessage.equalsIgnoreCase("drop point is added")) {

                            newPointAdded();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }

    private void clcickevent() {
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviGationWith();
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
                toCancelRide();
            }
        });

        timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Timer_STS_STR.equalsIgnoreCase("STOP")) {
                    startTime = System.currentTimeMillis() - millis - 1;
                    startCountingTimer();
                    Timer_STS_STR = "START";
                    timer_status.setText("" + getResources().getString(R.string.stop));

                    timer_image.setImageResource(R.drawable.ic_hold_fill);

                    new TimerAsc().execute(Timer_STS_STR);
                } else {
                    Timer_STS_STR = "STOP";
                    timer_status.setText("" + getResources().getString(R.string.start));
                    timer_image.setImageResource(R.drawable.ic_hold_blnk);

                    if (handler == null) {

                    } else {
                        handler.removeCallbacks(task);

                        new TimerAsc().execute(Timer_STS_STR);
                    }

                }


            }
        });


        tripsts_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Status >>", ">" + Status);
                if (Status.equalsIgnoreCase("Accept")) {
                    Log.e("-----------------------","");
                    sts_text.setText(getResources().getString(R.string.ontheway));
                    // startTrip();
                    areUsureEnd("arrived");
                } else if (Status.equalsIgnoreCase("Arrived")) {
                    sts_text.setText(getResources().getString(R.string.pickuppesanger));
                    // startTrip();
                    areUsureEnd("start");
                } else if (Status.equalsIgnoreCase("Start")) {
                    sts_text.setText(getResources().getString(R.string.inroute));

                    double distance_difference = distFrom(latitude, longitude, drop_lat, drop_lon);
                    if (distance_difference > 1) {
                        //distanceWarning();
                        distanceWarningPop();
                    } else {
                        //endTrip();
                        areUsureEnd("end");
                    }

                    //    endTrip();
                }
            }
        });
    }
    public void startCountingTimer() {
//                handler.removeCallbacks(task);

        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {


                millis = System.currentTimeMillis() - startTime;
                secs = millis / 1000 % 60; // seconds, 0 - 59
                mins = millis / 1000 / 60 % 60; // total seconds / 60, 0 - 59
                hours = millis / 1000 / 60 / 60; // total seconds / 3600, 0 - limitless
                String timeString = String.format("%02d:%02d:%02d", hours, mins, secs);
                timer_time_tv.setText(timeString);
                handler.postDelayed(task, 1000);

            }
        };
        task.run();
    }

    private void naviGationWith() {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_navigate_option);
        //  dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.back_pop_col)));
        TextView waze = (TextView) dialogSts.findViewById(R.id.waze);
        TextView googlemap = (TextView) dialogSts.findViewById(R.id.googlemap);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  sts = 1;
                dialogSts.dismiss();

                //employerAccept();

            }
        });
        googlemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + dropofflocation_str));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // startActivityForResult(intent,22);


            }
        });
        waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


                String uri = "https://waze.com/ul?ll=" + drop_lat + "," + drop_lon + "&navigate=yes";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));


                //String uri = "geo: latitude,longtitude";

            }
        });

        dialogSts.show();


    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    private void areUsureEnd(final String status) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        if (status.equalsIgnoreCase("end")) {
            message_tv.setText("" + getResources().getString(R.string.toendtrip));
        } else if (status.equalsIgnoreCase("arrived")) {
            message_tv.setText("" + getResources().getString(R.string.toarrived));
        } else {
            message_tv.setText("" + getResources().getString(R.string.tostarttrip));
        }

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equalsIgnoreCase("end")) {
                    Status_Chk = "End";
                    if (Timer_STS_STR.equalsIgnoreCase("START")) {
                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.plsstoptimer), Toast.LENGTH_LONG).show();
                    } else {
                        Status_Chk = "End";
                        new ResponseToRequest().execute(Status_Chk);
                        // endTrip();
                    }
                } else if (status.equalsIgnoreCase("arrived")) {
                    Status_Chk = "Arrived";
                    new ResponseToRequest().execute(Status_Chk);
                } else {
                    Status_Chk = "Start";
                    new ResponseToRequest().execute(Status_Chk);
                }




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

    private void distanceWarningPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);

        heading_tv.setText("" + getResources().getString(R.string.warning));
        message_tv.setText("" + getResources().getString(R.string.notonpoint));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        no_tv.setText("" + getResources().getString(R.string.arrived));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (Timer_STS_STR.equalsIgnoreCase("START")) {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.plsstoptimer), Toast.LENGTH_LONG).show();
                } else {
                    Status_Chk = "End";
                    new ResponseToRequest().execute(Status_Chk);
                    // endTrip();
                }


            }
        });
        canceldialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TripStatusAct.this.getApplicationContext());
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
        new GetCurrentBooking().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(TripStatusAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isVisible = false;
    }

    private void idinits() {
        timer_status = (TextView) findViewById(R.id.timer_status);
        timer_time_tv = (TextView) findViewById(R.id.timer_time_tv);
        timer_lay = (LinearLayout) findViewById(R.id.timer_lay);
        timer_image = (ImageView) findViewById(R.id.timer_image);

        streatnumber = findViewById(R.id.streatnumber);
        navigate = findViewById(R.id.navigate);
        botumlay = findViewById(R.id.botumlay);
        sts_text = findViewById(R.id.sts_text);
        exit_app_but = findViewById(R.id.exit_app_but);
        location_tv = findViewById(R.id.location_tv);
        tripsts_but = findViewById(R.id.tripsts_but);
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

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                if (isVisible){
                    ac_dialog.show();
                }

            }

            if (gMap != null) {
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
                Log.e("User Sta id,", "" + user_id);

                params.put("user_id", user_id);
                params.put("type", "DRIVER");
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
            if (ac_dialog != null) {
                if (isVisible){
                    ac_dialog.dismiss();
                }

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
                            booking_user_id = jsonObject1.getString("user_id");
                            Timer_STS_STR = jsonObject1.getString("waiting_status");
                            MainActivity.request_id = request_id;
                            JSONArray jsonArray3 = jsonObject1.getJSONArray("user_details");
                            for (int user = 0; user < jsonArray3.length(); user++) {
                                JSONObject jsonObject2 = jsonArray3.getJSONObject(user);
                                username_str = jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                // ridecrname_str = jsonObject2.getString("first_name");
                                usermobile_str = jsonObject2.getString("mobile");
                                userimage_str = jsonObject2.getString("profile_image");

                            }


                            dropofflocation_str = jsonObject1.getString("dropofflocation");
                            streatnumber.setText("" + jsonObject1.getString("street_no"));
                            location_tv.setText("" + jsonObject1.getString("picuplocation"));
                            Status = jsonObject1.getString("status");
                            if (Status == null || Status.equalsIgnoreCase("")) {
                            } else {
                            }
                            if (Status.equalsIgnoreCase("Accept")) {
                                Log.e("-----------------------","");
                                sts_text.setText(getResources().getString(R.string.picpassenger));
                                tripsts_but.setText(getResources().getString(R.string.arrived));
                                //navigate.setVisibility(View.GONE);
                                botumlay.setBackgroundResource(R.drawable.header);
                            } else if (Status.equalsIgnoreCase("Arrived")) {
                                sts_text.setText(getResources().getString(R.string.picpassenger));
                                tripsts_but.setText(getResources().getString(R.string.slidebegintrip));
                                // navigate.setVisibility(View.GONE);
                                botumlay.setBackgroundResource(R.drawable.header);
                            } else if (Status.equalsIgnoreCase("Start")) {
                                location_tv.setText("" + jsonObject1.getString("dropofflocation"));

                                // navigation_sts = 1;
                                 //navigate.setVisibility(View.VISIBLE);
                                sts_text.setText(getResources().getString(R.string.inroute));
                                tripsts_but.setText(getResources().getString(R.string.slideendtrip));
                                botumlay.setBackgroundResource(R.color.red);
                                timer_lay.setVisibility(View.VISIBLE);
                                if (Timer_STS_STR.equalsIgnoreCase("STOP")) {
                                    if (!jsonObject1.getString("waiting_cnt").equalsIgnoreCase("0")) {
                                        startTime = System.currentTimeMillis() - jsonObject1.getLong("milisecond");
                                        long endtime = jsonObject1.getLong("ed_milisecond");
                                        millis = System.currentTimeMillis() - startTime;
                                        secs = millis / 1000 % 60; // seconds, 0 - 59
                                        mins = millis / 1000 / 60 % 60; // total seconds / 60, 0 - 59
                                        hours = millis / 1000 / 60 / 60; // total seconds / 3600, 0 - limitless
                                        String timeString = String.format("%02d:%02d:%02d", hours, mins, secs);
                                        timer_time_tv.setText(timeString);
                                    }
                                    timer_status.setText("" + getResources().getString(R.string.start));
                                    timer_image.setImageResource(R.drawable.ic_hold_blnk);
                                } else {
                                    startTime = jsonObject1.getLong("st_milisecond") - jsonObject1.getLong("milisecond");
                                    startCountingTimer();
                                    timer_status.setText("" + getResources().getString(R.string.stop));
                                    timer_image.setImageResource(R.drawable.ic_hold_fill);
                                }


                            } else if (Status.equalsIgnoreCase("End")) {
                                Intent j = new Intent(TripStatusAct.this, PaymentAct.class);
                                startActivity(j);
                                finish();

                            }

                            if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                            } else {
                                pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                startlatlong = new LatLng(pic_lat, pick_lon);
                                    if (gMap == null) {
                                    Log.e("Come Map Null", "");
                                } else {

                                    MarkerOptions drmar = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
                                    LatLng latLngs = new LatLng(latitude, longitude);

                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngs, 17);
                                    drivermarker = gMap.addMarker(drmar);
                                    drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));

                                    //  MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_marker)).flat(true).anchor(0.5f, 0.5f);

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
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                        }

                                    }
                                    MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);

                                  /*  MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Pickup"))).flat(true).anchor(0.5f, 0.5f).title("Pickup: "+jsonObject1.getString("picuplocation"));
                                    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Destination"))).flat(true).anchor(0.5f, 0.5f).title("Dropoff: "+jsonObject1.getString("dropofflocation"));

*/
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                                    gMap.animateCamera(zoom);
                                    //gMap.addMarker(markers1);
                                    gMap.addMarker(markers);
                                    if (jsonObject1.getString("dropofflocation")!=null&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("")&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("null")) {
                                        drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                        drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));

                                        MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                                        gMap.addMarker(marker2);
                                    }


                                    gMap.moveCamera(center);
                                    Log.e("Come Map True", "" + pic_lat);


                                  /*  String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                    FetchUrl FetchUrl = new FetchUrl();
                                    FetchUrl.execute(url, "first");
                                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                            .include(new LatLng(pic_lat, pick_lon))
                                            .include(new LatLng(drop_lat, drop_lon))
                                            .build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
*/

                                    if (Status!=null&&!Status.equalsIgnoreCase("Accept")){

                                        if (jsonObject1.getString("dropofflocation")!=null&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("")&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("null")) {

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
                        timer.schedule(new TimerTask() {
                            public void run() {


                                if (driver_lat != 0) {
                                    String driver_lat_sr = String.valueOf(driver_lat);
                                    String driver_lon_sr = String.valueOf(driver_lon);
                                    new GetDriverLat().execute(driver_lat_sr, driver_lon_sr);
                                }
                            }
                        }, 1000, 10000);


                    }else {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


            //  updateLocationUI();
        }
    }

    protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", new String(""));
        smsIntent.putExtra("sms_body", getResources().getString(R.string.app_name));

        try {
            startActivity(smsIntent);

            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TripStatusAct.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

/*if (mCurrentLocation!=null){

    LatLng latLng1 = new LatLng(location.getLatitude(),location.getLongitude());
    Log.e("Location Change","FF"+latLng1);
    if (old_driver_lat!=null){

        Log.e("Come IN Change",""+old_driver_lat);
        Log.e("Come IN Change New",""+latLng1);

        if (drivermarker!=null){
            drivermarker.setAnchor(0.5f, 0.5f);
            drivermarker.setRotation(getBearing(old_driver_lat, latLng1));
        }


*//*
                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(latLng1)
                                        .zoom(16f).build()));
*//*


    }

    old_driver_lat = latLng1;

}*/


        // mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //   updateLocationUI();
/*        if (drivermarker == null) {

        } else {
            if (mCurrentLocation == null) {

            } else {
                LatLng latlong = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                drivermarker.setPosition(latlong);
            }
        }*/

    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pessengerdetail:
                pessengerDetail();
                return true;
            case R.id.cancelbook:
                //cancletrip();
                toCancelRide();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pessengerDetail() {
        //Log.e("Pasenger Detail >>", "" + userimage_str + " usetnema >>" + userimage_str + " mobile>>" + usermobile_str);

        //   Log.e("War Msg in dialog", war_msg);
        final Dialog carselection = new Dialog(TripStatusAct.this);
        carselection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        carselection.setCancelable(false);
        //carselection.setContentView(R.layout.pessengerdetail);
        carselection.setContentView(R.layout.passenger_lay);
        // carselection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        carselection.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cancel = (ImageView) carselection.findViewById(R.id.cancel);
        CircleImageView userimage = (CircleImageView) carselection.findViewById(R.id.userimage);
        TextView username = carselection.findViewById(R.id.username);
        TextView numberofrate = carselection.findViewById(R.id.numberofrate);
        LinearLayout msglay = carselection.findViewById(R.id.msglay);
        LinearLayout call = carselection.findViewById(R.id.call);
        username.setText("" + username_str);
        if (userimage_str == null || userimage_str.equalsIgnoreCase("") || userimage_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

        } else {
            Picasso.with(TripStatusAct.this).load(userimage_str).into(userimage);

        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usermobile_str == null || usermobile_str.equalsIgnoreCase("")) {

                } else {
                    if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + usermobile_str));
                    startActivity(callIntent);

                }
            }
        });
        msglay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripStatusAct.this, ChatingAct.class);
                i.putExtra("receiver_id", booking_user_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", userimage_str);
                i.putExtra("receiver_name", username_str);
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
            }
        });
        carselection.show();
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void toCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status_Chk = "Cancel";
                new ResponseToRequest().execute(Status_Chk);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setBuildingsEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        LatLng latLng = new LatLng(latitude, longitude);
        gMap.setMyLocationEnabled(true);
        driver_lat = latitude;
        driver_lon = longitude;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        drivermarker = gMap.addMarker(marker);
        drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


               // LatLng newlats = new LatLng(22.6977, 75.8645);
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                driver_lat = location.getLatitude();
                driver_lon = location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (drivermarker != null) {
                    Log.e("Google map"," > "+latLng1);

                    String latitudes = String.valueOf(location.getLatitude());
                    String longitudes = String.valueOf(location.getLongitude());


                    if (old_driver_lat!=null){


                        drivermarker.setPosition(latLng1);
                        drivermarker.setAnchor(0.5f, 0.5f);
                        drivermarker.setRotation(location.getBearing());

                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(latLng1)
                                        .zoom(16f).build()));


                    }

                    old_driver_lat = latLng1;




                    Log.e("On Trip Change loc >>", "" + latLng1);
                    //   new GetDriverLat().execute(latitudes,longitudes);
                }

/*
                try {
                    if (pic_lat != 0.0) {

                        String url = getUrlLive(latLng1, startlatlong);
                        FetchUrlLive FetchUrl = new FetchUrlLive();
                        FetchUrl.execute(url, "third");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }
        });


    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.e("FindTruck Latitude", "" + latitude);
            Log.e("FindTruck longitude", "" + longitude);
        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }


    }


    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String getUrlLive(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
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
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        String col = "";

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                col = url[1];
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result, col);
        }
    }

    private class FetchUrlLive extends AsyncTask<String, Void, String> {
        String col = "";

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                col = url[1];
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTaskLive parserTask = new ParserTaskLive();
            parserTask.execute(result, col);
        }
    }

    private class ParserTaskLive extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String cols = "";
        ArrayList<LatLng> points;

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                cols = jsonData[1];
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }


        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String cols = "";

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                cols = jsonData[1];
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
            if (result!=null){
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    if (cols.equalsIgnoreCase("second")) {
                        lineOptions.color(Color.GRAY);
                    } else {
                        lineOptions.color(Color.BLACK);

                    }
                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                }

            }
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);

            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


// other code for location check

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            // updateUI();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final com.google.android.gms.common.api.Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();



              /*  Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                finish();
*/
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(TripStatusAct.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

/*
        if (requestCode == 11) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    //Freely draw over other apps
                }
            }
        }
*/

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        finish();
                        break;

                }
                break;
            case 2:
                // startService(new Intent(getApplicationContext(), BackButtonService.class));
                // naviGationWith();
                //  startActivityForResult(intent,22);
                break;

        }

    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {

    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view) {
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                //  setButtonsEnabledState();

            }
        });

    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        //setButtonsEnabledState();
        // updateLocationUI();
    }

    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    private void updateLocationUI_n() {
        if (mCurrentLocation != null) {
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                //  setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    // end location check code


    private class GetDriverLat extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/update_lat_lon?lat=123&lon=321&user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_lat_lon?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("lat", strings[0]);
                params.put("lon", strings[1]);
                Log.e("strings[0]>>", "" + strings[0]);
                Log.e("strings[1]>>", "" + strings[1]);


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
                Log.e("Update Trip onchange ", ">>>>>>>>>>>>" + response);
                return response;
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            }catch (Exception e1) {

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

            }


        }
    }

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                canceldialog.dismiss();
                finish();

            }
        });
        canceldialog.show();


    }
    private void newPointAdded() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        message_tv.setText(""+getResources().getString(R.string.dropadd));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                new GetCurrentBooking().execute();
                canceldialog.dismiss();


            }
        });
        canceldialog.show();


    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
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
                // Log.e("Status >.", "" + strings[0]);

                params.put("request_id", request_id);
                params.put("status", strings[0]);
                params.put("timezone", time_zone);
                params.put("driver_id", user_id);


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
                // Log.e("Json Start End", ">>>>>>>>>>>>" + response);
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
                if (ac_dialog != null) {
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
                if (ac_dialog != null) {
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

            } else {

                try {
                    if (ac_dialog != null) {
                        if (isVisible){
                            ac_dialog.dismiss();
                        }

                    }

                    JSONObject jsonObject = new JSONObject(result);


                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        reideAllreadyCanceled();

                    } else {
                        if (Status_Chk.equalsIgnoreCase("Arrived")) {
                            Status = "Arrived";
                            sts_text.setText(getResources().getString(R.string.arrived));
                            tripsts_but.setText(getResources().getString(R.string.slidebegintrip));

                            new GetCurrentBooking().execute();

                        } else if (Status_Chk.equalsIgnoreCase("Start")) {
                            location_tv.setText("" +dropofflocation_str );
                            timer_lay.setVisibility(View.VISIBLE);
                            Status = "Start";
                            sts_text.setText(getResources().getString(R.string.inroute));
                            tripsts_but.setText(getResources().getString(R.string.slideendtrip));
                            botumlay.setBackgroundResource(R.color.red);


                        } else if (Status_Chk.equalsIgnoreCase("End")) {
                            Status = "End";
                            // Toast.makeText(TripStatusAct.this,"In working..",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(TripStatusAct.this, PaymentAct.class);
                            startActivity(i);
                            finish();
                        } else if (Status_Chk.equalsIgnoreCase("Cancel")) {
                            finish();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }
    }


    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
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


    private class TimerAsc extends AsyncTask<String, String, String> {
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
            //http://halatx.halasmart.com/hala/webservice/manage_waiting_time?request_id=1&waiting_status=STOP
            try {
                String postReceiverUrl = BaseUrl.baseurl + "manage_waiting_time?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                params.put("waiting_status", strings[0]);
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
                // Log.e("Json Start End", ">>>>>>>>>>>>" + response);
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
            Log.e("Timer Response >>", " dd " + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {

            }


        }
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
}
