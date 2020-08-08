package mil.army.acft.recorder.ACFT;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ACFTViewModel extends ViewModel {

    private MutableLiveData<ACFTRecord> mACFTRecord;

    public ACFTViewModel() {
        mACFTRecord = new MutableLiveData<>();
    }

    public LiveData<ACFTRecord> getACFTRecord() { return mACFTRecord; }
}