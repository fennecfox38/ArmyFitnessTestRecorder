package mil.army.fitnesstest.recorder.apft;

import android.provider.BaseColumns;

public class APFTDBContract implements BaseColumns {
    public static final String TABLE_NAME = "APFTRecord";
    public static final String COLUMN_RECORD_DATE = "RecordDate";
    public static final String COLUMN_RAW_PU = "PURaw", COLUMN_RAW_SU = "SURaw", COLUMN_RAW_CARDIO = "CardioRaw";
    public static final String COLUMN_SCORE_PU = "PUScore", COLUMN_SCORE_SU = "SUScore", COLUMN_SCORE_CARDIO = "CardioScore";
    public static final String COLUMN_CARDIO_ALTER = "CardioAlter", COLUMN_SEX = "Sex", COLUMN_AGE_GROUP = "AgeGroup";
    public static final String COLUMN_SCORE_TOTAL = "ScoreTotal", COLUMN_IS_PASSED = "isPassed";

    public static final String SQL_CREATE_TBL="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            //_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            COLUMN_RECORD_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
            COLUMN_RAW_PU+" INTEGER NOT NULL,"+ COLUMN_SCORE_PU+" INTEGER NOT NULL,"+
            COLUMN_RAW_SU+" INTEGER NOT NULL,"+ COLUMN_SCORE_SU+" INTEGER NOT NULL,"+
            COLUMN_RAW_CARDIO+" TEXT NOT NULL,"+ COLUMN_SCORE_CARDIO+" INTEGER NOT NULL,"+
            COLUMN_CARDIO_ALTER+" TEXT NOT NULL,"+
            COLUMN_SEX+" TEXT NOT NULL,"+ COLUMN_AGE_GROUP+" TEXT NOT NULL,"+
            COLUMN_SCORE_TOTAL+" INTEGER NOT NULL,"+ COLUMN_IS_PASSED+" TEXT NOT NULL)";
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    public static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+TABLE_NAME+
            "("+COLUMN_RECORD_DATE+", "+
            COLUMN_RAW_PU+", "+COLUMN_SCORE_PU+", "+ COLUMN_RAW_SU+", "+COLUMN_SCORE_SU+", "+
            COLUMN_RAW_CARDIO+", "+COLUMN_SCORE_CARDIO+", "+COLUMN_CARDIO_ALTER+", "+
            COLUMN_SEX+", "+COLUMN_AGE_GROUP+", "+ COLUMN_SCORE_TOTAL+", "+ COLUMN_IS_PASSED+") VALUES";
    public static final String SQL_DELETE_WHERE = "DELETE FROM " + TABLE_NAME + " WHERE ";
    public static final String SQL_DELETE_ALL = "DELETE FROM " + TABLE_NAME;
}
