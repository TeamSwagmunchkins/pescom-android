package com.example.anjana.pescom;

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

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_OTP = 0;
    protected static EditText _phoneText;
    protected static Button _loginButton;
    protected static TextView _error;
    private ProgressDialog progressDialog;

    public boolean success;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _phoneText = (EditText) findViewById(R.id.input_phone);

        _loginButton = (Button) findViewById(R.id.btn_login);

        _error = (TextView) findViewById(R.id.error);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                _error.setText("");
                Log.i("FUNC", "loginbuttonClickedINPage1");
                //send post request
                if(validate())
                    sendRequest_phone();
                else
                {
                    Toast.makeText(getBaseContext(), "INVALID PHONE NUMBER", Toast.LENGTH_LONG).show();
                }
                Log.i("return:", "after everything");

            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);

    }

    public String sendRequest_phone() {

        String phone = _phoneText.getText().toString();
        _error.setText("");
        Log.i("FUNC", "sendRequest_phone");
        try {

            progressDialog.setMessage("Authenticating..");
            progressDialog.show();
            new MyTask().execute(phone);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return null;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OTP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here

                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public static void onLoginSuccess() {
        _loginButton.setEnabled(true);
    }

    public void onLoginFailed(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {


        boolean valid = true;

        String phone = _phoneText.getText().toString();

        if(phone.length()!=10)
            valid=false;
        else
        {
            if(!phone.matches("[0-9]+"))
            {
                valid=false;
            }

        }

        return valid;
    }


    private class MyTask extends AsyncTask<String, Boolean, Boolean> {

        private String error_str="";

        @Override
        protected void onPreExecute() {


        }

        protected Boolean doInBackground(String... urls) {

            try {

                    String data = "phone_number=" + urls[0].toString();
                    URL url = new URL("https://secure-garden-80717.herokuapp.com/signup");
                    Log.i("  EventLogin", "openURL");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Log.i("  EventLogin", "OpeningURLConnection");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    DataOutputStream dStream = new DataOutputStream(conn.getOutputStream());
                    dStream.writeBytes(data);
                    dStream.flush();

                    int status = conn.getResponseCode();
                    Log.i("  Status", String.valueOf(status));
                    if (status != 200) {
                        throw new IOException("Post failed with error code " + status);
                    }


                    Log.i("  EventLogin", "sentOTPRequest");

                    InputStream res = conn.getInputStream();
                    Log.i("  EventLogin", "Receiving Response");

                    String response = convertStreamToString(res);
                    Log.i("Response:", response);
                    JSONObject json = new JSONObject(response);

                    if (json.get("success").toString().equals("true")) {
                        //receive the otp ---> askshay sowmyaji
                    } else {
                        //display message as otp was not received

                        error_str="OTP not received. Login failed";
                        return false;

                    }

            } catch (Exception E) {

                E.printStackTrace();
                Log.i("errorLOGIN", "Exception");

                error_str="Login failed";
                return false;


            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean s) {
            progressDialog.dismiss();
            if (s) {
                Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                intent.putExtra("phone", _phoneText.getText().toString());

                startActivityForResult(intent, REQUEST_OTP);
                onLoginSuccess();
                LoginActivity.this.finish();
            } else {
                Log.i("elsePostExecute", "failed");
                onLoginFailed(error_str);
            }
        }


        private String convertStreamToString(InputStream is) {


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
