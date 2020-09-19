package mil.army.fitnesstest.recorder.apft;

import android.content.ContentValues;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;
import mil.army.fitnesstest.recorder.apft.event.CountAPFTEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationAPFTEvent;

import static mil.army.fitnesstest.recorder.apft.APFTDBContract.*;

public class APFTRecord extends Record{
    public Sex sex = Sex.Male; public AgeGroup ageGroup = AgeGroup._17_21;
    public int raw_PU=0, raw_SU=0; public Duration raw_Cardio = new Duration(0,0);
    public int[] sco = {0,0,0}; public APFTCardioAlter cardioAlter;
    public int sco_total=0;

    public APFTRecord(){ super(); }

    public void updateRecord(ArrayList<APFTEvent> eventList) {
        CountAPFTEvent countEvent = (CountAPFTEvent) eventList.get(APFTEvent.PU);
        raw_PU = countEvent.raw;
        sco[0] = countEvent.sco;
        countEvent = (CountAPFTEvent) eventList.get(APFTEvent.SU);
        raw_SU = countEvent.raw;
        sco[1] = countEvent.sco;
        DurationAPFTEvent durationEvent = (DurationAPFTEvent) eventList.get(APFTEvent.CARDIO);
        raw_Cardio = durationEvent.duration;
        cardioAlter = durationEvent.cardioAlter;
        sco[2] = durationEvent.sco;
        sco_total = sco[0] + sco[1] + sco[2];
    }

    public void restoreList(ArrayList<APFTEvent> eventList) {
        CountAPFTEvent countEvent = (CountAPFTEvent) eventList.get(APFTEvent.PU);
        countEvent.sex = sex;
        countEvent.ageGroup = ageGroup;
        countEvent.raw = raw_PU;
        countEvent.sco = sco[0];
        countEvent = (CountAPFTEvent) eventList.get(APFTEvent.SU);
        countEvent.sex = sex;
        countEvent.ageGroup = ageGroup;
        countEvent.raw = raw_SU;
        countEvent.sco = sco[1];
        DurationAPFTEvent durationEvent = (DurationAPFTEvent) eventList.get(APFTEvent.CARDIO);
        durationEvent.duration = raw_Cardio;
        durationEvent.cardioAlter = cardioAlter;
        durationEvent.sco = sco[2];
    }

    public void invalidate(ArrayList<APFTEvent> eventList) {
        if(eventList!=null) updateRecord(eventList);
        isPassed=(sco[0]>=60 && sco[1]>=60 && sco[2]>=60);
    }

    public void validateEvent(MutableLiveData<ArrayList<APFTEvent>> mutableList){
        ArrayList<APFTEvent> eventList = Objects.requireNonNull(mutableList.getValue());
        for(APFTEvent event : eventList){
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
        return "Record Date: " +dateToString() +
                "\nSex: " + sex.name() +
                "\nAge Group: " + ageGroup.toString() +
                "\nPush-Up: " + raw_PU + "reps / score: " + sco[0] +
                "\nSit-Up: " + raw_SU + "reps / score: " + sco[1] +
                "\n"+ cardioAlter.toString() +": " + raw_Cardio.toString() + " / score: " + sco[2] +
                "\nScore Total: " + sco_total +
                "\n" + getPassed(isPassed);
    }

    public enum AgeGroup{
        _17_21("17–21"), _22_26("22–26"), _27_31("27–31"), _32_36("32–36"),
        _37_41("37–41"), _42_46("42–46"), _47_51("47–51"), _52_56("52–56");

        private String str; // contains default string.
        private static HashMap<String, AgeGroup> map = new HashMap<String, AgeGroup>(){{
            put("17–21", _17_21); put("22–26", _22_26); put("27–31", _27_31); put("32–36", _32_36);
            put("37–41", _37_41); put("42–46", _42_46); put("47–51",  _47_51); put("52–56", _52_56);
        }};
        AgeGroup(String str){this.str=str;} // constructor & setter.
        @NotNull public String toString(){return str;}

        public static AgeGroup findById(int ordinal){ return values()[ordinal]; }
        public static AgeGroup findByString(String str){
            return map.get(str);
        }
    }
}
