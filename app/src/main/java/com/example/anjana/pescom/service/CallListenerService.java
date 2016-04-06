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
    public static int LISTEN_PORT = 9832;
    public static void startListening(Context context) {
        Intent intent = new Intent(context, CallListenerService.class);
        context.startService(intent);
    }

    public CallListenerService() {
        super("CallListenerServie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ServerSocket listenerSocket = new ServerSocket();
            listenerSocket.setReuseAddress(true);
            listenerSocket.bind(new InetSocketAddress("0.0.0.0", LISTEN_PORT));
            Log.d("PHILIP", "listening");
            Socket connection = listenerSocket.accept();
            AudioRecorderThread thread = new AudioRecorderThread(connection.getOutputStream(),
                    connection.getInputStream());
            thread.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
