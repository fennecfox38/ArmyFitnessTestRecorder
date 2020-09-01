package mil.army.fitnesstest.recorder.apft.event;

import mil.army.fitnesstest.Standard;

public class CountEvent extends Event {
    public int raw = 0;

    public CountEvent(int eventType, String title, int max) {
        super(eventType, title, max);
    }

    @Override public void giveScore() {
        switch (eventType){
            case PU: sco = Standard.APFT.PUScore(sex, ageGroup, raw); break;
            case SU: sco = Standard.APFT.SUScore(sex, ageGroup, raw); break;
        }
    }
}
