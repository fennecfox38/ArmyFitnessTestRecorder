package army.prt.recorder.acft.event;

public class CountFloatEvent extends Event{
    public String unit;
    public float raw = 0;
    public CountFloatEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }
    public void giveScore(){
        sco = SPTScore((int)(raw*10));
    }
    public static int SPTScore(int rawInt){
        if(rawInt>=125) return 100;
        else if(rawInt>45) return 60;
        else if(rawInt>33) return ((rawInt-33)*5);
        else return 0;
    }
}
