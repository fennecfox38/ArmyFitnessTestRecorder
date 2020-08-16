package army.prt.recorder.acft;

import java.util.ArrayList;
import java.util.Calendar;

import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;

public class ACFTRecord{
    public int raw_MDL=0, raw_HPU=0, raw_LTK=0, cardio_Alter = 0; public float raw_SPT=0.0f;
    public Duration duration_SDC, duration_Cardio; public Calendar dateRecord;
    public int sco_MDL=0, sco_SPT=0, sco_HPU=0, sco_SDC=0, sco_LTK=0, sco_Cardio=0, sco_total=0;
    public int qualifiedLevel = 0;

    ACFTRecord(){
        duration_SDC = new Duration(0,0);
        duration_Cardio = new Duration(0,0);
        dateRecord = Calendar.getInstance();
    }

    public void updateRecord(ArrayList<Event> eventList){
        CountEvent countEvent = (CountEvent) eventList.get(Event.MDL);
        raw_MDL = countEvent.raw;
        sco_MDL = countEvent.sco;
        countEvent = (CountEvent) eventList.get(Event.SPT);
        raw_SPT = (countEvent.raw/10.0f);
        sco_SPT = countEvent.sco;
        countEvent = (CountEvent) eventList.get(Event.HPU);
        raw_HPU = countEvent.raw;
        sco_HPU = countEvent.sco;
        countEvent = (CountEvent) eventList.get(Event.LTK);
        raw_LTK = countEvent.raw;
        sco_LTK = countEvent.sco;
        DurationEvent durationEvent = (DurationEvent) eventList.get(Event.SDC);
        duration_SDC = durationEvent.duration;
        sco_SDC = durationEvent.sco;
        durationEvent = (DurationEvent) eventList.get(Event.CARDIO);
        duration_Cardio = durationEvent.duration;
        sco_Cardio = durationEvent.sco;
        cardio_Alter = durationEvent.cardioAlter;

        sco_total = sco_MDL + sco_SPT + sco_HPU + sco_SDC + sco_LTK + sco_Cardio;

        if(sco_MDL>=70&&sco_SPT>=70&&sco_HPU>=70&&sco_SDC>=70&&sco_LTK>=70&&sco_Cardio>=70) qualifiedLevel = Event.HEAVY;
        else if(sco_MDL>=65&&sco_SPT>=65&&sco_HPU>=65&&sco_SDC>=65&&sco_LTK>=65&&sco_Cardio>=65) qualifiedLevel = Event.SIGNIFICANT;
        else if(sco_MDL>=60&&sco_SPT>=60&&sco_HPU>=60&&sco_SDC>=60&&sco_LTK>=60&&sco_Cardio>=60) qualifiedLevel = Event.MODERATE;
        else qualifiedLevel = Event.FAIL;

    }

}
