package mil.army.fitnesstest.recorder.acft;

public enum CardioAlter {
    RUN(0,"2 Mile Run"),
    ROW(1,"5000 M Row"),
    BIKE(2,"15000 M Bike"),
    SWIM(3,"1000 M Swim");

    private int id; // contains id.
    private String str; // contains default string.
    CardioAlter(int id, String str){this.id=id; this.str=str;} // constructor & setter.
    public String toString(){return str;}

    public static CardioAlter findById(int id){
        switch(id){
            case 0: return RUN;
            case 1: return ROW;
            case 2: return BIKE;
            case 3: return SWIM;
        }
        return null;
    }
    public static CardioAlter findByString(String str){
        if(str.equals(RUN.str)) return RUN;
        else if(str.equals(ROW.str)) return ROW;
        else if(str.equals(BIKE.str)) return BIKE;
        else if(str.equals(SWIM.str)) return SWIM;
        else return null;
    }
}