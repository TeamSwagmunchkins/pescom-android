package com.example.anjana.pescom.call;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by abrahamphilip on 26/1/16.
 */
public interface Call {
    enum FailureReason {
        BUSY, REJECTED, IO, UNKNOWN
    }
    interface CallEvent {
        void onCallConnected(Call call, CallConnection connection);
        void onCallFailed(Call call, FailureReason reason);
        void onCallCompleted(Call call);
    }
    interface CallConnection {
        OutputStream getOutputStream() throws IOException;
        InputStream getInputStream() throws IOException;
    }
    interface Address {
        // empty interface
    }
    void startCall(Address address, CallEvent callEvent) throws IOException;
}
