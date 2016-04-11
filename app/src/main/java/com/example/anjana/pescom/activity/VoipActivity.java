package com.example.anjana.pescom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.service.CallListenerService;
import com.example.anjana.pescom.service.CallMakerService;
import com.example.anjana.pescom.service.ServerRequestService;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.RequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class VoipActivity extends AppCompatActivity {

    public static String EXTRA_IP_TEST = "type";

    public static String LOG_TAG = "VoipActivity";

    private boolean isIpTest;

    private EditText mDestEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isIpTest = getIntent().getBooleanExtra(EXTRA_IP_TEST, false);

        if (isIpTest) {
            CallListenerService.startListening(this, Constants.VOIP_REC_PORT);
        }

        mDestEt = (EditText) findViewById(R.id.ip_et);
    }

    public void call(View v) {
        Log.d("isIptest", "" + isIpTest);
        if (isIpTest) {
            CallMakerService.makeCall(this, mDestEt.getText().toString(), Constants.VOIP_REC_PORT);
        } else {
            Intent serviceIntent = new Intent(this, ServerRequestService.class);
            serviceIntent.putExtra(ServerRequestService.EXTRA_VOIP_GET_IP_TO,
                    mDestEt.getText().toString());

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
                                        Toast.makeText(VoipActivity.this, ip + ":" + port,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        CallMakerService.startVoipNeg(VoipActivity.this, ip, port);
                                    } catch (JSONException e) {
                                        Log.e(VoipActivity.LOG_TAG, "JSON parsing failed for: "
                                                + result.RESPONSE_BODY, e);
                                    }
                                    break;
                                case 404:
                                    Toast.makeText(VoipActivity.this,
                                            "Contact not registered with server!",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    break;
                                case 401:
                                    Toast.makeText(VoipActivity.this,
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
    }
}
