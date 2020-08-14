package army.prt.recorder.acft.event;

import army.prt.recorder.acft.Duration;

public class DurationEvent extends Event{
    public Duration duration;
    public DurationEvent(int eventType, String title, int max){
        super(eventType, title, max);
        duration = new Duration(0,0);
    }
}