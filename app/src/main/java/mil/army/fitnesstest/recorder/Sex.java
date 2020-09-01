package mil.army.fitnesstest.recorder;

public enum Sex{
    Male(),
    Female();

    Sex(){} // constructor
    public static Sex valueOf(int ordinal){ return values()[ordinal]; }
}
