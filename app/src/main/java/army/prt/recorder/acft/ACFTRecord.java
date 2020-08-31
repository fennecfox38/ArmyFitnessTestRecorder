package army.prt.recorder.acft;

import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;

import static army.prt.recorder.log.ACFTDBHelper.DBContract.*;

public class ACFTRecord{
    public int[] sco = {0, 0, 0, 0, 0, 0};
    public int raw_0 = 0, raw_2 = 0, raw_4 = 0; public float raw_1 = 0;
    public Duration raw_3 = new Duration(0), raw_5 = new Duration(0);
    public CardioAlter cardioAlter = CardioAlter.RUN;
    public Level qualifiedLevel = Level.Fail;
    public int sco_total=0;
    public MOS mos = MOS.Moderate;
    public boolean isPassed=false;
    public int year, month, day;

    public ACFTRecord(){
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH)+1;
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    public void updateRecord(ArrayList<Event> eventList){
        sco_total = 0; qualifiedLevel = Level.Heavy;
        for(Event event : eventList){
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
        invalidateLevel();
    }

    public void restoreEventList(ArrayList<Event> eventList){
        for(Event event : eventList){
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

    public void invalidateLevel(){
        isPassed = (qualifiedLevel.ordinal()>mos.ordinal()); // qualifiedLevel>=mos.ordinal()+1
    }

    public String dateToString(){
        String str = String.valueOf(year);
        str += "-"; str += make2digit(String.valueOf(month));
        str += "-"; str += make2digit(String.valueOf(day));
        return str;
    }

    public void stringToDate(String str){
        String[] array = str.split("-");
        year = Integer.parseInt(array[0]);
        month = Integer.parseInt(array[1]);
        day = Integer.parseInt(array[2]);
    }

    private String make2digit(String num){ // make number 2 digit by adding 0 at front.
        return ( (num.length()<2) ? "0"+num : num );
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
    public static String getPassed(boolean isPassed){ return (isPassed ? "Pass": "Fail"); }


    public enum MOS{
        Moderate(0,"Moderate"),
        Significant(1,"Significant"),
        Heavy(2,"Heavy");

        final private int id; // contains string resources id.
        final private String str; // contains default string.
        MOS(int id, String str){ this.id=id; this.str=str; } // constructor & setter.
        public String toString(){ return str; }
    }

}
