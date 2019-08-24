package main.com.jjtaxidriver.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.constant.MyLanguageSession;

public class WelcomeAct extends AppCompatActivity {
    MyLanguageSession myLanguageSession;
    private String language = "";
    private Button loginbut,signupbut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_welcome);
        idinit();
        clickevent();
    }

    private void clickevent() {
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeAct.this,LoginAct.class);
                startActivity(i);
            }
        });
        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeAct.this,SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void idinit() {
        loginbut = findViewById(R.id.loginbut);
        signupbut = findViewById(R.id.signupbut);
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
}
