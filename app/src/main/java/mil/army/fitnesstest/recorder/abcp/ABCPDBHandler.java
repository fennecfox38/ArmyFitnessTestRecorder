package mil.army.fitnesstest.recorder.abcp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.Sex;

import static mil.army.fitnesstest.recorder.abcp.ABCPDBContract.*;

public class ABCPDBHandler{

    public static ArrayList<ABCPRecord<Item>> getRecordList(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ArrayList<ABCPRecord<Item>> list = new ArrayList<>();
        ABCPRecord<Item> record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new ABCPRecord<Item>();
            record.stringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            record.sex = Sex.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            record.ageGroup = ABCPRecord.AgeGroup.findByString(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            record.height = preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT)));
            record.weight = cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT));
            record.neck = preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_NECK)));
            record.abdomen_waist = preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_ABDOMEN_WAIST)));
            try{ record.hips = cursor.getFloat(cursor.getColumnIndex(COLUMN_HIPS)); }
            catch (Exception e){ record.hips = 0.f; }
            record.bodyFatPercentage = preciseFloat(cursor.getFloat(cursor.getColumnIndex(COLUMN_BODY_FAT_PERCENT)));
            record.height_weight = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_HW_PASSED)));
            record.bodyFatPass = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_BODY_FAT_PASSED)));
            record.isPassed = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PASSED)));
            list.add(record);
        }

        cursor.close();
        db.close();
        dbHelper.close();
        return list;
    }

    public static void saveRecordList(Context context, ArrayList<ABCPRecord<Item>> list){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();          //clear the table first
            db.delete(TABLE_NAME,null,null);
            for(ABCPRecord<Item> record : list)   //go through the list and add one by one
                db.insert(TABLE_NAME, null, record.getContentValues());
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
        dbHelper.close();
    }

    public static void insertRecord(Context context, ABCPRecord<Item> record){
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

    public static void deleteRecord(Context context, ABCPRecord<Item> record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SEX,record.sex.name()) + "AND " + sqlWhere(COLUMN_AGE_GROUP,record.ageGroup.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_HEIGHT,record.height) + "AND " + sqlWhere(COLUMN_WEIGHT,record.weight) + "AND ";
        sqlExec += sqlWhere(COLUMN_NECK,record.neck) + "AND " + sqlWhere(COLUMN_ABDOMEN_WAIST,record.abdomen_waist) + "AND ";
        sqlExec += "("+sqlWhere(COLUMN_HIPS,record.hips)+"OR "+ COLUMN_HIPS + " IS NULL) AND " ;
        sqlExec += sqlWhere(COLUMN_BODY_FAT_PERCENT,record.bodyFatPercentage) + "AND ";
        sqlExec += sqlWhere(COLUMN_HW_PASSED,Boolean.toString(record.height_weight)) + "AND ";
        sqlExec += sqlWhere(COLUMN_BODY_FAT_PASSED,Boolean.toString(record.bodyFatPass)) + "AND ";
        sqlExec += sqlWhere(COLUMN_TOTAL_PASSED,Boolean.toString(record.isPassed));

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

        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        int rowIndex = 1;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            row.createCell(1).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            row.createCell(2).setCellValue(cursor.getString(cursor.getColumnIndex(COLUMN_AGE_GROUP)));
            row.createCell(3).setCellValue(preciseDouble(cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT))));
            row.createCell(4).setCellValue(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT)));
            row.createCell(5).setCellValue(preciseDouble(cursor.getFloat(cursor.getColumnIndex(COLUMN_NECK))));
            row.createCell(6).setCellValue(preciseDouble(cursor.getFloat(cursor.getColumnIndex(COLUMN_ABDOMEN_WAIST))));
            try { row.createCell(7).setCellValue(cursor.getFloat(cursor.getColumnIndex(COLUMN_HIPS))); }
            catch(Exception e){ row.createCell(7).setCellValue((String) null); }
            row.createCell(8).setCellValue(preciseDouble(cursor.getFloat(cursor.getColumnIndex(COLUMN_BODY_FAT_PERCENT))));
            row.createCell(9).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_HW_PASSED))));
            row.createCell(10).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_BODY_FAT_PASSED))));
            row.createCell(11).setCellValue(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_PASSED))));
        }

        cursor.close();
    }

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+ arg +" "); }
    private static String sqlWhere(String column, float arg){ return ("abs("+column+"-"+ arg +")<0.1 "); }
    private static double preciseDouble(float obj){ return (Math.round(obj*10)/10.0); }
    private static float preciseFloat(float obj){ return (Math.round(obj*10)/10.f); }

}
