package army.prt.recorder.abcp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ABCPViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ABCPViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ABCP fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}