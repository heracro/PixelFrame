package com.pixelframe.model.eventListeners.eventListeners;

import android.view.ViewTreeObserver;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.pixelframe.controller.R;

public class LayoutDimensionsListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final ConstraintLayout constraintLayout;
    private final float firstBlockRatio;
    private final float imageWidthRatio;
    public LayoutDimensionsListener(ConstraintLayout constraintLayout, float firstBlockRatio, float imageWidthRatio) {
        this.constraintLayout = constraintLayout;
        this.firstBlockRatio = firstBlockRatio;
        this.imageWidthRatio = imageWidthRatio;
    }

    @Override
    public void onGlobalLayout() {
        constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int height = constraintLayout.getHeight();
        int width = constraintLayout.getWidth();
        updateConstraints(constraintLayout, width, height);
    }

    private void updateConstraints(ConstraintLayout constraintLayout, int viewWidth, int viewHeight) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        int picHeight = (int)(viewWidth * imageWidthRatio);
        int infoBlockHeight = (int)((viewHeight - picHeight) * firstBlockRatio);
        int buttonsBlockHeight = (int)((viewHeight - picHeight) * (1 - firstBlockRatio));
        constraintSet.constrainHeight(R.id.infoBlock, infoBlockHeight);
        constraintSet.constrainHeight(R.id.picBlock, picHeight);
        constraintSet.constrainHeight(R.id.buttonsBlock, buttonsBlockHeight);
        constraintSet.connect(R.id.infoBlock, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(R.id.picBlock, ConstraintSet.TOP, R.id.infoBlock, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.buttonsBlock, ConstraintSet.TOP, R.id.picBlock, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.buttonsBlock, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);
    }
}
