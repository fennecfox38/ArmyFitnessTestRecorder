package mil.army.fitnesstest.recorder.abcp;

import android.provider.BaseColumns;

public class ABCPDBContract implements BaseColumns {
    public static final String TABLE_NAME = "ABCPRecord";
    public static final String COLUMN_RECORD_DATE = "RecordDate", COLUMN_SEX = "Sex", COLUMN_AGE_GROUP = "AgeGroup";
    public static final String COLUMN_HEIGHT = "Height", COLUMN_WEIGHT = "Weight", COLUMN_NECK = "Neck";
    public static final String COLUMN_ABDOMEN_WAIST = "Abdomen_Waist", COLUMN_HIPS = "Hips", COLUMN_BODY_FAT_PERCENT = "BodyFatPercentage";
    public static final String COLUMN_HW_PASSED = "HeightWeightPassed", COLUMN_BODY_FAT_PASSED = "BodyFatPassed", COLUMN_TOTAL_PASSED = "TotalPassed";

    public static final String SQL_CREATE_TBL="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            //_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_RECORD_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
            COLUMN_SEX+" TEXT NOT NULL,"+ COLUMN_AGE_GROUP+" TEXT NOT NULL,"+
            COLUMN_HEIGHT+" FLOAT NOT NULL,"+ COLUMN_WEIGHT+" INTEGER NOT NULL,"+
            COLUMN_NECK+" FLOAT NOT NULL,"+ COLUMN_ABDOMEN_WAIST+" FLOAT NOT NULL,"+
            COLUMN_HIPS+" FLOAT,"+ COLUMN_BODY_FAT_PERCENT+" FLOAT NOT NULL,"+
            COLUMN_HW_PASSED+" TEXT NOT NULL,"+ COLUMN_BODY_FAT_PASSED+" TEXT NOT NULL,"+
            COLUMN_TOTAL_PASSED+" TEXT NOT NULL)";
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    public static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+TABLE_NAME+
            "("+COLUMN_RECORD_DATE+", "+
            COLUMN_SEX+", "+COLUMN_AGE_GROUP+", "+ COLUMN_HEIGHT+", "+COLUMN_WEIGHT+", "+
            COLUMN_NECK+", "+COLUMN_ABDOMEN_WAIST+", "+ COLUMN_HIPS+", "+COLUMN_BODY_FAT_PERCENT+", "+
            COLUMN_HW_PASSED+", "+COLUMN_BODY_FAT_PASSED+", "+ COLUMN_TOTAL_PASSED+") VALUES";
    public static final String SQL_DELETE_WHERE = "DELETE FROM " + TABLE_NAME + " WHERE ";
    public static final String SQL_DELETE_ALL = "DELETE FROM " + TABLE_NAME;
}
