package main.com.jjtaxiuser.paymentclasses;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.jjtaxiuser.MainActivity;
import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.constant.ACProgressCustom;
import main.com.jjtaxiuser.constant.BaseUrl;
import main.com.jjtaxiuser.constant.MyLanguageSession;
import main.com.jjtaxiuser.constant.MySession;

public class ConfirmPayment extends AppCompatActivity {
    Button sending,package_money,edit_button_here;
    EditText namecard,edt_cardnumber,expiry_date,year,security_code,postalcode;
    private static final String TAG = "SetupActivity";
    ProgressDialog progressDialog;
    String strnamecard,cardnumber,strexpiry_date,cvv_number,stryear;
    String str_token="",pack_price="",pack_name="",pack_id="",user_log_data="",user_id="";
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    TextView demoplus_name;
    CardInputWidget mCardInputWidget;
    Dialog dialog;

    int month, year_int;
    private String token_id;
    private MySession mySession;
    private RelativeLayout exit_app_but;
    String request_id="",transaction_type="",car_charge_str="",rating_str="",tips_amount_str="0",time_zone="",comment_str="";
    private CreditCardFormatTextWatcher tv;
    /**/
    ACProgressCustom ac_dialog;
    MyLanguageSession myLanguageSession;
    private String language = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_confirm_payment);
        progressDialog = new ProgressDialog(this);
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
      Bundle  b = getIntent().getExtras();
        if (b != null&&!b.isEmpty()) {
            pack_price = b.getString("amount_str");
            request_id = b.getString("request_id");
            car_charge_str = b.getString("car_charge_str");
            rating_str = b.getString("rating");
            tips_amount_str = b.getString("tips_amount_str");
            time_zone = b.getString("time_zone");
            comment_str = b.getString("comment_str");
            transaction_type = b.getString("transaction_type");

            Log.d("payment", pack_price);
            System.out.println("payment" + pack_price);
        }else
        {
            Toast.makeText(this,"payment is null",Toast.LENGTH_SHORT).show();
        }

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
        //   mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        namecard = (EditText) findViewById(R.id.namecard);
        edt_cardnumber = (EditText) findViewById(R.id.edt_cardnumber);
        expiry_date = (EditText) findViewById(R.id.expiry_date);
        year = (EditText) findViewById(R.id.year);
        security_code = (EditText) findViewById(R.id.security_code);
        postalcode = (EditText) findViewById(R.id.postalcode);
        package_money = (Button) findViewById(R.id.package_money);

        demoplus_name = (TextView) findViewById(R.id.demoplus_name);
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sending = (Button) findViewById(R.id.sending_payment);

        dialog = new Dialog(ConfirmPayment.this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        tv = new CreditCardFormatTextWatcher(edt_cardnumber);
        edt_cardnumber.addTextChangedListener(tv);
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                }if (cvv_number.equals(""))
                {
                    security_code.setError(getResources().getString(R.string.seccodenotempty));
                }else {
                    showDialog();
                    month = Integer.parseInt(strexpiry_date);
                    year_int = Integer.parseInt(stryear);

                    onClickSomething(cardnumber, month, year_int, cvv_number);
                    mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
                    Card card = new Card(cardnumber, month, year_int, cvv_number);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                    Stripe stripe = new Stripe(ConfirmPayment.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    System.out.println("----------------Token" + token.getId());
                                    hideDialog();
                                    token_id = token.getId();
                                    paymentwithcard();

                             }
                                public void onError(Exception error) {
                                    // Show localized error message
                                    Toast.makeText(ConfirmPayment.this, "\n" + "The expiration year or the security code of your card is not valid",
                                            Toast.LENGTH_LONG
                                    ).show();
                                    System.out.println("Eeeeeeeeeeeeeeerrrrr" + error.toString());
                                    hideDialog();
                                }
                            });
                }

            }
        });
        edit_button_here=(Button)findViewById(R.id.button2);
        if (transaction_type.equalsIgnoreCase("ride_payment")){
            edit_button_here.setVisibility(View.GONE);
        }

        edit_button_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    startActivity(new Intent(ConfirmPayment.this,WalletActivity.class));
              finish();
            }
        });

        package_money.setText(pack_price);
        demoplus_name.setText(pack_name);
        new GetCardDetail().execute();
    }

    private void paymentwithcard() {
        // Tag used to cancel the request
        if(Utils.isConnected(getApplicationContext())){
            Paymentjsontask task = new Paymentjsontask();
            task.execute();

        }

        else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.checknetworkconnection), Toast.LENGTH_SHORT).show();
        }
    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }
    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
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
    }

    public class Paymentjsontask extends AsyncTask<String, Void, String> {
        boolean iserror = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String cancel_req_tag = "paymentin";
        progressDialog.setMessage("Payment you in...");
        showDialog();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            //HttpClient client = new DefaultHttpClient();


            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            HttpPost post = new HttpPost(BaseUrl.baseurl+"strips_payment?transaction_type="+transaction_type+"&payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id+"&request_id="+request_id+"&rating="+rating_str+"&review="+comment_str+"&car_charge="+car_charge_str+"&tip="+tips_amount_str+"&time_zone="+time_zone);
Log.e("STRIPE URL >> "," >> "+BaseUrl.baseurl+"strips_payment?transaction_type="+transaction_type+"&payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id+"&request_id="+request_id+"&rating="+rating_str+"&review="+comment_str+"&car_charge="+car_charge_str+"&tip="+tips_amount_str);
            //http://hitchride.net/webservice/strips_payment?transaction_type=ride_payment&payment_type=Card&amount=200&user_id=117&request_id=164&tip=5&car_charge=12&token=123&currency=USD&rating=4&review=nice%20job
            try {
                HttpResponse response = client.execute(post);
                String object = EntityUtils.toString(response.getEntity());
                System.out.println("#####object=" + object);
                //JSONArray js = new JSONArray(object);
                JSONObject jobect1 = new JSONObject(object);
                result = jobect1.getString("message");
                if(result.equalsIgnoreCase("payment successfull")){
                    String details = jobect1.getString("result");

                }else{

                }
            }

            catch (Exception e) {
                Log.v("22", "22" + e.getMessage());
                e.printStackTrace();
                iserror = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result1) {
            // TODO Auto-generated method stub
            super.onPostExecute(result1);
            hideDialog();
            if(iserror== false){
                if (result.equalsIgnoreCase("payment successfull")){

                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.paymentaddsucc),Toast.LENGTH_SHORT).show();
                    if (transaction_type.equalsIgnoreCase("ride_payment")){
                        Intent i = new Intent(ConfirmPayment.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }
                    else {
                        finish();
                    }



                } else {
                    Toast.makeText(ConfirmPayment.this,getResources().getString(R.string.cardinfowrong),Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.chkserver),
                        Toast.LENGTH_SHORT).show();
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

                        sending.setText(""+getResources().getString(R.string.update));
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k=0;k<jsonArray.length();k++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);

                            namecard.setText(""+jsonObject1.getString("holder_name"));
                            edt_cardnumber.setText(""+jsonObject1.getString("card_number"));
                            expiry_date.setText(""+jsonObject1.getString("expiry_month"));
                            year.setText(""+jsonObject1.getString("expiry_year"));
                        }
                    }
                    else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
