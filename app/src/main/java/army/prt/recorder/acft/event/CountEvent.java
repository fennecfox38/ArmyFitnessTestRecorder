package army.prt.recorder.acft.event;

public class CountEvent extends Event{
    public String unit;
    public int raw = 0;
    public CountEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }
}
