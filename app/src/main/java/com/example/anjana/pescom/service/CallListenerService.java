package com.example.anjana.pescom.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.anjana.pescom.call.AudioRecorderThread;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.UdpInputStream;
import com.example.anjana.pescom.util.UdpOutputStream;

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
            String ip = connection.getInetAddress().getHostAddress();
            Log.d("CallListenerService", "Incoming ip is: " + ip);
            connection.close();

            UdpOutputStream os = new UdpOutputStream(ip, Constants.VOIP_UDP_SENDER_PORT);
            UdpInputStream is = new UdpInputStream("0.0.0.0", Constants.VOIP_UDP_RECEIVER_PORT);
            AudioRecorderThread thread = new AudioRecorderThread(os,
                    is);
            thread.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
