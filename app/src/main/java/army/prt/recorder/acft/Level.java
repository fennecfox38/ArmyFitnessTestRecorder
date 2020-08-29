package army.prt.recorder.acft;

public enum Level {
    Fail(0,"Fail"),
    Moderate(1,"Moderate"),
    Significant(2,"Significant"),
    Heavy(3,"Heavy");

    private int id; // contains id.
    private String str; // contains default string.
    Level(int id, String str){this.id=id; this.str=str;} // constructor & setter.
    public String toString(){return str;}

    public static Level findById(int id){
        switch(id){
            case 0: return Fail;
            case 1: return Moderate;
            case 2: return Significant;
            case 3: return Heavy;
        }
        return null;
    }
    /*public static Level findByString(String str){
        return Level.valueOf(str);
    }*/
}
