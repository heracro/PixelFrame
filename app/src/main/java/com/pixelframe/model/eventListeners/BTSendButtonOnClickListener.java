package com.pixelframe.model.eventListeners;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.pixelframe.controller.ui.activity.TransferActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BTSendButtonOnClickListener implements View.OnClickListener {

    private final TransferActivity activity;
    private BluetoothSocket btSocket;
    //UUID for standard RFCOMM device with SPP service (serial port profile)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> picoDevices = new ArrayList<>();

    public BTSendButtonOnClickListener(TransferActivity activity) {
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onClick(View v) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e("BTSendButton", "Bluetooth initialization failed");
            activity.showToastMessage("Bluetooth is not enabled or not supported.");
            return;
        }
        searchForPicoDevices();
        if (picoDevices.size() != 1) {
            activity.showToastMessage("Found " + picoDevices.size() + " devices named 'PicoFram'." +
                    " Ensure exactly one is available.");
            return;
        }
        if (!connectToDevice(picoDevices.get(0))) {
            Log.e("BTSendButton", "Bluetooth connection failed");
            activity.showToastMessage("Failed to connect to the PicoFram device.");
            return;
        }
        try {
            transferData();
        } catch (IOException ignored) {
            Log.e("BTSendButton","Transferring data to Pico failed");
        } finally {
            closeConnection();
        }
    }

    private void searchForPicoDevices() {
        picoDevices.clear();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BTSendButton","Permission not granted, skipping search for BT devices.");
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if ("PicoFram".equals(device.getName())) {
                picoDevices.add(device);
            }
        }
        if (picoDevices.isEmpty()) {
            activity.showToastMessage("Searching for devices named 'PicoFram'...");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(bluetoothReceiver, filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED || device == null) {
                    Log.e("BTSendButton","Permission not granted or no device found," +
                            " skipping search for BT devices.");
                    return;
                }
                if ("PicoFram".equals(device.getName())) {
                    picoDevices.add(device);
                }
            }
        }
    };

    private boolean connectToDevice(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            return true;
        } catch (IOException e) {
            Log.e("BTSendButton","Connecting to BT device failed");
            return false;
        }
    }

    private void transferData() throws IOException {
        if (btSocket == null || !btSocket.isConnected()) {
            throw new IOException("Bluetooth socket is not connected");
        }
        OutputStream outputStream = btSocket.getOutputStream();
        int slot = activity.getChosenSlot();
        float time = activity.getTime();
        Bitmap bitmap = activity.getImage();
        sendInt(outputStream, slot);
        sendFloat(outputStream, time);
        sendBitmap(outputStream, bitmap);
    }

    private void sendInt(OutputStream outputStream, int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        outputStream.write(buffer.array());
    }

    private void sendFloat(OutputStream outputStream, float value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        outputStream.write(buffer.array());
    }

    private void sendBitmap(OutputStream outputStream, Bitmap bitmap) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
        for (int pixel : pixels) {
            buffer.putInt(pixel);
        }
        outputStream.write(buffer.array());
    }

    private void closeConnection() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e("BTSendButton","Failed to close connection to BT device");
            }
        }
    }
    
}
