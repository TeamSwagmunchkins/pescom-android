package com.example.anjana.pescom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class OTPActivity extends AppCompatActivity {

    protected Button _loginButton;
    protected EditText _OTPText;
    protected static TextView _error;
    protected String phone;
    protected static String storage = "User_tokens";
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        Intent intent = getIntent();

        // 2. get message value from intent
        phone = intent.getStringExtra("phone");

        _loginButton = (Button) findViewById(R.id.btn_login);
        _OTPText = (EditText) findViewById(R.id.input_otp);
        _error = (TextView) findViewById(R.id.error);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        progressDialog = new ProgressDialog(OTPActivity.this, R.style.AppTheme_Dark_Dialog);

    }


    public void login() {

        _loginButton.setEnabled(false);

        _error.setText("");
        Log.i("EventOTP:", "INLOgin");

        String otp = _OTPText.getText().toString();


        try {

            progressDialog.setMessage("Authenticating..");
            progressDialog.show();
            if (!otp.equals("")) {

                new Task().execute(phone, otp);
            }
            else {
               onLoginFailed("Please enter OTP");
            }

        } catch (Exception E) {
            E.printStackTrace();
        }

    }

    public void storeData(JSONObject json, String phone) {
        SharedPreferences mem = getSharedPreferences(storage, MODE_PRIVATE);
        try {
            mem.edit().putString(phone, json.get("token").toString());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


   @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);

    }


    public static void setErrorText(String s) {
        _error.setText(s);
    }


    private class Task extends AsyncTask<String, Void, Boolean> {

        private String error_str="";

        protected Boolean doInBackground(String... urls) {

            String data = null;
            try {
                Log.i("EventOTP:", "INTASK");
                //data = URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(urls[0], "UTF-8");
                //data += "&" + URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(urls[1], "UTF-8");
                data="phone_number="+ URLEncoder.encode(urls[0], "UTF-8")+"&otp="+ URLEncoder.encode(urls[1], "UTF-8");
                URL url = new URL("https://secure-garden-80717.herokuapp.com/authenticate");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);


                System.out.println(data);
                DataOutputStream dStream = new DataOutputStream(conn.getOutputStream());
                dStream.writeBytes(data);
                dStream.flush();

                int status = conn.getResponseCode();
                if (status != 200) {
                    throw new IOException("Post failed with error code " + status);
                }
                InputStream res = conn.getInputStream();
                String response = convertStreamToString(res);

                JSONObject json = new JSONObject(response);

                if (json.get("success").toString().equals("true")) {
                    //save token and ph number
                    OTPActivity.this.storeData(json, urls[0]);


                } else {

                    error_str="Login failed";
                    return false;

                }
            } catch (Exception E) {

                E.printStackTrace();
                error_str="Login failed";
                return false;


            }
            return true; //change this
        }

        @Override
        protected void onPostExecute(Boolean s) {
            progressDialog.dismiss();
            if (s) {


                //Create new activity for user account
                onLoginSuccess();
                OTPActivity.this.finish();
            } else {
                Log.i("elsePostExecute", "failed");
                onLoginFailed(error_str);
            }
        }


        private String convertStreamToString(InputStream is) {

            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            Scanner se = new Scanner(is);

            String line = "";
            try {
                while (se.hasNextLine()) {
                    sb.append(se.nextLine() + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

}
