// Copyright MyScript. All rights reserved.

package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

public class RadioButtonCenter extends RadioButton {
    Drawable buttonDrawable;
    private Bitmap bitmap;

    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonCenter, 0, 0);
        buttonDrawable = a.getDrawable(R.styleable.RadioButtonCenter_drawable);

        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_pen);

    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (buttonDrawable != null) {
//            Log.e("Nebo", Thread.currentThread().getStackTrace()[2]+"getDrawableState() " +
//                    ""+getDrawableState());
//            buttonDrawable.setState(getDrawableState());
//            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
//            final int height = buttonDrawable.getIntrinsicHeight();
//
//            int y = 0;
//
//            switch (verticalGravity) {
//                case Gravity.BOTTOM:
//                    y = getHeight() - height;
//                    break;
//                case Gravity.CENTER_VERTICAL:
//                    y = (getHeight() - height) / 2;
//                    break;
//            }
//
//            int buttonWidth = buttonDrawable.getIntrinsicWidth();
//            int buttonLeft = (getWidth() - buttonWidth) / 2;
//            buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
//            buttonDrawable.draw(canvas);


//        if(isChecked()){

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
//            Drawable drawable=getResources().getDrawable(R.drawable.ic_pen);
//            drawable.draw(canvas);

        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "bitmap" + bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        //      }else{


        //    }
    }
}
