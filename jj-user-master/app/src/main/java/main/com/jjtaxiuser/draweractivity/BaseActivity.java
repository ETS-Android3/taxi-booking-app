package main.com.jjtaxiuser.draweractivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

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
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.activity.FavoriteDrivers;
import main.com.jjtaxiuser.activity.LanguageAct;
import main.com.jjtaxiuser.activity.LoginAct;
import main.com.jjtaxiuser.activity.ProfileAct;
import main.com.jjtaxiuser.activity.RideHistory;
import main.com.jjtaxiuser.activity.WalletActivity;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.paymentclasses.SaveCardDetail;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationview;
    boolean exit = false;
    MySession mySession;
    private LinearLayout add_card_lay,mywallet,ridehistory,logout,adddriver,myprofile,languagelay,callEmergenci,homelay;
    private TextView mywalletmoney;
    public static String Card_Added_Sts="";
    MyLanguageSession myLanguageSession;
    private String language = "";
    private String user_log_data="",user_id="";
    private TextView user_name;
    private CircleImageView user_imguser_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_base);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_name = findViewById(R.id.user_name);
                    user_id = jsonObject1.getString("id");
                    Log.e("COME TRUE "," >."+jsonObject1.getString("first_name"));
                    user_name.setText(""+jsonObject1.getString("first_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        idinit();
        adddrawer();
        clickevents();

    }

    private void clickevents() {
        mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, WalletActivity.class);
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
        ridehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, RideHistory.class);
                startActivity(i);
            }
        });adddriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, FavoriteDrivers.class);
                startActivity(i);
            }
        });myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ProfileAct.class);
                startActivity(i);
            }
        });add_card_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, SaveCardDetail.class);
                startActivity(i);
            }
        });languagelay.setOnClickListener(new View.OnClickListener() {
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.logoutUser();
                Intent i = new Intent(BaseActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

    }

    private void idinit() {
        homelay=findViewById(R.id.homelay);
        user_imguser_img = findViewById(R.id.user_imguser_img);
        languagelay = findViewById(R.id.languagelay);
        add_card_lay = findViewById(R.id.add_card_lay);
        mywalletmoney = findViewById(R.id.mywalletmoney);
        myprofile = findViewById(R.id.myprofile);
        adddriver = findViewById(R.id.adddriver);
        logout = findViewById(R.id.logout);
        mywallet = findViewById(R.id.mywallet);
        callEmergenci= findViewById(R.id.emergencylay);
        ridehistory = findViewById(R.id.ridehistory);
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
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }

        new GetUserProfile().execute();
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
            Toast.makeText(this, getResources().getString(R.string.pressagain),
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
                        MainActivity.amount = jsonObject1.getString("amount");
                        MainActivity.identity = jsonObject1.getString("identity");
                        mywalletmoney.setText("$"+MainActivity.amount);
                            if (jsonObject1.getString("register_id") == null || jsonObject1.getString("register_id").equalsIgnoreCase("") || jsonObject1.getString("register_id").equalsIgnoreCase("0")|| jsonObject1.getString("register_id").equalsIgnoreCase("null")) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);

                                String firebase_regid = pref.getString("regId", null);
                            if (firebase_regid != null) {
                                new UpdateRegistrationid().execute(firebase_regid);
                            }
                        }
                        JSONArray jsonArray = jsonObject1.getJSONArray("card_detail");
                        if (jsonArray==null||jsonArray.length()==0){
                            Card_Added_Sts ="Not Added";
                        }
                        else {
                            Card_Added_Sts ="Added";
                        }
                        String image_url = jsonObject1.getString("image");
                        if (image_url == null) {

                        } else if (image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else if (image_url.equalsIgnoreCase("")) {

                        } else {

                            Picasso.with(BaseActivity.this).load(image_url).placeholder(R.drawable.profile_ic).into(user_imguser_img);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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


}
