package mil.army.fitnesstest.recorder.apft.event;

import java.util.HashMap;

public enum APFTCardioAlter {
    RUN("2 Mile Run"),
    WALK("2.5 Mile Walk"),
    BIKE("6.2 Mile Bike"),
    SWIM("800 Yard Swim");

    private String str; // contains default string.
    APFTCardioAlter(String str){this.str=str;} // constructor & setter.
    public String toString(){return str;}
    private static HashMap<String, APFTCardioAlter> map = new HashMap<String, APFTCardioAlter>(){{
        put("2 Mile Run",RUN); put("2.5 Mile Walk",WALK); put("6.2 Mile Bike",BIKE); put("800 Yard Swim",SWIM);
    }};
    public static APFTCardioAlter findById(int id){ return values()[id]; }
    public static APFTCardioAlter findByString(String str){
        return map.get(str);
        /*switch(str){
            case "2 Mile Run": return RUN;
            case "2.5 Mile Walk": return WALK;
            case "6.2 Mile Bike": return BIKE;
            case "800 Yard Swim": return SWIM;
            default: return null;
        }*/
    }
}