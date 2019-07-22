/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     This is the DAO(Data Access Object) for the AirData Room database
 *
 */
package com.example.airquality;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AirDataDao {
    @Query("SELECT * FROM AIR_DATA")
    LiveData<List<AirData>> getAll();

    @Query("SELECT * FROM AIR_DATA WHERE MAC_Address IN (:macIDs)")
    LiveData<List<AirData>> loadAllByMac(String[] macIDs);

    @Query("SELECT * FROM AIR_DATA WHERE MAC_Address= (:macAddress) AND Timestamp= (:timestamp)")
    AirData get(String macAddress, long timestamp);

    @Insert
    void insertAll(AirData... items);

    @Insert
    void insert(AirData word);

    @Delete
    void delete(AirData item);

    @Query("DELETE FROM AIR_DATA")
    void deleteAll();
}