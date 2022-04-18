package com.example.firebaseupload.NewsAndRadio;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseupload.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class RadioActivity extends AppCompatActivity {
    private static final String TAG = "RadioActivity";
    private static final int ACTIVITY_NUMBER = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");


    }

}
