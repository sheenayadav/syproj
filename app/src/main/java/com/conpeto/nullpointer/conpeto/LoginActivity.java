package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.http.NameValuePair;

import static com.facebook.HttpMethod.POST;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView textView;
    private static final String EMAIL = "email";
    private String userID;
    private String tempPW = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("token return is",loginResult.getAccessToken().getToken());
                userID = loginResult.getAccessToken().getUserId();
                AddUser addUser = new AddUser();
                addUser.execute();
            }

            @Override
            public void onCancel() {
                // need to add more if needed
            }

            @Override
            public void onError(FacebookException exception) {
                // need to add more if needed
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class AddUser extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void...params) {
            String urlString = "http://null-pointers.herokuapp.com/user";
            StringBuffer response = new StringBuffer();
            try {
                Log.e("before connection","let's start");
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestProperty("Content-Type","application/json");
                client.setRequestMethod("POST");
                client.setDoOutput(true);

                // append the content in JSON format
                StringBuilder body = new StringBuilder();
                body.append("{\"user\":");
                body.append("\"");
                body.append(userID);
                body.append("\"");
                body.append(", ");
                body.append("\"pass\":");
                body.append("\"");
                body.append(tempPW);
                body.append("\"");
                body.append("}");

                String userInfo = body.toString();
                Log.d("Body is",userInfo);

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

            } catch(MalformedURLException E){Log.e( "URL","The URL is not correct");}
            catch(IOException E){}

            return response.toString();
        }

        protected void onProgressUpdate(Integer...parms) {
            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {
            Log.e("onPost login response:",result);
            if(result.contains("Added")||result.contains("Exists")){
                Intent myIntent = new Intent(LoginActivity.this, PostLogin.class);
                myIntent.putExtra("user_ID", userID);
                 LoginActivity.this.startActivity(myIntent);
            }
            else
                return;
        }
    }
}