package mil.army.fitnesstest;

import java.util.HashMap;

import mil.army.fitnesstest.recorder.apft.APFTRecord;
import mil.army.fitnesstest.recorder.Duration;
import mil.army.fitnesstest.recorder.Sex;

public class Standard {
    public static final class ACFT{

        public static int MDLScore(int raw){
            if(raw>=340) return 100;
            else if(raw>=330) return 97;
            else if(raw>=190) return ((((raw/10)-19)*2)+68);
            else if(raw>=150) return (((raw/10)-15)+62);
            else if(raw>=80) return (((raw/10)-8)*10);
            else return 0;
        }

        public static int SPTScore(int rawInt){
            HashMap<Integer,Integer> map = new HashMap<Integer, Integer>(){{
                put(124,99); put(123,98); put(122,98);  put(121,97); put(120,96); put(119,96);
                put(118,95); put(117,94); put(116,94);  put(115,93); put(114,92); put(113,92);
                put(112,91); put(111,90); put(110,90);  put(109,89); put(108,88); put(107,88);
                put(106,87); put(105,86); put(104,86);  put(103,85); put(102,84); put(101,84);
                put(100,83); put(99,82); put(98,82);    put(97,81); put(96,80); put(95,80);
                put(94,79); put(93,78); put(92,78);     put(91,77); put(90,76); put(89,76);
                put(88,75); put(87,74); put(86,74);     put(85,73); put(84,72); put(83,72);
                put(82,71); put(81,70); put(80,70);
            }};
            if(rawInt>=125) return 100;
            else if(rawInt>=80) {
                try{ return map.get(rawInt); }
                catch (NullPointerException e){ return 70; }
            }
            else if(rawInt>45){
                if(rawInt>=78) return 69;
                else if(rawInt>=75) return 68;
                else if(rawInt>=71) return 67;
                else if(rawInt>=68) return 66;
                else if(rawInt>=65) return 65;
                else if(rawInt>=62) return 64;
                else if(rawInt>=58) return 63;
                else if(rawInt>=54) return 62;
                else if(rawInt>=49) return 61;
                else return 60;
            }
            else if(rawInt>33) return ((rawInt-33)*5);
            else return 0;
        }

        public static int HPUScore(int raw){
            if(raw>=60) return 100;
            else if(raw>=30) return raw+40;
            else if(raw>=10) return (((raw-10)/2)+60);
            else if(raw>0) return (raw*5+10);
            else return 0;
        }

        public static int SDCScore(Duration duration){
            if(duration.compareTo(1,33)<=0) return 100;
            else if(duration.compareTo(1,39)<=0) return (98+((Duration.totalInSec(1,39)-duration.getTotalInSec())/3));
            else if(duration.compareTo(1,45)<=0) return (95+((Duration.totalInSec(1,45)-duration.getTotalInSec())/2));
            else if(duration.compareTo(2,10)<=0) return (70+(Duration.totalInSec(2,10)-duration.getTotalInSec()));
            else if(duration.compareTo(2,30)<=0) return (65+((Duration.totalInSec(2,30)-duration.getTotalInSec())/4));
            else if(duration.compareTo(2,50)<=0) return (61+((Duration.totalInSec(2,50)-duration.getTotalInSec())/5));
            else if(duration.compareTo(3,0)<=0) return 60;
            else if(duration.compareTo(3,10)<=0) return (50+(Duration.totalInSec(3,10)-duration.getTotalInSec()));
            else if(duration.compareTo(3,34)<=0) return ((Duration.totalInSec(3,35)-duration.getTotalInSec())*2);
            else return 0;
        }

        public static int LTKScore(int raw){
            if(raw>=20) return 100;
            else if(raw>=5) return ((raw-5)*2+70);
            else{
                switch (raw){
                    case 4: return 67;
                    case 3: return 65;
                    case 2: return 63;
                    case 1: return 60;
                    default: return 0;
                }
            }
        }

