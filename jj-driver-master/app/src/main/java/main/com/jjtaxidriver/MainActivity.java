package main.com.jjtaxidriver;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.jjtaxidriver.activity.DashBoardAct;
import main.com.jjtaxidriver.activity.PaymentAct;
import main.com.jjtaxidriver.activity.ProfileAct;
import main.com.jjtaxidriver.activity.TripStatusAct;
import main.com.jjtaxidriver.activity.WalletAct;
import main.com.jjtaxidriver.app.Config;
import main.com.jjtaxidriver.constant.ACProgressCustom;
import main.com.jjtaxidriver.constant.BaseUrl;
import main.com.jjtaxidriver.constant.GPSTracker;
import main.com.jjtaxidriver.constant.MyCarBean;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.constant.MySession;
import main.com.jjtaxidriver.draweractivity.BaseActivity;
import main.com.jjtaxidriver.service.MyFirebaseMessagingService;
import main.com.jjtaxidriver.utils.NotificationUtils;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    TextView changecar, user_name, mywalletmoney, online_offline_tv;
    public static int sts = 0;
    MySession mySession;
    private String user_log_data = "";
    public static String amount = "0", promo_code = "", user_id = "", image_url, firstname_str = "", lastname_str = "";
    private CircleImageView user_img;
    Switch switch_driver_sts;
    private String status;
    private String selected_car_id = "";
    ArrayList<MyCarBean> myCarBeanArrayList;
    private TextView carname, carnumber;
    private ImageView carimage, gpslocator;
    private LinearLayout addcarlay, carinfolay;
    public String stateResult;
    String status_job = "", request_id_main = "", payment_type;
    public static int driver_sts = 0;
    ProgressBar progressbar;

    private ProgressBar progressBarCircle;
    boolean dialogsts_show = false;
    MapStyleOptions style;
    Dialog booking_request_dialog ;
    RelativeLayout color_Background;
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private long timeCountInMilliSeconds;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    public static String request_id = "", strDate = "";
    private String diff_second = "";
    Marker drivermarker;
    public static String ACTIVE_CAR_ID = "";
    public static String time_zone = "";
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView todaytripcount, ratetv, tripcount, todaytipsamount, todayearning, driver_name, car_nametv, cartypename, car_numbertv, lasttrip_time, lasttripamount, lasttripdate;
    String currentVersion;
    private boolean min_sts = true;
    MyLanguageSession myLanguageSession;
    private String language = "";
    private LinearLayout infolay;
    private ImageView showhideimg;
    private boolean isVisible = true;
    public Bundle savedActivity;
    public static String user_pincode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedActivity=savedInstanceState;
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        //   setContentView(R.layout.activity_main);
        contentFrameLayout = findViewById(R.id.contentFrame);
        //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>", tz.getDisplayName());
        Log.e("TIME ZONE ID>>", tz.getID());
        time_zone = tz.getID();




        try {

            final Window win= getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            // unlock screen

//            if (MyFirebaseMessagingService.kl!=null){
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            }

                        /*KeyguardManager km = (KeyguardManager) getApplicationContext()
                                .getSystemService(Context.KEYGUARD_SERVICE);
                        final KeyguardManager.KeyguardLock kl = km
                                .newKeyguardLock("MyKeyguardLock");
                        kl.reenableKeyguard();*/


        }catch (Exception e){

        }



        sts = 0;
        min_sts = true;
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
                    user_name = findViewById(R.id.user_name);
                    user_name.setText("" + jsonObject1.getString("first_name"));

                    // amount = jsonObject1.getString("amount");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mySession.getAppUpdate().equalsIgnoreCase("cancel")) {
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.e("OnCreate", "Current version " + currentVersion);

                new GetVersionCode().execute();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.e("OnCreate EXC", "Current version " + currentVersion);
            }
            Log.e("OnCreate OUT", "Current version " + currentVersion);
        }

        idinits();
        checkGps();
        clickevents();

        final String TAG ="MainActivity";
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

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
                    Log.e("KEY MSG MAIN ACT=", "-----------------------" );

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("KEY MSG MAIN ACT=", "-----------------------" );
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG MAIN ACT=", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                            if (booking_request_dialog == null) {
                                Log.e("COME ", "null");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = data.getString("favorite_ride");
                                payment_type = data.getString("payment_type");
                                diff_second = data.getString("diff_second");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);

                            } else if (booking_request_dialog.isShowing()) {
                                Log.e("COME ", "show");
                            } else {
                                Log.e("COME ", "else");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                diff_second = data.getString("diff_second");
                                payment_type = data.getString("payment_type");
                                String favorite_ride = data.getString("favorite_ride");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);

                            }
                        } else if (keyMessage.equalsIgnoreCase("your booking request is Letter")) {
                            if (booking_request_dialog == null) {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = data.getString("favorite_ride");
                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                payment_type = data.getString("payment_type");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);

                            } else if (booking_request_dialog.isShowing()) {

                            } else {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = data.getString("favorite_ride");
                                payment_type = data.getString("payment_type");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);

                            }
