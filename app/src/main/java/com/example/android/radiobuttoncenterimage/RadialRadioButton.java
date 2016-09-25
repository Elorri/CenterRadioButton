package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RadioButton;

/**
 * Created by Elorri on 11/09/2016.
 */
public class RadialRadioButton extends RadioButton
{
    //Attributes user can customise
    //The radius of the point that will be drawn in the center of the view
    private float radiusPoint;

    //The color of the point
    private int colorPoint;

    //The radius of the circle that will be drawn around the point when the radio button will be selected
    private float radiusSelector;

    //The color of the selector
    private int colorSelector;

    //The text of the radiobutton if there is some
    private String text;
    private float textSize;

    //If the button can be checked or not. Useful when the button is not in a radiogroup and is used like a button.
    private boolean checkable;

    //Use for calculations and drawing
    //Values for attributes not in px
    private Typeface defaultNormalTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private int defaultTextColor;

    //Values for attributes in px
    private float radiusPointInPx;
    private float radiusSelectorInPx;
    private float textMarginTopInPx;
    private float shapeMinPaddingInPx;
    private float textSizePx;
    private float strokeWidthInPx;

    //Size requested by the xml layout layout_width and layout_height attributes
    private float measureHeightInPx;
    private float measuredWidthInPx;

    //circle related
    private float centerShapeX; //coordinates of the center of the circles (point and selector)
    private float centerYcircles; //coordinates of the center of the circles (point and selector)
    private float minDiameterCirclesInPx;

    //text related
    private float textHeight;
    private float textWidth;

    //size of the complete drawing with our without text
    private float minShapeHeight;
    private float minShapeWidth;

    //Paints
    Paint pointPaint = new Paint();
    Paint selectorPaint = new Paint();
    Paint textPaint = new Paint();

