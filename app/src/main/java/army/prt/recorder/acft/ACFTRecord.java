package army.prt.recorder.acft;

import android.content.ContentValues;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;

import army.prt.recorder.R;
import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.log.ACFTDBHelper;

public class ACFTRecord{
    public int[] sco = {0, 0, 0, 0, 0, 0};
    public int raw_0 = 0, raw_2 = 0, raw_4 = 0; public float raw_1 = 0;
    public Duration raw_3, raw_5;
    public int cardioAlter = 0, sco_total=0, qualifiedLevel = 0;
    public int year, month, day;

    public ACFTRecord(){
        raw_3 = new Duration(0);
        raw_5 = new Duration(0);
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH)+1;
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    public void updateRecord(ArrayList<Event> eventList){
        sco_total = 0; qualifiedLevel = Event.HEAVY;
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

            if(sco[event.eventType]<60) qualifiedLevel = Event.FAIL;
            else if(sco[event.eventType]<65){ if(qualifiedLevel > Event.MODERATE) qualifiedLevel = Event.MODERATE; }
            else if(sco[event.eventType]<70){ if(qualifiedLevel > Event.SIGNIFICANT) qualifiedLevel = Event.SIGNIFICANT; }
            //else // nothing need to do for over 70.
            sco_total += sco[event.eventType];
        }
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
        }
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

    public ContentValues getContentValues(Resources resources){
        ContentValues cv = new ContentValues();
        cv.put(ACFTDBHelper.DBContract.COLUMN_RECORD_DATE,dateToString());
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_MDL,raw_0);
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_MDL,sco[0]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_SPT,raw_1);
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_SPT,sco[1]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_HPU,raw_2);
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_HPU,sco[2]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_SDC,raw_3.toString());
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_SDC,sco[3]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_LTK,raw_4);
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_LTK,sco[4]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_RAW_CARDIO,raw_5.toString());
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_CARDIO,sco[5]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_CARDIO_ALTER,resources.getStringArray(R.array.CardioEvent)[cardioAlter]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_QUALIFIED_LEVEL,resources.getStringArray(R.array.Level)[qualifiedLevel]);
        cv.put(ACFTDBHelper.DBContract.COLUMN_SCORE_TOTAL,sco_total);
        return cv;
    }

}
