package com.example.anjana.pescom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;



public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_OTP = 0;
    private EditText _phoneText;
    private Button _loginButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //private TextView _signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _phoneText = (EditText) findViewById(R.id.input_phone);

        _loginButton = (Button) findViewById(R.id.btn_login);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sendRequest_phone();//send post request
                Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                startActivityForResult(intent, REQUEST_OTP);
                onLoginSuccess();
            }
        });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void sendRequest_phone()
    {

        String phone = _phoneText.getText().toString();
        String data = null;
        try {
            data = URLEncoder.encode("name", "UTF-8")+"="+ URLEncoder.encode(phone,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL url = null;//change name
        try {
            url = new URL("www.google.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn= null;
        try {
            conn = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try
        {
            //sending request for otp
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        //receive response
        InputStream res= null;
        try
        {
              res = conn.getInputStream();
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }

        String response=convertStreamToString(res);

        JSONObject json= null;
        try
        {
            json = new JSONObject(response);
            if(json.get("success").equals("true"))
            {
                //receive the otp ---> askshay sowmayaji
            }
            else
            {
                //display message as otp was not received


            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }






    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line ="";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
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

    /*public void otp() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String phone = _phoneText.getText().toString();
        // String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OTP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {


        boolean valid = true;

        String phone = _phoneText.getText().toString();
        //String password = _passwordText.getText().toString();

        char plus = phone.charAt(0);
        if (plus == '+') {
            System.out.print("aaaa");
            if (phone.length() != 15 || phone.length() != 13) {
                System.out.print("abbb");
                valid = false;
            }

        } else if (phone.length() != 10) {
            valid = false;
            System.out.print("hfjjdna");
        }


        //||!android.util.Patterns.PHONE.matcher(phone).matches()
        if (phone.isEmpty() || !valid) {
            _phoneText.setError("Please enter correct phone number");
            valid = false;

        } else {
            _phoneText.setError(null);
        }


        /*if (password.isEmpty() || password.length() < 4 || password.length() > 10)
        {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        }
        else
        {
            _passwordText.setError(null);
        }*/

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.anjana.pescom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.anjana.pescom/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
