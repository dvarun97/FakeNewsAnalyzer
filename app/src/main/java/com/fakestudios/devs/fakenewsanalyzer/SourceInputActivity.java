package com.fakestudios.devs.fakenewsanalyzer;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SourceInputActivity extends AppCompatActivity {

    Button analyzeButton,clearButton, updateSourcesButton;
    ImageButton pasteButton;
    EditText sourceEdittext;
    String domain;
    ProgressBar progressBar;

    private final String SOURCES_FILE_NAME = "sources.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_input);

        pasteButton = (ImageButton) findViewById(R.id.clipboard_button);
        analyzeButton = (Button) findViewById(R.id.source_analyze_button);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        clearButton = (Button) findViewById(R.id.source_clear_button);
        updateSourcesButton = (Button) findViewById(R.id.update_sources_button);

        sourceEdittext = (EditText) findViewById(R.id.source_input_edittext);

        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if(clipboard.hasPrimaryClip()){
                    Toast.makeText(SourceInputActivity.this, "Copied URL from clipboard", Toast.LENGTH_SHORT).show();
                    sourceEdittext.setText(clipboard.getPrimaryClip().getItemAt(0).coerceToText(getApplicationContext()));
                }
                else{
                    Toast.makeText(SourceInputActivity.this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceEdittext.setText("");
            }
        });

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeAction();
            }
        });

        updateSourcesButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(SourceInputActivity.this, "Updating sources...", Toast.LENGTH_SHORT).show();
               updateSourcesButton.setText("Updating...");
               updateSourcesButton.setEnabled(false);
                new JsonTask().execute("https://firebasestorage.googleapis.com/v0/b/fake-news-analyzer.appspot.com/o/sources.json?alt=media&token=c85b1388-109e-4008-8d5f-c7620ee497c5");
            }
        });



    }

    private void analyzeAction(){

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

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
//                    Log.d("Response: ", "> " + line);   //logging json response

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            updateSourcesButton.setText("Update Sources");
            updateSourcesButton.setEnabled(true);

            //store json in a file
            storeSources(result);
        }
    }

    public void storeSources(String result)
    {
        File file = new File(getFilesDir(), SOURCES_FILE_NAME);
        FileOutputStream outputStream;

        //saving a json file of sources in internal storage
        try {
            outputStream = openFileOutput(SOURCES_FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(result.getBytes());
            outputStream.close();
            Toast.makeText(SourceInputActivity.this, "Sources updated", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SourceInputActivity.this, "Unable to update sources", Toast.LENGTH_SHORT).show();
        }

        //file debugging code
//        String[] files = fileList();
//        Toast.makeText(SourceInputActivity.this, files[0], Toast.LENGTH_SHORT).show();
    }
}
