package com.jsqix.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dq on 2016/3/15.
 */
public class BaseActivity extends AppCompatActivity {
    public ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameApplication.addToActivityList(this);
        aCache = ACache.get(this);
    }

}