/*
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }
*/


                        } else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {
                            stopCountDownTimer();
                            if (booking_request_dialog == null) {

                            } else {
                                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                    booking_request_dialog.cancel();
                                    booking_request_dialog.dismiss();
                                    diff_second = "";
                                }

                            }
                            // reideAllreadyCanceled();


                        } else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {
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

    private void clickevents() {
        gpslocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap == null) {

                } else {
                    Location loc = gMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLang = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        if (infolay.getVisibility()==View.VISIBLE){
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 16);
                            gMap.animateCamera(cameraUpdate);
                        }
                        else {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 17);
                            gMap.animateCamera(cameraUpdate);
                        }

                    }

                }
            }
        });

        showhideimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infolay.getVisibility() == View.VISIBLE) {
                    infolay.setVisibility(View.GONE);
                    if (gMap!=null){
                        gMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                    }
                    showhideimg.setImageResource(R.drawable.ic_up);
                } else {
                    if (gMap!=null){
                        gMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
                    }

                    infolay.setVisibility(View.VISIBLE);
                    showhideimg.setImageResource(R.drawable.ic_down);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        initilizeMap();
       /* if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }*/

        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(MainActivity.this.getApplicationContext());

        /*new GetStateBooking().execute();*/
        new GetCurrentBooking().execute();
        new GetDriverProfile().execute();

    }

    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
/*
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
*/
        // unregisterReceiver(broadcastReceiver);
    }

    private void idinits() {
        infolay = findViewById(R.id.infolay);
        infolay.setVisibility(View.GONE);
        showhideimg = findViewById(R.id.showhideimg);
        todaytripcount = (TextView) findViewById(R.id.todaytripcount);
        todaytipsamount = (TextView) findViewById(R.id.todaytipsamount);
        tripcount = (TextView) findViewById(R.id.tripcount);
        lasttrip_time = (TextView) findViewById(R.id.lasttrip_time);
        todayearning = (TextView) findViewById(R.id.todayearning);
        todaytipsamount = (TextView) findViewById(R.id.todaytipsamount);
        lasttripamount = (TextView) findViewById(R.id.lasttripamount);
        lasttripdate = (TextView) findViewById(R.id.lasttripdate);
        mywalletmoney = findViewById(R.id.mywalletmoney);
        user_img = findViewById(R.id.user_img);
        online_offline_tv = (TextView) findViewById(R.id.online_offline_tv);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        carinfolay = (LinearLayout) findViewById(R.id.carinfolay);
        addcarlay = (LinearLayout) findViewById(R.id.addcarlay);
        carname = (TextView) findViewById(R.id.carname);
        carnumber = (TextView) findViewById(R.id.carnumber);
        changecar = (TextView) findViewById(R.id.changecar);
        carimage = (ImageView) findViewById(R.id.carimage);
        gpslocator = (ImageView) findViewById(R.id.gpslocator);
        switch_driver_sts = (Switch) findViewById(R.id.switch_driver_sts);
        switch_driver_sts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    status = "ONLINE";
                    new ChgStatus().execute(status);
                }
                else {
                    status = "OFFLINE";
                    new ChgStatus().execute(status);
                }


