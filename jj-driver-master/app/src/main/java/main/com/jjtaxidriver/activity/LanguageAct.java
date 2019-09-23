package main.com.jjtaxidriver.activity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import main.com.jjtaxidriver.R;
import main.com.jjtaxidriver.constant.MyLanguageSession;


public class LanguageAct extends AppCompatActivity {
private RelativeLayout exit_app_but;
    private TextView save_tv;
    private RadioGroup langradiobutton;
    MyLanguageSession myLanguageSession;
    private String language ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")){
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }

        idint();
        clickevent();
    }

    private void clickevent() {
        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = langradiobutton.getCheckedRadioButtonId();
                if (id==R.id.arabicid){
                    myLanguageSession.insertLanguage("es");
                    myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
                    String oldLanguage = language;
                    language = myLanguageSession.getLanguage();

                }
                else {
                    myLanguageSession.insertLanguage("en");
                    myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
                    String oldLanguage = language;
                    language = myLanguageSession.getLanguage();

                }
                finish();

            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idint() {
        langradiobutton = (RadioGroup) findViewById(R.id.langradiobutton);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        save_tv = (TextView) findViewById(R.id.save_tv);
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
