package com.skydiveuas.skydiveflightlogger.logger;

import com.skydive.java.data.CalibrationSettings;
import com.skydive.java.data.ControlSettings;

/**
 * Created by Bartosz Nawrot on 2018-06-06.
 */

public class Parser {
    private static final String DEBUG_TAG = Parser.class.getSimpleName();

    private Listener listener;

    public Parser(Listener listener) {
        this.listener = listener;

    }

    public void parse(byte[] data, int dataSize) {

    }

    public interface Listener {
        void onReceived(ExtendedDebugData message);

        void onReceived(CalibrationSettings message);

        void onReceived(ControlSettings message);
    }
}
