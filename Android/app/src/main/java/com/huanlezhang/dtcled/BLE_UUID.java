package com.huanlezhang.dtcled;

import java.util.UUID;

public interface BLE_UUID {

    String DEVICE_ADDR = "3C:A3:08:A8:29:54";

    UUID LED_SERVICE_UUID = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
    UUID LED_CHARACTERISTIC_UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");

    byte[] LED_ON = {0x56, 0x00, 0x00, 0x00, 0x20, (byte)0x0f, (byte) 0xaa};
    byte[] LED_OFF = {0x56, 0x00, 0x00, 0x00, 0x00, (byte)0x0f, (byte) 0xaa};
}
