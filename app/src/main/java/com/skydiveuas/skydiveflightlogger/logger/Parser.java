package com.skydiveuas.skydiveflightlogger.logger;

import android.util.Log;

import com.skydiveuas.skydiveflightlogger.logger.data.ExtendedDebugData;

/**
 * Created by Bartosz Nawrot on 2018-06-06.
 */

public class Parser {
    private static final String DEBUG_TAG = Parser.class.getSimpleName();

    private static final byte[] DEFINED_PREAMBLE_EXT_DEBUG_DATA =
            { (byte)0xFF, (byte)0xFE, (byte)0xFD, (byte)0xFC };

    private Listener listener;

    private int receivedDataCounter = -1;

    private byte[] preambleBuffer = new byte[4];
    private byte[] inBuffer = new byte[1024];

    public Parser(Listener listener) {
        this.listener = listener;
    }

    public void parse(byte[] data, int dataSize) {
        for (int i = 0; i < dataSize; ++i) {
            putByte(data[i]);
        }
    }

    private void putByte(final byte receivedByte) {
        if (validatePreamble(receivedByte)) {
            if (receivedDataCounter >= 0) {
                Log.e(DEBUG_TAG, "New preamble received before completion of previous frame");
            }
            // Indicate that buffer is ready to receive data, correct preamble received
            receivedDataCounter = 0;
        }
        else if (receivedDataCounter >= 0) {
            // Preamble was received, fill the buffer with arrived data
            inBuffer[receivedDataCounter] = receivedByte;
            receivedDataCounter++;

            if (receivedDataCounter >= (ExtendedDebugData.getSize() + 4)) {
                listener.onReceived(inBuffer);
                // Indicate that reception is finished, at least it should be
                // wait for new preamble
                receivedDataCounter = -1;
            }
        }
    }

    private boolean validatePreamble(final byte receivedByte) {
        // Shift preamble
        System.arraycopy(preambleBuffer, 0, preambleBuffer, 1, 3);
        preambleBuffer[0] = receivedByte;

        // Validate preamble:
        for (byte i=0; i<4; i++) {
            if (preambleBuffer[i] != DEFINED_PREAMBLE_EXT_DEBUG_DATA[i]) {
                return false;
            }
        }
        // valid preamble received
        return true;
    }

    public interface Listener {
        void onReceived(byte[] data);
    }
}
