package mil.army.fitnesstest.recorder.acft.event;

import mil.army.fitnesstest.Standard;
import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.acft.Level;

public class DurationEvent extends Event {
    public Duration duration;
    public CardioAlter cardioAlter = CardioAlter.RUN;

    public DurationEvent(int eventType, String title, int max){
        super(eventType, title, max);
        duration = new Duration(0,0);
    }

    @Override public void giveScore(){
        if(eventType == SDC)
            sco = Standard.ACFT.SDCScore(duration);
        else if(cardioAlter== CardioAlter.RUN)
            sco = Standard.ACFT.RUNScore(duration);
        else sco = AlterScore(duration);
        giveLevel();
    }

    @Override public void giveLevel(){
        if(cardioAlter == CardioAlter.RUN) super.giveLevel();
        else level = (sco>=60 ? Level.Pass : Level.Fail);
    }

    public static int AlterScore(Duration duration) { return (duration.compareTo(25,00)<=0 ? 60 : 0); }
}