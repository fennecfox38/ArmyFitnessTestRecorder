package mil.army.fitnesstest.recorder.acft;

import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.acft.event.CardioAlter;
import mil.army.fitnesstest.recorder.acft.event.CountEvent;
import mil.army.fitnesstest.recorder.acft.event.DurationEvent;
import mil.army.fitnesstest.recorder.acft.event.Event;

import static mil.army.fitnesstest.recorder.acft.ACFTDBHelper.DBContract.*;

public class ACFTRecord<T extends Event> extends Record<T> {
    public int[] sco = {0, 0, 0, 0, 0, 0};
    public int raw_0 = 0, raw_2 = 0, raw_4 = 0; public float raw_1 = 0;
    public Duration raw_3 = new Duration(0), raw_5 = new Duration(0);
    public CardioAlter cardioAlter = CardioAlter.RUN;
    public Level qualifiedLevel = Level.Fail;
    public int sco_total=0;
    public MOS mos = MOS.Moderate;

    public ACFTRecord(){ super(); }

    public void updateRecord(ArrayList<T> eventList){
        sco_total = 0; qualifiedLevel = Level.Heavy;
        for(T event : eventList){
            switch (event.eventType){
                case Event.MDL: raw_0 = ((CountEvent) event).raw; break;
                case Event.SPT: raw_1 = (((CountEvent) event).raw/10.0f); break;
                case Event.HPU: raw_2 = ((CountEvent) event).raw; break;
                case Event.SDC: raw_3.setTotalInSec(((DurationEvent)event).duration.getTotalInSec()); break;
                case Event.LTK: raw_4 = ((CountEvent) event).raw; break;
                case Event.CARDIO:
                    raw_5.setTotalInSec(((DurationEvent)event).duration.getTotalInSec());
                    cardioAlter = ((DurationEvent)event).cardioAlter;
                    break;
            }
            sco[event.eventType] = event.sco;
            sco_total += sco[event.eventType];

            if (qualifiedLevel.compareTo(event.level) > 0) qualifiedLevel = event.level;
        }
    }

    public void restoreList(ArrayList<T> eventList){
        for(T event : eventList){
            switch (event.eventType){
                case Event.MDL: ((CountEvent)event).raw = raw_0; break;
                case Event.SPT: ((CountEvent)event).raw = ((int)(raw_1*10)); break;
                case Event.HPU: ((CountEvent)event).raw = raw_2; break;
                case Event.SDC: ((DurationEvent)event).duration.setTotalInSec(raw_3.getTotalInSec()); break;
                case Event.LTK: ((CountEvent)event).raw = raw_4; break;
                case Event.CARDIO:
                    ((DurationEvent)event).duration.setTotalInSec(raw_5.getTotalInSec());
                    ((DurationEvent)event).cardioAlter = cardioAlter;
                    break;
            }
            event.sco = sco[event.eventType];
            event.giveLevel();
        }
    }

    public void invalidate(ArrayList<T> eventList){
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
        cv.put(COLUMN_QUALIFIED_LEVEL,qualifiedLevel.toString());
        cv.put(COLUMN_SCORE_TOTAL,sco_total);
        cv.put(COLUMN_MOS_REQUIREMENT,mos.toString());
        cv.put(COLUMN_IS_PASSED,Boolean.toString(isPassed));
        return cv;
    }

    @NotNull @Override public String toString() {
        return "Record Date: " +dateToString() +
                "\nMOS Requirement: " + mos.toString() +
                "\nMDL: " + raw_0 + "lbs / score: "+ sco[0] +
                "\nSPT: " + raw_1 + "m / score: "+ sco[1] +
                "\nHPU: " + raw_2 + "reps / score: "+ sco[2] +
                "\nSDC: " + raw_3.toString() + " / score: "+ sco[3] +
                "\nLTK: " + raw_4 + "reps / score: "+ sco[4] +
                "\n"+ cardioAlter.toString() +": " + raw_5.toString() + " / score: "+ sco[5] +
                "\nQualified: " + qualifiedLevel.toString() +
                "\nScore Total: " + sco_total +
                "\n" + getPassed(isPassed);
    }


    public enum MOS{
        Moderate(0,"Moderate"),
        Significant(1,"Significant"),
        Heavy(2,"Heavy");

        final private int id; // contains string resources id.
        final private String str; // contains default string.
        MOS(int id, String str){ this.id=id; this.str=str; } // constructor & setter.
        @NotNull public String toString(){ return str; }
    }

}
