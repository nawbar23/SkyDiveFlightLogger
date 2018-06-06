package com.skydiveuas.skydiveflightlogger.logger;

import com.skydive.java.CommMessage;
import com.skydive.java.data.ControlData;
import com.skydive.java.data.DebugData;
import com.skydive.java.data.GpsData;
import com.skydive.java.data.ImuData;
import com.skydive.java.data.StateVector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by Bartosz Nawrot on 2018-06-06.
 */

public class ExtendedDebugData {
    private static final String DEBUG_TAG = ExtendedDebugData.class.getSimpleName();

    public ControlData controlData;
    public StateVector stateVector;
    public GpsData gpsData;
    public ImuData imuData;
    public float motors[] = new float[8];
    public DebugData.ControllerState controllerState;
    public int timestamp;

    private int crcValue;

    public ExtendedDebugData(final byte[] dataArray) {
        controlData = new ControlData(dataArray);
        stateVector = new StateVector(dataArray,
                ControlData.getSize());
        gpsData = new GpsData(dataArray,
                ControlData.getSize() + StateVector.getSize());
        imuData = new ImuData(dataArray,
                ControlData.getSize() + StateVector.getSize() + GpsData.getSize());

        int offset = ControlData.getSize() + StateVector.getSize() + GpsData.getSize() + ImuData.getSize();

        ByteBuffer buffer = ByteBuffer.wrap(dataArray, offset, dataArray.length - offset);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 8; ++i) {
            motors[i] = buffer.getFloat();
        }
        controllerState = DebugData.ControllerState.getControllerState(buffer.getShort());
        buffer.getShort(); // dummy padding

        timestamp = buffer.getInt();
        crcValue = buffer.getInt();
    }

    public byte[] serialize() {
        byte[] out = new byte[getSize()];
        serialize(out);
        return out;
    }

    public void serialize(byte[] out) {
        if (out.length >= getSize()) {
            ByteBuffer buffer = ByteBuffer.wrap(out);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            System.arraycopy(buffer.array(), 0, out, 0, getSize());
        }
    }

    private int computeCrc() {
        // compute CRC value from whole data, excluding last 4 bytes (CRC value)
        return CommMessage.computeCrc32(Arrays.copyOfRange(serialize(), 0, getSize() - 4));
    }

    public boolean isValid() {
        return crcValue == computeCrc();
    }

    public static int getSize() {
        return ControlData.getSize() + StateVector.getSize() + GpsData.getSize() + ImuData.getSize()
                + 8*4 + 4 + 2*4;
    }
}
