package army.prt.recorder.acft.event;

import army.prt.recorder.acft.Duration;

public class DurationEvent extends Event{
    public static final int RUN=0,ROW=1,BIKE=2,SWIM=3;
    public Duration duration;
    public int cardioAlter = -1;
    public DurationEvent(int eventType, String title, int max){
        super(eventType, title, max);
        duration = new Duration(0,0);
        if(eventType==CARDIO) cardioAlter = 0;
    }
    public void giveScore(){
        sco=60;
    }
}