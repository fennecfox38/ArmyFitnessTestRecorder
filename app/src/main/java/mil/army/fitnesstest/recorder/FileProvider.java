package mil.army.fitnesstest.recorder;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public class FileProvider extends androidx.core.content.FileProvider {
    // https://developer.android.com/reference/android/support/v4/content/FileProvider.html
    public static final String dbName = "RecordLog.db";
    public static final int dbVersion = 1;
    public static final String xlsName = "RecordLog.xls";
    public static final String AUTHORITY = "mil.army.fitnesstest.recorder.FileProvider";

    public static File getDBFile(Context context){ return context.getDatabasePath(dbName); } //new File(context.getDatabasePath(dbName),dbName);
    public static File getXLSFile(Context context){ return context.getFileStreamPath(xlsName); } //new File(context.getFilesDir(), xlsName);

    public static Uri getDatabaseUri(Context context) {
        return getUriForFile(context, AUTHORITY, getDBFile(context));
    }
    public static Uri getXLSUri(Context context) {
        return getUriForFile(context, AUTHORITY, getXLSFile(context));
    }
}
