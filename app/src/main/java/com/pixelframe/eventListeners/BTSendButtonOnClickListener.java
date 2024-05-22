package com.pixelframe.eventListeners;

import static androidx.core.content.ContextCompat.startActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

import com.pixelframe.controller.ui.MainActivity;
import com.pixelframe.controller.ui.TransferActivity;

import android.media.ExifInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BTSendButtonOnClickListener implements View.OnClickListener {
    private Context context;
    private Bitmap image;
    private int slot;
    private float time;
    private File file;
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public BTSendButtonOnClickListener(Context context) {
        this.context = context;
        TransferActivity a = (TransferActivity)context;
        this.image = a.getImage();
        this.slot = a.getChosenSlot();
        this.time = a.getTime();
    }
    @Override
    public void onClick(View v) {
        if (!checkAndRequestPermissions()) {
            Toast.makeText(context, "Fix permissions and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        onPermissionsGranted();
    }

    public void onPermissionsGranted() {
        try {
            file = attachMetadata();
        } catch (IOException e) {
            Toast.makeText(context, "Failed to attach metadata and create file." +
                    " Clear cache and try again.", Toast.LENGTH_SHORT).show();
        }
        try {
            sendOverBT();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to send file to device. " +
                    "Check connection and health status and try again.", Toast.LENGTH_SHORT).show();
        }
        jumpToMainActivity();
    }

    private File attachMetadata() throws IOException {
        // Convert image JPEG
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        // Save image to cache
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, slot + ".pfb");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(byteArray);
        fos.close();
        // Add slot and time to meta
        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        exif.setAttribute("time", String.valueOf(time));
        exif.setAttribute("slot", String.valueOf(slot));
        exif.saveAttributes();
        return file;
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
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

    void sendOverBT() {
        //here goes the logic of sending over BT
    }

    void jumpToMainActivity() {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
