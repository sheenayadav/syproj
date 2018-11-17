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
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateGroup extends AppCompatActivity {

    private String userID;
    private ArrayList<String> groupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userID = getIntent().getStringExtra("user_ID");
        final Button back = (Button)findViewById(R.id.button_Cancel);
        final Button submit = (Button)findViewById(R.id.button_Submit);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(CreateGroup.this,PostLogin.class);
                goBack.putExtra("user_ID",userID);
                CreateGroup.this.startActivity(goBack);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText group_Name = (EditText) findViewById(R.id.group_Name);
                EditText group_Activity = (EditText) findViewById(R.id.group_Activity);
                String name = group_Name.getText().toString();
                String activity = group_Activity.getText().toString();
                System.out.println("group name is" + name);
                System.out.println("Activity name is"+ activity);

                if(name.equals("")|| activity.equals("")){
                    Toast.makeText(CreateGroup.this, "You must specify either the group name or group activity before proceed",
                            Toast.LENGTH_LONG).show();
                }

                else{
                    // user_id
                    // group_name
                    // group_Details
                    // need date and time as well
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    String time = new SimpleDateFormat("HH:mm").format(new Date());

                    groupInfo = new ArrayList<String>();
                    groupInfo.add(userID);
                    groupInfo.add(name);
                    groupInfo.add(activity);
                    groupInfo.add(date);
                    groupInfo.add(time);
                    NewGroup createGroup = new NewGroup();
                    createGroup.execute();
                    }
            }
        });

    }


    private class NewGroup extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void... params) {
            String urlString = "http://null-pointers.herokuapp.com/group";
            StringBuffer response = new StringBuffer();
            try {
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestMethod("POST");
                client.setDoOutput(true);

                // append the content in JSON format
                StringBuilder body = new StringBuilder();
                body.append("{\"user\":");
                body.append("\"");
                body.append(groupInfo.get(0));
                body.append("\"");
                body.append(", ");
                body.append("\"group_name\":");
                body.append("\"");
                body.append(groupInfo.get(1));
                body.append("\"");
                body.append(", ");
                body.append("\"group_details\":");
                body.append("\"");
                body.append(groupInfo.get(2));
                body.append("\"");
                body.append(", ");
                body.append("\"date\":");
                body.append("\"");
                body.append(groupInfo.get(3));
                body.append("\"");
                body.append(", ");
                body.append("\"time\":");
                body.append("\"");
                body.append(groupInfo.get(4));
                body.append("\"");

                body.append("}");

                String userInfo = body.toString();
                Log.d("Body is", userInfo);

                // Send post request
                DataOutputStream wr = new DataOutputStream(client.getOutputStream());
                System.out.println("before Write\n");
                wr.writeBytes(userInfo);
                wr.flush();
                wr.close();

                int responseCode = client.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post body : " + userInfo);
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

            return response.toString();
        }

        protected void onProgressUpdate(Integer... parms) {
            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {
            // add toast message for added and not added
            Log.e("onPostExecute response:", result);
            Intent goBack = new Intent(CreateGroup.this,PostLogin.class);
            Toast.makeText(CreateGroup.this, "Group Created!",
                    Toast.LENGTH_LONG).show();
            goBack.putExtra("user_ID",userID);
            CreateGroup.this.startActivity(goBack);
        }

    }

}
