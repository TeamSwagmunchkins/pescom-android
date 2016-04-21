package com.example.anjana.pescom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.service.PushService;
import com.example.anjana.pescom.service.ServerRequestService;
import com.example.anjana.pescom.util.RequestHelper;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this, PushService.class);
        startService(intent);
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

    @SuppressWarnings("unused") // used in xml
    public void onUpdateClick(View v) {
        Intent intent = new Intent(this, ServerRequestService.class);
        intent.setAction(ServerRequestService.ACTION_UPDATE_IP);

        Messenger messenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                RequestHelper.RequestResult result = (RequestHelper.RequestResult) msg.obj;
                if (result == null) {
                    Toast.makeText(getBaseContext(), "Could not connect to server!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (result.RESPONSE_CODE == 200) {
                        Toast.makeText(getBaseContext(), "IP Updated successfully!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),
                                "IP Update failed with " + result.RESPONSE_CODE,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

        intent.putExtra(ServerRequestService.EXTRA_MESSENGER, messenger);

        startService(intent);
    }

    @SuppressWarnings("unused") // used in xml
    public void onSettingsClick(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("unused") // used in xml
    public void onMainScreenClick(View v) {
        Intent i = new Intent(this, ContactsTabActivity.class);
        startActivity(i);
    }
}