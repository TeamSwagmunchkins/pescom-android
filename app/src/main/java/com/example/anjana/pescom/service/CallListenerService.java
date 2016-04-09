package com.example.anjana.pescom.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.anjana.pescom.call.AudioRecorderThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static helper methods
 */
public class CallListenerService extends IntentService {
    private static final String EXTRA_PORT = "port";
    public static void startListening(Context context, int port) {
        Intent intent = new Intent(context, CallListenerService.class);
        intent.putExtra(EXTRA_PORT, port);
        context.startService(intent);
    }

    public CallListenerService() {
        super("CallListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ServerSocket listenerSocket = new ServerSocket();
            listenerSocket.setReuseAddress(true);
            listenerSocket.bind(new InetSocketAddress("0.0.0.0", intent.getIntExtra(EXTRA_PORT, -1)));
            Log.d("PHILIP", "listening at " + intent.getIntExtra(EXTRA_PORT, -1));
            Socket connection = listenerSocket.accept();
            AudioRecorderThread thread = new AudioRecorderThread(connection.getOutputStream(),
                    connection.getInputStream());
            thread.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
