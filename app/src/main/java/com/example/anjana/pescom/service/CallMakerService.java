package com.example.anjana.pescom.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.example.anjana.pescom.call.AudioRecorderThread;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.PduHelper;
import com.example.anjana.pescom.util.Preferences;
import com.example.anjana.pescom.util.UdpInputStream;
import com.example.anjana.pescom.util.UdpOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CallMakerService extends IntentService {

    // actually connect to the call receiving socket, after user has accepted call
    private final static String ACTION_CALL = "call";
    // begin negotiation with a given IP and port
    private final static String ACTION_NEGOTIATE = "negotiate";

    private static final String EXTRA_IP = "ip";
    private static final String EXTRA_PORT = "port";

    public CallMakerService() {
        super("CallMakerService");
    }

    public static void makeCall(Context context, String ip, int port) {
        Log.d("CallMakerService", "making call");
        Intent intent = new Intent(context, CallMakerService.class);
        intent.setAction(ACTION_CALL);
        intent.putExtra(EXTRA_IP, ip);
        intent.putExtra(EXTRA_PORT, port);
        context.startService(intent);
    }

    public static void startVoipNeg(Context context, String ip, int port) {
        Log.d("CallMakerService", "initiating negotiation");
        Intent intent = new Intent(context, CallMakerService.class);
        intent.setAction(ACTION_NEGOTIATE);
        intent.putExtra(EXTRA_IP, ip);
        intent.putExtra(EXTRA_PORT, port);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d("PHILIP", "handling intent");
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_CALL:
                    try {
                        Socket connection = new Socket();
                        //Log.d("PHILIP", "connecting");
                        connection.connect(new InetSocketAddress(intent.getStringExtra(EXTRA_IP),
                                intent.getIntExtra(EXTRA_PORT, -1)));
                        // we just want the connection to be successful so the other person gets
                        // our ip and knows we're ready. That happened if we reaached here.
                        connection.close();
                        //Log.d("PHILIP", "connected");

                        UdpOutputStream os = new UdpOutputStream(intent.getStringExtra(EXTRA_IP),
                                Constants.VOIP_UDP_RECEIVER_PORT);
                        UdpInputStream is = new UdpInputStream("0.0.0.0",
                                Constants.VOIP_UDP_SENDER_PORT);

                        new AudioRecorderThread(os, is).run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case ACTION_NEGOTIATE:
                    Socket connection = null;
                    try {
                        connection = new Socket();
                        connection.connect(new InetSocketAddress(intent.getStringExtra(EXTRA_IP),
                                intent.getIntExtra(EXTRA_PORT, -1)));
                        PduHelper.sendProtocolMessage(connection.getOutputStream(),
                                PduHelper.CODE_VOIP_NEG_INCOMING + ":" + Constants.VOIP_REC_PORT
                                        + ":" + Preferences.getPreferences(this).getNumber());
                        String parts[] = PduHelper.getProtocolMessage(connection.getInputStream());
                        if (parts[0].equals(PduHelper.CODE_RESPONSE_ACK + "")) {
                            Log.i("CallMakerService", "Negotiation successful, started listening!");
                            CallListenerService.startListening(this, Constants.VOIP_REC_PORT);
                        }
                    } catch (Exception e) {
                        // whatever happens, just print info and exit
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private class VoipNegotiationListenerThread {
        public VoipNegotiationListenerThread(Messenger messenger) {

        }
    }
}
