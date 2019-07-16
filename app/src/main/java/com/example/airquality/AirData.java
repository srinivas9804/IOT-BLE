/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     AirData entity for the Room database
 *
 */
package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "AIR_DATA",primaryKeys = {"MAC_Address","Timestamp"})
public class AirData {

    @NonNull
    @ColumnInfo(name = "MAC_Address")
    public String macAddress;

    @NonNull
    @ColumnInfo(name = "Timestamp")
    public long timestamp;

    @ColumnInfo(name = "Humidity")
    public double humidity;

    @ColumnInfo(name = "Temperature")
    public double temperature;

    @ColumnInfo(name = "Air_Pressure")
    public double airPressure;

    @ColumnInfo(name = "Altitude")
    public double altitude;

    @ColumnInfo(name = "VOCs")
    public double vocs;

    @ColumnInfo(name = "eC02")
    public double eco2;

    @ColumnInfo(name = "Pm1")
    public double pm1;

    @ColumnInfo(name = "Pm25")
    public double pm25;

    @ColumnInfo(name = "Pm10")
    public double pm10;

    //11 params
    public AirData(String macAddress, long timestamp, double temperature, double humidity, double airPressure, double altitude, double vocs, double eco2, double pm1, double pm10, double pm25){
        this.macAddress = macAddress;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.altitude = altitude;
        this.airPressure = airPressure;
        this.vocs = vocs;
        this.eco2 = eco2;
        this.pm1 = pm1;
        this.pm25 = pm10;
        this.pm10 = pm25;
    }
}
