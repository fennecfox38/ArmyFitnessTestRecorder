package army.prt.recorder.log;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

import army.prt.recorder.abcp.ABCPRecord;

import static army.prt.recorder.log.ABCPDBHelper.DBContract.*;

public class ABCPDBHelper extends SQLiteOpenHelper {

    public ABCPDBHelper(Context context) {
        super(context, FileProvider.dbName, null, FileProvider.dbVersion);
    }

    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_TBL); }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TBL); db.execSQL(SQL_CREATE_TBL);
    }
    @Override public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        db.execSQL(SQL_CREATE_TBL);
        return db;
    }
    @Override public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        db.execSQL(SQL_CREATE_TBL);
        return db;
    }

    public ArrayList<ABCPRecord> getRecordList(){
        ArrayList<ABCPRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ABCPRecord record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new ABCPRecord();
            record.stringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            record.sex = ABCPRecord.Sex.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            record.ageGroup = ABCPRecord.AgeGroup.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            record.height = cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT));
            record.weight = cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT));
            record.neck = cursor.getFloat(cursor.getColumnIndex(COLUMN_NECK));
            record.abdomen_waist = cursor.getFloat(cursor.getColumnIndex(COLUMN_ABDOMEN_WAIST));
            float hips = 0.f;
            try{ hips = cursor.getFloat(cursor.getColumnIndex(COLUMN_HIPS)); } //catch (Exception e){ hips = 0.f; }
            finally{ record.hips = hips; }
            record.bodyFatPercentage = cursor.getFloat(cursor.getColumnIndex(COLUMN_BODY_FAT_PERCENT));
            record.height_weight = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_HW_PASSED)));
            record.bodyFatPass = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_BODY_FAT_PASSED)));
            record.totalPass = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PASSED)));
            list.add(record);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void saveRecordList(ArrayList<ABCPRecord> list){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();          //clear the table first
            db.delete(TABLE_NAME,null,null);
            for(ABCPRecord record : list)   //go through the list and add one by one
                db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void insertRecord(ABCPRecord record){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();  // add one by one
            db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void deleteRecord(ABCPRecord record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SEX,record.sex.toString()) + "AND " + sqlWhere(COLUMN_AGE_GROUP,record.ageGroup.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_HEIGHT,record.height) + "AND " + sqlWhere(COLUMN_WEIGHT,record.weight) + "AND ";
        sqlExec += sqlWhere(COLUMN_NECK,record.neck) + "AND " + sqlWhere(COLUMN_ABDOMEN_WAIST,record.abdomen_waist) + "AND ";
        sqlExec += sqlWhere(COLUMN_HIPS,record.hips) + "AND " + sqlWhere(COLUMN_BODY_FAT_PERCENT,record.bodyFatPercentage) + "AND ";
        sqlExec += sqlWhere(COLUMN_HW_PASSED,Boolean.toString(record.height_weight)) + "AND ";
        sqlExec += sqlWhere(COLUMN_BODY_FAT_PASSED,Boolean.toString(record.bodyFatPass)) + "AND ";
        sqlExec += sqlWhere(COLUMN_TOTAL_PASSED,Boolean.toString(record.totalPass));
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
        row.createCell(1).setCellValue(COLUMN_SEX);
        row.createCell(2).setCellValue(COLUMN_AGE_GROUP);
        row.createCell(3).setCellValue(COLUMN_HEIGHT);
        row.createCell(4).setCellValue(COLUMN_WEIGHT);
        row.createCell(5).setCellValue(COLUMN_NECK);
        row.createCell(6).setCellValue(COLUMN_ABDOMEN_WAIST);
        row.createCell(7).setCellValue(COLUMN_HIPS);
        row.createCell(8).setCellValue(COLUMN_BODY_FAT_PERCENT);
        row.createCell(9).setCellValue(COLUMN_HW_PASSED);
        row.createCell(10).setCellValue(COLUMN_BODY_FAT_PASSED);
        row.createCell(11).setCellValue(COLUMN_TOTAL_PASSED);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        int rowIndex = 1;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            row.createCell(1).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            row.createCell(2).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            row.createCell(3).setCellValue(preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT))));
            row.createCell(4).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT)));
            row.createCell(5).setCellValue(preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_NECK))));
            row.createCell(6).setCellValue(preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_ABDOMEN_WAIST))));
            float hips = 0.f;
            try { hips = cursor.getFloat(cursor.getColumnIndex(COLUMN_HIPS)); } //catch (Exception e){ hips = 0.f; }
            finally{ row.createCell(7).setCellValue(hips); }
            row.createCell(8).setCellValue(preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_BODY_FAT_PERCENT))));
            row.createCell(9).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_HW_PASSED))));
            row.createCell(10).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_BODY_FAT_PASSED))));
            row.createCell(11).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PASSED))));
        }

        cursor.close();
        db.close();
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }
    private static String sqlWhere(String column, float arg){ return ("abs("+column+"-"+ arg +")<0.01 "); }
    private static double preciseFloat(float obj){ return (double)(Math.floor(obj*10)/10); }

    public static final class DBContract implements BaseColumns {
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

}
