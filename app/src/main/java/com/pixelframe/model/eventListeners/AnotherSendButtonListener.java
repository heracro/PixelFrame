package com.pixelframe.model.eventListeners;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AnotherSendButtonListener implements View.OnClickListener {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final CountDownLatch discoveryLatch = new CountDownLatch(1);
    private final TransferActivity activity;
    private BluetoothSocket btSocket;
    //UUID for standard RFCOMM device with SPP service (serial port profile)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> picoDevices = new ArrayList<>();

    public AnotherSendButtonListener(TransferActivity activity) {
        Log.d("BTSendButton", "Creating Send Button Listener");
        this.activity = activity;
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            throw new UnsupportedOperationException("Bluetooth is not supported on this device");
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("BTSendButton", "onClick(): Transfer button clicked!");
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e("BTSendButton", "Bluetooth initialization failed");
            activity.showToastMessage("Bluetooth is not enabled or not supported.");
            return;
        }
        executorService.execute(() -> {
            Log.d("BTSendButton", "executor (outer) lambda entered");
            searchForPicoDevices();
            try {
                if (!discoveryLatch.await(10, TimeUnit.SECONDS)) {
                    Log.e("BTSendButton", "Bluetooth discovery timed out");
                }
            } catch (InterruptedException ignored) {}
            activity.runOnUiThread(() -> {
                Log.d("BTSendButton", "Thread runner (inner) lambda entered");
                if (picoDevices.size() != 1) {
                    Log.e("BTSendButton", "Found " + picoDevices.size() + " devices named 'PicoFram'." +
                            " Ensure exactly one is available.");
                    activity.showToastMessage("Found " + picoDevices.size() + " devices named 'PicoFram'." +
                            " Ensure exactly one is available.");
                    return;
                }
                Log.d("BTSendButton", "Found 1 PicoFram. Excellent. Trying to connect...");
                if (!connectToDevice(picoDevices.get(0))) {
                    Log.e("BTSendButton", "Failed to connect to the PicoFram device.");
                    activity.showToastMessage("Failed to connect to the PicoFram device.");
                    return;
                }
                Log.d("BTSendButton", "Connected! Trying to transfer data");
                try {
                    transferData();
                    Log.d("BTSendButton", "Looks like transferred :)");
                } catch (IOException ignored) {
                    Log.e("BTSendButton", "Transferring data to Pico failed");
                } finally {
                    closeConnection();
                }
                Log.d("BTSendButton", "END: Thread runner (inner) lambda");
            });
            Log.d("BTSendButton", "END: executor (outer) lambda");
        });
    }

    private void searchForPicoDevices() {
        Log.d("BTSendButton", "searchForPicoDevices()");
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BTSendButton","Permission not granted, skipping search for BT devices.");
            activity.runOnUiThread(
                    () -> activity.showToastMessage("Permission not granted for Bluetooth.")
            );
            return;
        }
        Log.d("BTSendButton", "Permissions granted. Check if we have Pico connected...");
        if (!picoDevices.isEmpty() && picoDevices.get(0).getName().equals("PicoFram")) {
            Log.d("BTSendButton", "Only one Pico connected. " +
                    "Returning from searchForPicoDevices()");
            return;
        }
        Log.d("BTSendButton", "Either no BT device connected or it's not PicoFram. " +
                "Clear list and re-try...");
        picoDevices.clear();
        activity.runOnUiThread(
                () -> activity.showToastMessage("Searching for devices named 'PicoFram'...")
        );
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(bluetoothReceiver, filter);
        bluetoothAdapter.startDiscovery();
        Log.d("BTSendButton", "searchForPicoDevices: Found " + picoDevices.size() +
                " devices. Discovery started.");
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("bluetoothReceiver", "bluetoothReceiver.onReceive() called");
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BTSendButton","onReceive: Permission not granted or no device found," +
                        " skipping search for BT devices.");
                return;
            }
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) return;
                Log.d("BTSendButton", "onReceive: Permission granted. Device found: " +
                        device.getName());
                if ("PicoFram".equals(device.getName())) {
                    Log.d("BTSendButton","onReceive: PicoFram found, adding...");
                    picoDevices.add(device);
                    bluetoothAdapter.cancelDiscovery();
                    try {
                        Thread.sleep(500); // because pico might be still in discovery?
                    } catch (InterruptedException ignored) {}
                    Log.d("BTSendButton","onReceive: PicoFram (" + picoDevices.size() +
                            ") found, added: " + device);
                }
            }
        }
    };

    private boolean connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Log.d("BTSendButton", "connectToDevice(" + device.getName() + ")");
        try {
            btSocket = (BluetoothSocket) device
                    .getClass()
                    .getMethod("createRfcommSocket", int.class)
                    .invoke(device, 1);
            bluetoothAdapter.cancelDiscovery();
            btSocket.connect();
            Log.d("BTSendButton","Connecting to BT device SUCCESSFUL");
            return true;
        } catch (IOException | InvocationTargetException
                 | NoSuchMethodException | IllegalAccessException e) {
            Log.e("BTSendButton","Connecting to BT device failed");
            return false;
        }
    }

    private void transferData() throws IOException {
        Log.d("BTSendButton", "transferData()");
        if (btSocket == null || !btSocket.isConnected()) {
            throw new IOException("Bluetooth socket is not connected");
        }
        OutputStream outputStream = btSocket.getOutputStream();
        sendInt(outputStream, activity.getChosenSlot());
        sendFloat(outputStream, activity.getTime());
        sendBitmap(outputStream, activity.getImage());
        Log.d("BTSendButton", "Data transferred successfully");
    }

    private void sendInt(OutputStream outputStream, int value) throws IOException {
        Log.d("BTSendButton", "sendInt(..., " + value + ")");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        outputStream.write(buffer.array());
        Log.d("BTSendButton", "Sent int: " + value);
    }

    private void sendFloat(OutputStream outputStream, float value) throws IOException {
        Log.d("BTSendButton", "sendFloat(..., " + value + ")");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        outputStream.write(buffer.array());
        Log.d("BTSendButton", "Sent float: " + value);
    }

    private void sendBitmap(OutputStream outputStream, Bitmap bitmap) throws IOException {
        Log.d("BTSendButton", "sendBitmap(..., ...)");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
        for (int pixel : pixels) {
            buffer.putInt(pixel);
        }
        outputStream.write(buffer.array());
        Log.e("BTSendButton", "Sent bitmap with width: " + width + " and height: " + height);
    }

    private void closeConnection() {
        Log.d("BTSendButton", "closeConnection()");
        if (btSocket != null) {
            try {
                btSocket.close();
                Log.d("BTSendButton", "Bluetooth connection closed");
            } catch (IOException e) {
                Log.e("BTSendButton","Failed to close connection to BT device");
            }
        }
    }

}
