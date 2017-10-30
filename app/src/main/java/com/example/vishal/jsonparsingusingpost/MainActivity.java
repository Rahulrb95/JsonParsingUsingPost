package com.example.vishal.jsonparsingusingpost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//#0d6a8c
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    private ProgressDialog progress;
    ListView lv;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    ;
//    ArrayList<HashMap<String,String>> clist;
//    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

    }

    public void sendPostRequest(View View) {
        new PostClass(this).execute();
    }


    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c) {

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {


            try {

                final TextView outputView = (TextView) findViewById(R.id.showOutput);
                URL url = new URL("http://202.38.172.188:6030/EASService.svc/GetStateMasterData");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/json");

                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Host", "202.38.172.188:6030");
                connection.setDoOutput(true);


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("", "");

                int responseCode;
                final StringBuilder output;
                BufferedReader br;
                String line;
                final StringBuilder responseOutput;
                try (DataOutputStream dStream = new DataOutputStream(connection.getOutputStream())) {
                    dStream.writeBytes(jsonParam.toString());
                    dStream.flush();
                    dStream.close();
                }

                responseCode = connection.getResponseCode();


                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                line = "";
                responseOutput = new StringBuilder();
                //   System.out.println("output===============" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                MainActivity.this.runOnUiThread(new Runnable() {


                    @Override
                    public void run() {

                        String json = String.valueOf(responseOutput);

                        try {

                            JSONObject obj = new JSONObject(json);

                            // Getting JSON Array node
                            JSONArray jsonData = obj.getJSONArray("DATA");

                            for (int i = 0; i < jsonData.length(); i++) {

                                // Parsing Json Object to String

                                JSONObject c = jsonData.getJSONObject(i);
                                String id = c.getString("STATE_ID");
                                String name = c.getString("STATE_NAME");

                                // tmp hash map for single contact
                                HashMap<String, String> data = new HashMap<>();

                                // adding each child node to HashMap key => value
                                data.put("id", id);
                                data.put("name", name);

                                // adding state to state list
                                dataList.add(data);

                            }

                            Log.d("dataList", String.valueOf(dataList));


                        } catch (Throwable tx) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        // Parsing json stirng to  Custom list view..........

                        ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, dataList, R.layout.list_item,
                                new String[]{"id", "name"}, new int[]{R.id.textViewID,
                       R.id.textViewName});

                        lv.setAdapter(adapter);
                        outputView.setText(responseOutput.toString());
                        progress.dismiss();
                    }
                });


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
           // progress.dismiss();


        }

    }

}