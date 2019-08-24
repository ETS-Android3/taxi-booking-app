package main.com.jjtaxiuser.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.DriverDetailBean;
import main.com.jjtaxiuser.constant.GPSTracker;
import main.com.jjtaxiuser.constant.MultipartUtility;
import main.com.jjtaxiuser.constant.MyCarBean;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.draglocation.DataParser;
import main.com.jjtaxiuser.draglocation.MyTask;
import main.com.jjtaxiuser.draglocation.WebOperations;
import main.com.jjtaxiuser.draweractivity.BaseActivity;
import main.com.jjtaxiuser.paymentclasses.SaveCardDetail;
import main.com.jjtaxiuser.utils.NotificationUtils;
import main.com.jjtaxiuser.utils.Pinter;

public class TripStatusAct extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    private TextView titletext, fare_rate_tv, away_minute, people_size;
    private Button confirmbooking_but;
    Dialog dialogSts, route_detail_pop;
    RippleBackground content1;
    CountDownTimer yourCountDownTimer;
    RecyclerView cartypelist;
    CarHoriZontalLay carHoriZontalLay;
    private String selected_car_id = "";
    private ProgressBar progressbar;
    String base_fare_str = "", per_km = "", car_charge = "", distance = "", total = "", min_way = "", request_id = "";
    String passenger_str = "1", sharing_seats = "0";
    static Bitmap bitmap, bitmap1, bmOverlay;
    String Fullpath = "";
    File mapfile;
    TextView pickuplocation, dropofflocation;
    private Integer THRESHOLD = 2;
    private int count = 0, countDrop = 0;
    boolean sts;
    private ProgressDialog dialog;
    Timer timerObj;
    ScheduledExecutorService scheduleTaskExecutor;
    MySession mySession;
    LatLng picklatLng, droplatlong;

    Dialog waitingdialogSts;
    TextView markerRating, destinationtype;
    int pop_sts = 0;
    String time_zone = "";
    private ArrayList<MyCarBean> myCarBeanArrayListss;
    String alllatitude_str = "", alllongitude_str = "";
    public static Activity fa;
    TextView distance_km, estimate_fare, arriving_time, comingsoon_tv;
    private boolean is_selected = true;
    private ArrayList<DriverDetailBean> driverDetailBeanArrayList;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    public ArrayList<MyCarBean> myCarBeanArrayList, myCarBeanArrayList_check;
    private long timeCountInMilliSeconds;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TextView textViewTime, time_tv;
    private CountDownTimer countDownTimer;
    TextView cancel_tv, confirm_tv;
    long diffHours = 0, diffMinutes = 0;
    private ArrayList<String> latitudelist;
    private ArrayList<String> longitudelist;
    ACProgressFlower ac_dialog;
    private String user_log_data = "", user_id = "", driver_id = "", min_away = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageView cashimg, creditimg, walletimg;
    private TextView cashtv, credittv, wallettv, applycupon, discount_tv;
    private LinearLayout cashlay, creditlay, walletlay;
    private String payment_type_str = "Cash",reference_number="";
    private double Estimate_Amount = 0;
    private String diff_second = "1", coupon_str = "", streatnumber_str = "";
    TimerTask timerTask2;
    MyLanguageSession myLanguageSession;
    private String language = "";
    private EditText streatnumber,refrencenumber;
    private boolean isVisible = false,apists=false;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_trip_status);
        ac_dialog = new ACProgressFlower.Builder(this)
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
        latitudelist = new ArrayList<>();
        longitudelist = new ArrayList<>();
        pop_sts = 0;
        fa = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        selected_car_id = MainActivity.selected_car_id;
        myCarBeanArrayList_check = new ArrayList<>();
        mySession = new MySession(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        imgBack= findViewById(R.id.imageView4);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        idinits();
        clickevetn();
/*
        InputMethodManager inputManager = (InputMethodManager)
                TripStatusAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(TripStatusAct.this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
*/

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");

                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY ACCEPT REJ", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is ACCEPT")) {
                            request_id = data.getString("request_id");
                            driver_id = data.getString("driver_id");
                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }

                            }

                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }

                            if (MainActivity.booktype.equalsIgnoreCase("Letter")) {

                                bookConfirm();

                                /*booking_confirmation();*/

                            } else {
                                booking_confirmation();


                            }

                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");

                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }
                            }
                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }
                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            requestCancel();
                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is assign to new driver")) {
                            request_id = data.getString("request_id");

                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }
                            }
                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }
                            // requestReassign();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

       // autocompleteView();
        checkGps();
        try {
            // Loading map

        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        new GetCarLists().execute();
    }


/*
    private void autocompleteView() {
        pickuplocation.setThreshold(THRESHOLD);
        pickuplocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    clear_pick_ic.setVisibility(View.GONE);
                    loadData(pickuplocation.getText().toString());
                } else {
                    clear_pick_ic.setVisibility(View.INVISIBLE);
                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD);
        dropofflocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    clear_drop_ic.setVisibility(View.VISIBLE);
                    map_ic.setVisibility(View.GONE);
                    loadDataDrop(dropofflocation.getText().toString());
                } else {
                    clear_drop_ic.setVisibility(View.INVISIBLE);
                    map_ic.setVisibility(View.VISIBLE);
                }
            }
        });
    }
*/

    private void loadData(String s) {
        try {
            if (count == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = true;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(TripStatusAct.this, l1, "" + latitude, "" + longitude);
                 //   pickuplocation.setAdapter(ga);

                }

            }
            count++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataDrop(String s) {
        try {
            if (countDrop == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = false;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(TripStatusAct.this, l1, "" + latitude, "" + longitude);
                   // dropofflocation.setAdapter(ga);

                }

            }
            countDrop++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clickevetn() {

        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                time_zone = tz.getID();
                streatnumber_str = streatnumber.getText().toString();
                reference_number = refrencenumber.getText().toString();
               /* if (streatnumber_str == null || streatnumber_str.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.entersteatnumber), Toast.LENGTH_LONG).show();
                } else {
                    if (payment_type_str.equalsIgnoreCase("Cash")) {

                        new SendRequestToDriver().execute();

                    } else {
                        new SendRequestToDriver().execute();
                    }
                }*/

                if (payment_type_str.equalsIgnoreCase("Cash")) {

                    new SendRequestToDriver().execute();

                } else {
                    new SendRequestToDriver().execute();
                }


            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelAgenda();
            }
        });




        cashlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashimg.setImageResource(R.drawable.ic_notes_col);
                cashtv.setTextColor(getResources().getColor(R.color.buttoncol));

                creditimg.setImageResource(R.drawable.credit_card);
                credittv.setTextColor(getResources().getColor(R.color.darkgrey));
                walletimg.setImageResource(R.drawable.ic_skyline);
                wallettv.setTextColor(getResources().getColor(R.color.darkgrey));
                payment_type_str = "Cash";
            }
        });
        creditlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(TripStatusAct.this,"In working..",Toast.LENGTH_LONG).show();

                if (BaseActivity.Card_Added_Sts == null || BaseActivity.Card_Added_Sts.equalsIgnoreCase("")) {
                    cardStatus();
                } else if (BaseActivity.Card_Added_Sts.equalsIgnoreCase("Added")) {
                    cashimg.setImageResource(R.drawable.cash_unsel);
                    cashtv.setTextColor(getResources().getColor(R.color.darkgrey));

                    creditimg.setImageResource(R.drawable.credit);
                    credittv.setTextColor(getResources().getColor(R.color.buttoncol));
                    walletimg.setImageResource(R.drawable.wallet_unsel);
                    wallettv.setTextColor(getResources().getColor(R.color.darkgrey));
                    payment_type_str = "Card";
                } else {
                    cardStatus();
                }


            }
        });
        walletlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cashimg.setImageResource(R.drawable.ic_notes);
                cashtv.setTextColor(getResources().getColor(R.color.darkgrey));

                creditimg.setImageResource(R.drawable.credit_card);
                credittv.setTextColor(getResources().getColor(R.color.darkgrey));
                walletimg.setImageResource(R.drawable.ic_skyline_col);
                wallettv.setTextColor(getResources().getColor(R.color.buttoncol));
                payment_type_str = "Cash";
