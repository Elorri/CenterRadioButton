package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Elorri on 18/09/2016.
 */
public class BasicCustomView extends View {

    private final Drawable drawable;
    private int imageWidth;
    private int imageHeight;
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int paddingLeft;

    public BasicCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasicCustomView, 0, 0);
        //Our drawable is 24dp x 24dp see it's size tag in drawable folder
        drawable = a.getDrawable(R.styleable.BasicCustomView_drawable2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawable == null) return;
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "");
        //Get the size of the image we want to draw
        imageWidth = drawable.getIntrinsicWidth();
        imageHeight = drawable.getIntrinsicHeight();

        //Get the padding if the user has set one
        paddingTop=getPaddingTop();
        paddingBottom=getPaddingBottom();
        paddingRight=getPaddingRight();
        paddingLeft=getPaddingLeft();

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

        //We set the size of the canvas we plan to draw on.
        // Here we set the exact size of the drawable plus the eventual padding
        setMeasuredDimension(imageWidth+paddingRight+paddingLeft, imageHeight+paddingTop+paddingBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable == null) return;
        //Using onMeasure default super method would give an onDraw canvas with strange?? size
        // getHeight 275.0dp getWidth 328.0dp

        //Using custom onMeasure (setMeasuredDimension(imageWidth, imageHeight)) will gives
        // getHeight 24.0dp getWidth 24.0dp
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "getHeight " + MainActivity.convertPixelsToDp(canvas.getHeight(), getContext())
                + " getWidth " + MainActivity.convertPixelsToDp(canvas.getWidth(), getContext()));

        //We set the canvas to be the exact size of our drawble. Our drawable can now draw itself
        // on it and be completely visible.
        int left = paddingLeft;
        int top = paddingTop;
        int right = paddingLeft+imageWidth;
        int bottom = paddingTop+imageHeight;
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }
}
