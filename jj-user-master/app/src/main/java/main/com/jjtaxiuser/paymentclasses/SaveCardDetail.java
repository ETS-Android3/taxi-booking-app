package main.com.jjtaxiuser.paymentclasses;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.constant.ACProgressCustom;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.MySession;

public class SaveCardDetail extends AppCompatActivity {
    EditText namecard,edt_cardnumber,expiry_date,year,security_code,postalcode;
    private Button sending;
    String strnamecard="",cardnumber="",strexpiry_date="",cvv_number="",stryear="";
    ACProgressCustom ac_dialog;
    private RelativeLayout exit_app_but;
    private MySession mySession;
    private String user_log_data="",user_id="",card_id="";
    private TextView removecard;
    private CreditCardFormatTextWatcher tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_card_detail);
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
                    Log.e("user_id >>>>", "" + user_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        idinti();
        clickevent();
        new GetCardDetail().execute();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        removecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemooveCardDetail().execute();
            }
        });
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strnamecard = namecard.getText().toString().trim();
                cardnumber = edt_cardnumber.getText().toString().trim();
                strexpiry_date = expiry_date.getText().toString().trim();
                stryear = year.getText().toString().trim();
                cvv_number = security_code.getText().toString().trim();
                //Validate();



                if (strnamecard.equals(""))
                {
                    namecard.setError(getResources().getString(R.string.cardnameempty));
                }
                if (cardnumber.equals("")){
                    edt_cardnumber.setError(getResources().getString(R.string.cardnumnotempty));
                }if (strexpiry_date.equals(""))
                {
                    expiry_date.setError(getResources().getString(R.string.expdatenotempty));
                }if (stryear.equals(""))
                {
                    year.setError(getResources().getString(R.string.yearnotempty));
                }


                else {
                    if (card_id==null||card_id.equalsIgnoreCase("")){
                        new AddCardDetail().execute();
                    }
                    else {
                        new UpdateCardDetail().execute();
                    }

                }
            }
        });
    }

    private void idinti() {
        removecard = findViewById(R.id.removecard);
        exit_app_but = findViewById(R.id.exit_app_but);
        namecard = (EditText) findViewById(R.id.namecard);
        edt_cardnumber = (EditText) findViewById(R.id.edt_cardnumber);
        expiry_date = (EditText) findViewById(R.id.expiry_date);
        year = (EditText) findViewById(R.id.year);
        security_code = (EditText) findViewById(R.id.security_code);
        postalcode = (EditText) findViewById(R.id.postalcode);

        sending = (Button) findViewById(R.id.sending_payment);
        tv = new CreditCardFormatTextWatcher(edt_cardnumber);
        edt_cardnumber.addTextChangedListener(tv);

    }


    private class AddCardDetail extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
            //http://hitchride.net/webservice/save_card?user_id=1&holder_name=shyam&card_number=1236545212545633&expiry_month=04&expiry_year=%202019
            try {
                String postReceiverUrl = BaseUrl.baseurl + "save_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("holder_name", strnamecard);
                params.put("card_number", cardnumber);
                params.put("expiry_month", strexpiry_date);
                params.put("expiry_year", stryear);
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
                Log.e("Add Card", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                Toast.makeText(SaveCardDetail.this,getResources().getString(R.string.yourcarddetailsaved),Toast.LENGTH_LONG).show();
finish();
            }


        }
    }

    private class UpdateCardDetail extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://hitchride.net/webservice/update_card?card_id=2&user_id=1&holder_name=shyam&card_number=1236545212545633&expiry_month=04&expiry_year=%202019
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("card_id", card_id);
                params.put("user_id", user_id);
                params.put("holder_name", strnamecard);
                params.put("card_number", cardnumber);
                params.put("expiry_month", strexpiry_date);
                params.put("expiry_year", stryear);
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
                Log.e("Update Card", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                Toast.makeText(SaveCardDetail.this,getResources().getString(R.string.cardupdated),Toast.LENGTH_LONG).show();
finish();
            }


        }
    }
    private class GetCardDetail extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://hitchride.net/webservice/get_card?user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

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
                Log.e("Get Card", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")){
                        removecard.setVisibility(View.VISIBLE);
                        sending.setText(""+getResources().getString(R.string.update));
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k=0;k<jsonArray.length();k++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            card_id = jsonObject1.getString("id");
                            namecard.setText(""+jsonObject1.getString("holder_name"));
                            edt_cardnumber.setText(""+jsonObject1.getString("card_number"));
                            expiry_date.setText(""+jsonObject1.getString("expiry_month"));
                            year.setText(""+jsonObject1.getString("expiry_year"));
                        }
                    }
                    else {
                        removecard.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private class RemooveCardDetail extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://hitchride.net/webservice/delete_card?card_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "delete_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("card_id", card_id);

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
                Log.e("Delete Card", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                Toast.makeText(SaveCardDetail.this,getResources().getString(R.string.cardremoved),Toast.LENGTH_LONG).show();

                finish();
            }


        }
    }


}
