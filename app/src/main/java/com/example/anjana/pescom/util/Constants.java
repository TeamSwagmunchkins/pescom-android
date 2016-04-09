package com.example.anjana.pescom.util;

public class Constants {
    private static final boolean LOCAL = true;
    public final static String BASE_URL= LOCAL ?
            "http://192.168.5.34:8080/" : "https://secure-garden-80717.herokuapp.com/";
    public static final String SIGNUP_URL = BASE_URL + "login";
    public static final String OTP_URL = BASE_URL + "authenticate";
    public static final String CALL_URL = BASE_URL + "call";

    public static final int PUSH_PORT = 8989;
    public static final int VOIP_NEG_PORT = 7678;
    public static final int MAX_PDU_LENGTH_LIMIT = 1028; // number of characters

    public static final int VOIP_REC_PORT = 9768;

    public static class Timeout {
        public static final int PUSH_SOCK_READ = 30 * 1000;

        public static final int VOIP_NEG_READ = 10 * 1000;
        public static final int VOIP_NEG_ACCEPT = 30 * 1000;
        // how long user has to accept call
        public static final int VOIP_CALL_USER_ACCEPT = 30 * 1000;
        // how long to wait for a response after negotiation is done (for socket)
        public static final int VOIP_CALL_SOCK_ACCEPT = VOIP_CALL_USER_ACCEPT + 10 * 1000;
    }
}
