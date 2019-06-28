package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    static TextView mHumidityValue, mTemperatureValue, mAirPressureValue, mAltitudeValue, mVocValue, mEco2Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mHumidityValue = (TextView) findViewById(R.id.humidityValueLabel);
        mTemperatureValue = (TextView) findViewById(R.id.temperatureValueLabel);
        mAirPressureValue = (TextView) findViewById(R.id.airPressureValueLabel);
        mAltitudeValue = (TextView) findViewById(R.id.altitudeValueLabel);
        mVocValue = (TextView) findViewById(R.id.vocValueLabel);
        mEco2Value = (TextView) findViewById(R.id.eco2ValueLabel);
    }
    static void update(String value){
        Log.i("BGatt","shaata " + value + mHumidityValue.getText().toString());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DisplayActivity.mHumidityValue.setText(value);
            }
        });
    }
}
