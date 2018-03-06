package com.fakestudios.devs.fakenewsanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SourceInputActivity extends AppCompatActivity {

    Button analyzeBUtton,clearButton;
    EditText sourceEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_input);

        analyzeBUtton = (Button) findViewById(R.id.source_analyze_button);
        clearButton = (Button) findViewById(R.id.source_clear_button);
        sourceEdittext = (EditText) findViewById(R.id.source_input_edittext);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceEdittext.setText("");
            }
        });
    }
}
