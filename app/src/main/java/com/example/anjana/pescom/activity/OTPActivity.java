package com.example.anjana.pescom.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.Preferences;

import org.json.JSONException;
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
        Preferences preferences = Preferences.getPreferences(this);
        preferences.setNumber(phone);
        try {
            preferences.setToken(json.getString("token"));
            Log.d("TOKEN", preferences.getToken());
        } catch (JSONException e) {
            Log.e("OTPActivity", "Fail parsing json: " + json, e);
        }
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, TestActivity.class));
    }

    public void onLoginFailed(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);

    }

    private class Task extends AsyncTask<String, Void, Boolean> {

        private String error_str="";

        protected Boolean doInBackground(String... urls) {

            String data;
            try {
                Log.i("EventOTP:", "INTASK");
                //data = URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(urls[0], "UTF-8");
                //data += "&" + URLEncoder.encode("otp", "UTF-8") + "=" + URLEncoder.encode(urls[1], "UTF-8");
                data="phone_number="+ URLEncoder.encode(urls[0], "UTF-8")+"&otp="+ URLEncoder.encode(urls[1], "UTF-8");
                URL url = new URL(Preferences.getPreferences(OTPActivity.this).getUrl(
                        Constants.OTP_EP));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);


                System.out.println(data);
                DataOutputStream dStream = new DataOutputStream(conn.getOutputStream());
                dStream.writeBytes(data);
                dStream.flush();

                int status = conn.getResponseCode();
                if (status != 200) {
                    // TODO: show correct error reason based on response code
                    throw new IOException("Post failed with error code " + status);
                }
                InputStream res = conn.getInputStream();
                String response = convertStreamToString(res);

                JSONObject json = new JSONObject(response);
                    //save token and ph number
                    storeData(json, urls[0]);
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
            } else {
                Log.i("elsePostExecute", "failed");
                onLoginFailed(error_str);
            }
        }


        private String convertStreamToString(InputStream is) {

            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            Scanner se = new Scanner(is);

            try {
                while (se.hasNextLine()) {
                    sb.append(se.nextLine());
                    sb.append("\n");
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
