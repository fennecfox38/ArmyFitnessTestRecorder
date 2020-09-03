package mil.army.fitnesstest.recorder.abcp;

import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import mil.army.fitnesstest.Standard;
import mil.army.fitnesstest.recorder.Record;
import mil.army.fitnesstest.recorder.Sex;

import static mil.army.fitnesstest.recorder.abcp.ABCPDBContract.*;

public class ABCPRecord<T extends Item> extends Record<T> {
    public Sex sex = Sex.Male; public AgeGroup ageGroup= AgeGroup._17_20;
    public float height=58.0f, neck=10.0f, abdomen_waist =20.0f, hips=20.0f, bodyFatPercentage; public int weight=90;
    public boolean height_weight = false, bodyFatPass = true;

    public ABCPRecord(){ super(); }

    public void updateRecord(ArrayList<T> items){
        height = ((items.get(Item.HEIGHT).raw)/2.f + 58);
        weight = (items.get(Item.WEIGHT).raw+90);
        neck = ((items.get(Item.NECK).raw)/2.f + 10);
        abdomen_waist = ((items.get(Item.ABDOMEN_WAIST).raw)/2.f + 20);
        try{ hips = ((items.get(Item.HIPS).raw)/2.f + 20); }
        catch (Exception e) { e.printStackTrace(); }
    }
    public void restoreList(ArrayList<T> items){
        items.get(Item.HEIGHT).raw = ((int) (height-58)*2);
        items.get(Item.WEIGHT).raw = (weight-90);
        items.get(Item.NECK).raw = ((int) (neck-10)*2);
        items.get(Item.ABDOMEN_WAIST).raw = ((int) (abdomen_waist -20)*2);
        try{ items.get(Item.HIPS).raw = ((int) (hips-20)*2); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void invalidate(ArrayList<T> items){
        if(items!=null) updateRecord(items);
        height_weight = Standard.ABCP.isHWPassed(sex.ordinal(),ageGroup.ordinal(),height,weight);
        bodyFatPercentage = (sex==Sex.Male ? Standard.ABCP.maleBodyFat(height,neck, abdomen_waist) : Standard.ABCP.femaleBodyFat(height,neck, abdomen_waist,hips));
        bodyFatPass = Standard.ABCP.isBodyFatPassed(sex.ordinal(), ageGroup.ordinal(), bodyFatPercentage);
        isPassed = height_weight && bodyFatPass;
    }


    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECORD_DATE,dateToString());
        cv.put(COLUMN_SEX,sex.name());
        cv.put(COLUMN_AGE_GROUP,ageGroup.toString());
        cv.put(COLUMN_HEIGHT,height);
        cv.put(COLUMN_WEIGHT,weight);
        cv.put(COLUMN_NECK,neck);
        cv.put(COLUMN_ABDOMEN_WAIST,abdomen_waist);
        cv.put(COLUMN_HIPS,(sex==Sex.Female?hips:null));
        cv.put(COLUMN_BODY_FAT_PERCENT,bodyFatPercentage);
        cv.put(COLUMN_HW_PASSED,Boolean.toString(height_weight));
        cv.put(COLUMN_BODY_FAT_PASSED,Boolean.toString(bodyFatPass));
        cv.put(COLUMN_TOTAL_PASSED,Boolean.toString(isPassed));
        return cv;
    }

    public enum AgeGroup{
        _17_20("17–20"), _21_27("21–27"),
        _28_39("28–39"), _40_("40+");

        private String str; // contains default string.
        AgeGroup(String str){this.str=str;} // constructor & setter.
        @NotNull public String toString(){ return str; }
        private static HashMap<String,AgeGroup> map = new HashMap<String,AgeGroup>(){{
           put("17–20",_17_20);  put("21–27",_21_27); put("28–39",_28_39); put("40+",_40_);
        }};
        public static AgeGroup valueOf(int ordinal){ return values()[ordinal]; }
        public static AgeGroup findByString(String str){
            return map.get(str);
            /*switch (str){
                case "17–20": return _17_20;
                case "21–27": return _21_27;
                case "28–39": return _28_39;
                case "40+": return _40_;
                default: return null;
            }*/
        }
    }

    @NotNull @Override public String toString() {
        return "Record Date: " +dateToString() +
                "\nSex: " + sex.name() +
                "\nAge Group: " + ageGroup.toString() +
                "\nHeight: " + height + "inches\nWeight: " + weight +
                "lbs\nNeck: " + neck + "inches\n"+
                (sex==Sex.Male?"Abdomen: ":"Waist: ")+ abdomen_waist +
                "inches\nHips: " + hips + "inches\nBody Fat Percentage: " + bodyFatPercentage +
                "%\nHeight/Weight: " + getPassed(height_weight) +
                "\nBody Fat: " + getPassed(bodyFatPass) +
                "\nTotal: " + getPassed(isPassed);
    }
}
