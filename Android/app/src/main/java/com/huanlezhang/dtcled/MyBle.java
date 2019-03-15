package com.huanlezhang.dtcled;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyBle {

    private static final String TAG = "DTC MyBle";

    private Context mContext;

    private BluetoothDevice mLivingRoomDevice;
    private BluetoothGatt mLivingRoomGatt;
    private BluetoothGattCharacteristic mLivingRoomCharacteristic;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private ScanCallback mBleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (result.getDevice().getAddress().equals(BLE_UUID.DEVICE_ADDR)){
                Log.d(TAG, "Device found");
                mBluetoothScanner.stopScan(mBleScanCallback);
                mLivingRoomDevice = result.getDevice();

                mLivingRoomDevice.connectGatt(mContext, true, mBleGattCallback);
            }

        }
    };

    private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "scan result");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "gatt found");

                if (gatt.getDevice() == mLivingRoomDevice) {
                    Log.d(TAG, "Gatt device match living room");

                    gatt.discoverServices();
                    mLivingRoomGatt = gatt;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            try {
                mLivingRoomCharacteristic = gatt.getService(BLE_UUID.LED_SERVICE_UUID)
                        .getCharacteristic(BLE_UUID.LED_CHARACTERISTIC_UUID);

                if (mLivingRoomCharacteristic != null) {
                    Log.d(TAG, "living room charateristic found");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public MyBle(Context context) {

        Log.d(TAG, "MyBle");

        mContext = context;

        Activity activity = (Activity) mContext;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            activity.finish();
        }

        connect();

    }


    private void connect() {
        // connect to ble
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothManager == null || !mBluetoothAdapter.isEnabled()) {
            ((Activity) mContext).finish();
        }


        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilterBuilder.setDeviceAddress(BLE_UUID.DEVICE_ADDR);
        ScanFilter scanFilter = scanFilterBuilder.build();

        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        builder.setReportDelay(0);

        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothScanner.startScan(Arrays.asList(scanFilter), builder.build(), mBleScanCallback);

        Log.d(TAG, "scan starts");

    }

    public boolean livingRoom(boolean onOff) {

        if (mLivingRoomCharacteristic != null) {
            if (onOff) {
                mLivingRoomCharacteristic.setValue(BLE_UUID.LED_ON);
            } else {
                mLivingRoomCharacteristic.setValue(BLE_UUID.LED_OFF);
            }
            if (mLivingRoomGatt.writeCharacteristic(mLivingRoomCharacteristic)) {
                return true;
            } else {
                connect();
                return false;
            }


        }
        return false;
    }

    public void clearBles() {

        if (mLivingRoomGatt != null) {
            mLivingRoomGatt.disconnect();
            mLivingRoomGatt = null;
            mLivingRoomCharacteristic = null;
        }
    }

}