/*                if (MainActivity.amount != null && !MainActivity.amount.equalsIgnoreCase("")) {
                    double amount = Double.parseDouble(MainActivity.amount);
                    if (amount > Estimate_Amount) {
                        cashimg.setImageResource(R.drawable.cash_unsel);
                        cashtv.setTextColor(getResources().getColor(R.color.darkgrey));

                        creditimg.setImageResource(R.drawable.credit_card);
                        credittv.setTextColor(getResources().getColor(R.color.darkgrey));
                        walletimg.setImageResource(R.drawable.wallet_type_col);
                        wallettv.setTextColor(getResources().getColor(R.color.buttoncol));
                        payment_type_str = "Wallet";
                    } else {
                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.notenough), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.notenough), Toast.LENGTH_LONG).show();
                }*/


            }
        });
    }

    private void cardStatus() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.pleaseaddcarddetail));
        no_tv.setText("" + getResources().getString(R.string.cancel));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(TripStatusAct.this, SaveCardDetail.class);
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

    private void idinits() {


        refrencenumber = findViewById(R.id.refrencenumber);
        discount_tv = findViewById(R.id.discount_tv);
        streatnumber = findViewById(R.id.streatnumber);
        applycupon = findViewById(R.id.applycupon);
        cashlay = findViewById(R.id.cashlay);
        cashimg = findViewById(R.id.cashimg);
        cashtv = findViewById(R.id.cashtv);
        creditlay = findViewById(R.id.creditlay);
        creditimg = findViewById(R.id.creditimg);
        credittv = findViewById(R.id.credittv);
        walletlay = findViewById(R.id.walletlay);
        walletimg = findViewById(R.id.walletimg);
        wallettv = findViewById(R.id.wallettv);

        confirm_tv = (TextView) findViewById(R.id.confirm_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);




        pickuplocation = findViewById(R.id.pickuplocation);
        dropofflocation = findViewById(R.id.dropofflocation);
        people_size = (TextView) findViewById(R.id.people_size);
        away_minute = (TextView) findViewById(R.id.away_minute);
        fare_rate_tv = (TextView) findViewById(R.id.fare_rate_tv);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        cartypelist = (RecyclerView) findViewById(R.id.cartypelist);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(TripStatusAct.this, LinearLayoutManager.HORIZONTAL, false);
        cartypelist.setLayoutManager(horizontalLayoutManagaer);

        pickuplocation.setText("" + MainActivity.pickuploc_str);
        dropofflocation.setText("" + MainActivity.dropoffloc_str);

        titletext = (TextView) findViewById(R.id.titletext);

        applycupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCoupon();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

        gMap.moveCamera(center);

        if (MainActivity.dropoffloc_str!=null&&!MainActivity.dropoffloc_str.equalsIgnoreCase("")){
            String url = getUrl(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str));
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);
            final LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str))
                    .include(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str))
                    .build();
            int width = getResources().getDisplayMetrics().widthPixels;
            final int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
            gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
                }
            });

        }



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


    class CarHoriZontalLay extends RecyclerView.Adapter<CarHoriZontalLay.MyViewHolder> {
        ArrayList<MyCarBean> myCarBeanArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView carname, total_dis, total_amt, eta_min;
            ImageView carimage;
            View viewLine, small_verview;
            RelativeLayout backview;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.viewLine = (View) itemView.findViewById(R.id.viewLine);
                this.carname = (TextView) itemView.findViewById(R.id.carname);
                this.backview = itemView.findViewById(R.id.backview);
                this.carimage = itemView.findViewById(R.id.carimage);

            }
        }

        public CarHoriZontalLay(ArrayList<MyCarBean> myCarBeanArrayList) {
            this.myCarBeanArrayList = myCarBeanArrayList;
        }

        @Override
        public CarHoriZontalLay.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_car_lay, parent, false);
            CarHoriZontalLay.MyViewHolder myViewHolder = new CarHoriZontalLay.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CarHoriZontalLay.MyViewHolder holder, final int listPosition) {
            if (listPosition == myCarBeanArrayList.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            }
            holder.carname.setText("" + myCarBeanArrayList.get(listPosition).getCarname());
            if (myCarBeanArrayList.get(listPosition).isSelected()) {
                if (myCarBeanArrayList.get(listPosition).getTotal() != null && !myCarBeanArrayList.get(listPosition).getTotal().equalsIgnoreCase("")) {
                    Estimate_Amount = Double.parseDouble(myCarBeanArrayList.get(listPosition).getTotal());
                }
                fare_rate_tv.setText("$" + myCarBeanArrayList.get(listPosition).getTotal());
                if (myCarBeanArrayList.get(listPosition).getCab_find().equalsIgnoreCase("no_cab")) {
                    away_minute.setText(getResources().getString(R.string.nodrivers_s));

                } else {
                    away_minute.setText("" + myCarBeanArrayList.get(listPosition).getCab_find() + " min");

                }
                holder.backview.setBackgroundResource(R.drawable.selected_round_back);
            } else {
                holder.backview.setBackgroundResource(R.drawable.unselectedback);
            }
            String car_url = myCarBeanArrayList.get(listPosition).getCar_image();
            if (car_url == null || car_url.equalsIgnoreCase("") || car_url.equalsIgnoreCase(BaseUrl.baseurl)) {
            } else {
                Picasso.with(TripStatusAct.this).load(myCarBeanArrayList.get(listPosition).getCar_image()).into(holder.carimage);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = listPosition;
                    for (int k = 0; k < myCarBeanArrayList.size(); k++) {
                        if (pos == k) {
                            if (myCarBeanArrayList.get(k).isSelected()) {
                                myCarBeanArrayList.get(k).setSelected(false);
                                selected_car_id = "";
                            } else {
                                myCarBeanArrayList.get(k).setSelected(true);
                                selected_car_id = myCarBeanArrayList.get(k).getId();
                            }
                        } else {
                            myCarBeanArrayList.get(k).setSelected(false);
                        }
                    }
                    carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                    cartypelist.setAdapter(carHoriZontalLay);
                    cartypelist.scrollToPosition(listPosition);
                    carHoriZontalLay.notifyDataSetChanged();
                    //  selected_service_fare.setVisibility(View.VISIBLE);

                }
            });


        }

        @Override
        public int getItemCount() {
            // return 4;
            return myCarBeanArrayList == null ? 0 : myCarBeanArrayList.size();
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
                latitudelist = new ArrayList<>();
                longitudelist = new ArrayList<>();

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    latitudelist.add(point.get("lat"));
                    longitudelist.add(point.get("lng"));
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
               // MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_rojo)).flat(true).anchor(0.5f, 0.5f);
               // MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_azul)).flat(true).anchor(0.5f, 0.5f);
               // gMap.addMarker(pick);
               // gMap.addMarker(drop);
                if (animation_list != null && !animation_list.isEmpty()) {
                    // startAnim(animation_list);
                }

            } else {

            }
        }
    }

    private void startAnim(ArrayList<LatLng> points) {
        Log.e("COME ON START ANIMATION", " True");
        if (gMap != null) {
            //      MapAnimator.getInstance().animateRoute(gMap, points);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private class GetFareRate extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
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
                //http://mobileappdevelop.co/NAXCAN/webservice/get_fare?car_type_id=2&org_lat=22.6996933&org_lon=75.8672798&des_lat=22.7532848&des_lon=75.89369620000002
                String postReceiverUrl = BaseUrl.baseurl + "get_fare?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("car_type_id", strings[0]);
                params.put("org_lat", MainActivity.pickup_lat_str);
                params.put("org_lon", MainActivity.pickup_lon_str);
                params.put("des_lat", MainActivity.drop_lat_str);
                params.put("des_lon", MainActivity.drop_lon_str);
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
            Log.e("FARE RESULT", " > " + result);
            //progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
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
                            comingsoon_tv.setVisibility(View.GONE);

                            away_minute.setText("" + jsonObject1.getInt("min_way") + " min");
                            arriving_time.setText("" + jsonObject1.getInt("min_way") + " min");
                            estimate_fare.setText("$ " + jsonObject1.getInt("total"));
                            base_fare_str = jsonObject1.getString("base_fare");
                            per_km = jsonObject1.getString("per_km");
                            distance = jsonObject1.getString("distance");
                            total = jsonObject1.getString("total");
                            distance_km.setText("" + distance + " km");
                            min_way = String.valueOf(jsonObject1.getInt("min_way"));

                            if (jsonObject1.getInt("estimate_time") == -1) {
                                //  titletext.setText("no driver available");
                            } else {
                                car_charge = jsonObject1.getString("car_charge");
                                people_size.setText("" + jsonObject1.getString("no_of_seats") + " people");
                                // titletext.setText("" + jsonObject1.getInt("estimate_time") + " minute away");
                                String time = jsonObject1.getInt("estimate_time") + "\nmin";

                            }

                        }
                    } else {
                        away_minute.setText(getResources().getString(R.string.nodrivers_s));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            apists = true;
            //  progressbar.setVisibility(View.VISIBLE);

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

                Map<String, Object> params = new LinkedHashMap<>();


                params.put("user_id", user_id);
                params.put("type", "USER");
                params.put("timezone", time_zone);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                /*postReceiverUrl =postReceiverUrl+postData.toString();
                URL url = new URL(postReceiverUrl);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);*/
               /* OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);*/
                /* writer.flush();*/
                String urlParameters = postData.toString();
                URL url = new URL(postReceiverUrl);

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
            apists = false;
            // progressbar.setVisibility(View.GONE);

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    Log.e("Current Booking Main", ">>" + result);
                    if (msg.equalsIgnoreCase("successfull")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String status = jsonObject1.getString("status");
                            Log.e("Current Booking status", ">> " + status);
                            String pay_status = jsonObject1.getString("pay_status");
                            if (status.equalsIgnoreCase("Pending")) {

                            } else if (status.equalsIgnoreCase("End")) {
                                if (isVisible) {
                                    if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                                        waitingdialogSts.dismiss();
                                        if (yourCountDownTimer != null) {
                                            yourCountDownTimer.cancel();
                                        }

                                    }
                                }

                                if (jsonObject1.getString("user_rating_status").equalsIgnoreCase("Yes")) {

                                } else {
                                    Intent l = new Intent(TripStatusAct.this, FeedbackUs.class);
                                    startActivity(l);
                                }
                            } else {
                                if (isVisible) {
                                    if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                                        waitingdialogSts.dismiss();
                                        if (yourCountDownTimer != null) {
                                            yourCountDownTimer.cancel();
                                        }

                                    }
                                }

                                Log.e("Current Booking status", ">TRUE> " + status);
                                Intent j = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                                startActivity(j);
                                yourCountDownTimer.cancel();


                            }


                        }

                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class ResendRequest extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  progressbar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456
            String charset = "UTF-8";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            String postReceiverUrl = "";
            postReceiverUrl = BaseUrl.baseurl + "booking_request?";
            Log.e("BOOKING URL >>", ">> " + postReceiverUrl + "car_type_id=" + selected_car_id + "&user_id=" + user_id + "&picuplocation=" + MainActivity.pickuploc_str + "&dropofflocation=" + MainActivity.dropoffloc_str + "&picuplat=" + MainActivity.pickup_lat_str + "&pickuplon=" + MainActivity.pickup_lon_str + "&droplat=" + MainActivity.drop_lat_str + "&droplon=" + MainActivity.drop_lon_str + "&shareride_type=no&booktype=" + MainActivity.booktype + "&passenger=1&current_time=" + format + "&timezone=" + time_zone + "&status=" + MainActivity.booktype);


            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
                multipart.addFormField("car_type_id", selected_car_id);
                multipart.addFormField("device_type", "android");
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("picuplocation", MainActivity.pickuploc_str);
                multipart.addFormField("dropofflocation", MainActivity.dropoffloc_str);
                multipart.addFormField("picuplat", "" + MainActivity.pickup_lat_str);
                multipart.addFormField("pickuplon", "" + MainActivity.pickup_lon_str);
                multipart.addFormField("droplat", "" + MainActivity.drop_lat_str);
                multipart.addFormField("droplon", "" + MainActivity.drop_lon_str);
                multipart.addFormField("shareride_type", "no");
                multipart.addFormField("booktype", MainActivity.booktype);
                multipart.addFormField("passenger", "1");
                multipart.addFormField("current_time", "" + format);
                multipart.addFormField("timezone", "" + time_zone);
                multipart.addFormField("payment_type", "" + payment_type_str);
                multipart.addFormField("status", "" + MainActivity.booktype);
                if (MainActivity.booktype == null || MainActivity.booktype.equalsIgnoreCase("") || MainActivity.booktype.equalsIgnoreCase("Now")) {

                } else {
                    multipart.addFormField("picklatertime", MainActivity.time_str);
                    multipart.addFormField("picklaterdate", MainActivity.date_str.trim());
                }
                if (Fullpath.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(Fullpath);
                    multipart.addFilePart("route_img", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // progressbar.setVisibility(View.GONE);

            Log.e("RESEND RESPONSE", ">> " + result);
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        if (jsonObject.getString("message").equalsIgnoreCase("already in ride")) {
                            if (isVisible) {
                                if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                                    waitingdialogSts.dismiss();
                                    if (yourCountDownTimer != null) {
                                        yourCountDownTimer.cancel();
                                    }

                                }
                            }

                            Intent i = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                            i.putExtra("request_id", "");
                            i.putExtra("driver_id", "");
                            startActivity(i);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    public class SendRequestToDriver extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  progressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456
            String charset = "UTF-8";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            String postReceiverUrl = "";
            if (MainActivity.booktype.equalsIgnoreCase("Letter")) {
                postReceiverUrl = BaseUrl.baseurl + "latter_booking_request?";
            } else {
                postReceiverUrl = BaseUrl.baseurl + "booking_request?";
            }
            Log.e("BOOKING URL >>", ">> " + postReceiverUrl + "car_type_id=" + selected_car_id + "&user_id=" + user_id + "&picuplocation=" + MainActivity.pickuploc_str + "&dropofflocation=" + MainActivity.dropoffloc_str + "&picuplat=" + MainActivity.pickup_lat_str + "&pickuplon=" + MainActivity.pickup_lon_str + "&droplat=" + MainActivity.drop_lat_str + "&droplon=" + MainActivity.drop_lon_str + "&shareride_type=no&booktype=" + MainActivity.booktype + "&passenger=1&current_time=" + date + "&timezone=" + time_zone + "&status=" + MainActivity.booktype);


            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
                multipart.addFormField("car_type_id", "10");
                //multipart.addFormField("car_type_id", selected_car_id);
                multipart.addFormField("device_type", "android");
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("picuplocation", MainActivity.pickuploc_str);
                multipart.addFormField("dropofflocation", MainActivity.dropoffloc_str);
                multipart.addFormField("picuplat", "" + MainActivity.pickup_lat_str);
                multipart.addFormField("pickuplon", "" + MainActivity.pickup_lon_str);
                multipart.addFormField("droplat", "" + MainActivity.drop_lat_str);
                multipart.addFormField("droplon", "" + MainActivity.drop_lon_str);
                multipart.addFormField("shareride_type", "no");
                multipart.addFormField("booktype", MainActivity.booktype);
                multipart.addFormField("passenger", "1");
                multipart.addFormField("current_time", "" + format);
                multipart.addFormField("timezone", "" + time_zone);
                multipart.addFormField("payment_type", "" + payment_type_str);
                multipart.addFormField("status", "" + MainActivity.booktype);
                multipart.addFormField("apply_code", coupon_str);
                multipart.addFormField("street_no", streatnumber_str);
                multipart.addFormField("reference_number", reference_number);
                if (MainActivity.booktype == null || MainActivity.booktype.equalsIgnoreCase("") || MainActivity.booktype.equalsIgnoreCase("Now")) {

                } else {
                    multipart.addFormField("picklatertime", MainActivity.time_str);
                    multipart.addFormField("picklaterdate", MainActivity.date_str.trim());
                }
                if (Fullpath.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(Fullpath);
                    multipart.addFilePart("route_img", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // progressbar.setVisibility(View.GONE);
            Log.e("BOOKING RES", " >> " + result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("status");
                    if (msg.equalsIgnoreCase("1")) {


                        if (MainActivity.booktype.equalsIgnoreCase("Letter")) {
                            /*booking_confirmation();*/
                            bookConfirm();
                        } else {
                            request_id = jsonObject.getString("request_id");
                            showWaitPopup();
                            //  startTimer();
                        }
                       // booking_confirmation();
                    } else {
                        noDriverFound();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }



    private void notfoundpopup() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        heading_tv.setText("" + getResources().getString(R.string.thisreqnotacc));
        message_tv.setText("" + getResources().getString(R.string.plstryagain));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.selcartype), Toast.LENGTH_LONG).show();
                } else {
                    Calendar c = Calendar.getInstance();
                    TimeZone tz = c.getTimeZone();
                    Log.e("TIME ZONE >>", tz.getDisplayName());
                    Log.e("TIME ZONE ID>>", tz.getID());
                    time_zone = tz.getID();

                    new SendRequestToDriver().execute();
                }

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                finish();

            }
        });
        canceldialog.show();


    }

    private void areusureCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                // stopCountDownTimer();
                if (timerObj != null) {
                    timerObj.cancel();
                }
                if (yourCountDownTimer != null) {
                    yourCountDownTimer.cancel();
                }


                new CancelRide().execute();
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
    public void onResume()
    {
        super.onResume();
        isVisible = true;
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TripStatusAct.this.getApplicationContext());


        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    new GetNearestDriverAll().execute();
                } else {
                    new GetNearestDriver().execute();
                }

                //

            }
        }, 0, 12, TimeUnit.SECONDS);

        GetUpdatedCarList();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        if (!apists){
            new GetCurrentBooking().execute();
        }

    }

    public void GetUpdatedCarList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timerTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        Log.e("TIMER", " >RUN");
                        new GetCarLists().execute();
                    }
                };

                Timer mTimer2 = new Timer();
                mTimer2.schedule(timerTask2, 1500, 13000);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (scheduleTaskExecutor != null) {
            scheduleTaskExecutor.shutdown();
        }
    }

    @Override
    public void onPause() {


        super.onPause();
        isVisible = false;
        LocalBroadcastManager.getInstance(TripStatusAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
        // timerObj.cancel();
        if (scheduleTaskExecutor != null) {
            scheduleTaskExecutor.shutdown();
        }

        if (dialogSts != null && dialogSts.isShowing()) {
            dialogSts.cancel();
        }
/*
        if (yourCountDownTimer != null) {
            yourCountDownTimer.cancel();
        }
*/
    }


    private void bookConfirm() {
        //   Log.e("War Msg in dialog", war_msg);
        dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.letterbook_confirmlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes = (TextView) dialogSts.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                MainActivity.mylocset = true;
                MainActivity.ride_status = "complete";
                MainActivity.pickuploc_str = "";
                MainActivity.dropoffloc_str = "";
                MainActivity.pickup_lat_str = 0;
                MainActivity.pickup_lon_str = 0;
                MainActivity.drop_lat_str = 0;
                MainActivity.drop_lon_str = 0;

                dialogSts.dismiss();
                finish();
            }
        });
        dialogSts.show();


    }

    private void booking_confirmation() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.booking_success_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                Intent i = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                i.putExtra("request_id", request_id);
                i.putExtra("driver_id", driver_id);
                startActivity(i);
                finish();
            }
        });
        dialogSts.show();


    }

    private void requestCancel() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_heading_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }


            }
        });
        dialogSts.show();


    }

    private void noDriverFound() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_heading_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        TextView txtmsg = (TextView) dialogSts.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) dialogSts.findViewById(R.id.bodymsg);
        txtmsg.setText(getResources().getString(R.string.drivernotfound));

        bodymsg.setText(getResources().getString(R.string.nodriverfound));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


            }
        });
        dialogSts.show();


    }

    public void CaptureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap1 = snapshot;


                try {
                    Fullpath = getOutputMediaFile();

                    mapfile = new File(Fullpath);
                    FileOutputStream out = new FileOutputStream(Fullpath);
                    bitmap1.compress(Bitmap.CompressFormat.PNG, 90, out);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception >>>>", e.getMessage().toString());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    Log.e("Exception >>>>", e.getMessage().toString());
                    Toast.makeText(getApplicationContext(), e + "Memory Full,Please Remove Data", Toast.LENGTH_SHORT).show();
                }

                // openShareImageDialog(Fullpath);
            }
        };
        gMap.snapshot(callback);
    }

    private static String getOutputMediaFile() {


        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/HITCH");
        myDir.mkdirs();


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());


        File file = new File(myDir, timeStamp + ".jpg");


        String currentTemplatePath = file.toString();
        return currentTemplatePath;
    }

    public void captureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    View mView = getWindow().getDecorView().getRootView();
                    mView.setDrawingCacheEnabled(true);
                    Bitmap backBitmap = mView.getDrawingCache();
                    bmOverlay = Bitmap.createBitmap(
                            backBitmap.getWidth(), backBitmap.getHeight(),
                            backBitmap.getConfig());
                    Canvas canvas = new Canvas(bmOverlay);
                    canvas.drawBitmap(snapshot, new Matrix(), null);
                    canvas.drawBitmap(backBitmap, 0, 0, null);
                    FileOutputStream out = new FileOutputStream(
                            Environment.getExternalStorageDirectory()
                                    + "/HITCH"
                                    + System.currentTimeMillis() + ".png");

                    bmOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);
                    String screen = BitMapToString(bmOverlay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        gMap.snapshot(callback);

    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable {

        private Activity context;
        private List<String> l2 = new ArrayList<>();
        private LayoutInflater layoutInflater;
        private WebOperations wo;
        private String lat, lon;

        public GeoAutoCompleteAdapter(Activity context, List<String> l2, String lat, String lon) {
            this.context = context;
            this.l2 = l2;
            this.lat = lat;
            this.lon = lon;
            layoutInflater = LayoutInflater.from(context);
            wo = new WebOperations(context);
        }

        @Override
        public int getCount() {

            return l2.size();
        }

        @Override
        public Object getItem(int i) {
            return l2.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.geo_search_result, viewGroup, false);
            TextView geo_search_result_text = (TextView) view.findViewById(R.id.geo_search_result_text);
            try {
                geo_search_result_text.setText(l2.get(i));
                geo_search_result_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        InputMethodManager inputManager = (InputMethodManager)
                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        if (MainActivity.pickuploc_str == null || MainActivity.pickuploc_str.equalsIgnoreCase("")) {

                        } else if (MainActivity.dropoffloc_str == null || MainActivity.dropoffloc_str.equalsIgnoreCase("")) {

                        } else {

                        }
                        if (sts) {

                            pickuplocation.setText("" + l2.get(i));
                           // pickuplocation.dismissDropDown();
                            MainActivity.dropoffloc_str = dropofflocation.getText().toString();
                            MainActivity.pickuploc_str = pickuplocation.getText().toString();
                            if (MainActivity.dropoffloc_str == null || MainActivity.dropoffloc_str.equalsIgnoreCase("") || MainActivity.pickuploc_str == null || MainActivity.pickuploc_str.equalsIgnoreCase("")) {
                            } else {
                                new GetPickRoute().execute();
                            }

                        } else {
                            dropofflocation.setText("" + l2.get(i));
                          //  dropofflocation.dismissDropDown();
                            MainActivity.dropoffloc_str = dropofflocation.getText().toString();
                            MainActivity.pickuploc_str = pickuplocation.getText().toString();
                            if (MainActivity.dropoffloc_str == null || MainActivity.dropoffloc_str.equalsIgnoreCase("") || MainActivity.pickuploc_str == null || MainActivity.pickuploc_str.equalsIgnoreCase("")) {
                            } else {
                                new GetPickRoute().execute();
                            }

                        }

                    }
                });

            } catch (Exception e) {

            }

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyBXvrm0wKFaamcHvScRaQ2_Oi9lZw8if6k&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=geocode&sensor=true");
                        String result = null;
                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);


                        // Assign the data to the FilterResults
                        filterResults.values = l2;
                        filterResults.count = l2.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count != 0) {
                        l2 = (List) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        private void parseJson(String result) {
            try {
                l2 = new ArrayList<>();
                JSONObject jk = new JSONObject(result);

                JSONArray predictions = jk.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject js = predictions.getJSONObject(i);
                    l2.add(js.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private List<Address> findLocations(Context context, String query_text) {

            List<Address> geo_search_results = new ArrayList<Address>();

            Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);
            List<Address> addresses = null;

            try {
                // Getting a maximum of 15 Address that matches the input text
                addresses = geocoder.getFromLocationName(query_text, 15);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return addresses;
        }
    }

    private class GetPickRoute extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
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
            String address1 = MainActivity.pickuploc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    MainActivity.pickup_lat_str = location.getDouble("lat");
                    MainActivity.pickup_lon_str = location.getDouble("lng");
                    new GetDropOffLatRoute().execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private class GetDropOffLatRoute extends AsyncTask<String, String, String> {
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
            String address1 = MainActivity.dropoffloc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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
            //progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    MainActivity.drop_lat_str = location.getDouble("lat");
                    MainActivity.drop_lon_str = location.getDouble("lng");
                    if (gMap == null) {

                    } else {
                        gMap.clear();
                        //  MarkerOptions markers = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickpoint)).flat(true).anchor(0.5f, 0.5f);
                        double distance_difference = distFrom(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str, MainActivity.drop_lat_str, MainActivity.drop_lon_str);
                        double distance_km = 0;
                        if (distance_difference > 0) {
                            distance_km = distance_difference * 1.5;
                        } else {
                            distance_km = 1;
                        }
                        String distan_str = String.format("%.1f", new BigDecimal(distance_km));
                        String distan_str_km = String.format("%.1f", new BigDecimal(distance_difference));
                        //MarkerOptions myloc = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).icon(BitmapDescriptorFactory.fromBitmap(myLocationWindow("" + distan_str, MainActivity.pickuploc_str, distan_str_km)));

                        //   MarkerOptions myloc = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).icon(BitmapDescriptorFactory.fromBitmap(myLocationWindow(""+distan_str)));
                        // MarkerOptions myjob = new MarkerOptions().position(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str)).icon(BitmapDescriptorFactory.fromResource(R.drawable.droppoint)).flat(true).anchor(0.5f, 0.5f);
                        //MarkerOptions myjob = new MarkerOptions().position(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str)).icon(BitmapDescriptorFactory.fromBitmap(myJobWindow(Favourites.droplocfav_type, MainActivity.dropoffloc_str)));

                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                        gMap.animateCamera(zoom);
                        //  gMap.addMarker(markers);
                        // gMap.addMarker(marker2);
                        //  myloc_marker = gMap.addMarker(myloc);
                        // gMap.addMarker(myjob);
                        gMap.moveCamera(center);

                        String url = getUrl(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str));
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str))
                                .include(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str))
                                .build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                    }
                    //  new GetFareRate().execute(selected_car_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
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

        return dist;
    }


