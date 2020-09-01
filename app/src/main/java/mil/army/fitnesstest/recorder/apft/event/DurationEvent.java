package mil.army.fitnesstest.recorder.apft.event;

import mil.army.fitnesstest.Standard;
import mil.army.fitnesstest.recorder.Duration;

public class DurationEvent extends Event {
    public Duration duration;
    public CardioAlter cardioAlter = CardioAlter.RUN;

    public DurationEvent(int eventType, String title, int max) {
        super(eventType, title, max);
        duration = new Duration(0,0);
    }

    @Override public void giveScore() {
        switch (cardioAlter){
            case RUN: sco = Standard.APFT.RUNScore(sex, ageGroup, duration); break;
            case WALK: sco = Standard.APFT.WALKScore(sex, ageGroup, duration); break;
            case BIKE: sco = Standard.APFT.BIKEScore(sex, ageGroup, duration); break;
            case SWIM: sco = Standard.APFT.SWIMScore(sex, ageGroup, duration); break;
        }
    }
}
