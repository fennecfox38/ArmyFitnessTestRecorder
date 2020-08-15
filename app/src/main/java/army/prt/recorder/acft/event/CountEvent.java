package army.prt.recorder.acft.event;

import java.util.HashMap;

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
        else if(raw>=150) return (((raw/10)-15)+62);
        else if(raw>=80) return (((raw/10)-8)*10);
        else return 0;
    }

    public static int SPTScore(int rawInt){
        HashMap<Integer,Integer> map = new HashMap<Integer, Integer>(){{
            put(124,99); put(123,98); put(122,98);  put(121,97); put(120,96); put(119,96);
            put(118,95); put(117,94); put(116,94);  put(115,93); put(114,92); put(113,92);
            put(112,91); put(111,90); put(110,90);  put(109,89); put(108,88); put(107,88);
            put(106,87); put(105,86); put(104,86);  put(103,85); put(102,84); put(101,84);
            put(100,83); put(99,82); put(98,82);    put(97,81); put(96,80); put(95,80);
            put(94,79); put(93,78); put(92,78);     put(91,77); put(90,76); put(89,76);
            put(88,75); put(87,74); put(86,74);     put(85,73); put(84,72); put(83,72);
            put(82,71); put(81,70); put(80,70);
        }};
        if(rawInt>=125) return 100;
        else if(rawInt>=80) {
            try{ return map.get(rawInt); }
            catch (NullPointerException e){ return 70; }
        }
        else if(rawInt>45){
            if(rawInt>=78) return 69;
            else if(rawInt>=75) return 68;
            else if(rawInt>=71) return 67;
            else if(rawInt>=68) return 66;
            else if(rawInt>=65) return 65;
            else if(rawInt>=62) return 64;
            else if(rawInt>=58) return 63;
            else if(rawInt>=54) return 62;
            else if(rawInt>=49) return 61;
            else return 60;
        }
        else if(rawInt>33) return ((rawInt-33)*5);
        else return 0;
    }

    public static int giveHPUScore(int raw){
        if(raw>=60) return 100;
        else if(raw>=30) return raw+40;
        else if(raw>=10) return (((raw-10)/2)+60);
        else if(raw>0) return (raw*5+10);
        else return 0;
    }

    public static int giveLTKScore(int raw){
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
