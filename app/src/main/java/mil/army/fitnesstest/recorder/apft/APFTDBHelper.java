package mil.army.fitnesstest.recorder.apft;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.FileProvider;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;

import static mil.army.fitnesstest.recorder.apft.APFTDBHelper.DBContract.*;


public class APFTDBHelper extends SQLiteOpenHelper {
    public APFTDBHelper(Context context) {
        super(context, FileProvider.dbName, null, FileProvider.dbVersion);
    }
    // onCreate might be called only when db file is not exist.
    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_TBL); }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TBL); db.execSQL(SQL_CREATE_TBL);
    }
    @Override public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        db.execSQL(SQL_CREATE_TBL); // before return readable database,
        return db; // create table if not exist.
    }
    @Override public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        db.execSQL(SQL_CREATE_TBL); // before return writable database,
        return db; // create table if not exist.
    }

    public ArrayList<APFTRecord<APFTEvent>> getRecordList(){
        ArrayList<APFTRecord<APFTEvent>> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        APFTRecord<APFTEvent> record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new APFTRecord<APFTEvent>();
            record.stringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            record.raw_PU = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_PU));
            record.raw_SU = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_SU));
            record.raw_Cardio.fromString(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_CARDIO)));
            record.sco[0] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_PU));
            record.sco[1] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SU));
            record.sco[2] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_CARDIO));
            record.cardioAlter = APFTCardioAlter.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            record.sex = Sex.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            record.ageGroup = APFTRecord.AgeGroup.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            record.sco_total = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL));
            record.isPassed = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED)));
            list.add(record);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void saveRecordList(ArrayList<APFTRecord<APFTEvent>> list){
        SQLiteDatabase db = getWritableDatabase();
        if(db==null) return;
        try{
            db.beginTransaction();
            for(APFTRecord<APFTEvent> record : list)
                db.insert(TABLE_NAME,null,record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void insertRecord(APFTRecord<APFTEvent> record){
        SQLiteDatabase db = getWritableDatabase();
        if(db==null) return;
        try{
            db.beginTransaction();  // add one by one
            db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void deleteRecord(APFTRecord<APFTEvent> record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_PU,record.raw_PU) + "AND " + sqlWhere(COLUMN_SCORE_PU,record.sco[0]) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_SU,record.raw_SU) + "AND " + sqlWhere(COLUMN_SCORE_SU,record.sco[1]) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_CARDIO,record.raw_Cardio.toString()) + "AND " + sqlWhere(COLUMN_SCORE_CARDIO,record.sco[2]) + "AND ";
        sqlExec += sqlWhere(COLUMN_CARDIO_ALTER,record.cardioAlter.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SEX,record.sex.name()) + "AND " + sqlWhere(COLUMN_AGE_GROUP,record.ageGroup.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_TOTAL,record.sco_total) + "AND " + sqlWhere(COLUMN_IS_PASSED,Boolean.toString(record.isPassed));

        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();
            db.execSQL(sqlExec);
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();
            db.execSQL(SQL_DELETE_ALL);
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void exportExcel(Workbook workbook){
        Sheet sheet = workbook.createSheet(TABLE_NAME);

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(COLUMN_RECORD_DATE);
        row.createCell(1).setCellValue(COLUMN_RAW_PU);
        row.createCell(2).setCellValue(COLUMN_SCORE_PU);
        row.createCell(3).setCellValue(COLUMN_RAW_SU);
        row.createCell(4).setCellValue(COLUMN_SCORE_SU);
        row.createCell(5).setCellValue(COLUMN_RAW_CARDIO);
        row.createCell(6).setCellValue(COLUMN_SCORE_CARDIO);
        row.createCell(7).setCellValue(COLUMN_CARDIO_ALTER);
        row.createCell(8).setCellValue(COLUMN_SEX);
        row.createCell(9).setCellValue(COLUMN_AGE_GROUP);
        row.createCell(10).setCellValue(COLUMN_SCORE_TOTAL);
        row.createCell(11).setCellValue(COLUMN_IS_PASSED);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        int rowIndex = 1;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()) {
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            row.createCell(1).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_PU)));
            row.createCell(2).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_PU)));
            row.createCell(3).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_SU)));
            row.createCell(4).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SU)));
            row.createCell(5).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_CARDIO)));
            row.createCell(6).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_CARDIO)));
            row.createCell(7).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            row.createCell(8).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            row.createCell(9).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            row.createCell(10).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL)));
            row.createCell(11).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED))));
        }

        cursor.close();
        db.close();
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }

    public static final class DBContract implements BaseColumns {
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
}
