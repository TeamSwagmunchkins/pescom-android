package com.example.anjana.pescom.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.anjana.pescom.activity.IncomingCallActivity;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.PduHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PushService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Thread mThread;
    private Thread mVoipNegThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("PHILIP", "Push service starting!");
        if (mThread == null) {
            mThread = startListeningForPush();
        }
        return START_STICKY;
    }

    private Thread startListeningForPush() {
        Thread th = new MainPushThread();
        th.start();
        return th;
    }

    protected void startIncomingCallActivity(String ip, int port, String callerNumber) {
        Log.i("PHILIP", "Call incoming!");
        Intent intent = new Intent(this, IncomingCallActivity.class);
        intent.putExtra(IncomingCallActivity.EXTRA_IP, ip);
        intent.putExtra(IncomingCallActivity.EXTRA_PORT, port);
        intent.putExtra(IncomingCallActivity.EXTRA_CALLER_NUMBER, callerNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class MainPushThread extends Thread {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(Constants.PUSH_PORT);
                // TODO: Use executor service
                //noinspection InfiniteLoopStatement cuz we needz it to keep listening for incoming pushes. Forever and ever. FOREVER!
                while (true) {
                    Socket connSock = null;
                    try {
                        connSock = serverSocket.accept();
                        connSock.setSoTimeout(Constants.Timeout.PUSH_SOCK_READ);
                        Log.e("PHILIP socket", "reading");
                        String[] parts = PduHelper.getProtocolMessage(connSock.getInputStream());
                        String resp = handlePush(parts);
                        PduHelper.sendProtocolMessage(connSock.getOutputStream(), resp);
                        connSock.close();
                    } catch (Exception e) {
                        // whatever goes wrong, we don't care beyond waiting for next connection
                        Log.e("PushServiceThread", "Error with connection socket", e);
                    } finally {
                        if (connSock != null) connSock.close();
                    }

                }
            } catch (IOException e) {
                Log.e("PushServiceThread", "Error with welcome socket", e);
            }
        }

        /**
         * @return the response acknowledging this push
         */
        private String handlePush(String[] parts) {
            // exceptions are okay, die and wait for next push
            int code = Integer.parseInt(parts[0]);
            switch (code) {
                case PduHelper.CODE_PUSH_INCOMING_CALL:
                    if (mVoipNegThread != null && mVoipNegThread.isAlive()) {
                        // user is currently negotiating/in a call
                        return PduHelper.CODE_RESPONSE_NACK + ":";
                    }
                    mVoipNegThread = null;
                    int negPort = getVoipNegPort();
                    mVoipNegThread = new VoipNegotiationThread(negPort);
                    mVoipNegThread.start();
                    // TODO: ensure socket is created before replying to server
                    return PduHelper.CODE_RESPONSE_ACK + ":" + negPort;
            }
            return PduHelper.CODE_RESPONSE_UNRECOGNIZED + ":";
        }
    }

    private int getVoipNegPort() {
        // TODO: Reserve a port and make sure it is free before returning from here
        return Constants.VOIP_NEG_PORT;
    }

    // represents the state during which the phone is ringing
    private class VoipNegotiationThread extends Thread {
        private final int mNegPort;

        public VoipNegotiationThread(int port) {
            mNegPort = port;
        }

        @SuppressLint("HandlerLeak") // handler stays with the service, is not exposed
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(mNegPort);
                // if they haven't come yet, they ain't coming
                serverSocket.setSoTimeout(Constants.Timeout.VOIP_NEG_ACCEPT);
                Socket connSock = serverSocket.accept();
                // if legit connection, data should flow fast and thick
                connSock.setSoTimeout(Constants.Timeout.VOIP_NEG_READ);
                String resp = handlePdu(PduHelper.getProtocolMessage(connSock.getInputStream()),
                        connSock.getInetAddress().getHostAddress());
                PduHelper.sendProtocolMessage(connSock.getOutputStream(), resp);
            } catch (Exception e) {
                // TODO: Easy to DoS this, fix
                // we don't really care what the exception is, just end the connection
                Log.e("VoipThreadNeg", "Error during neg", e);
            } finally {
                mVoipNegThread = null;
                try {
                    if (serverSocket!=null) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String handlePdu(String[] parts, String ip) {
            int code = Integer.parseInt(parts[0]);
            switch (code) {
                case PduHelper.CODE_VOIP_NEG_INCOMING:
                    // incoming call
                    startIncomingCallActivity(ip, Integer.parseInt(parts[1]), parts[2]);
                    return PduHelper.CODE_RESPONSE_ACK + ":";
            }
            return PduHelper.CODE_RESPONSE_UNRECOGNIZED + ":";
        }
    }
}
