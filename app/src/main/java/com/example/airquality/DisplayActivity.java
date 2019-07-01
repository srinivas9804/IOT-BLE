package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    static TextView mHumidityValue, mTemperatureValue, mAirPressureValue, mAltitudeValue, mVocValue, mEco2Value;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mContext = this;
        mHumidityValue = (TextView) findViewById(R.id.humidityValueLabel);
        mTemperatureValue = (TextView) findViewById(R.id.temperatureValueLabel);
        mAirPressureValue = (TextView) findViewById(R.id.airPressureValueLabel);
        mAltitudeValue = (TextView) findViewById(R.id.altitudeValueLabel);
        mVocValue = (TextView) findViewById(R.id.vocValueLabel);
        mEco2Value = (TextView) findViewById(R.id.eco2ValueLabel);
    }
    static void update(String value){
        Log.i("BGatt","shaata " + value + mHumidityValue.getText().toString());
        String arr[] = value.split(",");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DisplayActivity.mHumidityValue.setText(arr[0]);
                DisplayActivity.mTemperatureValue.setText(arr[1]);
                DisplayActivity.mAirPressureValue.setText(arr[2]);
                DisplayActivity.mAltitudeValue.setText(arr[3]);
                DisplayActivity.mVocValue.setText(arr[4]);
                DisplayActivity.mEco2Value.setText(arr[5]);
            }
        });
    }
    static void endActivity(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,
                                "Lost Connection", Toast.LENGTH_SHORT).show();

                ((DisplayActivity)mContext).finish();
            }
        });
    }
}