        public static int RUNScore(Duration duration){
            if(duration.compareTo(13,30)<=0) return 100;
            else if(duration.compareTo(18,0)<=0) return (70+((Duration.totalInSec(18,0)-duration.getTotalInSec())/9));
            else if(duration.compareTo(19,0)<=0) return (65+((Duration.totalInSec(19,0)-duration.getTotalInSec())/12));
            else if(duration.compareTo(21,0)<=0) return (60+((Duration.totalInSec(21,0)-duration.getTotalInSec())/24));
            else if(duration.compareTo(21,9)<=0) return (55+((Duration.totalInSec(21,9)-duration.getTotalInSec())/2));
            else if(duration.compareTo(21,18)<=0) return (50+((Duration.totalInSec(21,18)-duration.getTotalInSec())/2));
            else if(duration.compareTo(21,27)<=0) return (45+((Duration.totalInSec(21,27)-duration.getTotalInSec())/2));
            else if(duration.compareTo(21,36)<=0) return (40+((Duration.totalInSec(21,36)-duration.getTotalInSec())/2));
            else if(duration.compareTo(21,45)<=0) return (35+((Duration.totalInSec(21,45)-duration.getTotalInSec())/2));
            else if(duration.compareTo(21,54)<=0) return (30+((Duration.totalInSec(21,54)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,3)<=0) return (25+((Duration.totalInSec(22,3)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,12)<=0) return (20+((Duration.totalInSec(22,12)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,21)<=0) return (15+((Duration.totalInSec(22,21)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,30)<=0) return (10+((Duration.totalInSec(22,30)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,39)<=0) return (5+((Duration.totalInSec(22,39)-duration.getTotalInSec())/2));
            else if(duration.compareTo(22,46)<=0) return ((Duration.totalInSec(22,48)-duration.getTotalInSec())/2);
            else return 0;
        }
    }

    public static final class APFT{
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
        public static final int[][][] HEIGHTWEIGHT = new int[][][]{
                {{58,91,0,0,0,0}, {59,94,0,0,0,0},
                        {60,97,132,136,139,141}, {61,100,136,140,144,146}, {62,104,141,144,148,150},
                        {63,107,145,149,153,155}, {64,110,150,154,158,160}, {65,114,155,159,163,165},
                        {66,117,160,163,168,170}, {67,121,165,169,174,176}, {68,125,170,174,179,181},
                        {69,128,175,179,184,186}, {70,132,180,185,189,192}, {71,136,185,189,194,197},
                        {72,140,190,195,200,203}, {73,144,195,200,205,208}, {74,148,201,206,211,214},
                        {75,152,206,212,217,220}, {76,156,212,217,223,226}, {77,160,218,223,229,232},
                        {78,164,223,229,235,238}, {79,168,229,235,241,244}, {80,173,234,240,247,250}},
                {{58,91,19,121,122,124}, {59,94,124,125,126,128},
                        {60,97,128,129,131,133}, {61,100,132,134,135,137}, {62,104,136,138,140,142},
                        {63,107,141,143,144,146}, {64,110,145,147,149,151}, {65,114,150,152,154,156},
                        {66,117,155,156,158,161}, {67,121,159,161,163,166}, {68,125,164,166,168,171},
                        {69,128,169,171,173,176}, {70,132,174,176,178,181}, {71,136,179,181,183,186},
                        {72,140,184,186,188,191}, {73,144,189,191,194,197}, {74,148,194,197,199,202},
                        {75,152,200,202,204,208}, {76,156,205,207,210,213}, {77,160,210,213,215,219},
                        {78,164,216,218,221,225}, {79,168,221,224,227,230}, {80,173,227,230,233,236}}
        };
        public static final int[][] BODYFAT = new int[][]{ {20,22,24,26}, {30,32,34,36} };
        public static boolean isHWPassed(int sex, int ageGroup, float height, int weight){
            int index = ((int) Math.ceil(height))-58;
            if(index>=23){
                int[] table = HEIGHTWEIGHT[sex][22];
                int increment = (index-22)*(sex==0 ? 6 : 5);
                return (weight>=(table[1]+increment) && weight <=(table[ageGroup+2]+increment));
            }
            else if(index>=0){
                int[] table = HEIGHTWEIGHT[sex][index];
                return (weight>=table[1] && weight<=table[ageGroup+2]);
            }
            else return false;
        }
        public static float maleBodyFat(double height, double neck, double abdomen){
            double res = ((86.010 * Math.log10(abdomen-neck)) - (70.041 * Math.log10(height)) + 36.76);
            return ((float) (res>=0 ? res : 0));
        }
        public static float femaleBodyFat(double height, double neck, double waist, double hip){
            double res = ((163.205 * Math.log10(waist+hip-neck)) - (97.684 * Math.log10(height)) - 78.387);
            return ((float) (res>=0 ? res : 0));
        }
        public static boolean isBodyFatPassed(int sex, int ageGroup, float percentage){
            return(percentage<=BODYFAT[sex][ageGroup]);
        }
    }
}
