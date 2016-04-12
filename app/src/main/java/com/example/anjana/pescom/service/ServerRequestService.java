package com.example.anjana.pescom.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;

import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.Preferences;
import com.example.anjana.pescom.util.RequestHelper;

import java.io.IOException;

public class ServerRequestService extends IntentService {

    public final static String ACTION_VOIP_GET_IP = "voip_get_ip";
    public final static String ACTION_UPDATE_IP = "update_ip";

    public final static String EXTRA_VOIP_GET_IP_TO = "voip_get_ip_to";
    public final static String EXTRA_MESSENGER = "messenger";

    private static final String LOG_TAG = "ServerRequestService";

    public ServerRequestService() {
        super("ServerRequestService");
    }

    private void getVoipDestIp(final String fromNo, final String fromToken, final String phno,
                               final Messenger messenger) {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    RequestHelper.RequestResult result = RequestHelper.makeVoipGetIp(fromNo,
                            fromToken, phno, ServerRequestService.this);
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

    @Override
    protected void onHandleIntent(Intent intent) {
        String fromNo = Preferences.getPreferences(this).getNumber();
        String token = Preferences.getPreferences(this).getToken();
        Log.d("PHILIP", fromNo+token);

        switch(intent.getAction()) {
            case ACTION_VOIP_GET_IP:
                getVoipDestIp(fromNo, token, intent.getStringExtra(EXTRA_VOIP_GET_IP_TO),
                        (Messenger) intent.getParcelableExtra(EXTRA_MESSENGER));
                break;
            case ACTION_UPDATE_IP:
                WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                RequestHelper.RequestResult result = null;
                try {
                    result = RequestHelper.makeUpdateIp(fromNo, token, ip, Constants.PUSH_PORT, this);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
                    Message message = new Message();
                    message.obj = result;
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                Log.e(LOG_TAG, "unknown intent received!");
        }
    }
}
