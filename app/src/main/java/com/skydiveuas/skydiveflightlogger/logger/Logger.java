package com.skydiveuas.skydiveflightlogger.logger;

import com.skydive.java.CommInterface;
import com.skydive.java.data.CalibrationSettings;
import com.skydive.java.data.ControlSettings;

import java.io.IOException;

/**
 * Created by Bartosz Nawrot on 2018-06-06.
 */

public class Logger implements CommInterface.CommInterfaceListener, Parser.Listener {
    private static final String DEBUG_TAG = Logger.class.getSimpleName();

    private Parser parser;

    public Logger() {
        this.parser = new Parser(this);
    }

    @Override
    public void onReceived(ExtendedDebugData message) {

    }

    @Override
    public void onReceived(CalibrationSettings message) {

    }

    @Override
    public void onReceived(ControlSettings message) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onError(IOException e) {

    }

    @Override
    public void onDataReceived(byte[] data, int dataSize) {

    }
}
