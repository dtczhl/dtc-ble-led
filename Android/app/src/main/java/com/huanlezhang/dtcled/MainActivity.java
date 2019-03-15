package com.huanlezhang.dtcled;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DTC MainActivity";

    private static final String PERMISSION_STRINGS[] = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };
    private final int PERMISSION_ID = 1;
    private final int BLE_REQUEST_ID = 2;

    private MyBle mMyBle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Program Starts");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STRINGS, PERMISSION_ID);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLE_REQUEST_ID);
        } else {
            mMyBle = new MyBle(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == BLE_REQUEST_ID) {
            mMyBle = new MyBle(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void livingRoomBtnClick(View view) {

        ToggleButton toggleButton = (ToggleButton) view;

        if (toggleButton.isChecked()) {
            // turn on light
            if (!mMyBle.livingRoom(true)) {
                toggleButton.setChecked(false);
            }
        } else {
            // turn off light
            if (!mMyBle.livingRoom(false)) {
                toggleButton.setChecked(true);
            }
        }
    }

    public void aboutMe(View view) {
        AboutMe.showDialog(this);
    }

    public void clearBle(View view) {
        mMyBle.clearBles();
    }
}
