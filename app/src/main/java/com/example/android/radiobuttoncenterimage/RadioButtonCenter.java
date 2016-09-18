// Copyright MyScript. All rights reserved.

package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

public class RadioButtonCenter extends RadioButton {
    Drawable drawable;
    private int availableWidth;
    private int availableHeight;
    private int contentWidth;
    private int contentHeight;
    private int availableOnMeasureWidth;
    private int availableOnMeasureHeight;
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int paddingLeft;


    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonCenter, 0, 0);
        drawable = a.getDrawable(R.styleable.RadioButtonCenter_drawable);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.AT_MOST));
//        availableWidth = getWidth();
//        availableHeight = getHeight();

        availableWidth = getMeasuredWidth();
        availableHeight = getMeasuredHeight();
        paddingTop=getPaddingTop();
        paddingBottom=getPaddingBottom();
        paddingRight=getPaddingRight();
        paddingLeft=getPaddingLeft();
        contentWidth = drawable.getIntrinsicWidth();
        contentHeight = drawable.getIntrinsicHeight();
        //drawable.setTint(getContext().getResources().getColor(R.color.colorPrimaryDark));

        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "paddingTop "
                + MainActivity.convertPixelsToDp(paddingTop, getContext())
                + " paddingBottom "
                + MainActivity.convertPixelsToDp(paddingBottom, getContext()));
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "paddingRight "
                + MainActivity.convertPixelsToDp(paddingRight, getContext())
                + " paddingLeft "
                + MainActivity.convertPixelsToDp(paddingLeft, getContext()));

        setMeasuredDimension(availableWidth, availableHeight);
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "availableWidth "
                + MainActivity.convertPixelsToDp(availableWidth, getContext())
                + "availableHeight "
                + MainActivity.convertPixelsToDp(availableHeight, getContext()));

        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "contentWidth "
                + MainActivity.convertPixelsToDp(contentWidth, getContext())
                + "contentHeight "
                + MainActivity.convertPixelsToDp(contentHeight, getContext()));

        setMeasuredDimension(availableWidth, availableHeight);

//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY));
//        setMeasuredDimension(contentWidth, contentHeight);

//        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
//                + "availableOnMeasureWidth "
//                + MainActivity.convertPixelsToDp(availableOnMeasureWidth, getContext())
//                + "availableOnMeasureHeight "
//                + MainActivity.convertPixelsToDp(availableOnMeasureHeight, getContext()));
//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec(availableOnMeasureWidth, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(availableOnMeasureHeight, MeasureSpec.EXACTLY));
//        setMeasuredDimension(availableOnMeasureWidth, availableOnMeasureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = (availableWidth - contentWidth) / 2;
        int top = (availableHeight - contentHeight) / 2;
        int right = left + contentWidth;
        int bottom = top + contentHeight;
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);

        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "availableWidth " + MainActivity.convertPixelsToDp(availableWidth, getContext())
                + " availableHeight " + MainActivity.convertPixelsToDp(availableHeight, getContext()));

        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "contentWidth " + MainActivity.convertPixelsToDp(contentWidth, getContext())
                + " contentHeight " + MainActivity.convertPixelsToDp(contentHeight, getContext()));
    }


}