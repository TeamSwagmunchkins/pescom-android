package com.example.anjana.pescom.util;

public class Constants {
    public static final String SIGNUP_EP = "login";
    public static final String OTP_EP = "authenticate";
    public static final String CALL_EP = "call";
    public static final String UPDATE_IP_EP = "update_ip";
    public static final String GET_USERS_EP = "users";
    public static final String GET_PENDING_MESSAGES_EP = "message_receive";
    public static final String SEND_MESSAGE_EP = "message_send";

    public static final int PUSH_PORT = 8989;
    public static final int VOIP_NEG_PORT = 7678;
    public static final int MAX_PDU_LENGTH_LIMIT = 1028; // number of characters

    public static final int VOIP_REC_PORT = 9768;
    public static final int VOIP_UDP_RECEIVER_PORT = 9799;
    public static final int VOIP_UDP_SENDER_PORT = 9899;

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
