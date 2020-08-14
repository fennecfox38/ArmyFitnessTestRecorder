package army.prt.recorder.acft.event;

public class CountFloatEvent extends Event{
    public String unit;
    public float raw = 0;
    public CountFloatEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }
}
