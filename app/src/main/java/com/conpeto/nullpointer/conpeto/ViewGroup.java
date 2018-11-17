package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewGroup extends AppCompatActivity {
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);
        userID = getIntent().getStringExtra("user_ID");

        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(ViewGroup.this,PostLogin.class);
                viewGroup.putExtra("user_ID",userID);
                ViewGroup.this.startActivity(viewGroup);
            }
        });

        CheckGroup checkGroup = new CheckGroup();
        checkGroup.execute();
    }


    private class CheckGroup extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void... params) {
            StringBuilder urlBuilder = new StringBuilder("http://null-pointers.herokuapp.com/group");
            urlBuilder.append("?user=");
            urlBuilder.append(userID);
            String urlString = urlBuilder.toString();
            StringBuffer response = new StringBuffer();
            System.out.println("The URL is" + urlString);
            try {
                Log.e("before connection", "let's start");
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestMethod("GET");
                // append the content in JSON format

                int responseCode = client.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (MalformedURLException E) {
                Log.e("URL", "The URL is not correct");
            } catch (IOException E) {
            }

            Log.e("group response is:",response.toString() );
            return response.toString();
        }

        protected void onProgressUpdate(Integer... parms) {

            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {
            TextView groupList = findViewById(R.id.group_List);
            groupList.setText(result);
        }
    }
}
