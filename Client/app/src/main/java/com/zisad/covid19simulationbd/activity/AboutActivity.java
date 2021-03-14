package com.zisad.covid19simulationbd.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zisad.covid19simulationbd.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setTitle("About us");
    }
}
