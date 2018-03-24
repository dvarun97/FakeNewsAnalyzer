package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.setFinishOnTouchOutside(false);

        LinearLayout appDetailsLL = findViewById(R.id.appdetails_ll);

        TextView appversionTV = findViewById(R.id.appversion_tv);
        appDetailsLL.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));

        String version = "1.0";
        try {
            PackageInfo pInfo = AboutActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversionTV.setText("( v" + version + " )");

        TextView sendFeedback_tv = findViewById(R.id.sendFeedback_tv);
        sendFeedback_tv.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                FeedbackHelper.sendFeedback(AboutActivity.this);
            }
        });

    }
}
