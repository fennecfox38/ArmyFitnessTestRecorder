package army.prt.recorder.acft;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import army.prt.recorder.acft.event.Event;

public class ACFTViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Event>> eventList;
    private MutableLiveData<ACFTRecord> record;

    public ACFTViewModel() {
        eventList = new MutableLiveData<>();
        eventList.setValue(null);
        record = new MutableLiveData<>();
        record.setValue(new ACFTRecord());
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
    public void updateRecord() {
        try{ record.getValue().updateRecord(eventList.getValue()); }
        catch (Exception e) { }
    }

    public LiveData<ArrayList<Event>> getEventList() { return eventList; }
    public LiveData<ACFTRecord> getRecord(){ return record; }

}