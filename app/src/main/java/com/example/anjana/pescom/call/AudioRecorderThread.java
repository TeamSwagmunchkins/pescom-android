package com.example.anjana.pescom.call;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
public class AudioRecorderThread extends Thread {
    private boolean stopped = false;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private AudioTrack mTrack;

    public AudioRecorderThread(OutputStream os, InputStream is) {
        mOutputStream = os;
        mInputStream = is;
    }

    @Override
    public void run() {

        // android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        Log.d("Audio", "Running Audio Thread");
        AudioRecord recorder = null;
        short[][] buffers = new short[256][160];
        int ix = 0;

        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
        final int encoding = AudioFormat.ENCODING_PCM_8BIT;
        final int bitRate = 8000;
        try {
            int N = AudioRecord.getMinBufferSize(bitRate, AudioFormat.CHANNEL_IN_MONO,
                    encoding);
            Log.d("N", "" + N);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, bitRate,
                    AudioFormat.CHANNEL_IN_MONO, encoding, N * 10);
            mTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, bitRate,
                    AudioFormat.CHANNEL_OUT_MONO, encoding, N * 10, AudioTrack.MODE_STREAM);
            recorder.startRecording();
            mTrack.play();

            final AudioEncoder encoder = new AudioEncoder(mOutputStream);
            final AudioDecoder decoder = new AudioDecoder(mInputStream, N * 10);
            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */

            new Thread() {
                @Override
                public void run() {
                    while (!stopped) {
                        short[] recv = new short[0];
                        try {
                            recv = decoder.read();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mTrack.write(recv, 0, recv.length);
                    }
                }
            }.start();
            while (!stopped) {
                // Log.i("Map", "Writing new data to buffer");
                short[] buffer = buffers[ix++ % buffers.length];
                recorder.read(buffer, 0, buffer.length);
                encoder.write(buffer, 0, buffer.length);
                // encoder.flush();
                // Log.d("N", "" + N);
                // mOutputStream.write(buffer, 0, buffer.length);
            }
        } catch (Throwable x) {
            Log.w("Audio", "Error reading voice audio", x);
        }
        /*
         * Frees the thread's resources after the loop completes so that it can be run again
         */ finally {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            mTrack.stop();
            mTrack.release();
        }
    }

    /**
     * Called from outside of the thread in order to stop the recording/playback loop
     */
    private void close() {
        stopped = true;
    }

    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};

    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d("AudioRecorderThread", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("AudioRecorderThread", rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }

}