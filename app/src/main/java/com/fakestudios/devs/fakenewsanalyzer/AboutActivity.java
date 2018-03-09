package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {

    private String version;
    private TextView sendFeedback_tv, appversion_tv;
    private TextView appnameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.setFinishOnTouchOutside(false);

        appnameTV = findViewById(R.id.appname_tv);

        //animating app name in about page
        appnameTV.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));

        version="1.0";
        try {
            PackageInfo pInfo = AboutActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sendFeedback_tv = findViewById(R.id.sendFeedback_tv);
        appversion_tv = findViewById(R.id.app_version_tv);
        appversion_tv.setText("App Version : " + version);

        sendFeedback_tv.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                FeedbackHelper.sendFeedback(AboutActivity.this);
            }
        });
    }
}
