package com.example.anjana.pescom.call;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by abrahamphilip on 27/1/16.
 */
public class AudioDecoder {

    private DataInputStream mInputStream;

    public AudioDecoder(InputStream is) {
        mInputStream = new DataInputStream(is);
    }

    public short[] read() throws IOException, EOFException {
        int bufLen = mInputStream.readInt();
        //Log.d("PHILIP", "read " + bufLen);
        if (bufLen == -1) throw new EOFException();

        short[] buf = new short[bufLen];

        byte[] tbuf = new byte[2*bufLen];

        for (int i = 0 ;i<bufLen;i++) {
            int b1 = mInputStream.read();
            //Log.d("PHILIP", "read " + b1);
            int b2 = mInputStream.read();
            //Log.d("PHILIP", "read " + b2);
            if (b1 == -1 || b2 == -1) throw new EOFException();
            buf[i] = (short)((b2 << 8) + b1);
        }

        return buf;
    }
}
