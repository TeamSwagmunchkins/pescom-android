package com.example.anjana.pescom.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.anjana.pescom.R;
import com.example.anjana.pescom.activity.ChatActivity;
import com.example.anjana.pescom.activity.ChatMessage;
import com.example.anjana.pescom.activity.ContactsTabActivity;
import com.example.anjana.pescom.activity.IncomingCallActivity;
import com.example.anjana.pescom.activity.CallingActivity;
import com.example.anjana.pescom.util.Constants;
import com.example.anjana.pescom.util.PduHelper;
import com.example.anjana.pescom.util.Preferences;
import com.example.anjana.pescom.util.RequestHelper;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class PushService extends Service {

    private static final String TAG = "PushService";


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
        Intent intent = new Intent(this, CallingActivity.class);
        intent.setAction(CallingActivity.ACTION_INCOMING);
        intent.putExtra(CallingActivity.EXTRA_IP, ip);
        intent.putExtra(CallingActivity.EXTRA_PORT, port);
        intent.putExtra(CallingActivity.EXTRA_NUMBER, callerNumber);
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
                case PduHelper.CODE_PUSH_INCOMING_MSG:
                    handlePendingMessages();
                    return PduHelper.CODE_RESPONSE_ACK + ":";
            }
            return PduHelper.CODE_RESPONSE_UNRECOGNIZED + ":";
        }

        private void handlePendingMessages() {
            // TODO: SOMZ
            Thread thd = new Thread() {
                public void run() {
                    try {
                        int notifID = 1;
                        int count;
                        String token = Preferences.getPreferences(PushService.this).getToken();
                        String ToNo = Preferences.getPreferences(PushService.this).getNumber();
                        RequestHelper.RequestResult result = RequestHelper.getPendingMessages(ToNo,
                                token, PushService.this);
                        switch (result.RESPONSE_CODE) {
                            case 200:
                                try {
                                    Log.d(TAG, "MessageDelivered");
                                    JSONObject json = new JSONObject(result.RESPONSE_BODY);
                                    String c = json.getString("count");
                                    count = Integer.parseInt(c);
                                    if (count > 0) {
                                        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        NotificationCompat.Builder mBuilder =
                                                new NotificationCompat.Builder(PushService.this)
                                                        .setSmallIcon(R.drawable.notif_icon)
                                                        .setContentTitle("PESCOM")
                                                        .setContentText("You have New Messages")
                                                        .setTicker("New Message")
                                                        .setSound(soundUri);
                                        Intent resultIntent = new Intent(PushService.this, ContactsTabActivity.class);
                                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(PushService.this);
                                        stackBuilder.addParentStack(ContactsTabActivity.class);
                                        stackBuilder.addNextIntent(resultIntent);
                                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                        mBuilder.setAutoCancel(true);
                                        mBuilder.addAction(0, "View", resultPendingIntent);
                                        mBuilder.setNumber(count);
                                        mBuilder.setContentIntent(resultPendingIntent);
                                        notificationManager.notify(notifID, mBuilder.build());
                                    }
                                    JSONArray jArray = json.getJSONArray("messages");
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject json_data = jArray.getJSONObject(i);
                                        JSONObject copy = new JSONObject(json_data.toString());
                                        copy.put("me",false);
                                        Log.i(TAG,copy.toString());
                                        Preferences.getPreferences(PushService.this).addMessageFor(json_data.getString("from_phone_number"),copy);
                                        //.displayMessage(chatMessage);
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON parsing failed for: "
                                            + result.RESPONSE_BODY, e);
                                    e.printStackTrace();
                                }
                                break;

                            case 404:
                                Toast.makeText(PushService.this,
                                        "User Not Found",
                                        Toast.LENGTH_SHORT).show();
                                break;

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IOError", e);
                    }
                }
            };
            thd.start();
        }

        private int getVoipNegPort() {
            // TODO: Reserve a port and make sure it is free before returning from here
            return Constants.VOIP_NEG_PORT;
        }

        public boolean isAppIsInBackground(Context context) {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                if (!taskInfo.isEmpty()) {
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (componentInfo.getPackageName().equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }

            return isInBackground;
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
                        if (serverSocket != null) {
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
}
