/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *      This is the Room database layer on top of the SQLite database.
 *
 */
package com.example.airquality;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AirData.class}, version = 1)
public abstract class AirDataDatabase extends RoomDatabase {
    public abstract AirDataDao airDao();

    private static volatile AirDataDatabase INSTANCE;

    static AirDataDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AirDataDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AirDataDatabase.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}