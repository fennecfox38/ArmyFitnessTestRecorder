package army.prt.recorder.log;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.acft.ACFTRecord;

import static android.content.ContentValues.TAG;
import static army.prt.recorder.log.ACFTDBHelper.DBContract.*;

public class ACFTDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="ACFTLog.db";
    private static final int DATABASE_VERSION=1;
    private Context context;
    private Resources resources;

    public ACFTDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context; resources = context.getResources();
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TBL);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TBL);
        onCreate(db);
    }

    public ArrayList<ACFTRecord> getRecordList(){
        ArrayList<ACFTRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery(SQL_SELECT,null);

        ACFTRecord record;
        for(boolean haveItem = cursor.moveToFirst(); haveItem; haveItem=cursor.moveToNext()){
            record = new ACFTRecord();
            record.stringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE)));
            record.raw_0 = cursor.getInt(cursor.getColumnIndex(COLUMN_RAW_MDL));
            record.raw_1 = cursor.getFloat(cursor.getColumnIndex(COLUMN_RAW_SPT));
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
            record.cardioAlter = getIndexOfStringArray(R.array.CardioEvent, cursor.getString(cursor.getColumnIndex(COLUMN_CARDIO_ALTER)));
            record.qualifiedLevel = getIndexOfStringArray(R.array.Level, cursor.getString(cursor.getColumnIndex(COLUMN_QUALIFIED_LEVEL)));
            record.sco_total = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE_TOTAL));
            list.add(record);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void saveRecordList(ArrayList<ACFTRecord> list){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();          //clear the table first
            db.delete(TABLE_NAME,null,null);
            for(ACFTRecord record : list)   //go through the list and add one by one
                db.insert(TABLE_NAME, null, record.getContentValues(resources));
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void insertRecord(ACFTRecord record){
        SQLiteDatabase db = getWritableDatabase();
        if(db == null) return;
        try {
            db.beginTransaction();  // add one by one
            db.insert(TABLE_NAME, null, record.getContentValues(resources));
            db.setTransactionSuccessful();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { db.endTransaction(); }
        db.close();
    }

    public void deleteRecord(ACFTRecord record){
        String sqlExec = SQL_DELETE_WHERE + sqlWhere(COLUMN_RECORD_DATE,record.dateToString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_MDL,record.raw_0) + "AND " + sqlWhere(COLUMN_RAW_SPT,record.raw_1) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_HPU,record.raw_2) + "AND " + sqlWhere(COLUMN_RAW_SDC,record.raw_3.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_RAW_LTK,record.raw_4) + "AND " + sqlWhere(COLUMN_RAW_CARDIO,record.raw_5.toString()) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_MDL,record.sco[0]) + "AND " + sqlWhere(COLUMN_SCORE_SPT,record.sco[1]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_HPU,record.sco[2]) + "AND " + sqlWhere(COLUMN_SCORE_SDC,record.sco[3]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_LTK,record.sco[4]) + "AND " + sqlWhere(COLUMN_SCORE_CARDIO,record.sco[5]) + "AND ";
        sqlExec += sqlWhere(COLUMN_CARDIO_ALTER,resources.getStringArray(R.array.CardioEvent)[record.cardioAlter]) + "AND ";
        sqlExec += sqlWhere(COLUMN_QUALIFIED_LEVEL,resources.getStringArray(R.array.Level)[record.qualifiedLevel]) + "AND ";
        sqlExec += sqlWhere(COLUMN_SCORE_TOTAL,record.sco_total);
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

    private static String sqlWhere(String column, String arg){ return (column+"=\""+arg+"\" "); }
    private static String sqlWhere(String column, int arg){ return (column+"="+String.valueOf(arg)+" "); }
    private static String sqlWhere(String column, float arg){ return (column+"="+String.valueOf(arg)+" "); }

    public int getIndexOfStringArray(int arrayId, String str){
        String[] array = resources.getStringArray(arrayId);
        for(int index = 0; index<array.length; ++index)
            if(str.equals(array[index])) return index;
        return -1;
    }


    public static final class DBContract implements BaseColumns {
        public static final String TABLE_NAME = "ACFTRecord";
        public static final String COLUMN_RECORD_DATE = "RecordDate";
        public static final String COLUMN_RAW_MDL = "MDLRaw", COLUMN_RAW_SPT = "SPTRaw", COLUMN_RAW_HPU = "HPURaw";
        public static final String COLUMN_RAW_SDC = "SDCRaw", COLUMN_RAW_LTK = "LTKRaw", COLUMN_RAW_CARDIO = "CardioRaw";
        public static final String COLUMN_SCORE_MDL = "MDLScore", COLUMN_SCORE_SPT = "SPTScore", COLUMN_SCORE_HPU = "HPUScore";
        public static final String COLUMN_SCORE_SDC = "SDCScore", COLUMN_SCORE_LTK = "LTKScore", COLUMN_SCORE_CARDIO = "CardioScore";
        public static final String COLUMN_CARDIO_ALTER = "CardioAlter", COLUMN_SCORE_TOTAL = "ScoreTotal", COLUMN_QUALIFIED_LEVEL = "QualifiedLevel";

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
                COLUMN_SCORE_TOTAL+" INTEGER NOT NULL)";
        public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        public static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
        public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+TABLE_NAME+
                "("+COLUMN_RECORD_DATE+", "+
                COLUMN_RAW_MDL+", "+COLUMN_SCORE_MDL+", "+ COLUMN_RAW_SPT+", "+COLUMN_SCORE_SPT+", "+
                COLUMN_RAW_HPU+", "+COLUMN_SCORE_HPU+", "+ COLUMN_RAW_SDC+", "+COLUMN_SCORE_SDC+", "+
                COLUMN_RAW_LTK+", "+COLUMN_SCORE_LTK+", "+ COLUMN_RAW_CARDIO+", "+COLUMN_SCORE_CARDIO+", "+
                COLUMN_CARDIO_ALTER+", "+COLUMN_QUALIFIED_LEVEL+", "+COLUMN_SCORE_TOTAL+") VALUES";
        public static final String SQL_DELETE_WHERE = "DELETE FROM " + TABLE_NAME + " WHERE ";
        public static final String SQL_DELETE_ALL = "DELETE FROM " + TABLE_NAME;
    }

}
