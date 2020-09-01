package mil.army.fitnesstest.recorder.acft.event;

import mil.army.fitnesstest.recorder.acft.Level;

public abstract class Event {
    public final static int MDL=0, SPT=1, HPU=2, SDC=3, LTK=4, CARDIO=5;
    public String title; public Level level = Level.Fail;
    public int eventType, max, sco = 0;

    public Event(int eventType, String title, int max){
        this.eventType = eventType;
        this.title = title;
        this.max = max;
    }

    public abstract void giveScore();

    public void giveLevel() {
        if(sco>=70) level = Level.Heavy;
        else if(sco>=65) level = Level.Significant;
        else if(sco>=60) level = Level.Moderate;
        else level = Level.Fail;
    }
}