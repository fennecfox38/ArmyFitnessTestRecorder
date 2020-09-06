package mil.army.fitnesstest.recorder.apft.event;

import mil.army.fitnesstest.Standard;

public class CountAPFTEvent extends APFTEvent {
    public int raw = 0;

    public CountAPFTEvent(int eventType, String title, int max) {
        super(eventType, title, max);
    }

    @Override public void giveScore(){
        if(eventType==PU) sco = Standard.APFT.PUScore(sex.ordinal(), ageGroup.ordinal(), raw);
        else sco = Standard.APFT.SUScore(ageGroup.ordinal(), raw);
    }
}
