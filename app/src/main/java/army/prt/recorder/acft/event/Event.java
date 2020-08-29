package army.prt.recorder.acft.event;

public class Event {
    public final static int MDL=0, SPT=1, HPU=2, SDC=3, LTK=4, CARDIO=5;
    public String title;
    public int eventType, max, sco = 0;

    public Event(int eventType, String title, int max){
        this.eventType = eventType;
        this.title = title;
        this.max = max;
    }
}