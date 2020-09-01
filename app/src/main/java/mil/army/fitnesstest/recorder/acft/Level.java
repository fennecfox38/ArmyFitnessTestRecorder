package mil.army.fitnesstest.recorder.acft;

public enum Level {
    Fail(),
    Moderate(),
    Significant(),
    Heavy(),
    Pass();

    Level(){ } // constructor
    public boolean isPassed(){ return (this!=Fail); }
    public static Level valueOf(int ordinal){ return values()[ordinal]; }
}
