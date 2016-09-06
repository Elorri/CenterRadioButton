package com.example.android.radiobuttoncenterimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view= findViewById(R.id.radio1);

        view.post(new Runnable() {
            @Override
            public void run() {
                Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                        + " height " + view.getHeight()
                        + " - measured height " + view.getMeasuredHeight()
                        + " - width " +view.getWidth()
                        + " - measured width " +view.getMeasuredWidth()
                );
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        view.post(new Runnable() {
            @Override
            public void run() {
                Log.e("Nebo", Thread.currentThread().getStackTrace()[2]
                        + " height " + view.getHeight()
                        + " - measured height " + view.getMeasuredHeight()
                        + " - width " +view.getWidth()
                        + " - measured width " +view.getMeasuredWidth()
                );
            }
        });
    }
}
