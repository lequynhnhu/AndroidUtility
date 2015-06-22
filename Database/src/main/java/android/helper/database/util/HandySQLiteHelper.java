package android.helper.database.util;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.helper.database.exception.HandySQLiteHelperException;
import android.os.Build;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Created by lqnhu on 6/21/15.
 */
public class HandySQLiteHelper extends SQLiteOpenHelper {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HandySQLiteHelper(Context context, InputStream input, File destinationPath, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(new ExternalDbContextWrapper(context, destinationPath.getParentFile()), destinationPath.getName(), factory, version, errorHandler);
        tryCopyIfNotExists(input, destinationPath);
    }
    public HandySQLiteHelper(Context context, InputStream input, File destinationPath, SQLiteDatabase.CursorFactory factory, int version) {
        super(new ExternalDbContextWrapper(context, destinationPath.getParentFile()), destinationPath.getName(), factory, version);
        tryCopyIfNotExists(input, destinationPath);
    }
    private static void tryCopyIfNotExists(InputStream input, File destinationPath) {
        try {
            copyIfNotExists(input, destinationPath);
        } catch (IOException e) {
            throw new HandySQLiteHelperException("Error writing database to file from the given input stream", e);
        }
    }
    private static void copyIfNotExists(InputStream input, File destinationPath) throws IOException {
        OutputStream output = null;
        try {
            destinationPath.getParentFile().mkdirs();
            if (!destinationPath.isFile()) { // If exists and is directory exception will be thrown, skip copying otherwise
                output = new BufferedOutputStream(new FileOutputStream(destinationPath));
                int bufferSize = 1024 * 4;
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.flush();
                output.close();
            }
        } finally {
            input.close();
            if (output != null) output.close();
        }
    }
    @Override public final void onCreate(SQLiteDatabase db) {
        throw new UnsupportedOperationException();
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();
    }
    private static class ExternalDbContextWrapper extends ContextWrapper {
        private File mDatabaseDir;
        public ExternalDbContextWrapper(Context base, File databaseDir) {
            super(base);
            mDatabaseDir = databaseDir;
        }
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            File f = new File(mDatabaseDir, name);
            int flags = SQLiteDatabase.CREATE_IF_NECESSARY;
            if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
            return SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler);
        }
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            File f = new File(mDatabaseDir, name);
            int flags = SQLiteDatabase.CREATE_IF_NECESSARY;
            if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
            return SQLiteDatabase.openDatabase(f.getPath(), factory, flags);
        }
    }
}
