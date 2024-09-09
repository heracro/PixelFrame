package com.pixelframe.model.eventListeners;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.pixelframe.controller.ui.activity.TransferActivity;

import java.nio.ByteBuffer;
import java.util.UUID;

public class BTSendButtonGATTListener implements View.OnClickListener {

    private final TransferActivity activity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice picoDevice;

    // UUIDs for GATT service and characteristics (replace with your own if necessary)
    private static final UUID PICO_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb"); // Example
    private static final UUID SLOT_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"); // Example
    private static final UUID TIME_CHARACTERISTIC_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"); // Example
    private static final UUID IMAGE_CHARACTERISTIC_UUID = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb"); // Example

    public BTSendButtonGATTListener(TransferActivity activity) {
        this.activity = activity;
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("BTSendButton", "Transfer button clicked!");

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e("BTSendButton", "Bluetooth is not enabled or not supported.");
            activity.showToastMessage("Bluetooth is not enabled or not supported.");
            return;
        }

        Log.d("BTSendButton", "Searching for devices named 'PicoFram'...");
        searchForPicoDevice();
    }

    private void searchForPicoDevice() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if ("PicoFram".equals(device.getName())) {
                Log.d("BTSendButton", "Found PicoFram device: " + device.getAddress());
                picoDevice = device;
                connectToPicoDevice();
                return;
            }
        }

        Log.e("BTSendButton", "PicoFram device not found.");
        activity.showToastMessage("PicoFram device not found.");
    }

    private void connectToPicoDevice() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (picoDevice != null) {
            Log.d("BTSendButton", "Connecting to PicoFram device...");
            bluetoothGatt = picoDevice.connectGatt(activity, false, gattCallback);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("GATT", "Connected to GATT server.");
                Log.d("GATT", "Attempting to start service discovery: " + bluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("GATT", "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("GATT", "Services discovered.");
                BluetoothGattService picoService = bluetoothGatt.getService(PICO_SERVICE_UUID);
                if (picoService != null) {
                    Log.d("GATT", "Found PicoFram service.");
                    sendTransferData(picoService);
                } else {
                    Log.e("GATT", "PicoFram service not found.");
                }
            } else {
                Log.e("GATT", "Service discovery failed, status: " + status);
            }
        }

        private void sendTransferData(BluetoothGattService service) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("GATT", "Sending data to PicoFram...");

            // Send slot number
            BluetoothGattCharacteristic slotCharacteristic = service.getCharacteristic(SLOT_CHARACTERISTIC_UUID);
            if (slotCharacteristic != null) {
                Log.d("GATT", "Sending slot number: " + activity.getChosenSlot());
                slotCharacteristic.setValue(ByteBuffer.allocate(4).putInt(activity.getChosenSlot()).array());
                bluetoothGatt.writeCharacteristic(slotCharacteristic);
            }

            // Send time value
            BluetoothGattCharacteristic timeCharacteristic = service.getCharacteristic(TIME_CHARACTERISTIC_UUID);
            if (timeCharacteristic != null) {
                Log.d("GATT", "Sending time value: " + activity.getTime());
                timeCharacteristic.setValue(ByteBuffer.allocate(4).putFloat(activity.getTime()).array());
                bluetoothGatt.writeCharacteristic(timeCharacteristic);
            }

            // Send bitmap data
            BluetoothGattCharacteristic imageCharacteristic = service.getCharacteristic(IMAGE_CHARACTERISTIC_UUID);
            if (imageCharacteristic != null) {
                Log.d("GATT", "Sending bitmap image...");
                byte[] bitmapBytes = getBitmapBytes(activity.getImage());
                imageCharacteristic.setValue(bitmapBytes);
                bluetoothGatt.writeCharacteristic(imageCharacteristic);
            }
        }
    };

    private byte[] getBitmapBytes(Bitmap bitmap) {
        // Convert bitmap to byte array
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // Convert to byte array
        byte[] byteArray = new byte[pixels.length * 4];
        int index = 0;
        for (int pixel : pixels) {
            byteArray[index++] = (byte) (pixel >> 24);
            byteArray[index++] = (byte) (pixel >> 16);
            byteArray[index++] = (byte) (pixel >> 8);
            byteArray[index++] = (byte) pixel;
        }
        return byteArray;
    }
}
