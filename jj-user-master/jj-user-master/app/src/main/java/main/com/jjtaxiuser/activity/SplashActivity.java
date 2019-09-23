package main.com.jjtaxiuser.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.extras.Base64;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.app.Config;
import main.com.jjtaxiuser.constant.GPSTracker;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.utils.NotificationUtils;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private static int SPLASH_TIME_OUT = 3000;

    MySession mySession;
    public static final int RequestPermissionCode = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
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
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    public static double latitude=0.0,longitude=0.0;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gpsTracker = new GPSTracker(SplashActivity.this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        mySession = new MySession(this);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        if (version <= 17)
        {
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    checkLocationSettings();
                }
            }, SPLASH_TIME_OUT);
        }
        else {
            if (checkPermission()) {
                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        checkLocationSettings();
                    }
                }, SPLASH_TIME_OUT);
            } else {
                requestPermission();
            }
        }

/*

        byte[] sha1 = {
                0x64, (byte) 0x59, (byte) 0x0C, (byte)0x6C, (byte)0xF1, (byte) 0xF0, (byte) 0xA6, (byte)0x4C, (byte)0x98, (byte)0x24, (byte)0x6D, (byte) 0xE8, (byte) 0x5C, 0x6F, (byte) 0x46, (byte)0x84, (byte) 0x84, (byte) 0xD4, (byte)0xFC, (byte)0x53
                //SHA1: 74:4C:6E:F8:80:BC:BE:76:82:AC:9F:E9:37:0F:9E:C7:FC:EA:38:AB
                // C6:CC:60:1E:56:83:97:0A:35:D8:2C:0F:F7:F0:A3:A7:92:B4:A6:9B
        };

        byte[] sha2 = {
                0x0A, (byte) 0xB5, (byte) 0x0E, (byte)0x3C, (byte)0x71, (byte) 0x22, (byte) 0xE5, (byte)0x09, (byte)0xBB, (byte)0x4F, (byte)0x52, (byte) 0xF0, (byte) 0x4D, (byte) 0x16, 0x18, (byte)0x4B, (byte) 0xF2, (byte) 0xCC, (byte)0x9B, (byte)0x21

                // SHA1: 66:E2:4B:78:92:14:C5:64:74:50:B3:6A:B8:D2:39:61:15:DE:2A:B4
                // C6:CC:60:1E:56:83:97:0A:35:D8:2C:0F:F7:F0:A3:A7:92:B4:A6:9B
        };
        System.out.println("keyhashGooglePlaySignIn1:"+ Base64.encodeToString(sha1, Base64.NO_WRAP));
        System.out.println("keyhashGooglePlaySignIn2:"+ Base64.encodeToString(sha2, Base64.NO_WRAP));
*/

//SHA1: 64: 59: 0C: 6C: F1: F0: A6: 4C: 98: 24: 6D: E8: 5C: 6F: 46: 84: 84: D4: FC: 53
//SHA1: 0A: B5: 0E: 3C: 71: 22: E5: 09: BB: 4F: 52: F0: 4D: 16: 18: 4B: F2: CC: 9B: 21
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mCurrentLocation==null){
        }
        else {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }
        //    updateLocationUI();
        //  Toast.makeText(this, "Location Change",Toast.LENGTH_SHORT).show();

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


    public boolean checkPermission() {


        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_NETWORK_STATE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int read = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CALL_PHONE = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE);


        return
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                        read == PackageManager.PERMISSION_GRANTED &&
                        write == PackageManager.PERMISSION_GRANTED&&
                        CALL_PHONE == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(SplashActivity.this, new String[]
                {

                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CALL_PHONE
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean ReadPhoneStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactsPermission1 = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean read = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean write = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean CALL_PHONE = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (ReadPhoneStatePermission && CameraPermission1 && ReadContactsPermission1 && read && write && CALL_PHONE) {

                        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                            @Override
                            public void run() {
                                checkLocationSettings();


                            }
                        }, SPLASH_TIME_OUT);

                        // Toast.makeText(SplashActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //   Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                          nesePermission();
                    }
                }

                break;
        }
    }

    public void nesePermission() {


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.allowpermission));
        alertDialog.setMessage(getResources().getString(R.string.nessper));
        alertDialog.setPositiveButton(getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (checkPermission()) {
                    new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                        @Override
                        public void run() {
                            checkLocationSettings();

                        }
                    }, SPLASH_TIME_OUT);
                } else {
                    requestPermission();
                }
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.closeapp), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String firebase_regid = pref.getString("regId", null);

        Log.e("Splash", "Firebase reg id: " + firebase_regid);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
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
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                if (mySession.IsLoggedIn()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrentLocation==null){
                                if (gpsTracker.canGetLocation()) {
                                    latitude = gpsTracker.getLatitude();
                                    longitude = gpsTracker.getLongitude();
                                }
                            }
                            else {
                                latitude = mCurrentLocation.getLatitude();
                                longitude = mCurrentLocation.getLongitude();
                            }

                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            //  Intent i = new Intent(SplashAct.this, CheckLocationActivity.class);
                            startActivity(i);
                            finish();

                        }
                    }, 1500);

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrentLocation==null){
                                if (gpsTracker.canGetLocation()) {
                                    latitude = gpsTracker.getLatitude();
                                    longitude = gpsTracker.getLongitude();
                                }                    }
                            else {
                                latitude = mCurrentLocation.getLatitude();
                                longitude = mCurrentLocation.getLongitude();
                            }
                            Intent i = new Intent(SplashActivity.this, WelcomeAct.class);
                            startActivity(i);
                            finish();

                        }
                    }, 1500);

                }

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
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
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        if (mySession.IsLoggedIn()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCurrentLocation==null){

                                    }
                                    else {
                                        latitude = mCurrentLocation.getLatitude();
                                        longitude = mCurrentLocation.getLongitude();
                                    }

                                    // Intent i = new Intent(SplashAct.this, CheckLocationActivity.class);
                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }, 1500);

                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCurrentLocation==null){

                                    }
                                    else {
                                        latitude = mCurrentLocation.getLatitude();
                                        longitude = mCurrentLocation.getLongitude();
                                    }

                                    Intent i = new Intent(SplashActivity.this, WelcomeAct.class);
                                    startActivity(i);
                                    finish();

                                }
                            }, 1500);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        finish();
                        break;
                }
                break;
        }
    }
   protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                //  setButtonsEnabledState();

            }
        });

    }
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



}