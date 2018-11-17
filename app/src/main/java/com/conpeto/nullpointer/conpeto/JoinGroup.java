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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JoinGroup extends AppCompatActivity {

   private String userID;
   private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        userID = getIntent().getStringExtra("user_ID");
        final Button back = (Button)findViewById(R.id.button_Cancel2);
        final Button submit = (Button)findViewById(R.id.button_Submit2);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(JoinGroup.this,PostLogin.class);
                goBack.putExtra("user_ID",userID);
                JoinGroup.this.startActivity(goBack);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText group_Name = (EditText) findViewById(R.id.group_Name2);
                groupName = group_Name.getText().toString();
                System.out.println("group name is" + groupName);


                if(groupName.equals("")){
                    Toast.makeText(JoinGroup.this, "You must specify a group name",
                            Toast.LENGTH_LONG).show();
                }

                else{
                    AddUserToGroup joinGroup = new AddUserToGroup();
                    joinGroup.execute();
                }
            }
        });

    }


    private class AddUserToGroup extends AsyncTask<Void, Integer, Boolean> {
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            StringBuffer response = new StringBuffer();
            String urlString = "http://null-pointers.herokuapp.com/group";
            try {
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestMethod("PUT");
                client.setDoOutput(true);

                // append the content in JSON format
                StringBuilder body = new StringBuilder();
                body.append("{\"user\":");
                body.append("\"");
                body.append(userID);
                body.append("\"");
                body.append(", ");
                body.append("\"group\":");
                body.append("\"");
                body.append(groupName);
                body.append("\"");
                body.append("}");

                String bodyInfo = body.toString();
                System.out.println("Body is" + bodyInfo);

                // Send put request
                DataOutputStream wr = new DataOutputStream(client.getOutputStream());
                System.out.println("before Write\n");
                wr.writeBytes(bodyInfo);
                wr.flush();
                wr.close();

                int responseCode = client.getResponseCode();
                System.out.println("\nSending 'PUT' request to URL : " + url);
                System.out.println("Put body : " + bodyInfo);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseContent = response.toString();
                if (responseContent.contains("Added")||(responseContent.contains("Already"))) {
                    success = true;
                }
            } catch (MalformedURLException E) {
                Log.e("URL", "The URL is not correct");
            } catch (IOException E) {
            }

            Log.e("Join group response:", response.toString());
            return success;
        }

        protected void onProgressUpdate(Integer...params){
            super.onProgressUpdate();
        }

        protected void onPostExecute(Boolean result) {
            // add toast message for added and not added
            Intent goBack = new Intent(JoinGroup.this,PostLogin.class);
            if(result==false) {
                Toast.makeText(JoinGroup.this, "No such group or user was found!",
                        Toast.LENGTH_LONG).show();
            }
            else{

                goBack.putExtra("user_ID",userID);
                Toast.makeText(JoinGroup.this, "Join the group successfully!",
                        Toast.LENGTH_LONG).show();
                 JoinGroup.this.startActivity(goBack);

            }
        }

    }
}
