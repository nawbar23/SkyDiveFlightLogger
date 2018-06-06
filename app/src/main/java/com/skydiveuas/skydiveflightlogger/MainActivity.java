package com.skydiveuas.skydiveflightlogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.skydiveuas.skydiveflightlogger.logger.Logger;
import com.skydiveuas.skydiveflightlogger.usb.UsbOtgPort;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    private UsbOtgPort usb;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = new Logger();

        usb = new UsbOtgPort(this);
        usb.setListener(logger);

        usb.connect();
    }
}
