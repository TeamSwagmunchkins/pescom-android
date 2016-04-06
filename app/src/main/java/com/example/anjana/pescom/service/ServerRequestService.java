package com.example.anjana.pescom.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.anjana.pescom.util.Preferences;
import com.example.anjana.pescom.util.RequestHelper;

import java.io.IOException;

public class ServerRequestService extends Service {

    public final static String ACTION_VOIP_GET_IP = "voip_get_ip";
    public final static String EXTRA_VOIP_GET_IP_TO = "voip_get_ip_to";
    public final static String EXTRA_HANDLER = "handler";

    private static final String LOG_TAG = "ServerRequestService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) return START_NOT_STICKY;

        switch(intent.getAction()) {
            case ACTION_VOIP_GET_IP:
                String fromNo = Preferences.getPreferences(this).getNumber();
                String token = Preferences.getPreferences(this).getToken();
                Log.d("PHILIP", fromNo+token);
                getVoipDestIp(fromNo, token, intent.getStringExtra(EXTRA_VOIP_GET_IP_TO),
                        (Messenger) intent.getParcelableExtra(EXTRA_HANDLER));
                break;
            default:
                Log.e(LOG_TAG, "unknown intent received!");
        }
        return START_NOT_STICKY;
    }

    private void getVoipDestIp(final String fromNo, final String fromToken, final String phno,
                               final Messenger messenger) {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    RequestHelper.RequestResult result = RequestHelper.makeVoipGetIp(fromNo,
                            fromToken, phno);
                    Log.d(LOG_TAG, "Returned from VoIP with: " + result.RESPONSE_CODE
                            + " " + result.RESPONSE_BODY);
                    Message msg = new Message();
                    msg.obj = result;
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        Log.e(LOG_TAG, "voip ip failed messenger", e);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "voip ip" ,e);
                }
            }
        };
        th.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
