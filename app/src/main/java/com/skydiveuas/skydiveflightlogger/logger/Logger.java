package com.skydiveuas.skydiveflightlogger.logger;

import android.util.Log;

import com.skydive.java.CommInterface;
import com.skydive.java.data.CalibrationSettings;
import com.skydive.java.data.ControlSettings;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bartosz Nawrot on 2018-06-06.
 */

public class Logger implements CommInterface.CommInterfaceListener, Parser.Listener {
    private static final String DEBUG_TAG = Logger.class.getSimpleName();

    private static final long MAX_INACTIVE_TIME = 3000; // [ms]
    private static final long STATUS_UPDATE_INTERVAL = 200; // [ms]

    private enum State { W4_CALIB, W4_CONTROL, LOGGING }

    private State state;

    private Listener listener;
    private Parser parser;

    private Timer timeoutTimer;
    private Timer statusTimer;

    private long startTime;
    private long receivedCount;
    private boolean received;

    public Logger(Listener listener) {
        this.listener = listener;
        this.parser = new Parser(this);

        state = State.W4_CALIB;
    }

    @Override
    public void onReceived(CalibrationSettings message) {
        if (state == State.W4_CALIB) {
            // TODO safe to file
            state = State.W4_CONTROL;
        } else {
            Log.e(DEBUG_TAG, "onReceived(CalibrationSettings) in bad state");
        }
    }

    @Override
    public void onReceived(ControlSettings message) {
        if (state == State.W4_CALIB) {
            // TODO safe to file
            startLog();
        } else {
            Log.e(DEBUG_TAG, "onReceived(ControlSettings) in bad state");
        }
    }

    @Override
    public void onReceived(ExtendedDebugData message) {
        if (state == State.LOGGING) {
            // TODO safe to file
            received = true;
        } else {
            Log.e(DEBUG_TAG, "onReceived(ExtendedDebugData) in bad state");
        }
    }

    @Override
    public void onConnected() {
        Log.e(DEBUG_TAG, "onConnected");
    }

    @Override
    public void onDisconnected() {
        endLog();
        listener.onError("Board disconnected");
    }

    @Override
    public void onError(IOException e) {
        endLog();
        listener.onError(e.getMessage());
    }

    @Override
    public void onDataReceived(byte[] data, int dataSize) {
        parser.parse(data, dataSize);
    }

    private TimerTask timeoutTask = new TimerTask() {
        @Override
        public void run() {
            if (state == State.LOGGING && !received) {
                endLog();
            }
            received = false;
        }
    };

    private TimerTask statusTask = new TimerTask() {
        @Override
        public void run() {
            long time = System.currentTimeMillis() - startTime;
            if (time > 0) {
                listener.onStatusUpdate(time, receivedCount / (time / 1000.0));
            }
        }
    };

    private void startLog() {
        openFile();
        startTime = System.currentTimeMillis();
        receivedCount = 0;
        timeoutTimer = new Timer();
        timeoutTimer.scheduleAtFixedRate(timeoutTask, MAX_INACTIVE_TIME, MAX_INACTIVE_TIME);
        statusTimer = new Timer();
        statusTimer.scheduleAtFixedRate(statusTask, STATUS_UPDATE_INTERVAL, STATUS_UPDATE_INTERVAL);
        listener.onStarted();
        state = State.LOGGING;
    }

    private void endLog() {
        closeFile();
        timeoutTimer.cancel();
        timeoutTimer = null;
        statusTimer.cancel();
        statusTimer = null;
        listener.onEnded();
        state = State.W4_CALIB;
    }

    private void openFile() {
        // TODO open new files
    }

    private void closeFile() {
        // TODO close opened files
    }

    public interface Listener {
        void onStarted();

        void onEnded();

        void onError(String message);

        void onStatusUpdate(long time, double freq);
    }
}
