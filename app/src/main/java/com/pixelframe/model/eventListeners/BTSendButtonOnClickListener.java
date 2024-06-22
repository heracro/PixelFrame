package com.pixelframe.model.eventListeners;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import com.pixelframe.controller.ui.activity.TransferActivity;
import com.pixelframe.model.configuration.Configuration;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.polidea.rxandroidble2.scan.ScanResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BTSendButtonOnClickListener implements View.OnClickListener {
    private final Context context;
    private final Bitmap image;
    private final int slot;
    private final float time;
    private final RxBleClient rxBleClient;
    private Disposable scanSubscription;
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public BTSendButtonOnClickListener(Context context) {
        Log.i("BTSendButtonListener", "Constructor: BTSendButtonOnClickListener()");
        this.context = context;
        TransferActivity a = (TransferActivity) context;
        this.image = a.getImage();
        this.slot = a.getChosenSlot();
        this.time = a.getTime();
        this.rxBleClient = RxBleClient.create(context);
    }

    @Override
    public void onClick(View v) {
        Log.i("BTSendButtonListener", "onClick()");
        if (checkAndRequestPermissions()) {
            initializeBluetooth();
        }
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

    public void initializeBluetooth() {
        Log.i("BTSendButtonListener", "initializeBluetooth()");
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d("BTSendButtonListener", "(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) = True");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("BTSendButtonListener", "(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) = False; Trying to find and connect to device");
            startScan();
        }
    }

    private void startScan() {
        Log.i("BTSendButtonListener", "startScan()");
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("12345678-1234-5678-1234-56789abcdef0"))
                .build();
        scanSubscription = rxBleClient.scanBleDevices(scanSettings, scanFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onScanResult,
                        throwable -> Log.e("BTSendButtonListener", "Scan failed", throwable)
                );
    }

    private void onScanResult(ScanResult scanResult) {
        RxBleDevice device = scanResult.getBleDevice();
        if (device.getName() != null && device.getName().equals(Configuration.PICO_FRAME_NAME)) {
            scanSubscription.dispose();
            connectToDevice(device);
        }
    }

    @SuppressLint("CheckResult")
    private void connectToDevice(RxBleDevice device) {
        Log.i("BTSendButtonListener", "connectToDevice()");
        device.establishConnection(false)
                .flatMapSingle(RxBleConnection::discoverServices)
                .flatMapSingle(services -> services.getCharacteristic(UUID.fromString("12345678-1234-5678-1234-56789abcdef1")))
                .subscribe(
                        characteristic -> sendFile(device, characteristic),
                        throwable -> Log.e("BTSendButtonListener", "Connection failed", throwable)
                );
    }

    @SuppressLint("CheckResult")
    private void sendFile(RxBleDevice device, BluetoothGattCharacteristic characteristic) {
        Log.i("BTSendButtonListener", "sendFile()");
        File file;
        try {
            file = createBitmapFile(image, slot, time);
        } catch (IOException e) {
            Log.e("BTSendButtonListener", "Failed to create bitmap file", e);
            return;
        }
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            device.establishConnection(false)
                    .flatMapSingle(connection -> connection.writeCharacteristic(characteristic, fileBytes))
                    .subscribe(
                            bytes -> Log.i("BTSendButtonListener", "File sent successfully"),
                            throwable -> Log.e("BTSendButtonListener", "File sending failed", throwable)
                    );
        } catch (IOException e) {
            Log.d("BTSendButtonListener", Objects.requireNonNull(e.getMessage()));
        }
    }

    private File createBitmapFile(Bitmap bitmap, int slot, float time) throws IOException {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, slot + ".pfb");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(buffer);
            fos.write(buffer.array());
        }
        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        exif.setAttribute("UserComment", "time=" + time + ",slot=" + slot);
        exif.saveAttributes();
        return file;
    }
}
