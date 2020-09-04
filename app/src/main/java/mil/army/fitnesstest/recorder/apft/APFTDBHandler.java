package mil.army.fitnesstest.recorder.apft;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;

import static mil.army.fitnesstest.recorder.apft.APFTDBContract.*;

public class APFTDBHandler{

    public static ArrayList<APFTRecord> getRecordList(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ArrayList<APFTRecord> list = new ArrayList<>();
        APFTRecord record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new APFTRecord();
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
        dbHelper.close();
        return list;
    }

    public static void saveRecordList(Context context, ArrayList<APFTRecord> list){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db==null) return;
        try{
            db.beginTransaction();
            for(APFTRecord record : list)
                db.insert(TABLE_NAME,null,record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void insertRecord(Context context, APFTRecord record){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db==null) return;
        try{
            db.beginTransaction();  // add one by one
            db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void deleteRecord(Context context, APFTRecord record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_PU,record.raw_PU) + "AND " + sqlWhere(COLUMN_SCORE_PU,record.sco[0]) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_SU,record.raw_SU) + "AND " + sqlWhere(COLUMN_SCORE_SU,record.sco[1]) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_CARDIO,record.raw_Cardio.toString()) + "AND " + sqlWhere(COLUMN_SCORE_CARDIO,record.sco[2]) + "AND ";
        sqlExec += sqlWhere(COLUMN_CARDIO_ALTER,record.cardioAlter.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SEX,record.sex.name()) + "AND " + sqlWhere(COLUMN_AGE_GROUP,record.ageGroup.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_TOTAL,record.sco_total) + "AND " + sqlWhere(COLUMN_IS_PASSED,Boolean.toString(record.isPassed));

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

    public static void exportExcel(SQLiteDatabase db, Workbook workbook){
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
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }
}
