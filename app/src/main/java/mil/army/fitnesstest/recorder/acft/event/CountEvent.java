package mil.army.fitnesstest.recorder.acft.event;

import mil.army.fitnesstest.Standard;

public class CountEvent extends Event {
    public String unit;
    public int raw = 0;
    public CountEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }

    @Override public void giveScore(){
        switch (eventType){
            case Event.MDL: sco = Standard.ACFT.MDLScore(raw); break;
            case Event.SPT: sco = Standard.ACFT.SPTScore(raw);break;
            case Event.HPU: sco = Standard.ACFT.HPUScore(raw); break;
            case Event.LTK: sco = Standard.ACFT.LTKScore(raw); break;
        }
        giveLevel();
    }
}
