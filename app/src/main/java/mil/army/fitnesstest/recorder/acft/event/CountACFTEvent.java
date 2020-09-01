package mil.army.fitnesstest.recorder.acft.event;

import mil.army.fitnesstest.Standard;

public class CountACFTEvent extends ACFTEvent {
    public String unit;
    public int raw = 0;
    public CountACFTEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }

    @Override public void giveScore(){
        switch (eventType){
            case ACFTEvent.MDL: sco = Standard.ACFT.MDLScore(raw); break;
            case ACFTEvent.SPT: sco = Standard.ACFT.SPTScore(raw);break;
            case ACFTEvent.HPU: sco = Standard.ACFT.HPUScore(raw); break;
            case ACFTEvent.LTK: sco = Standard.ACFT.LTKScore(raw); break;
        }
        giveLevel();
    }
}
