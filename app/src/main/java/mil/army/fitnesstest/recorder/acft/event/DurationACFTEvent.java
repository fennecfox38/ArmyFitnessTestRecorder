package mil.army.fitnesstest.recorder.acft.event;

import mil.army.fitnesstest.Standard;
import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.acft.Level;

public class DurationACFTEvent extends ACFTEvent {
    public Duration duration;
    public ACFTCardioAlter cardioAlter = ACFTCardioAlter.RUN;

    public DurationACFTEvent(int eventType, String title, int max){
        super(eventType, title, max);
        duration = new Duration(0,0);
    }

    @Override public void giveScore(){
        if(eventType == SDC)
            sco = Standard.ACFT.SDCScore(duration.getTotalInSec());
        else if(cardioAlter== ACFTCardioAlter.RUN)
            sco = Standard.ACFT.RUNScore(duration.getTotalInSec());
        else sco = Standard.ACFT.AlterScore(duration.getTotalInSec());
        giveLevel();
    }

    @Override public void giveLevel(){
        if(cardioAlter == ACFTCardioAlter.RUN) super.giveLevel();
        else level = (sco>=60 ? Level.Pass : Level.Fail);
    }
}