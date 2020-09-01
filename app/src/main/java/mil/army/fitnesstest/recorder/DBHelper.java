package mil.army.fitnesstest.recorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.poi.ss.usermodel.Workbook;

import mil.army.fitnesstest.recorder.abcp.ABCPDBContract;
import mil.army.fitnesstest.recorder.abcp.ABCPDBHandler;
import mil.army.fitnesstest.recorder.acft.ACFTDBContract;
import mil.army.fitnesstest.recorder.acft.ACFTDBHandler;
import mil.army.fitnesstest.recorder.apft.APFTDBContract;
import mil.army.fitnesstest.recorder.apft.APFTDBHandler;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) { super(context, FileProvider.dbName, null, FileProvider.dbVersion); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACFTDBContract.SQL_CREATE_TBL);
        db.execSQL(APFTDBContract.SQL_CREATE_TBL);
        db.execSQL(ABCPDBContract.SQL_CREATE_TBL);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ACFTDBContract.SQL_DROP_TBL); db.execSQL(ACFTDBContract.SQL_CREATE_TBL);
        db.execSQL(APFTDBContract.SQL_DROP_TBL); db.execSQL(APFTDBContract.SQL_CREATE_TBL);
        db.execSQL(ABCPDBContract.SQL_DROP_TBL); db.execSQL(ABCPDBContract.SQL_CREATE_TBL);
    }

    public void exportExcel(Workbook workbook){
        SQLiteDatabase db = getWritableDatabase();
        ACFTDBHandler.exportExcel(db, workbook);
        APFTDBHandler.exportExcel(db, workbook);
        ABCPDBHandler.exportExcel(db, workbook);
        db.close();
    }

}
