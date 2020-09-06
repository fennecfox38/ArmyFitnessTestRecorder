package mil.army.fitnesstest;

public class Standard {

    public static final class ACFT{
        static { System.loadLibrary("acft-lib"); }
        public static native int MDLScore(int raw);
        public static native int SPTScore(int rawInt);
        public static native int HPUScore(int raw);
        public static native int SDCScore(int sec);
        public static native int LTKScore(int raw);
        public static native int RUNScore(int sec);
        public static int AlterScore(int sec){ return (sec<=1500 ? 60 : 0); }
    }

    public static final class APFT{
        static { System.loadLibrary("apft-lib"); }
        public static native int PUScore(int sex, int ageGroup, int raw);
        public static native int SUScore(int ageGroup, int raw);
        public static native int RUNScore(int sex, int ageGroup, int sec);
        public static native int WALKScore(int sex, int ageGroup, int sec);
        public static native int BIKEScore(int sex, int ageGroup, int sec);
        public static native int SWIMScore(int sex, int ageGroup, int sec);
    }

    public static final class ABCP{
        static { System.loadLibrary("abcp-lib"); }
        public static native boolean isHWPassed(int sex, int ageGroup, float height, int weight);
        public static native float maleBodyFat(double height, double neck, double abdomen);
        public static native float femaleBodyFat(double height, double neck, double waist, double hip);
        public static native boolean isBodyFatPassed(int sex, int ageGroup, float percentage);
    }

}
