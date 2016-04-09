package com.example.anjana.pescom.util;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class PduHelper {

    public static final int CODE_PUSH_INCOMING_CALL = 1;

    public final static int CODE_VOIP_NEG_INCOMING = 1;

    public final static int CODE_RESPONSE_ACK = 1;
    public final static int CODE_RESPONSE_NACK = 0;
    public final static int CODE_RESPONSE_UNRECOGNIZED = -1;

    private final static String MSG_TERMINATOR = "#!";

    public static String[] getProtocolMessage(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        char[] buf = new char[Constants.MAX_PDU_LENGTH_LIMIT];
        int rctr = reader.read(buf);
        String msg = new String(buf, 0, rctr);
        Log.e("PHILIP socket string", msg);
        // cleaning because server
        int firstQ = msg.indexOf("\"");
        int secondQ = msg.lastIndexOf(MSG_TERMINATOR);
        return msg.substring(firstQ + 1, secondQ).split(":");
    }

    public static void sendProtocolMessage(OutputStream os, String msg) {
        PrintWriter respWriter = new PrintWriter(os);
        respWriter.print(msg + MSG_TERMINATOR);
        respWriter.flush();
    }
}
