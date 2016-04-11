package com.example.anjana.pescom.call;

import android.util.Log;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AudioDecoder {

    private DataInputStream mInputStream;
    private final int mBufSize;

    public AudioDecoder(InputStream is, int bufSize) {
        mInputStream = new DataInputStream(is);
        mBufSize = bufSize;
    }

    public short[] read() throws IOException {

        byte[] tbuf = new byte[2*mBufSize];

        int read = mInputStream.read(tbuf, 0, tbuf.length);

        // ensure even number
        if (read%2!=0) read--;

        short[] buf = new short[read/2];

        for (int i = 0 ;i<read;i+=2) {
            // always even must be ensured earlier in code
            byte b1 = tbuf[i];
            byte b2 = tbuf[i+1];
            buf[i/2] = (short)((b2 << 8) + b1);
            // Log.d("Decoder", "" + buf[i/2]);
        }
        return buf;
    }
}
