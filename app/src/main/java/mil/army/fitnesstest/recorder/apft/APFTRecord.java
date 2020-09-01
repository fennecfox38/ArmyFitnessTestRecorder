package mil.army.fitnesstest.recorder.apft;

import android.content.ContentValues;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.CardioAlter;
import mil.army.fitnesstest.recorder.apft.event.CountEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationEvent;
import mil.army.fitnesstest.recorder.apft.event.Event;

public class APFTRecord<T extends Event> extends Record<T> {
    public Sex sex = Sex.Male; public AgeGroup ageGroup = AgeGroup._17_21;
    public int raw_PU=0, raw_SU=0; public Duration raw_Cardio = new Duration(0,0);
    public int[] sco = {0,0,0}; public CardioAlter cardioAlter;
    public int sco_total=0;

    public APFTRecord(){ super(); }

    @Override public void updateRecord(ArrayList<T> eventList) {
        sco_total=0;
        for(T event : eventList){
            switch (event.eventType){
                case Event.PU: raw_PU = ((CountEvent)event).raw; break;
                case Event.SU: raw_SU = ((CountEvent)event).raw; break;
                case Event.CARDIO:
                    raw_Cardio = ((DurationEvent)event).duration;
                    cardioAlter = ((DurationEvent)event).cardioAlter;
                    break;
            }
            sco[event.eventType] = event.sco;
            sco_total += sco[event.eventType];
        }
    }

    @Override public void restoreList(ArrayList<T> eventList) {
        for(T event : eventList){
            event.sex = sex;
            event.ageGroup = ageGroup;
            switch (event.eventType){
                case Event.PU: ((CountEvent)event).raw = raw_PU; break;
                case Event.SU: ((CountEvent)event).raw = raw_SU; break;
                case Event.CARDIO:
                    ((DurationEvent)event).duration = raw_Cardio;
                    ((DurationEvent)event).cardioAlter = cardioAlter;
                    break;
            }
            event.sco = sco[event.eventType];
        }
    }

    @Override public void invalidate(ArrayList<T> eventList) {
        if(eventList!=null) updateRecord(eventList);
        isPassed=(sco[0]>=60 && sco[1]>=60 && sco[2]>=60);
    }

    public void validateEvent(MutableLiveData<ArrayList<T>> mutableList){
        ArrayList<T> eventList = mutableList.getValue();
        for(T event : eventList){
            event.sex = sex;
            event.ageGroup = ageGroup;
            event.giveScore();
        }
        mutableList.setValue(eventList);
    }

    @Override public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        return cv;
    }

    @NotNull @Override public String toString() {
        return null;
    }


    public enum AgeGroup{
        _17_21(0,"17-21"),
        _22_26(1,"22-26"),
        _27_31(2,"27-31"),
        _32_36(3,"32-36"),
        _37_41(4,"37-41"),
        _42_46(5,"42-46"),
        _47_51(6,"47-51"),
        _52_56(7,"52-56");

        private int id; // contains id.
        private String str; // contains default string.
        AgeGroup(int id, String str){this.id=id; this.str=str;} // constructor & setter.
        @NotNull public String toString(){return str;}

        public static AgeGroup findById(int id){
            switch(id){
                case 0: return _17_21; case 1: return _22_26;
                case 2: return _27_31; case 3: return _32_36;
                case 4: return _37_41; case 5: return _42_46;
                case 6: return _47_51; case 7: return _52_56;
            }
            return null;
        }
        public static AgeGroup findByString(String str){
            if(str.equals(_17_21.str)) return _17_21;
            else if(str.equals(_22_26.str)) return _22_26;
            else if(str.equals(_27_31.str)) return _27_31;
            else if(str.equals(_32_36.str)) return _32_36;
            else if(str.equals(_37_41.str)) return _37_41;
            else if(str.equals(_42_46.str)) return _42_46;
            else if(str.equals(_47_51.str)) return _47_51;
            else if(str.equals(_52_56.str)) return _52_56;
            else return null;
        }
    }
}
