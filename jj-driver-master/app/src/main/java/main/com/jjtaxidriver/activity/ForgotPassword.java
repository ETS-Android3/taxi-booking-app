package main.com.jjtaxidriver.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.jjtaxidriver.constant.MyLanguageSession;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.constant.ACProgressCustom;
import main.com.jjtaxidriver.restapi.ApiClient;


public class ForgotPassword extends AppCompatActivity {
    private TextView submit;
    private EditText forgot_email;
    private String forgot_str = "";
    private ProgressBar prgressbar;
    private RelativeLayout exit_app_but;

    ACProgressCustom ac_dialog;
    MyLanguageSession myLanguageSession;
    private String language = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_forgot_password);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        idinit();
        clcikevent();
    }

    private void clcikevent() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_str = forgot_email.getText().toString().toLowerCase();
                if (forgot_str == null || forgot_str.equalsIgnoreCase("")) {
                    Toast.makeText(ForgotPassword.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                } else {
                    callapi(forgot_str);               }
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        forgot_email = (EditText) findViewById(R.id.forgot_email);
        submit = (TextView) findViewById(R.id.submit);
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

    private void callapi(String email_str) {
        if (ac_dialog != null) {
            ac_dialog.show();
        }
        Call<ResponseBody> call = ApiClient.getApiInterface().ForgotCall(email_str,"DRIVER");
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
                        Log.e("Fogotpass >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            Toast.makeText(ForgotPassword.this, getResources().getString(R.string.checkpassreslink), Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ForgotPassword.this, getResources().getString(R.string.emailnotext), Toast.LENGTH_LONG).show();

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
