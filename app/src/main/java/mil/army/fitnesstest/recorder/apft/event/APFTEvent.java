package mil.army.fitnesstest.recorder.apft.event;

import mil.army.fitnesstest.recorder.apft.APFTRecord;
import mil.army.fitnesstest.recorder.Sex;

public abstract class APFTEvent {
    public final static int PU=0, SU=1, CARDIO=2;
    public String title; public Sex sex=Sex.Male; public APFTRecord.AgeGroup ageGroup = APFTRecord.AgeGroup._17_21;
    public int eventType, max, sco = 0;

    public APFTEvent(int eventType, String title, int max){
        this.eventType = eventType;
        this.title = title;
        this.max = max;
    }

    public abstract void giveScore();
    public boolean isPassed(){ return (sco>=60); }
    public String getPassed(){ return (sco>=60? "Pass":"Fail"); }
}
