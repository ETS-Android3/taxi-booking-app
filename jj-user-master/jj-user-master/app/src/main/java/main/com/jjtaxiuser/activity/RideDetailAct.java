package main.com.jjtaxiuser.activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.GPSTracker;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;
import main.com.jjtaxiuser.draglocation.DataParser;
import main.com.jjtaxiuser.utils.NotificationUtils;
import main.com.jjtaxiuser.utils.Pinter;

public class RideDetailAct extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    private double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    private String username_str = "", userimage_str = "", userrate_str = "", usermobile_str = "";

    private TextView amount, amounttype, bookid, datetime, pickuploc, droplocation, review_tv, date_tv;
    private RelativeLayout exit_app_but, userdetaillay, paymetdetail;
    private RatingBar rating;
    private ProgressBar prgressbar;
    private String shareride_type = "", ridecrname_str = "",user_log_data="",user_id="";
    ACProgressFlower ac_dialog;
    MySession mySession;
    MyLanguageSession myLanguageSession;
    private String language = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_ride_detail);
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
        Bundle bundle = getIntent().getExtras();
        idinit();
        checkGps();
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        clickevent();
        if (bundle == null) {
            Log.e("Come in this", "");
        } else {

            String rideid = bundle.getString("rideid");
            Log.e("Come in this", "" + rideid);
            new RideDetailAsc().execute(rideid);
        }

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
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(RideDetailAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RideDetailAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        // gMap.addMarker(marker).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cab_marker));
        gMap.animateCamera(cameraUpdate);
    }

    private void idinit() {
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        rating = (RatingBar) findViewById(R.id.rating);

        date_tv =  findViewById(R.id.date_tv);
        droplocation =  findViewById(R.id.droplocation);
        pickuploc = findViewById(R.id.pickuploc);
        datetime =  findViewById(R.id.datetime);
        bookid = findViewById(R.id.bookid);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);

    }

    private class RideDetailAsc extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  prgressbar.setVisibility(View.VISIBLE);
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
//http://hitchride.net/webservice/get_ride_detail?request_id=13&user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_ride_detail?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("request_id", strings[0]);


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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(RideDetailAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(RideDetailAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            pickuploc.setText("" + jsonObject1.getString("picuplocation"));
                            droplocation.setText("" + jsonObject1.getString("dropofflocation"));
                            bookid.setText("" + jsonObject1.getString("id"));
                            JSONArray jsonuser = jsonObject1.getJSONArray("users_feedback");
                            for (int m = 0; m < jsonuser.length(); m++) {
                                JSONObject jsonObject2 = jsonuser.getJSONObject(m);
                              /*  review_tv.setText("" + jsonObject2.getString("review"));
                                if (jsonObject2.getString("rating") == null || jsonObject2.getString("rating").equalsIgnoreCase("")) {

                                } else {
                                    rating.setRating(Float.parseFloat(jsonObject2.getString("rating")));
                                }*/

                            }
/*
                            JSONObject js = jsonObject1.getJSONObject("payment_detail");

                                amount.setText("Amount : $"+js.getString("amount"));
                                amounttype.setText("Payment Type : "+js.getString("payment_type"));
*/

                          /*  JSONArray js = jsonObject1.getJSONArray("payment_detail");
                            for (int j = 0; j < js.length(); j++) {
                                JSONObject jss = js.getJSONObject(j);
                                amount.setText("Amount : $" + jss.getString("amount"));
                                amounttype.setText("Payment Type : " + jss.getString("payment_type"));
                            }
                            JSONArray jsonArrayus = jsonObject1.getJSONArray("driver_details");
                            for (int user = 0; user < jsonArrayus.length(); user++) {
                                JSONObject jsonObject2 = jsonArrayus.getJSONObject(user);
                                username_str = jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                usermobile_str = jsonObject2.getString("mobile");
                                userimage_str = jsonObject2.getString("profile_image");

                            }
                            JSONArray jsonArrayuser = jsonObject1.getJSONArray("user_details");
                            for (int user = 0; user < jsonArrayuser.length(); user++) {
                                JSONObject jsonObject2 = jsonArrayuser.getJSONObject(user);
                                ridecrname_str = jsonObject2.getString("first_name");
                                //usermobile_str = jsonObject2.getString("mobile");
                                // userimage_str = jsonObject2.getString("profile_image");

                            }*/
                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                datetime.setText("" + strDate);

                                if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                                } else {
                                    pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                    pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));

                                    if (gMap == null) {
                                        Log.e("Come Map Null", "");
                                    } else {
                                        //  MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_marker)).flat(true).anchor(0.5f, 0.5f);

                                        MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_rojo)).flat(true).anchor(0.5f, 0.5f);

                                       /* MarkerOptions markercreat = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(jsonObject1.getString("picuplocation"), "Pickup"))).flat(true).anchor(0.5f, 0.5f).title(ridecrname_str + " " + "Pickup");
                                        MarkerOptions markercreat2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(jsonObject1.getString("dropofflocation"), "Destination"))).flat(true).anchor(0.5f, 0.5f).title(ridecrname_str + " " + "Dropoff");
                                       */ CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(pic_lat, pick_lon));

                                        //  gMap.addMarker(markers1);
                                       // gMap.addMarker(markers);
                                       // gMap.addMarker(marker2);
                                        gMap.addMarker(markers);
                                        gMap.moveCamera(center);
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                                        gMap.animateCamera(zoom);
                                        Log.e("Come Map True", "" + pic_lat);


                                        if (jsonObject1.getString("dropofflocation")!=null&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("")&&!jsonObject1.getString("dropofflocation").equalsIgnoreCase("null")) {
                                            drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                            drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));
                                            MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(Pinter.bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_marker_azul)).flat(true).anchor(0.5f, 0.5f);
                                            gMap.addMarker(marker2);

                                            String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                            FetchUrl FetchUrl = new FetchUrl();
                                            FetchUrl.execute(url, "first");
                                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                    .include(new LatLng(pic_lat, pick_lon))
                                                    .include(new LatLng(drop_lat, drop_lon))
                                                    .build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
                                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
                                        }
                                        else {

                                        }

                                        // }
                                    }
                                }



                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
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
        ;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
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
            ArrayList<LatLng> animation_list = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    animation_list.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                if (cols.equalsIgnoreCase("second")) {
                    lineOptions.color(Color.BLUE);
                } else {
                    lineOptions.color(Color.RED);

                }
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
                if (!cols.equalsIgnoreCase("second")) {
                    if (animation_list != null && !animation_list.isEmpty()) {
                      //  startAnim(animation_list);
                    }

/*
                    if (direction_arrow != null) {
                        new DirectionDraw().execute();

                    }
*/

                }

            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }


    }



}