// progress bar code

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            startCountDownTimer();

        } else {

            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    private void setTimerValues() {
        int time = 1;
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 59 * 1000;
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                //progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                // setProgressBarValues();
                //  // hiding the reset icon
                setTimerValues();
                stopCountDownTimer();
                // changing stop icon to start icon
                // making edit text editable

                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
               */ TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private String hmsTimeFormatterMin(long milliSeconds) {

        String hms = String.format("%02d %02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),
              */  TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        // progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        // progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    //end progress bar

    private void selectDateTime() {
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.selectdate_newlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView date_tv = (TextView) dialogSts.findViewById(R.id.date_tv);
        time_tv = (TextView) dialogSts.findViewById(R.id.time_tv);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);
        TextView ok = (TextView) dialogSts.findViewById(R.id.ok);
        LinearLayout time_lay = (LinearLayout) dialogSts.findViewById(R.id.time_lay);
        LinearLayout date_lay = (LinearLayout) dialogSts.findViewById(R.id.date_lay);
        date_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TripStatusAct.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                String mon = MONTHS[monthOfYear];
                                int mot = monthOfYear + 1;
                                String month = "";
                                if (mot >= 10) {
                                    month = String.valueOf(mot);
                                } else {
                                    month = "0" + String.valueOf(mot);
                                }
                                String daysss = "";
                                if (dayOfMonth >= 10) {
                                    daysss = String.valueOf(dayOfMonth);
                                } else {
                                    daysss = "0" + String.valueOf(dayOfMonth);
                                }
                                MainActivity.date_str = "" + year + "-" + month + "-" + daysss;
                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();
                                if (MainActivity.time_str == null || MainActivity.time_str.equalsIgnoreCase("")) {
                                    date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                    addTime();
                                } else {
                                    String sss = MainActivity.date_str.trim();
                                    String dtStart = sss + " " + MainActivity.time_str;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        Date selecteddate = format.parse(dtStart);
                                        long diff = selecteddate.getTime() - date.getTime();
                                        long diffSeconds = diff / 1000 % 60;
                                        diffMinutes = diff / (60 * 1000) % 60;
                                        diffHours = diff / (60 * 60 * 1000);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (diffHours >= 1) {
                                        date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                        addTime();
                                    } else if (diffMinutes >= 30) {
                                        date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                        addTime();
                                    } else {
                                        MainActivity.date_str = "";
                                        date_tv.setText("");
                                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.date_str == null || MainActivity.date_str.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, "Please Select Date", Toast.LENGTH_SHORT).show();
                } else if (MainActivity.time_str == null || MainActivity.time_str.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, "Please Select Time", Toast.LENGTH_SHORT).show();
                } else {
                    dialogSts.dismiss();
                    MainActivity.booktype = "Letter";

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.date_str = "";
                MainActivity.time_str = "";
                dialogSts.dismiss();
            }
        });
        time_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TripStatusAct.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int hour = hourOfDay;
                                int fullhour = hourOfDay;
                                int minutes = minute;
                                String timeSet = "";
                                if (hour > 12) {
                                    hour -= 12;
                                    timeSet = "PM";
                                } else if (hour == 0) {
                                    hour += 12;
                                    timeSet = "AM";
                                } else if (hour == 12) {
                                    timeSet = "PM";
                                } else {
                                    timeSet = "AM";
                                }
                                String min = "";
                                if (minutes < 10)
                                    min = "0" + minutes;
                                else
                                    min = String.valueOf(minutes);
                                MainActivity.time_str = "" + hourOfDay + ":" + min + ":00";
                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();

                                if (MainActivity.date_str == null || MainActivity.date_str.equalsIgnoreCase("")) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);

                                } else {
                                    String sss = MainActivity.date_str.trim();
                                    String dtStart = sss + " " + MainActivity.time_str;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        Date selecteddate = format.parse(dtStart);
                                        long diff = selecteddate.getTime() - date.getTime();
                                        long diffSeconds = diff / 1000 % 60;
                                        diffMinutes = diff / (60 * 1000) % 60;
                                        diffHours = diff / (60 * 60 * 1000);
                                        Log.e("diffHours ", " >> " + diffHours + " " + diffMinutes);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (diffHours >= 1) {
                                        time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                    } else if (diffMinutes >= 30) {
                                        time_tv.setText("" + hour + ":" + min + " " + timeSet);

                                    } else {
                                        MainActivity.time_str = "";
                                        time_tv.setText("");
                                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        dialogSts.show();


    }

    public void addTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(TripStatusAct.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        int hour = hourOfDay;
                        int fullhour = hourOfDay;
                        int minutes = minute;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12) {
                            timeSet = "PM";
                        } else {
                            timeSet = "AM";
                        }
                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes;
                        else
                            min = String.valueOf(minutes);
                        MainActivity.time_str = "" + hourOfDay + ":" + min + ":00";
                        Calendar c = Calendar.getInstance();
                        Date date = c.getTime();

                        if (MainActivity.date_str == null || MainActivity.date_str.equalsIgnoreCase("")) {
                            if (time_tv != null) {
                                time_tv.setText("" + hour + ":" + min + " " + timeSet);
                            }


                        } else {
                            String sss = MainActivity.date_str.trim();
                            String dtStart = sss + " " + MainActivity.time_str;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date selecteddate = format.parse(dtStart);
                                long diff = selecteddate.getTime() - date.getTime();
                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                diffHours = diff / (60 * 60 * 1000);
                                Log.e("diffHours ", " >> " + diffHours + " " + diffMinutes);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (diffHours >= 1) {
                                if (time_tv != null) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                }

                            } else if (diffMinutes >= 30) {
                                if (time_tv != null) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                }
                            } else {
                                MainActivity.time_str = "";
                                if (time_tv != null) {
                                    time_tv.setText("");
                                }

                                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public class SendREQdriver extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                // progressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/confirm_request?request_id=691
            String charset = "UTF-8";
            String postReceiverUrl = "";
            postReceiverUrl = BaseUrl.baseurl + "confirm_request?";

            Date date = new Date();
            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
                multipart.addFormField("request_id", request_id);
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("success")) {
                        request_id = jsonObject.getString("request_id");
                        showWaitPopup();
                    } else {
                        //noDriverFound();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private class GetCarLists extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCarBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/car_list
            //http://mobileappdevelop.co/NAXCAN/webservice/car_type_list?user_id=1&picuplat=22.699693&pickuplon=75.867280&droplat=22.753285&droplon=75.893696
            try {
                String postReceiverUrl = BaseUrl.baseurl + "car_type_list?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("picuplat", MainActivity.pickup_lat_str);
                params.put("pickuplon", MainActivity.pickup_lon_str);
                params.put("droplat", MainActivity.drop_lat_str);
                params.put("droplon", MainActivity.drop_lon_str);
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
            Log.e("CAR TYPE TRIP", " > >" + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MyCarBean myCarBean = new MyCarBean();
                            myCarBean.setId(jsonObject1.getString("id"));
                            myCarBean.setCarname(jsonObject1.getString("car_name"));
                            myCarBean.setCar_image(jsonObject1.getString("car_image"));
                            //myCarBean.setDistance(jsonObject1.getString("distance"));
                            myCarBean.setDistance(jsonObject1.getString("miles"));
                            myCarBean.setTotal(jsonObject1.getString("total"));
                            myCarBean.setCab_find(jsonObject1.getString("cab_find"));
                            if (selected_car_id != null) {
                                if (selected_car_id.equalsIgnoreCase(jsonObject1.getString("id"))) {
                                    myCarBean.setSelected(true);
                                } else {
                                    myCarBean.setSelected(false);
                                }
                            } else {
                                myCarBean.setSelected(false);
                            }
                            if (jsonObject1.getString("id").equalsIgnoreCase("10")) {
                                if (jsonObject1.getString("total") != null && !jsonObject1.getString("total").equalsIgnoreCase("")) {
                                    Estimate_Amount = Double.parseDouble(jsonObject1.getString("total"));
                                }
                                fare_rate_tv.setText("$" + jsonObject1.getString("total"));
                                if (jsonObject1.getString("cab_find").equalsIgnoreCase("no_cab")) {
                                    away_minute.setText(getResources().getString(R.string.nodrivers_s));

                                } else {
                                    away_minute.setText("" + jsonObject1.getString("cab_find") + " min");

                                }
                            }


                            myCarBeanArrayList.add(myCarBean);

                        }

/*                        if (myCarBeanArrayList_check == null || myCarBeanArrayList_check.isEmpty()) {
                            Log.e("LEVEL", "1");
                            myCarBeanArrayList_check = new ArrayList<>();
                            myCarBeanArrayList_check.addAll(myCarBeanArrayList);
                           *//* selected_car_id = "";
                            MainActivity.selected_car_id="";
                            min_away = "";*//*
                            carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                            cartypelist.setAdapter(carHoriZontalLay);
                            carHoriZontalLay.notifyDataSetChanged();
                        } else {
                            Log.e("LEVEL", "2");
                            if (myCarBeanArrayList.size() == myCarBeanArrayList_check.size()) {
                                Log.e("LEVEL", "3");
                                boolean checked = true;
                                for (int k = 0; k < myCarBeanArrayList.size(); k++) {
                                    if (myCarBeanArrayList.get(k).getCab_find().equalsIgnoreCase(myCarBeanArrayList_check.get(k).getCab_find())) {
                                        checked = true;
                                        Log.e("LEVEL", "4");
                                    } else {
                                        Log.e("LEVEL", "5");
                                        checked = false;
                                        break;
                                    }
                                }
                                if (!checked) {
                                    Log.e("LEVEL", "6");
                                    *//*selected_car_id = "";
                                    MainActivity.selected_car_id="";
                                    min_away = "";*//*
                                    myCarBeanArrayList_check = new ArrayList<>();
                                    myCarBeanArrayList_check.addAll(myCarBeanArrayList);
                                    carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                                    cartypelist.setAdapter(carHoriZontalLay);
                                    carHoriZontalLay.notifyDataSetChanged();
                                }
                            } else {
                                Log.e("LEVEL", "else");
                                *//*selected_car_id = "";
                                min_away = "";
                                MainActivity.selected_car_id="";*//*
                                myCarBeanArrayList_check = new ArrayList<>();
                                myCarBeanArrayList_check.addAll(myCarBeanArrayList);
                                carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                                cartypelist.setAdapter(carHoriZontalLay);
                                carHoriZontalLay.notifyDataSetChanged();
                            }

                        }*/

                        carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                        cartypelist.setAdapter(carHoriZontalLay);
                        carHoriZontalLay.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetNearestDriverAll extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            driverDetailBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_available_driver?latitude=22.7196&longitude=75.8577
            //http://mobileappdevelop.co/NAXCAN/webservice/available_car_driver?latitude=22.9650879&longitude=75.4492206&car_type_id=3

            //http://mobileappdevelop.co/NAXCAN/webservice/available_car_driver?latitude=22.9650879&longitude=75.4492206&car_type_id=3&user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_available_driver?";
                String CheckUrl = BaseUrl.baseurl + "get_available_driver?latitude=" + latitude + "&longitude=" + longitude + "&user_id=" + MainActivity.user_id;
                // String postReceiverUrl = "http://technorizen.com/WORKSPACE1/shipper/webservice/get_available_driver?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                if (latitude == 0.0) {
                    params.put("latitude", MainActivity.pickup_lat_str);
                    params.put("longitude", MainActivity.pickup_lon_str);
                } else {
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                }
                params.put("user_id", MainActivity.user_id);
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
            Log.e("ALL DRIVER", ">>" + result);
            if (result == null) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
                //  Toast.makeText(MainActivity.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("null")) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

            } else if (result.isEmpty()) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

                //  Toast.makeText(MainActivity.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    String msg = jsonobj.getString("message");
                    if (msg.equalsIgnoreCase("success")) {
                        if (gMap == null) {

                        } else {


                        }
                        JSONArray jsonArray = jsonobj.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String results = jsonObject.getString("result");
                            if (results.equalsIgnoreCase("successful")) {
                                JSONObject esti = jsonArray.getJSONObject(0);


                                DriverDetailBean driverDetailBean = new DriverDetailBean();
                                driverDetailBean.setId(jsonObject.getString("id"));
                                if (jsonObject.getString("lat") == null || jsonObject.getString("lat").equalsIgnoreCase("")) {
                                    driverDetailBean.setLatitude(Double.parseDouble("0.0"));
                                    driverDetailBean.setLongitude(Double.parseDouble("0.0"));

                                } else {
                                    driverDetailBean.setLatitude(Double.parseDouble(jsonObject.getString("lat")));
                                    driverDetailBean.setLongitude(Double.parseDouble(jsonObject.getString("lon")));

                                }
                                driverDetailBean.setFirst_name(jsonObject.getString("first_name"));
                                driverDetailBean.setEstimatetime(jsonObject.getInt("estimate_time"));
                                driverDetailBean.setCartypeid(jsonObject.getString("car_type_id"));


                                driverDetailBeanArrayList.add(driverDetailBean);
                            } else {

                            }


                        }

                        if (driverDetailBeanArrayList == null || driverDetailBeanArrayList.isEmpty()) {

                        } else {
                            if (driverDetailBeanArrayList.size() > 0) {

                                for (DriverDetailBean point : driverDetailBeanArrayList) {
                                    LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());


                                }
                            }

                        }


                    } else {
                        if (gMap == null) {

                        } else {


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetNearestDriver extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            driverDetailBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "available_car_driver?";
                // String postReceiverUrl = "http://technorizen.com/WORKSPACE1/shipper/webservice/get_available_driver?";
                String CheckUrl = BaseUrl.baseurl + "available_car_driver?car_type_id=" + selected_car_id + "&latitude=" + latitude + "&longitude=" + longitude + "&user_id=" + MainActivity.user_id;

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                if (latitude == 0.0) {
                    params.put("latitude", MainActivity.pickup_lat_str);
                    params.put("longitude", MainActivity.pickup_lon_str);
                } else {
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                }
                params.put("car_type_id", selected_car_id);
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
            Log.e("SINGLE DRIVER CHECK", ">>" + result);
            if (result == null) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
                //  Toast.makeText(MainActivity.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("null")) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

            } else if (result.isEmpty()) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

                //  Toast.makeText(MainActivity.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    String msg = jsonobj.getString("message");
                    if (msg.equalsIgnoreCase("success")) {
                        if (gMap == null) {

                        } else {

                        }
                        JSONArray jsonArray = jsonobj.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String results = jsonObject.getString("result");
                            if (results.equalsIgnoreCase("successful")) {
                                JSONObject esti = jsonArray.getJSONObject(0);
                                DriverDetailBean driverDetailBean = new DriverDetailBean();
                                driverDetailBean.setId(jsonObject.getString("id"));
                                if (jsonObject.getString("lat") == null || jsonObject.getString("lat").equalsIgnoreCase("")) {
                                    driverDetailBean.setLatitude(Double.parseDouble("0.0"));
                                    driverDetailBean.setLongitude(Double.parseDouble("0.0"));

                                } else {
                                    driverDetailBean.setLatitude(Double.parseDouble(jsonObject.getString("lat")));
                                    driverDetailBean.setLongitude(Double.parseDouble(jsonObject.getString("lon")));

                                }
                                driverDetailBean.setFirst_name(jsonObject.getString("first_name"));
                                driverDetailBean.setCartypeid(jsonObject.getString("car_type_id"));
                                driverDetailBean.setEstimatetime(jsonObject.getInt("estimate_time"));


                                driverDetailBeanArrayList.add(driverDetailBean);
                            }
                        }

                        if (driverDetailBeanArrayList == null || driverDetailBeanArrayList.isEmpty()) {

                        } else {
                            if (driverDetailBeanArrayList.size() > 0) {

                                for (DriverDetailBean point : driverDetailBeanArrayList) {
                                    LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());


                                }
                            }

                        }


                    } else {

                        if (gMap == null) {

                        } else {

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }


    }

    private void showWaitPopup() {

        waitingdialogSts = new Dialog(TripStatusAct.this);
        waitingdialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingdialogSts.setCancelable(false);
        waitingdialogSts.setContentView(R.layout.new_custom_waitinglay);
        waitingdialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView sendReq = waitingdialogSts.findViewById(R.id.sendReq);
        final TextView cancel = waitingdialogSts.findViewById(R.id.cancel);
        final TextView pick_location = waitingdialogSts.findViewById(R.id.pick_location);
        final TextView droplocation = waitingdialogSts.findViewById(R.id.droplocation);
        pick_location.setText("" + MainActivity.pickuploc_str);
        droplocation.setText("" + MainActivity.dropoffloc_str);
        final ProgressBar progressBarCircle = (ProgressBar) waitingdialogSts.findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) waitingdialogSts.findViewById(R.id.textViewTime);
        // timeCountInMilliSeconds = 1 * 61000;
        // timerStatus = TimerStatus.STOPPED;
        //  startStop();


        int sec = 60;
        int mili = 1000;
        int newsec = 1;
        Log.e("diff_second ?", "POPUP" + diff_second);
        if (diff_second == null || diff_second.equalsIgnoreCase("")) {
        } else {
            int difernce = Integer.parseInt(diff_second);
            newsec = sec - difernce;
        }
        Log.e("newsec >>", "dd " + newsec);
        timeCountInMilliSeconds = 1 * newsec * mili;
        Log.e("Count Timer", "gg " + timeCountInMilliSeconds);
        timerStatus = TimerStatus.STOPPED;
        // startStop();
        // progressBarCircle.setMax((int) 10);


        progressBarCircle.setMax((int) 60);
        //   progressBarCircle.setProgress(1);
        if (yourCountDownTimer != null) {
            yourCountDownTimer.cancel();
        }
        yourCountDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                Log.e("TICK 1", "" + millisUntilFinished / 1000);
                if (!apists){
                    new GetCurrentBooking().execute();
                }
                /*if (millisUntilFinished / 1000 == 45) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    if (!apists){
                        new GetCurrentBooking().execute();
                    }

                    //new ResendRequest().execute();
                }
                if (millisUntilFinished / 1000 == 30) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    if (!apists){

                        new GetCurrentBooking().execute();

                    }

                    //new ResendRequest().execute();
                }
                if (millisUntilFinished / 1000 == 20) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    if (!apists){

                        new GetCurrentBooking().execute();

                    }

                    //new ResendRequest().execute();
                }
                if (millisUntilFinished / 1000 == 15){
                    Log.e("TICK",""+millisUntilFinished / 1000);
                    if (!apists) {
                        new GetCurrentBooking().execute();
                        *//*new ResendRequest().execute();*//*
                    }
                }*/
