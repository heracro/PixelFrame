package com.pixelframe.model.eventListeners;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.pixelframe.controller.ui.MainActivity;
import com.pixelframe.controller.ui.TransferActivity;
import com.pixelframe.model.configuration.Configuration;

import android.support.media.ExifInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BTSendButtonOnClickListener implements View.OnClickListener {
    private final Context context;
    private final Bitmap image;
    private final int slot;
    private final float time;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice raspberryPiDevice;
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public BTSendButtonOnClickListener(Context context) {
        Log.i("BTSendButtonListener", "Constructor: BTSendButtonOnClickListener()");
        this.context = context;
        TransferActivity a = (TransferActivity) context;
        this.image = a.getImage();
        this.slot = a.getChosenSlot();
        this.time = a.getTime();

    }

    @Override
    public void onClick(View v) {
        Log.i("BTSendButtonListener", "onClick()");
        initializeBluetooth();
    }

    public void initializeBluetooth() {
        Log.i("BTSendButtonListener", "initializeBluetooth()");
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d("BTSendButtonListener", "(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) = True");
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                return;
            }
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("BTSendButtonListener", "(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) = False; Trying to find and connect to device");
            findAndConnectToDevice();
        }
    }

    private File attachMetadata() throws IOException {
        Log.i("BTSendButtonListener", "attachMetadata()");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, slot + ".pfb");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(byteArray);
        fos.close();

        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        exif.setAttribute("time", String.valueOf(time));
        exif.setAttribute("slot", String.valueOf(slot));
        exif.saveAttributes();
        return file;
    }

    private boolean checkAndRequestPermissions() {
        Log.i("BTSendButtonListener", "checkAndRequestPermissions()");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return false;
        }
    }

    private void findAndConnectToDevice() {
        Log.i("BTSendButtonListener", "findAndConnectToDevice()");
        if (bluetoothAdapter == null) {
            // Można dodać Toast lub log, aby poinformować użytkownika lub developerów o błędzie
            Log.e("BTSendButtonListener", "BluetoothAdapter is null. Bluetooth may not be supported on this device.");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth jest wyłączony, obsłuż to odpowiednio
            Log.e("BTSendButtonListener", "Bluetooth is not enabled.");
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BTSendButtonListener", "BLUETOOTH_CONNECT is not granted.");
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                Log.i("BTSendButtonListener", "Paired BT Device found: " + device.getName());
                if (device.getName().equals(Configuration.PICO_FRAME_NAME)) {
                    raspberryPiDevice = device;
                    break;
                }
            }
        } else {
            Log.e("BTSendButtonListener", "No paired devices found");
            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show();
        }
        if (raspberryPiDevice == null) {
            Log.e("BTSendButtonListener", Configuration.PICO_FRAME_NAME + " device not found");
            Toast.makeText(context, Configuration.PICO_FRAME_NAME + "e device not found", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("BTSendButtonListener", Configuration.PICO_FRAME_NAME + " found. Connecting...");
            connectToDevice(raspberryPiDevice);
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        Log.i("BTSendButtonListener", "connectToDevice()");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
            socket.connect();
            Log.i("BTSendButtonListener", "Connected to device");
            Toast.makeText(context, "Connected to device", Toast.LENGTH_SHORT).show();
            sendFile(socket);
        } catch (IOException e) {
            Log.d("BTSendButtonListener", e.getMessage());
            Toast.makeText(context, "Failed to connect to device", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendFile(BluetoothSocket socket) {
        Log.i("BTSendButtonListener", "sendFile()");
        File file;
        try {
             file = attachMetadata();
        } catch (IOException e) {
            Toast.makeText(context, "Failed to attach metadata and create file. Clear cache and try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(fileBytes);
            outputStream.flush();
            outputStream.close();

            socket.close();
            Toast.makeText(context, "File sent successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d("BTSendButtonListener", Objects.requireNonNull(e.getMessage()));
        }
    }

    void jumpToMainActivity() {
        Log.i("BTSendButtonListener", "jumpToMainActivity()");
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
