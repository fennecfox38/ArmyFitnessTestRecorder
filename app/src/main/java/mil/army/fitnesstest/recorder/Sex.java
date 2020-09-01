package mil.army.fitnesstest.recorder;

import org.jetbrains.annotations.NotNull;

public enum Sex{
    Male(0,"Male"),
    Female(1,"Female");

    private int id; // contains id.
    private String str; // contains default string.
    Sex(int id, String str){this.id=id; this.str=str;} // constructor & setter.
    @NotNull
    public String toString(){return str;}

    public static Sex valueOf(int id){ return (id==0 ? Male : Female); }
}
