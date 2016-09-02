// Copyright MyScript. All rights reserved.

package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

public class RadioButtonCenter extends RadioButton
{
  Drawable buttonDrawable;

  public RadioButtonCenter(Context context, AttributeSet attrs) {
    super(context, attrs);
    Resources resources=getResources();
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonCenter, 0, 0);
    buttonDrawable = resources.getDrawable(a.getInteger(R.styleable.RadioButtonCenter_drawable, 0));
    setButtonDrawable(android.R.color.transparent);
  }



  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (buttonDrawable != null) {
      buttonDrawable.setState(getDrawableState());
      final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
      final int height = buttonDrawable.getIntrinsicHeight();

      int y = 0;

      switch (verticalGravity) {
        case Gravity.BOTTOM:
          y = getHeight() - height;
          break;
        case Gravity.CENTER_VERTICAL:
          y = (getHeight() - height) / 2;
          break;
      }

      int buttonWidth = buttonDrawable.getIntrinsicWidth();
      int buttonLeft = (getWidth() - buttonWidth) / 2;
      buttonDrawable.setBounds(buttonLeft, y, buttonLeft+buttonWidth, y + height);
      buttonDrawable.draw(canvas);
    }
  }
}
