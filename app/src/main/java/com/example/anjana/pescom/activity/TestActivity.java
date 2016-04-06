package com.example.anjana.pescom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.anjana.pescom.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @SuppressWarnings("unused") // used in xml
    public void onIpClick(View v) {
        Intent i = new Intent(this, VoipActivity.class);
        i.putExtra(VoipActivity.EXTRA_IP_TEST, true);
        startActivity(i);
    }

    @SuppressWarnings("unused") // used in xml
    public void onNumberClick(View v) {
        Intent i = new Intent(this, VoipActivity.class);
        startActivity(i);
    }

}
