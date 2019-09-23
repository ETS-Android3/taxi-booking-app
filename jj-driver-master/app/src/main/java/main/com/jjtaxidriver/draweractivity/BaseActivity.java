package main.com.jjtaxidriver.draweractivity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.activity.FavoriteUserAct;
import main.com.jjtaxidriver.activity.LanguageAct;
import main.com.jjtaxidriver.activity.LoginAct;
import main.com.jjtaxidriver.activity.PerformanceAct;
import main.com.jjtaxidriver.activity.ProfileAct;
import main.com.jjtaxidriver.activity.RideHistory;
import main.com.jjtaxidriver.activity.TransectionHistory;
import main.com.jjtaxidriver.activity.WalletAct;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import main.com.jjtaxidriver.constant.MySession;
import main.com.jjtaxidriver.service.TrackingService;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationview;
    boolean exit = false;
    MySession mySession;
    private LinearLayout homelay, languagelay,logout,myprofile,callEmergenci,mywallet,ridehistory,myfavuser,performance_lay,trasaction_lay;

    private GoogleApiClient mGoogleApiClient;
private String user_log_data="",user_id="";

    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_base);
        mySession = new MySession(this);

        user_log_data = mySession.getKeyAlldata();
        if (isMyServiceRunning(TrackingService.class)){
            Log.e("BASE SER","RUNNING");
        }
        else {
            Log.e("BASE SER","NOT RUNNING");
            Intent k = new Intent(BaseActivity.this, TrackingService.class);
            startService(k);
        }

        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    Log.e("USER NAME"," >>"+jsonObject1.getString("first_name"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        idinit();
        clickevent();
        adddrawer();

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void clickevent() {
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ProfileAct.class);
                startActivity(i);
            }
        });
        homelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, LanguageAct.class);
                startActivity(i);
            }
        });
        mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, WalletAct.class);
                startActivity(i);
            }
        });
        ridehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, RideHistory.class);
                startActivity(i);
            }
        });
        myfavuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, FavoriteUserAct.class);
                startActivity(i);
            }
        });
        performance_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, PerformanceAct.class);
                startActivity(i);
            }
        });
        trasaction_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, TransectionHistory.class);
                startActivity(i);
            }
        });
        languagelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, LanguageAct.class);
                startActivity(i);
            }
        });callEmergenci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                        "911", null)));
            }
        });
    }

    private void idinit() {
        homelay=findViewById(R.id.homelay);
        languagelay = findViewById(R.id.languagelay);
        performance_lay = findViewById(R.id.performance_lay);
        trasaction_lay = findViewById(R.id.trasaction_lay);
        ridehistory = findViewById(R.id.ridehistory);
        myfavuser = findViewById(R.id.myfavuser);
        mywallet = findViewById(R.id.mywallet);
        callEmergenci= findViewById(R.id.emergencylay);
        myprofile = findViewById(R.id.myprofile);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(BaseActivity.this, TrackingService.class);
                stopService(k);
                mySession.logoutUser();
                Intent i = new Intent(BaseActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });


    }


    private void adddrawer() {

        setSupportActionBar(toolbar);
        navigationview = (NavigationView) findViewById(R.id.navigationview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.setDrawerListener(actionBarDrawerToggle);
// toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);

     /*   View header = View.inflate(context, R.layout.headerlayout, null);
        navigationview.addHeaderView(header);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  new GetUserProfile().execute();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





}



/*
Complete the feedback of given

Change the status of screen a
*/