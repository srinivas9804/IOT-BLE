package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    static TextView mHumidityValue, mTemperatureValue, mAirPressureValue, mAltitudeValue, mVocValue, mEco2Value,mPm1Value,mPm25Value,mPm10Value;
    private static Context mContext;

    ImageView uclBanner, airQualityLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        uclBanner = (ImageView) findViewById(R.id.uclBanner);
        airQualityLogo = (ImageView) findViewById(R.id.airTrackerLogo);

        uclBanner.getLayoutParams().width = (int)(0.6*width) ;
        airQualityLogo.getLayoutParams().width = (int)(0.3*width);

        mContext = this;
        mHumidityValue = (TextView) findViewById(R.id.humidityValueLabel);
        mTemperatureValue = (TextView) findViewById(R.id.temperatureValueLabel);
        mAirPressureValue = (TextView) findViewById(R.id.airPressureValueLabel);
        mAltitudeValue = (TextView) findViewById(R.id.altitudeValueLabel);
        mVocValue = (TextView) findViewById(R.id.vocValueLabel);
        mEco2Value = (TextView) findViewById(R.id.eco2ValueLabel);
        mPm1Value = (TextView) findViewById(R.id.pm1ValueLabel);
        mPm25Value = (TextView) findViewById(R.id.pm25ValueLabel);
        mPm10Value = (TextView) findViewById(R.id.pm10ValueLabel);
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
                DisplayActivity.mPm1Value.setText(arr[6]);
                DisplayActivity.mPm25Value.setText(arr[7]);
                DisplayActivity.mPm10Value.setText(arr[8]);
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
