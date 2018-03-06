package com.fakestudios.devs.fakenewsanalyzer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class SourceInputActivity extends AppCompatActivity {

    Button analyzeBUtton,clearButton;
    EditText sourceEdittext;
    String domain;

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

        String url = sourceEdittext.getText().toString();
        try{
            domain=getDomainName(url);
        }
        catch (URISyntaxException e){
            Toast.makeText(SourceInputActivity.this,"Invalid URL",Toast.LENGTH_SHORT);
        }

        try {
            String jsonSources = AssetJSONFile("fakeSources.json", SourceInputActivity.this);
            JSONObject sourceObj = new JSONObject(jsonSources);
            Iterator iterator = sourceObj.keys();
            JSONObject res=null;
            String key=null;
            boolean domainExists=false;
            while(iterator.hasNext()){
                key = (String)iterator.next();
                if(key.equals(domain)){
                    res = sourceObj.getJSONObject(key);
                    domainExists=true;
                    break;
                }
            }
            if(domainExists){
                displayResult(key,res.getString("type"),res.getString("2nd type"),res.getString("3rd type"),res.getString("Source Notes (things to know?)"),domainExists);
            }
            else{
                displayResult(key,"","","","",domainExists);
            }


        } catch (IOException e) {
            e.printStackTrace();//no source file
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void displayResult(String domain, String type1, String type2, String type3, String notes,boolean domainExists) {
        Intent intent = new Intent(SourceInputActivity.this,ResultActivity.class);
        intent.putExtra("domain",domain);
        intent.putExtra("type1",type1);
        intent.putExtra("type2",type2);
        intent.putExtra("type3",type3);
        intent.putExtra("notes",notes);
        intent.putExtra("domainExists",domainExists);
        startActivity(intent);
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String AssetJSONFile (String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }
}
