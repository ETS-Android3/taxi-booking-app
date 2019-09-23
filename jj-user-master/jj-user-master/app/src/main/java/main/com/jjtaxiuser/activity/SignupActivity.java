package main.com.jjtaxiuser.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.ACProgressCustom;
import main.com.jjtaxiuser.constant.GPSTracker;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.restapi.ApiClient;

public class SignupActivity extends AppCompatActivity {
    private Button signupbut;
    private RelativeLayout backbut;
    private EditText first_name, last_name,  password_et, email_et;
    private String first_name_str = "", last_name_str = "", phone_et_str = "", password_str = "", email_str = "";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude=0,longitude=0;
    GPSTracker gpsTracker;
    MySession mySession;
    ACProgressCustom ac_dialog;
    private EditText phone_et;
    MyLanguageSession myLanguageSession;
    private String language = "",firebase_regid="";
    private CheckBox privacyaccepchk;
    private TextView privacylink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_signup);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        firebase_regid = pref.getString("regId", null);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        checkGps();
        idint();
        clickevent();
    }

    private void clickevent() {
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        privacylink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this,PrivacyPolicyAct.class);
                startActivity(i);
            }
        });
        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String firebase_regid = pref.getString("regId", null);

                first_name_str = first_name.getText().toString();
                last_name_str = last_name.getText().toString();
                phone_et_str = phone_et.getText().toString();
                password_str = password_et.getText().toString();
                email_str = email_et.getText().toString();

                if (first_name_str == null || first_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenfrst),Toast.LENGTH_LONG).show();

                } else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenlst),Toast.LENGTH_LONG).show();

                } else if (phone_et_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenterphone),Toast.LENGTH_LONG).show();

                }
                else if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsemail),Toast.LENGTH_LONG).show();

                }else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenpass),Toast.LENGTH_LONG).show();

                }
                else {
                    if (privacyaccepchk.isChecked()){
                        callSignupapi(first_name_str,last_name_str,phone_et_str,email_str, password_str,firebase_regid,""+latitude,""+longitude,"USER");

                    }
                    else {
                       Toast.makeText(SignupActivity.this,getResources().getString(R.string.privacypolicytxt),Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    private void idint() {
        privacylink = findViewById(R.id.privacylink);
        privacyaccepchk = findViewById(R.id.privacyaccepchk);
        password_et = findViewById(R.id.password_et);
        phone_et = findViewById(R.id.phone_et);
        email_et = findViewById(R.id.email_et);
        last_name = findViewById(R.id.last_name);
        first_name = findViewById(R.id.first_name);
        backbut = findViewById(R.id.backbut);
        signupbut = findViewById(R.id.signupbut);
/*
        phone_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobVeriPopup();
            }
        });
*/

    }
    private void mobVeriPopup() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SignupActivity.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_verilay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView no_tv = (TextView) dialogSts.findViewById(R.id.no_tv);
        final TextView yes_tv = (TextView) dialogSts.findViewById(R.id.yes_tv);
        final EditText enter_number = (EditText) dialogSts.findViewById(R.id.enter_number);
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                finish();
            }
        });
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                // mobile_str = enter_number.getText().toString();



                Intent i = new Intent(SignupActivity.this, MobileVerificationActivity.class);
                startActivity(i);
            }
        });

        dialogSts.show();


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
/*
        if (MobileVerificationActivity.phoneNumberString!=null&&!MobileVerificationActivity.phoneNumberString.equalsIgnoreCase("")){
            phone_et.setText("" + MobileVerificationActivity.phoneNumberString);
            MobileVerificationActivity.phoneNumberString="";
        }
*/
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    private void checkGps() {
        gpsTracker = new GPSTracker(SignupActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
        } else {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            } else {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                Log.e("LAT", "" + latitude);
                Log.e("LON", "" + longitude);

            }
        }


    }
    private void callSignupapi(String first_name_str, String last_name_str, String phone_et_str, String email_str, String password_str, String firebase_regid, String lat, String lon, String user) {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0

        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().SignupCall(first_name_str, last_name_str,phone_et_str,email_str,password_str,firebase_regid,lat,lon,user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("SignupCall >", " >" + responseData);
                        if (object.getString("status").equals("1")) {

                            mySession.setlogindata(responseData);
                            mySession.signinusers(true);
                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }
                        else {
                            Toast.makeText(SignupActivity.this,getResources().getString(R.string.invalidcredential),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                Log.e("TAG", t.toString());
            }
        });
    }

}
