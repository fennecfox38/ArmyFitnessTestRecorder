package mil.army.fitnesstest;

import mil.army.fitnesstest.recorder.apft.APFTRecord;
import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Sex;

public class Standard {

    public static final class ACFT{
        static { System.loadLibrary("acft-lib"); }
        public static native int MDLScore(int raw);
        public static native int SPTScore(int rawInt);
        public static native int HPUScore(int raw);
        public static native int SDCScore(int min, int sec);
        public static native int LTKScore(int raw);
        public static native int RUNScore(int min, int sec);
        public static native int AlterScore(int min, int sec);
    }

    public static final class APFT{
        static { System.loadLibrary("apft-lib"); }
        public static int PUScore(Sex sex, APFTRecord.AgeGroup ageGroup, int raw){
            return 60;
        }
        public static int SUScore(Sex sex, APFTRecord.AgeGroup ageGroup, int raw){
            return 60;
        }
        public static int RUNScore(Sex sex, APFTRecord.AgeGroup ageGroup, Duration duration){
            return 60;
        }
        public static int WALKScore(Sex sex, APFTRecord.AgeGroup ageGroup, Duration duration){
            return 60;
        }
        public static int BIKEScore(Sex sex, APFTRecord.AgeGroup ageGroup, Duration duration){
            return 60;
        }
        public static int SWIMScore(Sex sex, APFTRecord.AgeGroup ageGroup, Duration duration){
            return 60;
        }
    }

    public static final class ABCP{
        static { System.loadLibrary("abcp-lib"); }
        public static native boolean isHWPassed(int sex, int ageGroup, float height, int weight);
        public static native float maleBodyFat(double height, double neck, double abdomen);
        public static native float femaleBodyFat(double height, double neck, double waist, double hip);
        public static native boolean isBodyFatPassed(int sex, int ageGroup, float percentage);
    }

}
