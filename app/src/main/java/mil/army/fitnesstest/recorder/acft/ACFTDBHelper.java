package mil.army.fitnesstest.recorder.acft;

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
import mil.army.fitnesstest.recorder.acft.event.CardioAlter;
import mil.army.fitnesstest.recorder.acft.event.Event;

import static mil.army.fitnesstest.recorder.acft.ACFTDBHelper.DBContract.*;

public class ACFTDBHelper extends SQLiteOpenHelper {

    public ACFTDBHelper(Context context) {
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

    public ArrayList<ACFTRecord<Event>> getRecordList(){
        ArrayList<ACFTRecord<Event>> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ACFTRecord<Event> record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new ACFTRecord<Event>();
            record.stringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            record.raw_0 = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_MDL));
            record.raw_1 = preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_RAW_SPT)));
            record.raw_2 = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_HPU));
            record.raw_3.fromString(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_SDC)));
            record.raw_4 = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_LTK));
            record.raw_5.fromString(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_CARDIO)));
            record.sco[0] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_MDL));
            record.sco[1] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SPT));
            record.sco[2] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_HPU));
            record.sco[3] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SDC));
            record.sco[4] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_LTK));
            record.sco[5] = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_CARDIO));
            record.cardioAlter = CardioAlter.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            record.qualifiedLevel = Level.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_QUALIFIED_LEVEL)));
            record.sco_total = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL));
            record.mos = ACFTRecord.MOS.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_MOS_REQUIREMENT)));
            record.isPassed = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED)));
            list.add(record);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void saveRecordList(ArrayList<ACFTRecord<Event>> list){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();          //clear the table first
            db.delete(TABLE_NAME,null,null);
            for(ACFTRecord<Event> record : list)   //go through the list and add one by one
                db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void insertRecord(ACFTRecord<Event> record){
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

    public void deleteRecord(ACFTRecord<Event> record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_MDL,record.raw_0) + "AND " + sqlWhere(COLUMN_RAW_SPT,record.raw_1) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_HPU,record.raw_2) + "AND " + sqlWhere(COLUMN_RAW_SDC,record.raw_3.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_LTK,record.raw_4) + "AND " + sqlWhere(COLUMN_RAW_CARDIO,record.raw_5.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_MDL,record.sco[0]) + "AND " + sqlWhere(COLUMN_SCORE_SPT,record.sco[1]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_HPU,record.sco[2]) + "AND " + sqlWhere(COLUMN_SCORE_SDC,record.sco[3]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_LTK,record.sco[4]) + "AND " + sqlWhere(COLUMN_SCORE_CARDIO,record.sco[5]) + "AND ";
        sqlExec += sqlWhere(COLUMN_CARDIO_ALTER,record.cardioAlter.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_QUALIFIED_LEVEL,record.qualifiedLevel.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_TOTAL,record.sco_total) + "AND " + sqlWhere(COLUMN_MOS_REQUIREMENT,record.mos.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_IS_PASSED,Boolean.toString(record.isPassed));
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
        row.createCell(1).setCellValue(COLUMN_RAW_MDL);
        row.createCell(2).setCellValue(COLUMN_SCORE_MDL);
        row.createCell(3).setCellValue(COLUMN_RAW_SPT);
        row.createCell(4).setCellValue(COLUMN_SCORE_SPT);
        row.createCell(5).setCellValue(COLUMN_RAW_HPU);
        row.createCell(6).setCellValue(COLUMN_SCORE_HPU);
        row.createCell(7).setCellValue(COLUMN_RAW_SDC);
        row.createCell(8).setCellValue(COLUMN_SCORE_SDC);
        row.createCell(9).setCellValue(COLUMN_RAW_LTK);
        row.createCell(10).setCellValue(COLUMN_SCORE_LTK);
        row.createCell(11).setCellValue(COLUMN_RAW_CARDIO);
        row.createCell(12).setCellValue(COLUMN_SCORE_CARDIO);
        row.createCell(13).setCellValue(COLUMN_CARDIO_ALTER);
        row.createCell(14).setCellValue(COLUMN_QUALIFIED_LEVEL);
        row.createCell(15).setCellValue(COLUMN_SCORE_TOTAL);
        row.createCell(16).setCellValue(COLUMN_MOS_REQUIREMENT);
        row.createCell(17).setCellValue(COLUMN_IS_PASSED);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        int rowIndex = 1;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            row.createCell(1).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_MDL)));
            row.createCell(2).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_MDL)));
            row.createCell(3).setCellValue(preciseDouble(cursor.getFloat(cursor.getColumnIndex(COLUMN_RAW_SPT))));
            row.createCell(4).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SPT)));
            row.createCell(5).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_HPU)));
            row.createCell(6).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_HPU)));
            row.createCell(7).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_SDC)));
            row.createCell(8).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_SDC)));
            row.createCell(9).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_LTK)));
            row.createCell(10).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_LTK)));
            row.createCell(11).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RAW_CARDIO)));
            row.createCell(12).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_CARDIO)));
            row.createCell(13).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            row.createCell(14).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_QUALIFIED_LEVEL)));
            row.createCell(15).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL)));
            row.createCell(16).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_MOS_REQUIREMENT)));
            row.createCell(17).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED))));
        }

        cursor.close();
        db.close();
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }
    private static String sqlWhere(String column, float arg){ return ("abs("+column+"-"+ arg +")<0.1 "); }
    private static double preciseDouble(float obj){ return (Math.round(obj*10)/10.0); }
    private static float preciseFloat(float obj){ return (Math.round(obj*10)/10.f); }

    public static final class DBContract implements BaseColumns {
        public static final String TABLE_NAME = "ACFTRecord";
        public static final String COLUMN_RECORD_DATE = "RecordDate";
        public static final String COLUMN_RAW_MDL = "MDLRaw", COLUMN_RAW_SPT = "SPTRaw", COLUMN_RAW_HPU = "HPURaw";
        public static final String COLUMN_RAW_SDC = "SDCRaw", COLUMN_RAW_LTK = "LTKRaw", COLUMN_RAW_CARDIO = "CardioRaw";
        public static final String COLUMN_SCORE_MDL = "MDLScore", COLUMN_SCORE_SPT = "SPTScore", COLUMN_SCORE_HPU = "HPUScore";
        public static final String COLUMN_SCORE_SDC = "SDCScore", COLUMN_SCORE_LTK = "LTKScore", COLUMN_SCORE_CARDIO = "CardioScore";
        public static final String COLUMN_CARDIO_ALTER = "CardioAlter", COLUMN_SCORE_TOTAL = "ScoreTotal", COLUMN_QUALIFIED_LEVEL = "QualifiedLevel";
        public static final String COLUMN_MOS_REQUIREMENT = "MOSRequirement", COLUMN_IS_PASSED = "isPassed";

        public static final String SQL_CREATE_TBL="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                //_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_RECORD_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
                COLUMN_RAW_MDL+" INTEGER NOT NULL,"+ COLUMN_SCORE_MDL+" INTEGER NOT NULL,"+
                COLUMN_RAW_SPT+" FLOAT NOT NULL,"+ COLUMN_SCORE_SPT+" INTEGER NOT NULL,"+
                COLUMN_RAW_HPU+" INTEGER NOT NULL,"+ COLUMN_SCORE_HPU+" INTEGER NOT NULL,"+
                COLUMN_RAW_SDC+" TEXT NOT NULL,"+ COLUMN_SCORE_SDC+" INTEGER NOT NULL,"+
                COLUMN_RAW_LTK+" INTEGER NOT NULL,"+ COLUMN_SCORE_LTK+" INTEGER NOT NULL,"+
                COLUMN_RAW_CARDIO+" TEXT NOT NULL,"+ COLUMN_SCORE_CARDIO+" INTEGER NOT NULL,"+
                COLUMN_CARDIO_ALTER+" TEXT NOT NULL,"+ COLUMN_QUALIFIED_LEVEL+" TEXT NOT NULL,"+
                COLUMN_SCORE_TOTAL+" INTEGER NOT NULL,"+COLUMN_MOS_REQUIREMENT+" TEXT NOT NULL,"+
                COLUMN_IS_PASSED+" TEXT NOT NULL)";
        public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        public static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
        public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+TABLE_NAME+
                "("+COLUMN_RECORD_DATE+", "+
                COLUMN_RAW_MDL+", "+COLUMN_SCORE_MDL+", "+ COLUMN_RAW_SPT+", "+COLUMN_SCORE_SPT+", "+
                COLUMN_RAW_HPU+", "+COLUMN_SCORE_HPU+", "+ COLUMN_RAW_SDC+", "+COLUMN_SCORE_SDC+", "+
                COLUMN_RAW_LTK+", "+COLUMN_SCORE_LTK+", "+ COLUMN_RAW_CARDIO+", "+COLUMN_SCORE_CARDIO+", "+
                COLUMN_CARDIO_ALTER+", "+COLUMN_QUALIFIED_LEVEL+", "+COLUMN_SCORE_TOTAL+", "+
                COLUMN_MOS_REQUIREMENT+", "+COLUMN_IS_PASSED+") VALUES";
        public static final String SQL_DELETE_WHERE = "DELETE FROM " + TABLE_NAME + " WHERE ";
        public static final String SQL_DELETE_ALL = "DELETE FROM " + TABLE_NAME;
    }

}
