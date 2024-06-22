package com.pixelframe.model.eventListeners;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.pixelframe.controller.ui.activity.ConvertImageActivity;
import com.pixelframe.controller.ui.activity.TransferActivity;

/**
 * In this class method onClick should send prepared bitmap to previously connected PicoFrame device
 */
public class SendButtonOnClickListener implements View.OnClickListener {
    final Context context;
    public SendButtonOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, TransferActivity.class);
        Bitmap convertedFragment = ((ConvertImageActivity) context).getConvertedFragment();
        if (convertedFragment == null) {
            Log.d("SendButtonOnClickListener", "ConvertedFragment == null");
            context.startActivity(new Intent(context, ConvertImageActivity.class));
        } else {
            Log.d("SendButtonOnClickListener",
                    "ConvertedFragment != null; imgW = " + convertedFragment.getWidth() +
                            ", imgH = " + convertedFragment.getHeight());
            intent.putExtra("convertedImage", convertedFragment);
            context.startActivity(intent);
        }
    }
}
