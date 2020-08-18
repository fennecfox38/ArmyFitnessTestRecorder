package army.prt.recorder.acft;

import java.util.ArrayList;
import java.util.Calendar;

import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;

public class ACFTRecord{
    public int[] raw = {0, 0, 0, 0, 0, 0}, sco = {0, 0, 0, 0, 0, 0};
    public int cardio_Alter = 0, sco_total=0, qualifiedLevel = 0;
    public int year, month, day;

    ACFTRecord(){
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH)+1;
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    public void updateRecord(ArrayList<Event> eventList){
        sco_total = 0; qualifiedLevel = Event.HEAVY;
        CountEvent countEvent;
        DurationEvent durationEvent;
        for(int i=0; i<6; ++i){
            switch (i){
                case Event.MDL: case Event.SPT: case Event.HPU: case Event.LTK:
                    countEvent = (CountEvent) eventList.get(i);
                    raw[i] = countEvent.raw;
                    sco[i] = countEvent.sco;
                    break;
                case Event.SDC: case Event.CARDIO:
                    durationEvent = (DurationEvent) eventList.get(i);
                    raw[i] = durationEvent.duration.getTotalInSec();
                    sco[i] = durationEvent.sco;
                    if(i == Event.CARDIO) cardio_Alter = durationEvent.cardioAlter;
                    break;
            }

            if(sco[i]<60) qualifiedLevel = Event.FAIL;
            else if(sco[i]<65){ if(qualifiedLevel > Event.MODERATE) qualifiedLevel = Event.MODERATE; }
            else if(sco[i]<70){ if(qualifiedLevel > Event.SIGNIFICANT) qualifiedLevel = Event.SIGNIFICANT; }
            //else // nothing need to do for over 70.
            sco_total += sco[i];
        }
    }

    public void restoreEventList(ArrayList<Event> eventList){
        for(int i=0; i<6; ++i){
            switch (i){
                case Event.MDL: case Event.SPT: case Event.HPU: case Event.LTK:
                    CountEvent countEvent = (CountEvent) eventList.get(i);
                    countEvent.raw = raw[i]; countEvent.sco = sco[i];
                    //eventList.set(i,countEvent);
                    break;
                case Event.SDC: case Event.CARDIO:
                    DurationEvent durationEvent = (DurationEvent) eventList.get(i);
                    durationEvent.duration.setTotalInSec(raw[i]);
                    durationEvent.sco = sco[i];
                    if(i == Event.CARDIO)  durationEvent.cardioAlter = cardio_Alter;
                    //eventList.set(i,durationEvent);
                    break;
            }
        }
        //return eventList;
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
}
