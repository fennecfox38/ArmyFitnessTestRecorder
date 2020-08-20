package army.prt.recorder.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class ACFTDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="ACFTLog.db";
    private static final int DATABASE_VERSION=1;
    private Context context;

    public ACFTDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.SQL_CREATE_TBL);
        Toast.makeText(context,"DBHelper onCreate",Toast.LENGTH_SHORT).show();
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_TBL);
        onCreate(db);
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
        public static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME;
        public static final String SQL_DELETE_ALL = "DELETE * FROM " + TABLE_NAME;
    }

}
