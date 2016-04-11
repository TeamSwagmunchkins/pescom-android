package com.example.anjana.pescom.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.util.Preferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String serverIp = Preferences.getPreferences(this).getUrl();
        if (serverIp != null) {
            ((EditText)findViewById(R.id.ip_et)).setText(serverIp);
        }
    }

    public void onSaveClick(View v) {
        Preferences.getPreferences(this)
                .setUrl(((EditText)findViewById(R.id.ip_et)).getText().toString());
        Toast.makeText(this, "Server IP saved!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
