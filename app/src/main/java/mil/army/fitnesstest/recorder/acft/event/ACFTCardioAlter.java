package mil.army.fitnesstest.recorder.acft.event;

public enum ACFTCardioAlter {
    RUN("2 Mile Run"),
    ROW("5000 M Row"),
    BIKE("15000 M Bike"),
    SWIM("1000 M Swim");

    private String str; // contains default string.
    ACFTCardioAlter(String str){this.str=str;} // constructor & setter.
    public String toString(){return str;}

    public static ACFTCardioAlter valueOf(int ordinal){ return values()[ordinal]; }
    public static ACFTCardioAlter findByString(String str){
        switch (str){
            case "2 Mile Run": return RUN;
            case "5000 M Row": return ROW;
            case "15000 M Bike": return BIKE;
            case "1000 M Swim": return SWIM;
            default: return null;
        }
    }
}