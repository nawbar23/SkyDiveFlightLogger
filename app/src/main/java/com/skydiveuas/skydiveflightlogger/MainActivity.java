package com.skydiveuas.skydiveflightlogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.skydiveuas.skydiveflightlogger.logger.Logger;
import com.skydiveuas.skydiveflightlogger.usb.UsbOtgPort;

public class MainActivity extends AppCompatActivity implements Logger.Listener {
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    private UsbOtgPort usb;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = new Logger(this);

        usb = new UsbOtgPort(this);
        usb.setListener(logger);

        usb.connect();
    }

    @Override
    public void onStarted() {
        Log.e(DEBUG_TAG, "onStarted");
    }

    @Override
    public void onEnded() {
        Log.e(DEBUG_TAG, "onEnded");
    }

    @Override
    public void onError(String message) {
        Log.e(DEBUG_TAG, "onError: " + message);
    }

    @Override
    public void onStatusUpdate(long time, double freq) {
        Log.e(DEBUG_TAG, "onStatusUpdate: time: " + time + " ,freq: " + freq);
    }
}
