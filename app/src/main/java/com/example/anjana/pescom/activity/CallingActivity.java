package com.example.anjana.pescom.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.service.CallMakerService;
import com.example.anjana.pescom.service.ServerRequestService;
import com.example.anjana.pescom.util.RequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class CallingActivity extends AppCompatActivity {

    public static final String EXTRA_IP = "ip";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_NUMBER = "number";

    public static final String ACTION_DIAL = "dial";
    public static final String ACTION_INCOMING = "incoming";

    private int mCallerPort;
    private String mCallerIp;
    private String mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNumber = getIntent().getStringExtra(EXTRA_NUMBER);

        switch (getIntent().getAction()) {
            case ACTION_INCOMING:
                setContentView(R.layout.activity_incoming_call);

                ((TextView) findViewById(R.id.tv_caller_name)).setText(getIntent().getStringExtra(
                        EXTRA_NUMBER));

                mCallerIp = getIntent().getStringExtra(EXTRA_IP);
                mCallerPort = getIntent().getIntExtra(EXTRA_PORT, -1);

                setNumber();
                break;
            case ACTION_DIAL:
                setContentView(R.layout.activity_dialling_call);
                setNumber();
                startNegotiation(mNumber);
                break;
        }
    }

    public void setTitle(String text) {
        ((TextView) findViewById(R.id.tv_title)).setText(text);
    }

    public void setNumber() {
        ((TextView) findViewById(R.id.tv_caller_name)).setText(mNumber);
    }

    public void startNegotiation(String number) {
        Intent serviceIntent = new Intent(this, ServerRequestService.class);
        serviceIntent.putExtra(ServerRequestService.EXTRA_VOIP_GET_IP_TO, number);

        Messenger messenger = new Messenger(
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        RequestHelper.RequestResult result
                                = (RequestHelper.RequestResult) msg.obj;
                        switch (result.RESPONSE_CODE) {
                            case 200:
                                try {
                                    Log.d("PHILIP", result.RESPONSE_BODY + "");
                                    JSONObject json = new JSONObject(result.RESPONSE_BODY);
                                    String ip = json.getString("ip_address");
                                    int port = json.getInt("port");
                                    Toast.makeText(CallingActivity.this, ip + ":" + port,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    CallMakerService.startVoipNeg(CallingActivity.this, ip, port);
                                } catch (JSONException e) {
                                    Log.e(VoipActivity.LOG_TAG, "JSON parsing failed for: "
                                            + result.RESPONSE_BODY, e);
                                }
                                break;
                            case 404:
                                Toast.makeText(CallingActivity.this,
                                        "Contact out of reach!",
                                        Toast.LENGTH_LONG)
                                        .show();
                                CallingActivity.super.onBackPressed();
                                break;
                            case 401:
                                Toast.makeText(CallingActivity.this,
                                        "Invalid login!",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                break;
                        }
                    }
                }
        );

        serviceIntent.putExtra(ServerRequestService.EXTRA_MESSENGER,
                messenger);

        serviceIntent.setAction(ServerRequestService.ACTION_VOIP_GET_IP);
        startService(serviceIntent);
    }
    // layout xml onClick methods

    public void onAcceptClick(View v) {
        CallMakerService.makeCall(this, mCallerIp, mCallerPort);
        setContentView(R.layout.activity_dialling_call);
        setNumber();
        setTitle("In Call");
    }

    public void onDeclineClick(View v) {
        CallMakerService.endCall(this);
        super.onBackPressed();
    }
}
