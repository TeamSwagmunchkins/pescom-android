package com.example.anjana.pescom.call;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioEncoder {

    private DataOutputStream mOutputStream;

    public AudioEncoder(OutputStream outputStream) {
        mOutputStream = new DataOutputStream(outputStream);
    }

    public void write(short[] buffer, int offset, int bufLen) throws IOException {
        // Log.d("PHILIP", "writing " + bufLen);
        mOutputStream.writeInt(bufLen);
        for(int i = offset; i< offset + bufLen; i++) {
            byte b1 = (byte) (buffer[i] & 0xFF);
            byte b2 = (byte) (buffer[i] >> 8);
            // Log.d("PHILIP", "writing " + b1);
            mOutputStream.write(b1);
            // Log.d("PHILIP", "writing " + b2);
            mOutputStream.write(b2);
        }
    }

    public void flush() throws IOException {
        mOutputStream.flush();
    }

    public void close() throws IOException {
        mOutputStream.close();
    }

}
