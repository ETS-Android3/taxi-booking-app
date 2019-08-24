package main.com.jjtaxiuser.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import main.com.jjtaxiuser.R;
import main.com.jjtaxiuser.constant.BaseUrl;


public class PrivacyPolicyAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    WebView aboutusdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        idinit();
        clickevetn();
    }

    private void clickevetn() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        aboutusdata= (WebView) findViewById(R.id.aboutusdata);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);

        aboutusdata.getSettings().setJavaScriptEnabled(true);
        aboutusdata.getSettings().setPluginState(WebSettings.PluginState.ON);
        aboutusdata.setWebViewClient(new Callback());
      //  String pdfURL = "http://mobileappdevelop.co/NAXCAN/about-us.html";
        String pdfURL = BaseUrl.privacy;
        aboutusdata.loadUrl(pdfURL);

    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

}

