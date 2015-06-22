package android.helper.database.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.helper.database.imp.TransactionAction;
import android.helper.database.bean.ColumnDescription;
import android.helper.database.bean.ModelDescription;
import android.helper.database.imp.QuerySupportedModel;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lqnhu on 6/21/15.
 */
public class DatabaseHelper {
    private static final String DEBUG_TAG = "rakutec.DEBUG";
    private static final String ERROR_TAG = "rakutec.ERROR";
    private static final String META_DATA_DATABASE_NAME = "DATABASE_NAME";
    private static final String META_DATA_DATABASE_VERSION = "DATABASE_VERSION";
    private static final String DEFAULT_DATABASE_NAME = "database.sqlite";
    private static final int DEFAULT_DATABASE_VERSION = 1;
    private Context context;
    private DatabaseOpenHelper helper;
    private String databaseName;
    private int databaseVersion;
    private boolean needCopy;
    private List<Class<?>> entityClassList;
    private HashMap<Class<?>, ModelDescription> modelDescriptionMap;
    public DatabaseHelper(Context context) {
        this(context, (List<Class<?>>) null);
    }
    public DatabaseHelper(Context context, Class<?>[] entityClasses) {
        this(context, Arrays.asList(entityClasses));
    }
    public DatabaseHelper(Context context, List<Class<?>> entityClassList) {
        this.context = context;
        this.entityClassList = entityClassList;
        databaseName = DEFAULT_DATABASE_NAME;
        databaseVersion = DEFAULT_DATABASE_VERSION;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            if (metaData != null) {
                databaseName = metaData.getString(META_DATA_DATABASE_NAME);
                if (databaseName == null) {
                    databaseName = DEFAULT_DATABASE_NAME;
                }
                databaseVersion = metaData.getInt(META_DATA_DATABASE_VERSION, DEFAULT_DATABASE_VERSION);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(ERROR_TAG, "Failed to find the package", e);
        }
        Log.d(DEBUG_TAG, String.format("Database Name:%s", databaseName));
        Log.d(DEBUG_TAG, String.format("Database Version:%d", databaseVersion));
        initialize();
    }
    public DatabaseHelper(Context context, List<Class<?>> entityClassList, String databaseName, int databaseVersion) {
        this.context = context;
        this.entityClassList = entityClassList;
        this.databaseName = databaseName;
        this.databaseVersion = databaseVersion;
        initialize();
    }
    private void initialize() {
        try {
            needCopy = Arrays.asList(this.context.getAssets().list("")).contains(databaseName);
        } catch (IOException e) {
            Log.e(ERROR_TAG, "Failed to list assets", e);
            needCopy = false;
        }
        Log.d(DEBUG_TAG, String.format("Need Copy:%s", needCopy));
        beforeCreate();
        helper = new DefaultSQLiteOpenHelper(context, databaseName, null, databaseVersion);
        modelDescriptionMap = new HashMap<Class<?>, ModelDescription>();
    }
    private void copyDatabaseFileIfNeeded() {
        File desFile = this.context.getDatabasePath(databaseName);
        if (!desFile.exists()) {
            File dir = desFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                InputStream inputStream = this.context.getAssets().open(databaseName);
                OutputStream outputStream = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                Log.e(ERROR_TAG, "Failed to generate database file.", e);
            }
        }
    }
    public String getDatabaseName() {
        return databaseName;
    }
    public int getDatabaseVersion() {
        return databaseVersion;
    }
    public Context getContext() {
        return context;
    }
    protected void beforeCreate() {
        if (needCopy) {
            copyDatabaseFileIfNeeded();
        }
    }
    public void onCreate(SQLiteDatabase db) {
        if (entityClassList == null || entityClassList.size() == 0) {
            return;
        }
        for (Class<?> type : entityClassList) {
            ArrayList<String> sqlList = createSQL(type);
            for (String sql : sqlList) {
                db.execSQL(sql);
            }
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (entityClassList == null || entityClassList.size() == 0) {
            return;
        }
        for (Class<?> type : entityClassList) {
            db.execSQL("DROP TABLE IF EXISTS " + getModelDescription(type).getTableName());
        }
        onCreate(db);
    }
    protected ArrayList<String> createSQL(Class<?> type) {
        ModelDescription description = getModelDescription(type);
        StringBuilder builder = new StringBuilder("create table ");
        builder.append(description.getTableName()).append("(");
        ColumnDescription keyColumn = description.getKeyColumn();
        builder.append(keyColumn.getColumnName()).append(" ").append(keyColumn.getSqliteType()).append(" PRIMARY KEY AUTOINCREMENT,");
        ArrayList<String> sqlList = new ArrayList<String>();
        ArrayList<ColumnDescription> columnDescriptions = description.getColumnDescriptions();
        for (ColumnDescription column : columnDescriptions) {
            if (column.equals(keyColumn)) {
                continue;
            }
            if (column.isIndex()) {
                sqlList.add(String.format("CREATE INDEX IF NOT EXISTS %s_%s_INDEX ON %s(%s)",
                        description.getTableName(), column.getColumnName(), description.getTableName(), column.getColumnName()));
            }
            builder.append(column.getColumnName()).append(" ").append(column.getSqliteType()).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        String sql = builder.toString();
        sqlList.add(0, sql);
        Log.d(DEBUG_TAG, sql);
        return sqlList;
    }
    public SQLiteDatabase getWritableDatabase() {
        return helper.getWritableDatabase();
    }
    public SQLiteDatabase getReadableDatabase() {
        return helper.getReadableDatabase();
    }
    private void closeDatabaseIfNotInTransaction(SQLiteDatabase db) {
        if (!db.inTransaction()) {
            db.close();
        }
    }
    public void doInTransaction(TransactionAction action) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            action.execute(this);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(ERROR_TAG, "Transaction failed", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public void save(Object entity) {
        save(entity, null);
    }
    public void save(Object entity, String[] columns) {
        Class<?> type = entity.getClass();
        ModelDescription description = getModelDescription(type);
        ColumnDescription keyColumn = description.getKeyColumn();
        if (keyColumn == null) {
            Log.e(ERROR_TAG, "No key field entity");
            return;
        }
        List<String> columnList = null;
        if (columns != null) {
            columnList = Arrays.asList(columns);
        }
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<ColumnDescription> columnDescriptions = description.getColumnDescriptions();
        ContentValues values = new ContentValues();
        for (ColumnDescription column : columnDescriptions) {
            if (column.equals(keyColumn)) {
                continue;
            }
            String fieldName = column.getColumnName();
            if (columnList != null && !columnList.contains(fieldName)) {
                continue;
            }
            Field columnField = column.getColumnField();
            Class<?> fieldType = columnField.getType();
            try {
                Object value = columnField.get(entity);
                if (value == null) {
                    continue;
                }
                if (fieldType.equals(String.class)) {
                    values.put(fieldName, value.toString());
                } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    values.put(fieldName, (Boolean) value);
                } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    values.put(fieldName, (Double) value);
                } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    values.put(fieldName, (Float) value);
                } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    values.put(fieldName, (Long) value);
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    values.put(fieldName, (Integer) value);
                }
            } catch (IllegalAccessException e) {
                Log.e(ERROR_TAG, "Save failed", e);
            }
        }
        try {
            Field keyColumnField = keyColumn.getColumnField();
            Object key = keyColumnField.get(entity);
            if (key == null) {
                keyColumnField.set(entity, db.insert(description.getTableName(), null, values));
                if (entity instanceof QuerySupportedModel) {
                    ((QuerySupportedModel) entity).setDatabaseHelper(this);
                }
            } else {
                db.update(description.getTableName(), values, keyColumn.getColumnName() + "=" + key.toString(), null);
            }
        } catch (IllegalAccessException e) {
            Log.e(ERROR_TAG, "Save failed", e);
        } finally {
            closeDatabaseIfNotInTransaction(db);
        }
    }
    public <E> void batchUpdate(Class<E> type, String[] columns, Object[] values, String where, String[] args) {
        ModelDescription description = getModelDescription(type);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        for (int i = 0; i < columns.length; i++) {
            Object value = values[i];
            if (value == null) {
                v.putNull(columns[i]);
            } else {
                Class<?> valueType = value.getClass();
                if (valueType.equals(String.class)) {
                    v.put(columns[i], value.toString());
                } else if (valueType.equals(Boolean.class)) {
                    v.put(columns[i], (Boolean) value);
                } else if (valueType.equals(Double.class)) {
                    v.put(columns[i], (Double) value);
                } else if (valueType.equals(Float.class)) {
                    v.put(columns[i], (Float) value);
                } else if (valueType.equals(Long.class)) {
                    v.put(columns[i], (Long) value);
                } else if (valueType.equals(Integer.class)) {
                    v.put(columns[i], (Integer) value);
                }
            }
        }
        db.update(description.getTableName(), v, where, args);
        closeDatabaseIfNotInTransaction(db);
    }
    public <E> void delete(Class<E> type, String where, String[] args) {
        ModelDescription description = getModelDescription(type);
        SQLiteDatabase db = getWritableDatabase();
        db.delete(description.getTableName(), where, args);
        closeDatabaseIfNotInTransaction(db);
    }
    public <E> int count(Class<E> type, String where, String[] args) {
        ModelDescription description = getModelDescription(type);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select count(*) from " + description.getTableName() + " where " + where;
        Cursor cursor = db.rawQuery(sql, args);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        closeDatabaseIfNotInTransaction(db);
        return count;
    }
    public <E> E querySingle(Class<E> type, String where, String[] args) {
        ArrayList<E> list = query(type, where, args);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    public <E> E querySingle(Class<E> type, String[] columns, String where, String[] args) {
        ArrayList<E> list = query(type, columns, where, args, null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    public <E> ArrayList<E> query(Class<E> type, String where, String[] args) {
        return query(type, null, where, args, null, null, null, null);
    }
    public <E> ArrayList<E> query(Class<E> type, String where, String[] args, String orderBy) {
        return query(type, null, where, args, null, null, orderBy, null);
    }
    public <E> ArrayList<E> query(Class<E> type, String where, String[] args, String orderBy, int limit) {
        return query(type, null, where, args, null, null, orderBy, limit + "");
    }
    public <E> ArrayList<E> query(Class<E> type, String[] columns, String where, String[] args, String orderBy) {
        return query(type, columns, where, args, null, null, orderBy, null);
    }
    public <E> ArrayList<E> query(Class<E> type, String[] columns, String where, String[] args,
                                  String groupBy, String having, String orderBy, String limit) {
        ModelDescription description = getModelDescription(type);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(description.getTableName(), columns, where, args, groupBy, having, orderBy, limit);
        ArrayList<E> result = new ArrayList<E>();
        try {
            if (cursor.moveToFirst()) {
                ArrayList<ColumnDescription> columnDescriptions = description.getColumnDescriptions();
                if (columns != null) {
                    columnDescriptions = new ArrayList<ColumnDescription>(columnDescriptions);
                    List<String> columnList = Arrays.asList(columns);
                    for (int i = 0; i < columnList.size(); i++) {
                        String column = columnList.get(i);
                        int spaceIndex = column.lastIndexOf(" ");
                        if (spaceIndex >= 0) {
                            columnList.set(i, column.substring(spaceIndex + 1));
                        }
                    }
                    for (Iterator<ColumnDescription> i = columnDescriptions.iterator(); i.hasNext(); ) {
                        ColumnDescription column = i.next();
                        if (!columnList.contains(column.getColumnName())) {
                            i.remove();
                        }
                    }
                }
                do {
                    E entity = type.newInstance();
                    if (entity instanceof QuerySupportedModel) {
                        ((QuerySupportedModel) entity).setDatabaseHelper(this);
                    }
                    populate(cursor, entity, columnDescriptions);
                    result.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException e) {
            Log.e(ERROR_TAG, "Query failed", e);
        } catch (IllegalAccessException e) {
            Log.e(ERROR_TAG, "Query failed", e);
        } finally {
            cursor.close();
            closeDatabaseIfNotInTransaction(db);
        }
        return result;
    }
    private ModelDescription getModelDescription(Class<?> type) {
        ModelDescription description = modelDescriptionMap.get(type);
        if (description == null) {
            description = new ModelDescription(type);
            modelDescriptionMap.put(type, description);
        }
        return description;
    }
    private void populate(Cursor cursor, Object entity, ArrayList<ColumnDescription> columnDescriptions) throws IllegalAccessException {
        for (ColumnDescription column : columnDescriptions) {
            String columnName = column.getColumnName();
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex < 0) {
                continue;
            }
            Field columnField = column.getColumnField();
            Class<?> fieldType = columnField.getType();
            if (cursor.isNull(columnIndex) && !fieldType.isPrimitive()) {
                columnField.set(entity, null);
            } else {
                if (fieldType.equals(String.class)) {
                    columnField.set(entity, cursor.getString(columnIndex));
                } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    columnField.set(entity, cursor.getInt(columnIndex) != 0);
                } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    columnField.set(entity, cursor.getDouble(columnIndex));
                } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    columnField.set(entity, cursor.getFloat(columnIndex));
                } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    columnField.set(entity, cursor.getLong(columnIndex));
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    columnField.set(entity, cursor.getInt(columnIndex));
                }
            }
        }
    }
    private interface DatabaseOpenHelper {
        SQLiteDatabase getWritableDatabase();
        SQLiteDatabase getReadableDatabase();
    }
    private class DefaultSQLiteOpenHelper extends SQLiteOpenHelper implements DatabaseOpenHelper {
        private DefaultSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            DatabaseHelper.this.onCreate(db);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            DatabaseHelper.this.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
