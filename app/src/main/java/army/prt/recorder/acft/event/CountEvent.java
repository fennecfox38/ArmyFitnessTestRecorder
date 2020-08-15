package army.prt.recorder.acft.event;

public class CountEvent extends Event{
    public String unit;
    public int raw = 0;
    public CountEvent(int eventType, String title, int max, String unit) {
        super(eventType, title, max);
        this.unit = unit;
    }
    public void giveScore(){
        switch (eventType){
            case Event.MDL: sco = MDLScore(raw); break;
            case Event.SPT: sco = SPTScore(raw);break;
            case Event.HPU: sco = giveHPUScore(raw); break;
            case Event.LTK: sco = giveLTKScore(raw); break;
        }
    }
    public static int MDLScore(int raw){
        if(raw>=340) return 100;
        else if(raw>=330) return 97;
        else if(raw>=190) return ((((raw/10)-19)*2)+68);
        else if(raw>=180) return 65;
        else if(raw>=170) return 64;
        else if(raw>=160) return 63;
        else if(raw>=150) return 62;
        else if(raw>=80) return (((raw/10)-8)*10);
        else return 0;
    }
    public static int SPTScore(int rawInt){
        if(rawInt>=125) return 100;
        else if(rawInt>45) return 60;
        else if(rawInt>33) return ((rawInt-33)*5);
        else return 0;
    }
    public int giveHPUScore(int raw){
        if(raw>=60) return 100;
        else if(raw>=30) return raw+40;
        else if(raw>=10) return (((raw-10)/2)+60);
        else if(raw>0) return (raw*5+10);
        else return 0;
    }
    public int giveLTKScore(int raw){
        if(raw>=20) return 100;
        else if(raw>=5) return ((raw-5)*2+70);
        else{
            switch (raw){
                case 4: return 67;
                case 3: return 65;
                case 2: return 63;
                case 1: return 60;
                default: return 0;
            }
        }
    }
}
