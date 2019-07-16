/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *      Repository is a class that abstracts access to multiple data sources. T
 *      A Repository class handles data operations. It provides a clean API to the rest of the app
 *      for app data.
 *
 */
package com.example.airquality;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AirDataRepository {

    private AirDataDao mAirDataDao;
    private LiveData<List<AirData>> mAllData;

    AirDataRepository(Application application) {
        AirDataDatabase db = AirDataDatabase.getDatabase(application);
        mAirDataDao = db.airDao();
        mAllData = mAirDataDao.getAll();
    }

    LiveData<List<AirData>> getAllWords() {
        return mAllData;
    }


    public void insert (AirData word) {
        new insertAsyncTask(mAirDataDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<AirData, Void, Void> {

        private AirDataDao mAsyncTaskDao;

        insertAsyncTask(AirDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AirData... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    public void deleteAll() { new deleteAllAsyncTask(mAirDataDao).execute();}

    private static class deleteAllAsyncTask extends AsyncTask<Void,Void,Void> {

        private AirDataDao mAsyncTaskDao;

        deleteAllAsyncTask(AirDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    public void delete(AirData item) {new deleteAsyncTask(mAirDataDao, item).execute();}

    private static class deleteAsyncTask extends AsyncTask<Void,Void,Void> {

        private AirDataDao mAsyncTaskDao;
        private AirData data;

        deleteAsyncTask(AirDataDao dao, AirData data) {
            mAsyncTaskDao = dao;
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.delete(data);
            return null;
        }
    }

    public AirData get(String macAddress, long timestamp){ return mAirDataDao.get(macAddress,timestamp);}


}
