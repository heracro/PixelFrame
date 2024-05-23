package com.pixelframe.model.eventListeners.eventListeners;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pixelframe.controller.ui.ConvertImageActivity;
import com.pixelframe.controller.ui.TransferActivity;

/**
 * In this class method onClick should send prepared bitmap to previously connected PicoFrame device
 */
public class SendButtonOnClickListener implements View.OnClickListener {
    Context context;
    public SendButtonOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra("convertedImage", ((ConvertImageActivity) context).getConvertedFragment());
        context.startActivity(intent);;
    }
}
