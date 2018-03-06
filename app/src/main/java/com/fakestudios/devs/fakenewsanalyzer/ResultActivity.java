package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView resultTextview,type1Textview,type2Textview,type3Textview,notesTextview;
    Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultTextview = (TextView) findViewById(R.id.result_textview);
        type1Textview = (TextView) findViewById(R.id.result_type1_textview);
        type2Textview = (TextView) findViewById(R.id.result_type2_textview);
        type3Textview = (TextView) findViewById(R.id.result_type3_textview);
        notesTextview = (TextView) findViewById(R.id.result_notes_textview);
        homeButton= (Button) findViewById(R.id.result_home_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();

        if(intent.getBooleanExtra("domainExists",false)){
            resultTextview.setText(intent.getStringExtra("domain"));
            type1Textview.setText(intent.getStringExtra("type1"));
            type2Textview.setText(intent.getStringExtra("type2"));
            type3Textview.setText(intent.getStringExtra("type3"));
            notesTextview.setText(intent.getStringExtra("notes"));
        }
        else{
            resultTextview.setText(intent.getStringExtra("domain"));
            type1Textview.setText(intent.getStringExtra("type1"));
            type2Textview.setText(intent.getStringExtra("type2"));
            type3Textview.setText(intent.getStringExtra("type3"));
            notesTextview.setText("Not found");
        }

    }

}
