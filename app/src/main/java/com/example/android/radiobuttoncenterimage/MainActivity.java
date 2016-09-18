package com.example.android.radiobuttoncenterimage;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private View view;
    private int viewWidth;
    private int viewHeight;
    private int viewMeasuredWidth;
    private int viewMeasuredHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.basic);
        view.post(new Runnable() {
            @Override
            public void run() {
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();
                viewMeasuredWidth = view.getMeasuredWidth();
                viewMeasuredHeight = view.getMeasuredHeight();

                Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                        + " viewWidth " + convertPixelsToDp(viewWidth, MainActivity.this)
                        + " - viewHeight " + convertPixelsToDp(viewHeight, MainActivity.this));
                Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                        + " viewMeasuredWidth " + convertPixelsToDp(viewMeasuredWidth, MainActivity.this)
                        + " - viewMeasuredHeight " + convertPixelsToDp(viewMeasuredHeight, MainActivity.this));
            }
        });

//        view = findViewById(R.id.tobefade);
//        Interpolator interpolator =
//                AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
//        view.setVisibility(View.VISIBLE);
//        //view.setTranslationY(offset);
//        view.setAlpha(1f);
//        // then animate back to natural position
//        view.animate()
//                // .translationY(0f)
//                .alpha(0f)
//                .setInterpolator(interpolator)
//                .setDuration(4000L)
//                .start();

//        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
//                + "getDimension douze " + "sp" + getResources().getDimension(R.dimen.douze_sp));
//        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
//                + "getDimensionPixelOffset douze " + "sp" + getResources().getDimensionPixelOffset(R.dimen.douze_sp));
//        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
//                + "getDimensionPixelSize douze " + "sp" + getResources().getDimensionPixelSize(R
//                .dimen.douze_sp));
//        Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
//                + "convertFromSpToPx douze " + "sp" + convertFromSpToPx(getResources().getDimensionPixelSize(R.dimen.douze_sp)));

    }


    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private float convertFromSpToPx(float sp)
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }
}
