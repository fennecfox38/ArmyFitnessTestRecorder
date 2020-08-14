package army.prt.recorder.acft;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import army.prt.recorder.acft.event.Event;

public class ACFTViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Event>> eventList;

    public ACFTViewModel() {
        eventList = new MutableLiveData<>();
        eventList.setValue(null);
    }

    public void setEventList(ArrayList<Event> eventList){
        this.eventList.setValue(eventList);
    }
    public void updateEvent(Event event, int position){
        ArrayList<Event> list = eventList.getValue();
        assert list != null;
        list.set(position,event);
        eventList.setValue(list);
    }

    public LiveData<ArrayList<Event>> getEventList() { return eventList; }

}