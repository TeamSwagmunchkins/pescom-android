package com.example.anjana.pescom.call;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by abrahamphilip on 26/1/16.
 */
public class IpCall implements Call {

    private static final int CALL_CONNECT_TIMEOUT = 100;

    public static final int PROTO_CODE_ACCEPTED = 1;
    private static final int PROTO_CODE_FAIL = 2;
    private static final int PROTO_CODE_BUSY = 1 << 2 + PROTO_CODE_FAIL;
    private static final int PROTO_CODE_REJECTED = 2 << 2 + PROTO_CODE_FAIL;

    private final String LOG_TAG = "IpCall";

    private Socket mConnectedSocket;

    @Override
    public void startCall(Address address, CallEvent callEvent) {
        if (!(address instanceof IpCallAddress)) {
            throw new IllegalArgumentException("IpCallAddress requried!");
        }
        String ip = ((IpCallAddress) address).mToIp;
        int port = ((IpCallAddress) address).mToPort;

        mConnectedSocket = new Socket();
        try {
            mConnectedSocket.connect(new InetSocketAddress(ip, port));
            Scanner socketScanner = new Scanner(mConnectedSocket.getInputStream());
            switch (socketScanner.nextInt()) {
                case PROTO_CODE_ACCEPTED: {
                    callEvent.onCallConnected(this, new CallConnection() {
                        @Override
                        public OutputStream getOutputStream() throws IOException {
                            return mConnectedSocket.getOutputStream();
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return mConnectedSocket.getInputStream();
                        }
                    });
                    break;
                }
                // TODO put in check for actual error codes
                default: {
                    callEvent.onCallFailed(this, FailureReason.REJECTED);
                }
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed connecting to " + ip + ":" + port, e);
            callEvent.onCallFailed(this, FailureReason.IO);
        }
    }

    public class IpCallAddress implements Call.Address {
        private final int mToPort;
        private final String mToIp;

        public IpCallAddress(String ip, int port) {
            mToIp = ip;
            mToPort = port;
        }
    }
}