/*                if (isChecked) {
                    status = "ONLINE";
                    online_offline_tv.setText(getResources().getString(R.string.gooffline));

                } else {
                    mySession.onlineuser(false);
                    status = "OFFLINE";
                    online_offline_tv.setText(getResources().getString(R.string.goonline));

                    //yourStatusChangeoffline();
                }
                if (sts == 0) {
                    sts = 1;
                } else if (sts == 1) {

                    //onlineStatusDialog(status);
                    // nowYourStatus(status);
                }

                new ChgStatus().execute(status);*/

            }
        });


        addcarlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(MainActivity.this, AddCar.class);
                startActivity(i);*/
            }
        });
        changecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCarBeanArrayList == null || myCarBeanArrayList.isEmpty()) {
                    Intent i = new Intent(MainActivity.this, ProfileAct.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, ProfileAct.class);
                    startActivity(i);
                    // carSelectionPop(myCarBeanArrayList);
                }

            }
        });
    }


    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
/*
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MainActivity.this, R.raw.stylemap_3));
*/
        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        if (drivermarker==null){
            drivermarker = gMap.addMarker(marker);
            drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
            gMap.animateCamera(cameraUpdate);
        }



        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (drivermarker != null) {


                    if (location != null) {

                        if (infolay.getVisibility()==View.VISIBLE){
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 16);
                            gMap.animateCamera(cameraUpdate);
                            drivermarker.setPosition(latLng1);
                        }
                        else {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 17);
                            gMap.animateCamera(cameraUpdate);
                            drivermarker.setPosition(latLng1);
                        }
                    }

                    Log.e("On Trip Change loc >>", "" + latLng1);
                    //   new GetDriverLat().execute(latitudes,longitudes);
                }
            }
        });


    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();


        } else {
            gpsTracker.showSettingsAlert();
        }


    }

    private void carSelectionPop(ArrayList<MyCarBean> myCarBeanArrayList) {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog carselection = new Dialog(MainActivity.this);
        carselection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        carselection.setCancelable(false);
        carselection.setContentView(R.layout.selectcar_lay);
        carselection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RadioGroup radioGroup_cartype = (RadioGroup) carselection.findViewById(R.id.radioGroup_mycar);
        TextView cancel = (TextView) carselection.findViewById(R.id.cancel);
        TextView select = (TextView) carselection.findViewById(R.id.select);
        for (int i = 0; i < myCarBeanArrayList.size(); i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(Integer.parseInt(myCarBeanArrayList.get(i).getId()));
            rbn.setText(myCarBeanArrayList.get(i).getCarname());
            radioGroup_cartype.addView(rbn);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
                int selectedId = radioGroup_cartype.getCheckedRadioButtonId();
                selected_car_id = String.valueOf(selectedId);
                // find the radiobutton by returned id
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.selectcar), Toast.LENGTH_LONG).show();
                } else {
                    RadioButton radioButton = (RadioButton) carselection.findViewById(selectedId);
                    String loadtype = radioButton.getText().toString();


                    new ChangeCar().execute(selected_car_id);
                }


            }
        });
        carselection.show();


    }

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride) {
        dialogsts_show = true;
        request_id_main = request_id;
        //   Log.e("War Msg in dialog", war_msg);
        booking_request_dialog = new Dialog(MainActivity.this);
        booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        booking_request_dialog.setCancelable(false);
        booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
        if(payment_type.equalsIgnoreCase("Corporate")) {
            booking_request_dialog.findViewById(R.id.requestScreen).setBackgroundColor(getResources().getColor(R.color.blue));
        }
        booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        color_Background = booking_request_dialog.findViewById(R.id.locationlay);
//        color_Background.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        TextView decline = (TextView) booking_request_dialog.findViewById(R.id.decline);
        TextView datetimetv = (TextView) booking_request_dialog.findViewById(R.id.datetimetv);
        TextView rating_tv = (TextView) booking_request_dialog.findViewById(R.id.rating_tv);
        rating_tv.setText("" + rating);
        if (booktype == null || booktype.equalsIgnoreCase("")) {
            datetimetv.setVisibility(View.GONE);
        } else {
            datetimetv.setVisibility(View.VISIBLE);
            // datetimetv.setText(""+getResources().getString(R.string.booktime)+" "+picklaterdate.trim()+" "+picklatertime);
        }
        ImageView favroiteride = booking_request_dialog.findViewById(R.id.favroiteride);
        if (favorite_ride.equalsIgnoreCase("yes")) {
            favroiteride.setVisibility(View.VISIBLE);
        } else {
            favroiteride.setVisibility(View.GONE);
        }
        TextView accept = (TextView) booking_request_dialog.findViewById(R.id.accept);
        TextView pick_location = (TextView) booking_request_dialog.findViewById(R.id.pick_location);
        TextView drop_location = (TextView) booking_request_dialog.findViewById(R.id.drop_location);
        textViewTime = (TextView) booking_request_dialog.findViewById(R.id.textViewTime);
        TextView username = (TextView) booking_request_dialog.findViewById(R.id.username);
        final ProgressBar progressBarCircle = (ProgressBar) booking_request_dialog.findViewById(R.id.progressBarCircle);
        username.setText("" + firstname + " " + lastname);
        pick_location.setText("" + picuplocation);
        drop_location.setText("" + dropofflocation);
        int sec = 60;
        //  int sec = 17;
        int mili = 1000;
        int newsec = 1;
        Log.e("diff_second ?", "POPUP" + diff_second);
        if (diff_second == null || diff_second.equalsIgnoreCase("")) {
            /*newsec=60;*/
        } else {
            int difernce = Integer.parseInt(diff_second);
//            newsec = sec - difernce;
            newsec = 59 ;
        }
        Log.e("newsec >>", "dd " + newsec);
        timeCountInMilliSeconds = 1 * newsec * mili;
        Log.e("Count Timer", "gg " + timeCountInMilliSeconds);
        timerStatus = TimerStatus.STOPPED;
        // startStop();
        progressBarCircle.setMax((int) 60);
        //progressBarCircle.setMax((int) 60);
        // progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                Log.e("TICK 1", "" + millisUntilFinished / 1000);
                Log.e("TICK 1", "" + millisUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (isVisible) {
                    if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                        booking_request_dialog.cancel();
                        booking_request_dialog.dismiss();
                        diff_second = "";
                    }
                    textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                    stopCountDownTimer();
                    timerStatus = TimerStatus.STOPPED;
                }


            }
        }.start();
        countDownTimer.start();


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                    booking_request_dialog.cancel();
                    booking_request_dialog.dismiss();
                    diff_second = "";
                }

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();

                status_job = "Cancel";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                    booking_request_dialog.cancel();
                    booking_request_dialog.dismiss();
                    diff_second = "";
                }

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();


                status_job = "Accept";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });

        if (isVisible) {
            if (booking_request_dialog.isShowing()) {

            } else {
                booking_request_dialog.show();
            }
        }


    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            Log.e("TICK----------------", "");

            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("TICK----------------", "");
            try {
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                params.put("request_id", strings[0]);
                params.put("status", strings[1]);
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
            Log.e("ACCEPT RESULT", "....... " + result);


            if (result == null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(result);


                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        if (jsonObject.getString("result").equalsIgnoreCase("already accepted")) {
                            reideAllreadyAccepted();
                        } else {
                            reideAllreadyCanceled();
                        }

                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {

                        } else {
                            Intent i = new Intent(MainActivity.this, TripStatusAct.class);
                            i.putExtra("request_id", request_id);
                            startActivity(i);
                            //finish();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.alreadyaccepted);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }


                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
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

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }


    /**
     * method to start count down timer
     */

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
               */ TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }


    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

    }

    /**
     * method to set circular progress bar values
     */

    private class GetDriverProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            Log.e("TICK----------------", "");
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.e("TICK----------------", "");
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "DRIVER");
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
            Log.e("TICK----------------", "");
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
                        if (jsonObject1.getString("status").equalsIgnoreCase("Deactive")) {
                            mySession.onlineuser(false);
                           /* Intent i = new Intent(MainActivity.this, DashBoardAct.class);
                            startActivity(i);
                            finish();*/
                        }

                        carnumber.setText("" + jsonObject1.getString("car_number").trim());
                        carname.setText("" + jsonObject1.getString("car_model"));
                        image_url = jsonObject1.getString("image");
                        mywalletmoney.setText("$" + jsonObject1.getString("amount"));
                        MainActivity.amount = jsonObject1.getString("amount");
                        String car_image = jsonObject1.getString("car_image");
                        if (image_url == null || image_url.equalsIgnoreCase("") || image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Glide.with(MainActivity.this)
                                    .load(image_url)
                                    .thumbnail(0.5f)
                                    .override(200, 200)
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                            return false;
                                        }
                                    })
                                    .into(user_img);

                        }
                        if (car_image == null || car_image.equalsIgnoreCase("") || car_image.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Glide.with(MainActivity.this)
                                    .load(car_image)
                                    .thumbnail(0.5f)
                                    .override(200, 200)
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                            return false;
                                        }
                                    })
                                    .into(carimage);

                        }

                        if (online_status.equalsIgnoreCase("ONLINE")) {
                            switch_driver_sts.setChecked(true);
                            online_offline_tv.setText(getResources().getString(R.string.gooffline));
                        } else {
                            switch_driver_sts.setChecked(false);
                            online_offline_tv.setText(getResources().getString(R.string.goonline));

                        }
                        JSONObject jsonObject3 = jsonObject1.getJSONObject("last_trip");
                        JSONObject jsonObject4 = jsonObject1.getJSONObject("today_earn");
                        //   JSONObject jsonObject5 = jsonObject1.getJSONObject("today_tips");
                        if (jsonObject3.getString("return").equalsIgnoreCase("success")) {
                            lasttripamount.setText("$" + jsonObject3.getString("earning"));
                            String lasttripdates = jsonObject3.getString("date_time");
                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lasttripdates);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm aa");
                                SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMM, yyyy");
                                String time = formatter.format(date1);

                                String mainDate = formatter2.format(date1);
                                lasttrip_time.setText("" + time);
                                lasttripdate.setText("" + mainDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        if (jsonObject4.getString("return").equalsIgnoreCase("success")) {
                            todayearning.setText("$" + jsonObject4.getString("amount"));
                            todaytripcount.setText("" + getResources().getString(R.string.trip) + " " + jsonObject4.getString("trip"));

                        }
                        if (jsonObject1.getString("register_id") == null || jsonObject1.getString("register_id").equalsIgnoreCase("") || jsonObject1.getString("register_id").equalsIgnoreCase("0") || jsonObject1.getString("register_id").equalsIgnoreCase("null")) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                            String firebase_regid = pref.getString("regId", null);
                            if (firebase_regid != null) {
                                new UpdateRegistrationid().execute(firebase_regid);
                            }
                        }
                        if (min_sts) {
                            min_sts = false;
                            // new MinimumWalletCheck().execute();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);

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
                params.put("user_id", MainActivity.user_id);
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
                //progressbar.setVisibility(View.GONE);
                if (result == null) {
                } else if (result.isEmpty()) {
                } else {

                    try {
                        Log.e("CURRENT BOOKING >>>", "" + result);
                        JSONObject jsonObject = new JSONObject(result);
                        String msg = jsonObject.getString("message");
                        /*if (msg.equalsIgnoreCase("successfull")&& (!stateResult.equalsIgnoreCase(msg))) {*/
                            if (msg.equalsIgnoreCase("successfull")) {
                            diff_second = jsonObject.getString("diff_second");
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                request_id = jsonObject1.getString("id");
                                try {
                                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                    strDate = formatter.format(date1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String status = jsonObject1.getString("status");
                                if (status.equalsIgnoreCase("Pending")) {
                                    String firstname = "";
                                    String lastname = "";
                                    String rating = "";
                                    String picuplocation = jsonObject1.getString("picuplocation");
                                    String dropofflocation = jsonObject1.getString("dropofflocation");
                                    request_id = String.valueOf(jsonObject1.getString("id"));
                                    String picklaterdate = jsonObject1.getString("picklaterdate");
                                    String picklatertime = jsonObject1.getString("picklatertime");
                                    String booktype = jsonObject1.getString("booktype");
                                    String favorite_ride = jsonObject1.getString("favorite_ride");
                                    payment_type = jsonObject1.getString("payment_type");
                                    JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                                    for (int k = 0; k < jsonArray1.length(); k++) {
                                        JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                        firstname = jsonObject2.getString("first_name");
                                        lastname = jsonObject2.getString("last_name");
                                        rating = jsonObject2.getString("rating");
                                    }

                                    if (booking_request_dialog == null) {
                                        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                            NotificationUtils.r.stop();
                                        }
                                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                                        notificationUtils.playNotificationSound();

                                        showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);
                                        //notificationUtils.notify();

                                    } else if (booking_request_dialog.isShowing()) {

                                    } else {
                                        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                            NotificationUtils.r.stop();
                                        }
                                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                                        notificationUtils.playNotificationSound();
                                        showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride);

                                    }


/*                                if (dialogsts_show) {
                                    Log.e("COME TRUE"," IF");
                                    dialogsts_show = false;
                                } else {

                                    Log.e("COME ELSE"," ELSE");
                                }*/
                                } else if (status.equalsIgnoreCase("Accept")) {
                                    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                        NotificationUtils.r.stop();
                                    }
                                    Intent k = new Intent(MainActivity.this, TripStatusAct.class);
                                    startActivity(k);
                                } else if (status.equalsIgnoreCase("Arrived")) {
                                    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                        NotificationUtils.r.stop();
                                    }
                                    Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                                    startActivity(j);
                                } else if (status.equalsIgnoreCase("Start")) {
                                    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                        NotificationUtils.r.stop();
                                    }
                                    Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                                    startActivity(j);
                                } else if (status.equalsIgnoreCase("End")) {
                                    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                        NotificationUtils.r.stop();
                                    }
                                    Intent j = new Intent(MainActivity.this, PaymentAct.class);
                                    startActivity(j);
                                }
                            }
                        } else {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }

                        }
                        /*stateResult=msg;*/


                    } catch (JSONException e) {
                        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                            NotificationUtils.r.stop();
                        }

                        e.printStackTrace();
                    }
                }


        }
    }

    private class ChgStatus extends AsyncTask<String, String, String> {
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

            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";
                Log.e("ONLINESTS >> "," >> "+postReceiverUrl+"user_id="+user_id+"&status="+strings[0]+"&type=DRIVER");
                URL url = new URL(postReceiverUrl);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("status", strings[0]);
                params.put("type", "DRIVER");
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
                Log.e("STS RS"," >> "+response);
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
                if (status.equalsIgnoreCase("ONLINE")){
                    mySession.onlineuser(true);
                }
                else {
                    mySession.onlineuser(false);
                }

            }


        }
    }

    private class MinimumWalletCheck extends AsyncTask<String, String, String> {
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
//http://hitchride.net/webservice/get_driver_setting
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_driver_setting?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
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
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    Log.e("Driver Setting", " >" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String cash_ride_minimum_amt = jsonObject1.getString("cash_ride_minimum_amt");
                        if (cash_ride_minimum_amt == null || cash_ride_minimum_amt.equalsIgnoreCase("")) {

                        } else {
                            double min_req = Double.parseDouble(cash_ride_minimum_amt);
                            double my_amount = Double.parseDouble(amount);
                            Log.e("Driver Setting Amt", " >" + min_req + " >> " + my_amount);

                            if (min_req > my_amount) {
                                updateWalletAmount();
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class ChangeCar extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/change_car?driver_id=21&car_id=3
            try {
                String postReceiverUrl = BaseUrl.baseurl + "change_car?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("driver_id", MainActivity.user_id);
                params.put("car_id", strings[0]);


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
            if (result == null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                //new GetMyCar().execute();
            }


        }
    }


    private class UpdateRegistrationid extends AsyncTask<String, String, String> {
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
            //http://mobileappdevelop.co/NAXCAN/webservice/update_register_id?user_id=31&register_id=1234
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_register_id?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("register_id", strings[0]);

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
                Log.e("Update Register id ", ">>>>>>>>>>>>" + response);
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
            }
        }
    }

    private void appUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.appupdateneed));
        no_tv.setText("" + getResources().getString(R.string.remindlater));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                final String appPackageName = BuildConfig.APPLICATION_ID; // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.setAppUpdate("later");
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void updateWalletAmount() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.updatewallet));
        no_tv.setText("" + getResources().getString(R.string.later));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(MainActivity.this, WalletAct.class);
                startActivity(i);
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


    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (currentVersion != null) {
                    if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                        //show anything
                        appUpdate();
                    }

                }

            }


        }
    }

}
