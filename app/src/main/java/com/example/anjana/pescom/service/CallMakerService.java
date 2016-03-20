package com.example.anjana.pescom.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.anjana.pescom.call.AudioRecorderThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by abrahamphilip on 26/1/16.
 */
public class CallMakerService extends IntentService {

    private static final String EXTRA_IP = "ip";

    public CallMakerService() {
        super("CallMakerService");
    }

    public static void makeCall(Context context, String ip) {
        Log.d("PHILIP", "making call");
        Intent intent = new Intent(context, CallMakerService.class);
        intent.putExtra(EXTRA_IP, ip);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d("PHILIP", "handling intent");
        try {
            Socket connection = new Socket();
            //Log.d("PHILIP", "connecting");
            connection.connect(new InetSocketAddress(intent.getStringExtra(EXTRA_IP),
                    CallListenerService.LISTEN_PORT));
            //Log.d("PHILIP", "connected");
            new AudioRecorderThread(connection.getOutputStream(), connection.getInputStream()).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
