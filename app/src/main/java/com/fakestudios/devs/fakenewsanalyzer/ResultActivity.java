package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ResultActivity extends AppCompatActivity {

    TextView resultTextview,type1Textview,type2Textview,type3Textview,notesTextview, probablyRealTextView;
    ProgressBar progressBar;
    TextView domainReachabilityTV, domainReachableTV, domainUnreachableTV;
    LinearLayout notesLL;
    Button homeButton;
    Button goToURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        //call domain checker
        new UrlChecker().execute("http://" + intent.getStringExtra("domain"));

        notesLL = findViewById(R.id.notes_ll);
        resultTextview = (TextView) findViewById(R.id.result_textview);
        type1Textview = (TextView) findViewById(R.id.result_type1_textview);
        type2Textview = (TextView) findViewById(R.id.result_type2_textview);
        type3Textview = (TextView) findViewById(R.id.result_type3_textview);
        notesTextview = (TextView) findViewById(R.id.result_notes_textview);
        probablyRealTextView = (TextView) findViewById(R.id.probablyReal_tv);
        probablyRealTextView.setVisibility(View.GONE);
        goToURL = findViewById(R.id.open_url_button);

        progressBar = findViewById(R.id.domain_reachability_pb);
        domainReachabilityTV = findViewById(R.id.domain_reachability_tv);
        domainReachableTV = findViewById(R.id.domain_reachable_tv);
        domainUnreachableTV = findViewById(R.id.domain_unreachable_tv);

        domainReachableTV.setVisibility(View.GONE);
        domainUnreachableTV.setVisibility(View.GONE);

        homeButton= (Button) findViewById(R.id.result_home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        goToURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                url = getIntent().getStringExtra("domain");
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        });

        resultTextview.setText(intent.getStringExtra("domain"));
        if(intent.getBooleanExtra("domainExists",false)){

            type1Textview.setText(intent.getStringExtra("type1"));

            if(intent.getStringExtra("type2").length() != 0){
                type2Textview.setText(intent.getStringExtra("type2"));
            }
            else{
                type2Textview.setVisibility(View.GONE);
            }

            if(intent.getStringExtra("type3").length() != 0){
                type3Textview.setText(intent.getStringExtra("type3"));
            }
            else{
                type3Textview.setVisibility(View.GONE);
            }

            if(intent.getStringExtra("notes").length() != 0){
                notesTextview.setText(intent.getStringExtra("notes"));
            }
            else{
                notesLL.setVisibility(View.GONE);
            }


        }
        else{
            probablyRealTextView.setVisibility(View.VISIBLE);
            type1Textview.setVisibility(View.GONE);
            type2Textview.setVisibility(View.GONE);
            type3Textview.setVisibility(View.GONE);
            notesLL.setVisibility(View.GONE);
        }


        //animating views
        type1Textview.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));
        type2Textview.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));
        type3Textview.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));
        probablyRealTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sample_animation));

    }

        public class UrlChecker extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            try {
                final URLConnection connection = new URL(url).openConnection();
                connection.connect();
                return true;
            } catch (final MalformedURLException e) {
                return false;
            } catch (final IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            domainReachabilityTV.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            if(exists){
                domainReachable();
            } else {
                domainUnreachable();
            }
        }
    }

    void domainReachable(){
        domainReachableTV.setVisibility(View.VISIBLE);
    }

    void domainUnreachable(){
        domainUnreachableTV.setVisibility(View.VISIBLE);
    }
}