/*
                if (millisUntilFinished / 1000==30){
                  //  new GetCurrentBooking().execute();
                }
*/

            }

            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (isVisible) {
                    if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                        waitingdialogSts.dismiss();

                    }
                    notfoundpopup();
                }


            }
        }.start();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areusureCancelRide();

            }
        });

if (TripStatusAct.this.isFinishing()){

}
else {
    if (isVisible){
        waitingdialogSts.show();
    }

}



    }

    private class CancelRide extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
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
//http://mobileappdevelop.co/NAXCAN/webservice/cancel_ride?request_id=230
            try {
                String postReceiverUrl = BaseUrl.baseurl + "cancel_ride?";
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(TripStatusAct.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(TripStatusAct.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                    } else {
                        if (yourCountDownTimer != null) {
                            yourCountDownTimer.cancel();
                        }
                        if (waitingdialogSts == null) {

                        } else {
                            waitingdialogSts.dismiss();
                        }
                        if (content1 != null) {
                            content1.stopRippleAnimation();
                        }
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    private void applyCoupon() {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.apply_coupon);
        TextView canceltv = (TextView) dialogSts.findViewById(R.id.canceltv);
        TextView applytv = (TextView) dialogSts.findViewById(R.id.applytv);
        final EditText apply_et = (EditText) dialogSts.findViewById(R.id.apply_et);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        canceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        applytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coupon_code = apply_et.getText().toString();
                if (coupon_code == null || coupon_code.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.entercode), Toast.LENGTH_LONG).show();
                } else {
                    dialogSts.dismiss();
                    new ApplyCouponAsc().execute(coupon_code);
                }

            }
        });

        dialogSts.show();


    }

    private class ApplyCouponAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ac_dialog != null) {
                ac_dialog.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://halatx.halasmart.com/hala/webservice/apply_code?code=JUN20
            try {
                String postReceiverUrl = BaseUrl.baseurl + "apply_code?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("code", strings[0]);
                params.put("user_id", user_id);
                coupon_str = strings[0];

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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        coupon_str = "";
                        if (jsonObject.getString("result").equalsIgnoreCase("code already expired")) {
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("1");
                        } else if (jsonObject.getString("result").equalsIgnoreCase("code already used")) {
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("2");
                        } else if (jsonObject.getString("result").equalsIgnoreCase("code not exist")) {
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("3");
                        }


                    } else {
                        couponCodeSucc();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void couponCodeSucc() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);
        statusmessage.setText("" + getResources().getString(R.string.yourcodeappsucc));
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discount_tv.setVisibility(View.VISIBLE);
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void invalidCode(String s) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);

        if (s.equalsIgnoreCase("1")) {
            statusmessage.setText("" + getResources().getString(R.string.expire));
        } else if (s.equalsIgnoreCase("2")) {
            statusmessage.setText("" + getResources().getString(R.string.allreadyused));
        } else if (s.equalsIgnoreCase("3")) {
            statusmessage.setText("" + getResources().getString(R.string.codeisinvalid));
        }


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
}