    public RadialRadioButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadialRadioButton, 0, 0);
        try
        {
            Resources resources = getContext().getResources();

            //Set Attributes from xml values or from default
            //Attributes that don't needs conversion in px
            defaultTextColor = resources.getColor(R.color.default_pen_color);
            text = a.getString(R.styleable.RadialRadioButton_text);

            int defaultPointColor = resources.getColor(R.color.default_pen_color);
            colorPoint = a.getInteger(R.styleable.RadialRadioButton_colorPoint, defaultPointColor);

            boolean defaultCheckableValue = resources.getBoolean(R.bool.radiobutton_default_checkable_value);
            checkable=a.getBoolean(R.styleable.RadialRadioButton_checkable, defaultCheckableValue);

            //Attributes that needs conversion in px
            float defaultMinimumRadius = resources.getDimension(R.dimen.radiobutton_default_minimum_radius);
            radiusPoint = a.getDimension(R.styleable.RadialRadioButton_radiusPoint, defaultMinimumRadius);
            radiusPointInPx = (int) convertFromDpToPx(radiusPoint);

            float defaultMinimumRadiusSelector = resources.getDimension(R.dimen.radiobutton_default_minimum_radius_selector);
            radiusSelector = a.getDimension(R.styleable.RadialRadioButton_radiusSelector, defaultMinimumRadiusSelector);
            radiusSelectorInPx = (int) convertFromDpToPx(radiusSelector);

            int defaultSelectorColor = resources.getColor(R.color.default_pen_color);
            colorSelector = a.getInteger(R.styleable.RadialRadioButton_colorSelector, defaultSelectorColor);

            float defaultTextSize = resources.getDimension(R.dimen.radiobutton_default_minimum_textsize);
            textSizePx = a.getDimension(R.styleable.RadialRadioButton_buttonTextSize,
                    defaultTextSize);
            //textSizePx=(int) convertFromSpToPx(textSize);

            float defaultTextMarginTop = resources.getDimension(R.dimen.radiobutton_text_margin_top);
            textMarginTopInPx =(int) convertFromSpToPx(defaultTextMarginTop);

            float defaultShapeMinPadding = resources.getDimension(R.dimen.radiobutton_shape_min_padding);
            shapeMinPaddingInPx =(int) convertFromSpToPx(defaultShapeMinPadding);

            float defaultStrokeWidth = resources.getDimension(R.dimen.radiobutton_default_stroke_width);
            strokeWidthInPx=(int) convertFromSpToPx(defaultStrokeWidth);
        }
        finally
        {
            a.recycle();
        }
    }

    public float getRadiusPoint()
    {
        return radiusPoint;
    }

    public void setRadiusPoint(float radiusPoint)
    {
        this.radiusPoint = radiusPoint;
        radiusPointInPx = convertFromDpToPx(radiusPoint);
        invalidate();
        requestLayout();
    }

    public float getRadiusSelector()
    {
        return radiusSelector;
    }

    public void setRadiusSelector(float radiusSelector)
    {
        this.radiusSelector = radiusSelector;
        radiusSelectorInPx = convertFromDpToPx(radiusSelector);
        invalidate();
        requestLayout();
    }

    public int getColorPoint()
    {
        return colorPoint;
    }

    public void setColorPoint(int colorPoint)
    {
        this.colorPoint = colorPoint;
        invalidate();
        requestLayout();
    }

    public int getColorSelector()
    {
        return colorSelector;
    }

    public void setColorSelector(int colorSelector)
    {
        this.colorSelector = colorSelector;
        invalidate();
        requestLayout();
    }

    @Override
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
        invalidate();
        requestLayout();
    }

    public float getTextSize()
    {
        return textSize;
    }

    public void setTextSize(float textSize)
    {
        this.textSize = textSize;
        textSizePx = (int) convertFromSpToPx(textSize);
        invalidate();
        requestLayout();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Get the sizes of the available space or the equivalent pixel of the xml layout_width and layout _height
        measuredWidthInPx = getMeasuredWidth();
        measureHeightInPx = getMeasuredHeight();

        minDiameterCirclesInPx = Math.max(radiusPointInPx * 2, radiusSelectorInPx * 2);
        if (text != null)
        {
            setTextPaint();
            Rect textBounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), textBounds);
            textHeight = textBounds.height();
            textWidth = textBounds.width();
            minShapeWidth = Math.max(minDiameterCirclesInPx, textWidth);
            minShapeHeight = minDiameterCirclesInPx + textHeight + textMarginTopInPx;
        }
        else
        {
            minShapeWidth = minDiameterCirclesInPx;
            minShapeHeight = minDiameterCirclesInPx;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
        {
            measureHeightInPx = minShapeHeight + shapeMinPaddingInPx;
        }
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)
        {
            measuredWidthInPx = minShapeWidth + shapeMinPaddingInPx;
        }

        if (text != null)
        {
            centerShapeX = Math.max(minShapeWidth / 2, measuredWidthInPx / 2);
            centerYcircles = (minDiameterCirclesInPx / 2) + ((measureHeightInPx - minShapeHeight) / 2);
        }
        else
        {
            centerShapeX = Math.max(minDiameterCirclesInPx / 2, measuredWidthInPx / 2);
            centerYcircles = (minDiameterCirclesInPx / 2) + ((measureHeightInPx - minShapeHeight) / 2);
        }
        setMeasuredDimension(Math.round(measuredWidthInPx), Math.round(measureHeightInPx));
    }



    @Override
    protected void onDraw(Canvas canvas)
    {
        //canvas default size is 64px x 64px. To make sure our canvas is big enough, we can't use canvas.getWidth() and canvas.getHeight() method.
        //We try waiting for onSizeChanged to be called to get the size of the canvas that is able to fit the view, but We didn't get better result.
        //We will rely only on the onMeasureMethod

        if (isChecked())
        {
            drawPoint(canvas);
            drawSelector(canvas);
            drawText(canvas);
        }
        else
        {
            drawPoint(canvas);
            drawText(canvas);
        }
    }

    private void setTextPaint()
    {
        int colorText = defaultTextColor;
        textPaint.setColor(colorText);
        textPaint.setTypeface(defaultNormalTypeface);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSizePx);
    }


    private void drawPoint(Canvas canvas)
    {
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(colorPoint);
        canvas.drawCircle(centerShapeX, centerYcircles, radiusPointInPx, pointPaint);
    }

    private void drawSelector(Canvas canvas)
    {
        if(checkable)
        {
            selectorPaint.setAntiAlias(true);
            selectorPaint.setStyle(Paint.Style.STROKE);
            selectorPaint.setStrokeWidth(strokeWidthInPx);
            selectorPaint.setColor(colorSelector);
            canvas.drawCircle(centerShapeX, centerYcircles, radiusSelectorInPx, selectorPaint);
        }
    }

    private void drawText(Canvas canvas)
    {
        if (text != null)
        {
            float centerYText = minDiameterCirclesInPx + textHeight
                    + ((measureHeightInPx - minShapeHeight) / 2) + textMarginTopInPx;
            canvas.drawText(text, centerShapeX - textWidth / 2, centerYText, textPaint);
        }
    }

    private float convertFromDpToPx(float dp)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private float convertFromSpToPx(float sp)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }

}