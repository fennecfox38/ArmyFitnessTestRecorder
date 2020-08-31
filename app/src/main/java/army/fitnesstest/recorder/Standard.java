package army.fitnesstest.recorder;

public class Standard {
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
