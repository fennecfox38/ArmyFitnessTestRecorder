package army.prt.recorder.log;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public class FileProvider extends androidx.core.content.FileProvider {
    // https://developer.android.com/reference/android/support/v4/content/FileProvider.html
    public static final String dbName = "RecordLog.db";
    public static final int dbVersion = 1;
    public static final String xlsName = "RecordLog.xls";
    private Context context;
    public File db;
    public File xls;

    public FileProvider(Context context){
        this.context = context;
        db = new File(context.getDatabasePath(dbName),dbName);
        xls = new File(context.getFilesDir(), xlsName);
    }

    public Uri getDatabaseUri() { return getFileUri(db); }

    public Uri getXLSUri() { return getFileUri(xls); }

    public Uri getFileUri(File file){
        return getUriForFile(context, "army.prt.recorder.log.FileProvider", file);
    }
}
