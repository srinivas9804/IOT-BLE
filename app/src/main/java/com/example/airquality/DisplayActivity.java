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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    static TextView mMacAddress;
    private Button mUpload, mDelete;
    private static Context mContext;

    ImageView uclBanner, airQualityLogo;

    private RecyclerView recycler;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter mAdapter;
    private List<String> list=new ArrayList<>();
    private static List<AirData> data;
    private AirDataViewModel mViewModel;
    private LiveData<List<AirData>> observer;
    private static String macAddress;

    private static List<String> headerList;
    private RequestQueue queue;



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
        mUpload = (Button) findViewById(R.id.uploadButton);
        mDelete = (Button) findViewById(R.id.deleteButton);

        uclBanner.getLayoutParams().width = (int)(0.6*width) ;
        airQualityLogo.getLayoutParams().width = (int)(0.3*width);

        mContext = this;

        mMacAddress = (TextView) findViewById(R.id.macAddress);
        Intent intent = getIntent();
        DisplayActivity.macAddress = intent.getStringExtra("MAC_Address");
        mMacAddress.setText(DisplayActivity.macAddress);

        headerList = new ArrayList<>();
        headerList.add("Timestamp");
        headerList.add("Temperature");
        headerList.add("Humidity");
        headerList.add("Air Pressure");
        headerList.add("Altitude");
        headerList.add("VOC");
        headerList.add("ECO2");
        headerList.add("PM1");
        headerList.add("PM10");
        headerList.add("PM25");

        recycler = findViewById(R.id.dataRecyclerView);
        recycler.setHasFixedSize(
                true);
        manager = new GridLayoutManager(this, 10, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(manager);
        mAdapter = new DataAdapter(list,this);
        recycler.setAdapter(mAdapter);
        mViewModel = ViewModelProviders.of(this).get(AirDataViewModel.class);
        mViewModel.getAll().observe(this, new Observer<List<AirData>>() {
            @Override
            public void onChanged(final List<AirData> airData) {
                list.clear();
                list.addAll(DisplayActivity.headerList);
                DisplayActivity.data = airData;
                for(int i = airData.size()-1; i>=0; i--){
                    AirData item = airData.get(i);
                    if(item.macAddress.equals(DisplayActivity.macAddress)) {
                        long yourmilliseconds = item.timestamp;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                        Date resultdate = new Date(yourmilliseconds);
                        list.add(sdf.format(resultdate));
                        list.add(Double.toString(item.temperature));
                        list.add(Double.toString(item.humidity));
                        list.add(Double.toString(item.airPressure));
                        list.add(Double.toString(item.altitude));
                        list.add(Double.toString(item.vocs));
                        list.add(Double.toString(item.eco2));
                        list.add(Double.toString(item.pm1));
                        list.add(Double.toString(item.pm10));
                        list.add(Double.toString(item.pm25));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        queue = Volley.newRequestQueue(this);
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
    }
    static void endActivity(){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(mContext,
                    "Lost Connection", Toast.LENGTH_SHORT).show();

            ((DisplayActivity) mContext).finish();
        });
    }
}
