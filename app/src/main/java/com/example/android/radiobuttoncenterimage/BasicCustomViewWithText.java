package com.example.android.radiobuttoncentershape;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.android.radiobuttoncenterimage.MainActivity;
import com.example.android.radiobuttoncenterimage.R;

/**
 * Created by Elorri on 18/09/2016.
 */
public class BasicCustomViewWithText extends View {

    private final Drawable drawable;

    //shape related (the shape paint is taken from xml drawable)
    private int shapeWidth;
    private int shapeHeight;

    //space between text and shape
    private int space;

    //text related (the text paint can't be created in xml drawable)
    private int textWidth;
    private int textHeight;
    //text paint creation related
    private final Paint textPaint=new Paint();
    private final String text="Something to say";
    private final Rect textBounds;

    //possible padding added on the view
    private int paddingTop;
    private int paddingBottom;
    private int paddingRight;
    private int paddingLeft;

    //The final image we will compose using the shape drawable plus the text created dynamically
    private int finalImageWidth;
    private int finalImageHeight;

    public BasicCustomViewWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasicCustomView, 0, 0);
        //Our drawable is 24dp x 24dp see it's size tag in drawable folder
        drawable = a.getDrawable(R.styleable.BasicCustomView_drawable2);

        //We init the space
        space= (int) getResources().getDimension(R.dimen.radiobutton_text_margin_top);

        //We create the text paint
        textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawable == null) return;
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "");
        //Get the size of the shape we want to draw
        shapeWidth = drawable.getIntrinsicWidth();
        shapeHeight = drawable.getIntrinsicHeight();

        //Get the size of the text we want to draw below
        textHeight = textBounds.height();
        textWidth = textBounds.width();

        //Get the padding if the user has set one
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();
        paddingLeft = getPaddingLeft();

        //We set the size of the canvas we plan to draw on.
        // Here we calculate he size of the final image
        finalImageWidth=Math.max(shapeWidth, textWidth);
        finalImageHeight=shapeHeight+space+textHeight;

        //and we add padding if there is some
        int canvasWidth= finalImageWidth + paddingRight + paddingLeft;
        int canvasHeight= finalImageHeight + paddingTop + paddingBottom;
        setMeasuredDimension(canvasWidth, canvasHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable == null) return;
        //Using onMeasure default super method would give an onDraw canvas with strange?? size
        // getHeight 275.0dp getWidth 328.0dp

        //Using custom onMeasure (setMeasuredDimension(shapeWidth, shapeHeight)) will gives
        // getHeight 24.0dp getWidth 24.0dp
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                + "getHeight " + MainActivity.convertPixelsToDp(canvas.getHeight(), getContext())
                + " getWidth " + MainActivity.convertPixelsToDp(canvas.getWidth(), getContext()));

        //We set the canvas to be the exact size of our drawable + text.
        // Our drawable can now draw itself on it and be completely visible.
        int left = paddingLeft;
        int top = paddingTop;
        int right = paddingLeft + shapeWidth;
        int bottom = paddingTop + shapeHeight;
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);

        //We draw the text below and we want it to be centered horizontally.
        int centerX=finalImageWidth/2;
        int textStart=centerX-textWidth/2; //if there was no left padding
        int textTop=finalImageHeight-textHeight;
        canvas.drawText(text, textStart+paddingLeft, textTop, textPaint);

    }
}