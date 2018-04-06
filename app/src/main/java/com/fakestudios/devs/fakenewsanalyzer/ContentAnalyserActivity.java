package com.fakestudios.devs.fakenewsanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ContentAnalyserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_analyser);

        //        Toolbar mTopToolbar = findViewById(R.id.contentInput_toolbar);
        //        setSupportActionBar(mTopToolbar);

        Button contentAnalyseButton = findViewById(R.id.contentAnalyse_button);
//        Button stanceDetectionButton = findViewById(R.id.stanceDetection_button);

        contentAnalyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContentAnalyserActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

//        stanceDetectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ContentAnalyserActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
