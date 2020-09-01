package mil.army.fitnesstest.recorder.apft.event;

public enum CardioAlter {
    RUN(0,"2 Mile Run"),
    WALK(1,"2.5 Mile Walk"),
    BIKE(2,"6.2 Mile Bike"),
    SWIM(3,"800 Yard Swim");

    private int id; // contains id.
    private String str; // contains default string.
    CardioAlter(int id, String str){this.id=id; this.str=str;} // constructor & setter.
    public String toString(){return str;}

    public static CardioAlter findById(int id){
        switch(id){
            case 0: return RUN;
            case 1: return WALK;
            case 2: return BIKE;
            case 3: return SWIM;
        }
        return null;
    }
    public static CardioAlter findByString(String str){
        if(str.equals(RUN.str)) return RUN;
        else if(str.equals(WALK.str)) return WALK;
        else if(str.equals(BIKE.str)) return BIKE;
        else if(str.equals(SWIM.str)) return SWIM;
        else return null;
    }
}