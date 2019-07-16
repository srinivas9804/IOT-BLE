/**
 * @author Srinivas Sivakumar <srinivas9804@gmail.com,www.github.com/srinivas9804>
 *
 *     This is a ViewModel for the AirData Database
 *     The ViewModel's role is to provide data to the UI and survive configuration changes. #
 *     A ViewModel acts as a communication center between the Repository and the UI
 *
 */
package com.example.airquality;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AirDataViewModel extends AndroidViewModel {
    private AirDataRepository mRepository;
    private LiveData<List<AirData>> mAllData;

    public AirDataViewModel(Application application) {
        super(application);
        mRepository = new AirDataRepository(application);
        mAllData = mRepository.getAllWords();
    }

    LiveData<List<AirData>> getAll() { return mAllData; }

    public AirData get(String macAdress, long timestamp){ return mRepository.get(macAdress, timestamp);}

    public void insert(AirData item) { mRepository.insert(item); }

    public void deleteAll(){
        mRepository.deleteAll();
    }

    public void delete(AirData item) { mRepository.delete(item); }

}
