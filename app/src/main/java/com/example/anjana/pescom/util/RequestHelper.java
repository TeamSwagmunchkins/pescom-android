package com.example.anjana.pescom.util;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class RequestHelper {

    private final static String LOG_TAG = "RequestHelper";

    enum HTTP_METHOD {
        POST,
        GET
    }

    public static class RequestResult implements Parcelable {
        public final int RESPONSE_CODE;
        public final String RESPONSE_BODY;

        private RequestResult(int code, String body) {
            RESPONSE_BODY = body;
            RESPONSE_CODE = code;
        }

        protected RequestResult(Parcel in) {
            RESPONSE_CODE = in.readInt();
            RESPONSE_BODY = in.readString();
        }

        public static final Creator<RequestResult> CREATOR = new Creator<RequestResult>() {
            @Override
            public RequestResult createFromParcel(Parcel in) {
                return new RequestResult(in);
            }

            @Override
            public RequestResult[] newArray(int size) {
                return new RequestResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(RESPONSE_CODE);
            dest.writeString(RESPONSE_BODY);
        }
    }

    private static RequestResult makeRequest(String ep, ContentValues params,
                                             HTTP_METHOD method,
                                             Context context) throws IOException {
        String encodedParams = getEncodedParams(params);
        HttpURLConnection conn = null;
        switch (method) {

            case POST: {
                URL url = new URL(Preferences.getPreferences(context).getUrl(ep));
                Log.d(LOG_TAG, "Making request to: " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // write out the parameters
                DataOutputStream dStream = new DataOutputStream(conn.getOutputStream());
                Log.d(LOG_TAG, "Writing request parameters: " + encodedParams);
                dStream.writeBytes(encodedParams);
                dStream.flush();
                break;
            }
            case GET: {
                URL url = new URL(Preferences.getPreferences(context).getUrl(ep)
                        + "?" + encodedParams);
                Log.d(LOG_TAG, "Making request to: " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
                break;
            }
        }

        return new RequestResult(conn.getResponseCode(),
                conn.getResponseCode() == 200 ? convertStreamToString(conn.getInputStream()) : "");
    }

    private static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        Scanner se = new Scanner(is);

        try {
            while (se.hasNextLine()) {
                sb.append(se.nextLine());
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String getEncodedParams(ContentValues params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String key : params.keySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.getAsString(key), "UTF-8"));
        }

        return result.toString();
    }

    public static RequestResult makeVoipGetIp(String fromNo, String token, String toNo,
                                              Context context) throws IOException {
        final String fromKey = "from_phone_number";
        final String toKey = "to_phone_number";
        final String tokenKey = "token";

        ContentValues params = new ContentValues();
        params.put(fromKey, fromNo);
        params.put(tokenKey, token);
        params.put(toKey, toNo);

        return makeRequest(Constants.CALL_EP, params, HTTP_METHOD.POST, context);
    }

    public static RequestResult makeUpdateIp(String number, String token, String ip, int port,
                                             Context context) throws IOException {
        final String numberKey = "phone_number";
        final String tokenKey = "token";
        final String ipKey = "ip_address";
        final String portKey = "port";

        ContentValues params = new ContentValues();
        params.put(numberKey, number);
        params.put(tokenKey, token);
        params.put(ipKey, ip);
        params.put(portKey, port);

        return makeRequest(Constants.UPDATE_IP_EP, params, HTTP_METHOD.POST, context);
    }

    public static RequestResult getRegisteredUsers(String token, Context context)
            throws IOException {
        final String tokenKey = "token";

        ContentValues params = new ContentValues();
        params.put(tokenKey, token);

        return makeRequest(Constants.GET_USERS_EP, params, HTTP_METHOD.GET, context);
    }
}
