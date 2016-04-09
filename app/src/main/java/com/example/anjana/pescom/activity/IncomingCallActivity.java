package com.example.anjana.pescom.activity;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.service.CallMakerService;

public class IncomingCallActivity extends AppCompatActivity {

    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_CALLER_NUMBER = "number";

    private int mCallerPort;
    private String mCallerIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incoming_call);

        ((TextView)findViewById(R.id.tv_caller_name)).setText(getIntent().getStringExtra(
                EXTRA_CALLER_NUMBER));

        mCallerIp = getIntent().getStringExtra(EXTRA_IP);
        mCallerPort = getIntent().getIntExtra(EXTRA_PORT, -1);
    }

    // layout xml onClick methods

    public void onAcceptClick(View v) {
        CallMakerService.makeCall(this, mCallerIp, mCallerPort);
    }

    public void onDeclineClick(View v) {
        super.onBackPressed();
    }
}
