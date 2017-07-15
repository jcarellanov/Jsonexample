package com.example.julioarellano.jsonexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;

import java.io.DataOutputStream;


import java.net.MalformedURLException;

import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Intent;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user;
    private EditText pswrd;
    private Button login;
    private String wiiselURL;
    private String email;
    private String password;
    private String HOST;
    private String DEFAULT_IPADDRESS;
    private String PREFERENCES_IPADDRESSSERVER;
    private String PREFERENCES_TOKEN;
    private Context ctx;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private String sRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HOST = "";
        DEFAULT_IPADDRESS = "81.169.151.83";
        PREFERENCES_IPADDRESSSERVER = "server_ip";
        PREFERENCES_TOKEN = "auth_token";
        user = (EditText) findViewById(R.id.Email);
        pswrd = (EditText) findViewById(R.id.Password);
        login = (Button) findViewById(R.id.button);
        wiiselURL = "http://81.169.151.83";


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = user.getText().toString();
                password = pswrd.getText().toString();
                try {
                    //HttpURLConnection example = serverLogin(email, password);
                    new AsyncLogin().execute(email, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private class AsyncLogin extends AsyncTask<String, String, String>   {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params)  {
            try {

                // Enter URL address
                url = new URL("http://81.169.151.83/api/v1/tokens");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                conn.setRequestMethod("POST");


                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject request = new JSONObject();
                try {
                    request.put("email", params[0]);
                    request.put("password", params[1]);

                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
                sRequest = request.toString();


                conn.connect();
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(sRequest);
                wr.flush();
                wr.close();


            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {


                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        pdLoading.dismiss();
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } /*finally {
                conn.disconnect();
            }*/


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("true")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(MainActivity.this, "GREAT SUCCESS", Toast.LENGTH_LONG).show();
             /*   Intent intent = new Intent(MainActivity.this,SuccessActivity.class);
                startActivity(intent);
                MainActivity.this.finish();*/

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

    public HttpURLConnection serverLogin(String email, String password) throws Exception {
        JSONObject request = new JSONObject();
        request.put("email", email);
        request.put("password", password);
        URL url = new URL(wiiselURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        //connection.connect();

      /*  OutputStream os = new BufferedOutputStream( connection.getOutputStream());
        String sRequest = request.toString();*/
        String sRequest = request.toString();
        OutputStream wr = connection.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(wr, "UTF-8");

        osw.write(sRequest);
        osw.flush();
        osw.close();


        return connection;
    }

    private String getHost() {

        //SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name),
        //      Context.MODE_PRIVATE);
        HOST = "http://81.169.151.83";
        String token = "auth_token";

        return token;

    }

   /* public JSONObject fromHttpToJson(HttpResponse response) throws Exception {
        InputStream content = response.getEntity().getContent();
        byte[] buffer = new byte[1024];
        StringBuilder jsonResponse = new StringBuilder();
        while (content.read(buffer) > 0) {
            jsonResponse.append(new String(buffer));
        }
        JSONObject jsonObject = new JSONObject(jsonResponse.toString());
        return jsonObject;
    }*/

}
