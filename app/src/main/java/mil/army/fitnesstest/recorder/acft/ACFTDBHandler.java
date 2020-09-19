package mil.army.fitnesstest.recorder.acft;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.acft.event.ACFTCardioAlter;
import mil.army.fitnesstest.recorder.acft.event.ACFTEvent;

import static mil.army.fitnesstest.recorder.acft.ACFTDBContract.*;

public class ACFTDBHandler{

    public static ArrayList<ACFTRecord> getRecordList(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ArrayList<ACFTRecord> list = new ArrayList<>();
        ACFTRecord record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new ACFTRecord();
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
            record.cardioAlter = ACFTCardioAlter.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            record.qualifiedLevel = Level.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_QUALIFIED_LEVEL)));
            record.sco_total = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL));
            record.mos = ACFTRecord.MOS.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_MOS_REQUIREMENT)));
            record.isPassed = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED)));
            list.add(record);
        }

        cursor.close();
        db.close();
        dbHelper.close();
        return list;
    }

    public static void saveRecordList(Context context, ArrayList<ACFTRecord> list){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();          //clear the table first
            db.delete(TABLE_NAME,null,null);
            for(ACFTRecord record : list)   //go through the list and add one by one
                db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void insertRecord(Context context, ACFTRecord record){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();  // add one by one
            db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void deleteAll(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();
            db.execSQL(SQL_DELETE_ALL);
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void deleteRecord(Context context, ACFTRecord record) {
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_MDL,record.raw_0) + "AND " + sqlWhere(COLUMN_RAW_SPT,record.raw_1) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_HPU,record.raw_2) + "AND " + sqlWhere(COLUMN_RAW_SDC,record.raw_3.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_LTK,record.raw_4) + "AND " + sqlWhere(COLUMN_RAW_CARDIO,record.raw_5.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_MDL,record.sco[0]) + "AND " + sqlWhere(COLUMN_SCORE_SPT,record.sco[1]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_HPU,record.sco[2]) + "AND " + sqlWhere(COLUMN_SCORE_SDC,record.sco[3]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_LTK,record.sco[4]) + "AND " + sqlWhere(COLUMN_SCORE_CARDIO,record.sco[5]) + "AND ";
        sqlExec += sqlWhere(COLUMN_CARDIO_ALTER,record.cardioAlter.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_QUALIFIED_LEVEL,record.qualifiedLevel.name()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_TOTAL,record.sco_total) + "AND " + sqlWhere(COLUMN_MOS_REQUIREMENT,record.mos.name()) + "AND ";
        sqlExec += sqlWhere(COLUMN_IS_PASSED,Boolean.toString(record.isPassed));

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(db == null) return;
        try {
            db.beginTransaction();
            db.execSQL(sqlExec);
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void exportExcel(SQLiteDatabase db, Workbook workbook){
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
            row.createCell(17).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_IS_PASSED)));
        }

        cursor.close();
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }
    private static String sqlWhere(String column, float arg){ return ("abs("+column+"-"+ arg +")<0.1 "); }
    private static double preciseDouble(float obj){ return (Math.round(obj*10)/10.0); }
    private static float preciseFloat(float obj){ return (Math.round(obj*10)/10.f); }
}
