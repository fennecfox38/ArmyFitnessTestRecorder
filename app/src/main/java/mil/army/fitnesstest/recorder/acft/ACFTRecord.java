package mil.army.fitnesstest.recorder.acft;

import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.acft.event.ACFTCardioAlter;
import mil.army.fitnesstest.recorder.acft.event.ACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.CountACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.DurationACFTEvent;

import static mil.army.fitnesstest.recorder.acft.ACFTDBContract.*;

public class ACFTRecord extends Record {
    public int[] sco = {0, 0, 0, 0, 0, 0};
    public int raw_0 = 0, raw_2 = 0, raw_4 = 0; public float raw_1 = 0;
    public Duration raw_3 = new Duration(0), raw_5 = new Duration(0);
    public ACFTCardioAlter cardioAlter = ACFTCardioAlter.RUN;
    public Level qualifiedLevel = Level.Fail;
    public int sco_total=0;
    public MOS mos = MOS.Moderate;

    public ACFTRecord(){ super(); }

    public void updateRecord(ArrayList<ACFTEvent> eventList){
        CountACFTEvent countEvent = (CountACFTEvent) eventList.get(ACFTEvent.MDL);
        raw_0 = countEvent.raw;
        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.SPT);
        raw_1 = countEvent.raw/10.f;
        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.HPU);
        raw_2 = countEvent.raw;
        DurationACFTEvent durationEvent = (DurationACFTEvent) eventList.get(ACFTEvent.SDC);
        raw_3 = durationEvent.duration;
        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.LTK);
        raw_4 = countEvent.raw;
        durationEvent = (DurationACFTEvent) eventList.get(ACFTEvent.CARDIO);
        raw_5 = durationEvent.duration;
        cardioAlter = durationEvent.cardioAlter;
        sco_total = 0; qualifiedLevel = Level.Heavy;
        for(ACFTEvent event : eventList){
            //sco[event.eventType] = event.sco;
            //sco_total += sco[event.eventType];
            sco_total += sco[event.eventType] = event.sco;
            if (qualifiedLevel.compareTo(event.level) > 0)
                qualifiedLevel = event.level;
        }
    }

    public void restoreList(ArrayList<ACFTEvent> eventList){
        CountACFTEvent countEvent = (CountACFTEvent) eventList.get(ACFTEvent.MDL);
        countEvent.raw = raw_0; countEvent.sco = sco[0]; countEvent.giveScore();

        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.SPT);
        countEvent.raw = ((int)(raw_1*10)); countEvent.sco = sco[1]; countEvent.giveScore();

        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.HPU);
        countEvent.raw = raw_2; countEvent.sco = sco[2]; countEvent.giveScore();

        DurationACFTEvent durationEvent = (DurationACFTEvent) eventList.get(ACFTEvent.SDC);
        durationEvent.duration = raw_3; durationEvent.sco = sco[3]; durationEvent.giveScore();

        countEvent = (CountACFTEvent) eventList.get(ACFTEvent.LTK);
        countEvent.raw = raw_4; countEvent.sco = sco[4]; countEvent.giveScore();

        durationEvent = (DurationACFTEvent) eventList.get(ACFTEvent.CARDIO);
        durationEvent.duration = raw_5; durationEvent.cardioAlter = cardioAlter;
        durationEvent.sco = sco[5]; durationEvent.giveScore();
    }

    public void invalidate(ArrayList<ACFTEvent> eventList){
        if(eventList!=null) updateRecord(eventList);
        isPassed = (qualifiedLevel.ordinal()>mos.ordinal()); // qualifiedLevel>=mos.ordinal()+1
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECORD_DATE,dateToString());
        cv.put(COLUMN_RAW_MDL,raw_0);
        cv.put(COLUMN_SCORE_MDL,sco[0]);
        cv.put(COLUMN_RAW_SPT,Math.round(raw_1*10)/10.f);
        cv.put(COLUMN_SCORE_SPT,sco[1]);
        cv.put(COLUMN_RAW_HPU,raw_2);
        cv.put(COLUMN_SCORE_HPU,sco[2]);
        cv.put(COLUMN_RAW_SDC,raw_3.toString());
        cv.put(COLUMN_SCORE_SDC,sco[3]);
        cv.put(COLUMN_RAW_LTK,raw_4);
        cv.put(COLUMN_SCORE_LTK,sco[4]);
        cv.put(COLUMN_RAW_CARDIO,raw_5.toString());
        cv.put(COLUMN_SCORE_CARDIO,sco[5]);
        cv.put(COLUMN_CARDIO_ALTER,cardioAlter.toString());
        cv.put(COLUMN_QUALIFIED_LEVEL,qualifiedLevel.name());
        cv.put(COLUMN_SCORE_TOTAL,sco_total);
        cv.put(COLUMN_MOS_REQUIREMENT,mos.name());
        cv.put(COLUMN_IS_PASSED,Boolean.toString(isPassed));
        return cv;
    }

    @NotNull @Override public String toString() {
        return "Record Date: " +dateToString() +
                "\nMOS Requirement: " + mos.name() +
                "\nMDL: " + raw_0 + "lbs / score: "+ sco[0] +
                "\nSPT: " + raw_1 + "m / score: "+ sco[1] +
                "\nHPU: " + raw_2 + "reps / score: "+ sco[2] +
                "\nSDC: " + raw_3.toString() + " / score: "+ sco[3] +
                "\nLTK: " + raw_4 + "reps / score: "+ sco[4] +
                "\n"+ cardioAlter.toString() +": " + raw_5.toString() + " / score: "+ sco[5] +
                "\nQualified: " + qualifiedLevel.name() +
                "\nScore Total: " + sco_total +
                "\n" + getPassed(isPassed);
    }


    public enum MOS{
        Moderate(), Significant(), Heavy();

        //MOS(){ } // constructor
        public MOS valueOf(int ordinal){ return values()[ordinal]; }
    }

}
