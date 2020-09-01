package mil.army.fitnesstest.recorder.apft;

import android.content.ContentValues;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;
import mil.army.fitnesstest.recorder.apft.event.CountAPFTEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationAPFTEvent;

import static mil.army.fitnesstest.recorder.apft.APFTDBHelper.DBContract.*;

public class APFTRecord<T extends APFTEvent> extends Record<T> {
    public Sex sex = Sex.Male; public AgeGroup ageGroup = AgeGroup._17_21;
    public int raw_PU=0, raw_SU=0; public Duration raw_Cardio = new Duration(0,0);
    public int[] sco = {0,0,0}; public APFTCardioAlter cardioAlter;
    public int sco_total=0;

    public APFTRecord(){ super(); }

    @Override public void updateRecord(ArrayList<T> eventList) {
        sco_total=0;
        for(T event : eventList){
            switch (event.eventType){
                case APFTEvent.PU: raw_PU = ((CountAPFTEvent)event).raw; break;
                case APFTEvent.SU: raw_SU = ((CountAPFTEvent)event).raw; break;
                case APFTEvent.CARDIO:
                    raw_Cardio = ((DurationAPFTEvent)event).duration;
                    cardioAlter = ((DurationAPFTEvent)event).cardioAlter;
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
                case APFTEvent.PU: ((CountAPFTEvent)event).raw = raw_PU; break;
                case APFTEvent.SU: ((CountAPFTEvent)event).raw = raw_SU; break;
                case APFTEvent.CARDIO:
                    ((DurationAPFTEvent)event).duration = raw_Cardio;
                    ((DurationAPFTEvent)event).cardioAlter = cardioAlter;
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
        cv.put(COLUMN_RECORD_DATE,dateToString());
        cv.put(COLUMN_RAW_PU,raw_PU);
        cv.put(COLUMN_SCORE_PU,sco[0]);
        cv.put(COLUMN_RAW_SU,raw_SU);
        cv.put(COLUMN_SCORE_SU,sco[1]);
        cv.put(COLUMN_RAW_CARDIO,raw_Cardio.toString());
        cv.put(COLUMN_SCORE_CARDIO,sco[2]);
        cv.put(COLUMN_CARDIO_ALTER,cardioAlter.toString());
        cv.put(COLUMN_SEX,sex.name());
        cv.put(COLUMN_AGE_GROUP,ageGroup.toString());
        cv.put(COLUMN_SCORE_TOTAL,sco_total);
        cv.put(COLUMN_IS_PASSED,Boolean.toString(isPassed));
        return cv;
    }

    @NotNull @Override public String toString() {
        return null;
    }


    public enum AgeGroup{
        _17_21("17-21"),
        _22_26("22-26"),
        _27_31("27-31"),
        _32_36("32-36"),
        _37_41("37-41"),
        _42_46("42-46"),
        _47_51("47-51"),
        _52_56("52-56");

        private String str; // contains default string.
        AgeGroup(String str){this.str=str;} // constructor & setter.
        @NotNull public String toString(){return str;}

        public static AgeGroup findById(int ordinal){ return values()[ordinal]; }
        public static AgeGroup findByString(String str){
            switch (str){
                case "17-21": return _17_21;
                case "22-26": return _22_26;
                case "27-31": return _27_31;
                case "32-36": return _32_36;
                case "37-41": return _37_41;
                case "42-46": return _42_46;
                case "47-51": return _47_51;
                case "52-56": return _52_56;
                default: return null;
            }
        }
    }
}
