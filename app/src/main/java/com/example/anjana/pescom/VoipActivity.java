package com.example.anjana.pescom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.example.anjana.pescom.service.CallListenerService;
import com.example.anjana.pescom.service.CallMakerService;

public class VoipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void startListening() {
        CallListenerService.startListening(this);
    }

    public void call(View v) {
        CallMakerService.makeCall(this, ((EditText) findViewById(R.id.ip_et)).getText().toString());
    }
}
