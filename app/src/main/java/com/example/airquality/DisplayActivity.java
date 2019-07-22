/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     This Activity displays the data received from the sensor.
 *     It also uploads data to the node server
 *
 */
package com.example.airquality;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    static TextView mMacAddress;
    private Button mUpload, mDelete, mLive;
    private static Context mContext;

    private ScrollView mRecyclerParentView;
    private LinearLayout mChartParentView;

    ImageView uclBanner, airQualityLogo;

    private RecyclerView recycler;
    private RecyclerView.LayoutManager manager;
    static private RecyclerView.Adapter mAdapter;
    private List<String> list=new ArrayList<>();
    private static List<AirData> data;
    private static List<AirData> serverData;
    private AirDataViewModel mViewModel;
    private LiveData<List<AirData>> observer;
    private static String macAddress;

    private RequestQueue queue;
    static boolean liveDataFlag;
    static LineChart mLineChart;
    private Spinner mSpinner;

    static long referenceTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        uclBanner = findViewById(R.id.uclBanner);
        airQualityLogo = findViewById(R.id.airTrackerLogo);
        mUpload = findViewById(R.id.uploadButton);
        mDelete = findViewById(R.id.deleteButton);
        mLive = findViewById(R.id.liveButton);
        mLineChart = findViewById(R.id.lineChart);
        mRecyclerParentView = findViewById(R.id.mRecyclerParent);
        mChartParentView = findViewById(R.id.mChartParent);
        mSpinner = findViewById(R.id.spinner);
        mMacAddress = findViewById(R.id.macAddress);
        recycler = findViewById(R.id.dataRecyclerView);

        queue = Volley.newRequestQueue(this);
        serverData = new ArrayList<>();
        mContext = this;
        liveDataFlag = true;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        uclBanner.getLayoutParams().width = (int)(0.6*width) ;
        airQualityLogo.getLayoutParams().width = (int)(0.3*width);

        Intent intent = getIntent();
        DisplayActivity.macAddress = intent.getStringExtra("MAC_Address");
        mMacAddress.setText(DisplayActivity.macAddress);


        recycler.setHasFixedSize(
                true);
        manager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(manager);
        mAdapter = new DataAdapter(list,this);
        recycler.setAdapter(mAdapter);
        mViewModel = ViewModelProviders.of(this).get(AirDataViewModel.class);
        mViewModel.getAll().observe(this, new Observer<List<AirData>>() {
            @Override
            public void onChanged(final List<AirData> airData) {
                if (DisplayActivity.liveDataFlag) {
                    list.clear();
                    DisplayActivity.data = airData;
                    for (int i = airData.size() - 1; i >= 0; i--) {
                        AirData item = airData.get(i);
                        if (item.macAddress.equals(DisplayActivity.macAddress)) {
                            long yourmilliseconds = item.timestamp;
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

                            list.add("Time");
                            Date resultdate = new Date(yourmilliseconds);
                            list.add(sdf.format(resultdate));
                            list.add("Temperature");
                            list.add(Double.toString(item.temperature));
                            list.add("Humidity");
                            list.add(Double.toString(item.humidity));
                            list.add("Air Pressure");
                            list.add(Double.toString(item.airPressure));
                            list.add("Altitude");
                            list.add(Double.toString(item.altitude));
                            list.add("VOC");
                            list.add(Double.toString(item.vocs));
                            list.add("ECO2");
                            list.add(Double.toString(item.eco2));
                            list.add("PM1");
                            list.add(Double.toString(item.pm1));
                            list.add("PM10");
                            list.add(Double.toString(item.pm10));
                            list.add("PM25");
                            list.add(Double.toString(item.pm25));
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mUpload.setOnClickListener((View view) -> {
            String url = "http://192.168.1.234:3000/app";
            Gson gson = new Gson();
            for (AirData item : data) {
                try {
                    JSONObject jsonObj = new JSONObject(gson.toJson(item));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response", response.toString());
                            try {
                                int status = response.getInt("status");
                                String macAddress = response.getJSONObject("data").getString("macAddress");
                                long timestamp = response.getJSONObject("data").getLong("timestamp");
                                //11000 - duplicate key error, delete element
                                //200 - OK, delete element from local storage
                                if(status == 11000 || status == 200){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AirData dataItem = mViewModel.get(macAddress, timestamp);
                                            Log.i("DeleteItem",gson.toJson(dataItem));
                                            mViewModel.delete(dataItem);
                                        }
                                    }).start();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Response", "Error lol" + error);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DisplayActivity.this,
                                            "Sorry, couldn't reach the server. Please make " +
                                                    "sure you are connected on the same network", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    queue.add(jsonObjectRequest);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mDelete.setOnClickListener((View view) -> {
            mViewModel.deleteAll();
        });

        mLive.setOnClickListener((View view)->{
            liveDataFlag = !liveDataFlag;
            ColorStateList csl = mLive.getTextColors();
            mLive.setTextColor(mLive.getBackgroundTintList());
            mLive.setBackgroundTintList(csl);
            if(liveDataFlag){
                mRecyclerParentView.setVisibility(View.VISIBLE);
                mChartParentView.setVisibility(View.INVISIBLE);
                mLive.setText("LIVE");
                list.clear();
                List<AirData> temp = mViewModel.getAll().getValue();
                for (int i = temp.size() - 1; i >= 0; i--) {
                    AirData item = temp.get(i);
                    if (item.macAddress.equals(DisplayActivity.macAddress)) {
                        long yourmilliseconds = item.timestamp;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

                        list.add("Time");
                        Date resultdate = new Date(yourmilliseconds);
                        list.add(sdf.format(resultdate));
                        list.add("Temperature");
                        list.add(Double.toString(item.temperature));
                        list.add("Humidity");
                        list.add(Double.toString(item.humidity));
                        list.add("Air Pressure");
                        list.add(Double.toString(item.airPressure));
                        list.add("Altitude");
                        list.add(Double.toString(item.altitude));
                        list.add("VOC");
                        list.add(Double.toString(item.vocs));
                        list.add("ECO2");
                        list.add(Double.toString(item.eco2));
                        list.add("PM1");
                        list.add(Double.toString(item.pm1));
                        list.add("PM10");
                        list.add(Double.toString(item.pm10));
                        list.add("PM25");
                        list.add(Double.toString(item.pm25));
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            else{
                mLive.setText("Server");
                mRecyclerParentView.setVisibility(View.INVISIBLE);
                mChartParentView.setVisibility(View.VISIBLE);
                serverData.clear();
                mAdapter.notifyDataSetChanged();
                Gson gson = new Gson();
                String url = "http://192.168.1.234:3000/app/" + DisplayActivity.macAddress;
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the first 500 characters of the response string.
                        for(int i=0;i< response.length();i++){
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                AirData item = gson.fromJson(obj.toString(),AirData.class);
                                serverData.add(item);
                                Log.i("Server",item.macAddress);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayList<Entry> entries = new ArrayList<>();
                        if(serverData.size()>0){
                            referenceTimeStamp = serverData.get(serverData.size()-1).timestamp;
                        }
                        for(AirData data : serverData){
                            entries.add(new Entry((int)referenceTimeStamp - data.timestamp,(float)data.temperature));
                        }
                        LineDataSet dataSet = new LineDataSet(entries, "Time series");
                        LineData data = new LineData(dataSet);
                        mLineChart.setData(data);
                        mLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                long newTimeStamp = referenceTimeStamp + (long)value;
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Date resultdate = new Date(newTimeStamp);
                                return sdf.format(resultdate);
                            }
                        });
                        mLineChart.invalidate();
                    }

                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("GetError",error.toString());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DisplayActivity.this,
                                        "Sorry, Couldn't reach the server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                queue.add(jsonArrayRequest);
            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(serverData.size()>0){
                    ArrayList<Entry> entries = new ArrayList<>();
                    for (AirData data : serverData) {
                        switch(pos){
                            case 0:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.temperature));
                                break;
                            case 1:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.humidity));
                                break;
                            case 2:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.airPressure));
                                break;
                            case 3:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.altitude));
                                break;
                            case 4:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.vocs));
                                break;
                            case 5:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.eco2));
                                break;
                            case 6:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.pm1));
                                break;
                            case 7:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.pm10));
                                break;
                            case 8:
                                entries.add(new Entry((int) referenceTimeStamp - data.timestamp, (float) data.pm25));
                                break;
                        }
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Time series");
                    LineData data = new LineData(dataSet);
                    mLineChart.setData(data);
                    mLineChart.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    static void endActivity(){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(mContext,
                    "Lost Connection", Toast.LENGTH_SHORT).show();

            ((DisplayActivity) mContext).finish();
        });
    }
}
