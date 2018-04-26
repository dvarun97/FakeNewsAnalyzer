package com.fakestudios.devs.fakenewsanalyzer;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SourceInputActivity extends AppCompatActivity {

    Button analyzeButton,clearButton;
    ArrayList<HistoryItem> historyItems;
    ImageButton pasteButton;
    EditText sourceEdittext;
    String domain;
    ProgressBar progressBar;
    ListView historyLV;
    private static HIstoryItemAdapter historyAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    private final String SOURCES_FILE_NAME = "sources.json";
    private final String HISTORY_FILE_NAME = "history.txt";
    private final String SOURCES_DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/fake-news-analyzer.appspot.com/o/sources.json?alt=media&token=3445778c-54db-4570-8285-ec50c7bbd61b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_input);

        try {
            copySourcesFromAssetsToInternalStorage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        android.support.v7.widget.Toolbar mTopToolbar = findViewById(R.id.sourceInput_toolbar);
        setSupportActionBar(mTopToolbar);

        historyLV = findViewById(R.id.history_list);
        historyItems = new ArrayList<>();
        try {
            readFromHistoryFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        historyAdapter = new HIstoryItemAdapter(historyItems, SourceInputActivity.this);
        historyLV.setAdapter(historyAdapter);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryItem historyItem= historyItems.get(position);
                sourceEdittext.setText(historyItem.getUrl());
            }
        });


        pasteButton = (ImageButton) findViewById(R.id.clipboard_button);
        analyzeButton = (Button) findViewById(R.id.source_analyze_button);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        clearButton = (Button) findViewById(R.id.source_clear_button);
        sourceEdittext = (EditText) findViewById(R.id.source_input_edittext);
        swipeRefreshLayout = findViewById(R.id.history_swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshHistoryList();
                    }
                }
        );


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }


        //disabling analyse and clear buttons when there is no URL
        sourceEdittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    analyzeButton.setEnabled(true);
                    clearButton.setEnabled(true);
                }
                else{
                    analyzeButton.setEnabled(false);
                    clearButton.setEnabled(false);
                }
            }
        });

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.source_input_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_sources) {
            new JsonTask().execute(SOURCES_DOWNLOAD_URL);
            return true;
        }
        else if(id == R.id.action_clear_history){
            if(delFile(HISTORY_FILE_NAME)){
                Toast.makeText(SourceInputActivity.this, "History cleared successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(SourceInputActivity.this, "Unable to clear history. Please try again.", Toast.LENGTH_SHORT).show();
            }
            refreshHistoryList();
        }
        else if(id == R.id.action_refresh_history){
            refreshHistoryList();
        }
        return super.onOptionsItemSelected(item);
    }

    void refreshHistoryList() {
        try {
            readFromHistoryFile();
            historyAdapter = new HIstoryItemAdapter(historyItems, SourceInputActivity.this);
            historyLV.setAdapter(historyAdapter);
            swipeRefreshLayout.setRefreshing(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            sourceEdittext.setText(sharedText);
            analyzeButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
    }

    private void analyzeAction(){

        String url = sourceEdittext.getText().toString();

        //write url to history file
        appendToFile(HISTORY_FILE_NAME, url);


        url.toLowerCase();

        if(Patterns.WEB_URL.matcher(url).matches() == false){
            Toast.makeText(SourceInputActivity.this, "Please enter valid URL!", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            domain=getDomainName(url);
        }
        catch (URISyntaxException e){
            Toast.makeText(SourceInputActivity.this,"Enter valid URL",Toast.LENGTH_SHORT);
        }


        try {
            String jsonSources = readJSONFile();
            JSONObject sourceObj = new JSONObject(jsonSources);
            Iterator iterator = sourceObj.keys();
            JSONObject res=null;
            String key=null;
            boolean domainExists=false;
            while(iterator.hasNext() && !domainExists){
                key = (String)iterator.next();
                if(key.equals(domain)){
                    res = sourceObj.getJSONObject(key);
                    domainExists=true;
                }
            }
            if(domainExists){
                displayResult(domain,res.getString("type"),res.getString("2nd type"),res.getString("3rd type"),res.getString("Source Notes (things to know?)"),domainExists);
            }
            else{
                displayResult(domain,"","","","",domainExists);
            }


        } catch (IOException e) {
            e.printStackTrace();//no source file
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayResult(String domain, String type1, String type2, String type3, String notes,boolean domainExists) {
        Intent intent = new Intent(SourceInputActivity.this,SourceAnalyserResultActivity.class);
        intent.putExtra("domain",domain);
        intent.putExtra("type1",type1);
        intent.putExtra("type2",type2);
        intent.putExtra("type3",type3);
        intent.putExtra("notes",notes);
        intent.putExtra("domainExists",domainExists);
        startActivity(intent);
    }

    public static String getDomainName(String url) throws URISyntaxException {
        if(url.startsWith("www."))
        {
            url="http://"+url;
        }
        else if(url.startsWith("http"))
        {
        }
        else
        {
            url="http://www."+url;
        }
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    void readFromHistoryFile() throws IOException {
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        historyItems.clear();
        try {
            fis = openFileInput(HISTORY_FILE_NAME);
            bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                historyItems.add(new HistoryItem(line)); //add each url from history file to array list
            }
            bufferedReader.close();
            Collections.reverse(historyItems);
        } catch (FileNotFoundException e) {
            historyItems.add(new HistoryItem("Empty"));
            e.printStackTrace();
        }
    }

    public String readJSONFile () throws IOException {

        //reading file "sources.json" from internal storage
        byte[] formArray;
        try (FileInputStream file = openFileInput(SOURCES_FILE_NAME)) {
            formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
        }
        return new String(formArray);
    }

    public void copySourcesFromAssetsToInternalStorage() throws IOException {
        if(isFileExists(SOURCES_FILE_NAME) == false){
            Toast.makeText(SourceInputActivity.this, "Copying sources from assets to internal storage for first time installation...", Toast.LENGTH_LONG).show();
            //copy sources.json from assets to internal storage
            String sources = loadJSONFromAsset();
            if(writeToFile(SOURCES_FILE_NAME, sources)){
//                Toast.makeText(SourceInputActivity.this, "Copying sources successful.", Toast.LENGTH_SHORT).show();

            } else {
//                Toast.makeText(SourceInputActivity.this, "Copying sources failed.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
//            Toast.makeText(SourceInputActivity.this, "Sources file already exists", Toast.LENGTH_SHORT).show();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = SourceInputActivity.this.getAssets().open(SOURCES_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public boolean isFileExists(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    //async task for downloading sources from firebase storage
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
            //store json in a file
            storeSources(result);
        }
    }

    protected void storeSources(String result)
    {
        boolean status = writeToFile(SOURCES_FILE_NAME, result);

        if(status) {
            Toast.makeText(SourceInputActivity.this, "Sources updated", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(SourceInputActivity.this, "Unable to update sources", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean writeToFile(String fileName, String data)
    {
        FileOutputStream outputStream;
        //saving a json file of sources in internal storage
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.i("Sources updating", "source file written successfully");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean appendToFile(String fileName, String data)
    {
        FileOutputStream outputStream;
        data+="\n";
        //saving a json file of sources in internal storage
        try {
            outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.i("Sources updating", "source file written successfully");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean delFile(String fileName){
        File file = new File(getFilesDir(), fileName);
        return file.delete();
    }
}
