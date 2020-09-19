package mil.army.fitnesstest.recorder;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileProvider extends androidx.core.content.FileProvider {
    public static final String dbName = "RecordLog.db";
    public static final int dbVersion = 1;
    public static final String dbMIME = "application/vnd.sqlite3";
    public static final String xlsName = "RecordLog.xls";
    public static final String xlsMIME = "application/excel";
    public static final String AUTHORITY = "mil.army.fitnesstest.recorder.FileProvider";

    public static File getDBFile(Context context){ return context.getDatabasePath(dbName); } //new File(context.getDatabasePath(dbName),dbName);
    public static File getXLSFile(Context context){ return context.getFileStreamPath(xlsName); } //new File(context.getFilesDir(), xlsName);

    public static Uri getDBUri(Context context) { return getUriForFile(context, AUTHORITY, getDBFile(context)); }
    public static Uri getXLSUri(Context context) { return getUriForFile(context, AUTHORITY, getXLSFile(context)); }

    public static void fileIO(ContentResolver resolver, Uri inputUri, Uri outputUri) throws FileNotFoundException {
        InputStream inputStream = resolver.openInputStream(inputUri);
        FileOutputStream outputStream = new FileOutputStream(Objects.requireNonNull(resolver.openFileDescriptor(outputUri, "w")).getFileDescriptor());
        try {
            byte[] buff = new byte[1024];
            int read;
            while ((read = inputStream.read(buff, 0, buff.length)) > 0)
                outputStream.write(buff, 0, read);
        } catch (IOException e) { e.printStackTrace(); }
        finally {
            try{ inputStream.close(); outputStream.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static File getTempFile(Context context, Uri uri) throws IOException {
        return File.createTempFile(Objects.requireNonNull(uri.getLastPathSegment()), null, context.getCacheDir());
    }
}
