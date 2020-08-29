package army.prt.recorder.abcp;

import java.util.ArrayList;
import java.util.Calendar;

import army.prt.recorder.Standard;

public class ABCPRecord {
    public Sex sex = Sex.Male; public AgeGroup ageGroup= AgeGroup._17_20;
    public int year, month, day;
    public float height=58.0f, neck=10.0f, abdomen_waist =20.0f, hips=20.0f, bodyFatPercentage; public int weight=90;
    public boolean height_weight = false, bodyFatPass = true, totalPass = false;

    public ABCPRecord(){
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH)+1;
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    public void updateRecord(ArrayList<Item> items){
        for(Item item : items){
            switch(item.itemType){
                case Item.HEIGHT: height = ((item.raw)/2.f + 58); break;
                case Item.WEIGHT: weight = (item.raw+90); break;
                case Item.NECK: neck = ((item.raw)/2.f + 10); break;
                case Item.ABDOMEN_WAIST: abdomen_waist = ((item.raw)/2.f + 20); break;
                case Item.HIPS: hips = ((item.raw)/2.f + 20); break;
            }
        }
        invalidate();
    }
    public void restoreItemList(ArrayList<Item> items){
        for(Item item : items){
            switch(item.itemType){
                case Item.HEIGHT: item.raw = ((int) (height-58)*2); break;
                case Item.WEIGHT: item.raw = (weight-90); break;
                case Item.NECK: item.raw = ((int) (neck-10)*2); break;
                case Item.ABDOMEN_WAIST: item.raw = ((int) (abdomen_waist -20)*2); break;
                case Item.HIPS: item.raw = ((int) (hips-20)*2); break;
            }
        }
    }

    public void invalidate(){
        height_weight = Standard.ABCP.isHWPassed(sex.ordinal(),ageGroup.ordinal(),height,weight);
        bodyFatPercentage = (sex==Sex.Male ? Standard.ABCP.maleBodyFat(height,neck, abdomen_waist) : Standard.ABCP.femaleBodyFat(height,neck, abdomen_waist,hips));
        bodyFatPass = Standard.ABCP.isBodyFatPassed(sex.ordinal(), ageGroup.ordinal(), bodyFatPercentage);
        totalPass = height_weight && bodyFatPass;
    }


    public String dateToString(){
        String str = String.valueOf(year);
        str += "-"; str += make2digit(String.valueOf(month));
        str += "-"; str += make2digit(String.valueOf(day));
        return str;
    }

    public void stringToDate(String str){
        String[] array = str.split("-");
        year = Integer.parseInt(array[0]);
        month = Integer.parseInt(array[1]);
        day = Integer.parseInt(array[2]);
    }

    private String make2digit(String num){ // make number 2 digit by adding 0 at front.
        return ( (num.length()<2) ? "0"+num : num );
    }

    public enum Sex{
        Male(0,"Male"),
        Female(1,"Female");

        private int id; // contains id.
        private String str; // contains default string.
        Sex(int id, String str){this.id=id; this.str=str;} // constructor & setter.
        public String toString(){return str;}

        public static Sex valueOf(int id){ return (id==0 ? Male : Female); }
    }
    public enum AgeGroup{
        _17_20(0,"17-20"),
        _21_27(1,"21-27"),
        _28_39(2,"28-39"),
        _40_(3,"40+");

        private int id; // contains id.
        private String str; // contains default string.
        AgeGroup(int id, String str){this.id=id; this.str=str;} // constructor & setter.
        public String toString(){return str;}

        public static AgeGroup findById(int id){
            switch(id){
                case 0: return _17_20;
                case 1: return _21_27;
                case 2: return _28_39;
                case 3: return _40_;
            }
            return null;
        }
        public static AgeGroup findByString(String str){
            if(str.equals(_17_20.str)) return _17_20;
            else if(str.equals(_21_27.str)) return _21_27;
            else if(str.equals(_28_39.str)) return _28_39;
            else if(str.equals(_40_.str)) return _40_;
            else return null;
        }
    }
}
