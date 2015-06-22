package android.helper.database.util;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lqnhu on 6/21/15.
 */
public class DataBase extends SQLiteOpenHelper {


    public DataBase(Context context) {
        super(context, "chsazan", null, 1);
    }
    @Override
    public synchronized void onCreate(SQLiteDatabase db) {
    }
    @Override
    public synchronized void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
    private static ArrayList<String> tableThatCheckedCreated = new ArrayList<String>();
    private void MakeSureCreateTableIfNotExist(SQLiteDatabase db, String tableName, Class<?> type) {
        if (!tableThatCheckedCreated.contains(tableName)) {
            StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            query.append(tableName);
            query.append(" (");//[RowNumber] INTEGER PRIMARY KEY AUTOINCREMENT");
            StringBuilder fields = new StringBuilder();
            String fieldType;
            boolean hasAutoNumber = false;
            for (java.lang.reflect.Field item: type.getFields()) {
                fieldType = item.getType().getName();
                if (item.getType().isArray()) {
                    fieldType = item.getType().getComponentType() + "[]";
                }
                if (fields.length() != 0) {
                    fields.append(',');
                }
                fields.append("[");
                fields.append(item.getName());
                fields.append("] ");
                if (item.getName().equals("RowNumber")) {
                    fields.append("INTEGER PRIMARY KEY AUTOINCREMENT");
                    hasAutoNumber = true;
                }
                else if (fieldType.equals("java.lang.String")) {
                    fields.append("TEXT");
                }
                else if (fieldType.equals("java.lang.Integer") || fieldType.equals("int")) {
                    fields.append("INTEGER");
                }
                else if (fieldType.equals("java.lang.Long") || fieldType.equals("long")) {
                    fields.append("INTEGER");
                }
                else if (fieldType.equals("java.lang.Byte") || fieldType.equals("byte")) {
                    fields.append("INTEGER");
                }
                else if (fieldType.equals("java.lang.Boolean") || fieldType.equals("boolean")) {
                    fields.append("INTEGER");
                }
                else if (fieldType.equals("byte[]")) {
                    fields.append("TEXT");
                }
                else {
                    fields.append("TEXT");
                }
            }
            if (!hasAutoNumber) {
                fields.append(",[RowNumber] INTEGER PRIMARY KEY AUTOINCREMENT");
            }
            query.append(fields);
            query.append(")");
            db.execSQL(query.toString());
            tableThatCheckedCreated.add(tableName);
        }
    }
    public synchronized <T> boolean Insert(T record) {
        ArrayList<T> records = new ArrayList<T>();
        records.add(record);
        return Insert(records);
    }
    public synchronized <T> boolean Insert(List<T> records) {
        if ((records == null) || records.isEmpty()) {
            return true;
        }
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Class<?> type = records.iterator().next().getClass();
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder query = new StringBuilder();
            StringBuilder filedsName = new StringBuilder();
            StringBuilder filedsValues = new StringBuilder();
            String fieldType;
            for (T obj : records) {
                query.setLength(0);
                filedsName.setLength(0);
                filedsValues.setLength(0);
                query.append("INSERT INTO ");
                query.append(tableName);
                for (java.lang.reflect.Field item: type.getFields()) {
                    fieldType = item.getType().getName();
                    if (item.getName().equals("RowNumber")) {
                        continue;
                    }
                    if (item.getType().isArray()) {
                        fieldType = item.getType().getComponentType() + "[]";
                    }
                    if (filedsName.length() != 0) {
                        filedsName.append(',');
                        filedsValues.append(',');
                    }
                    filedsName.append('[');
                    filedsName.append(item.getName());
                    filedsName.append(']');
                    if (fieldType.equals("java.lang.String")) {
                        filedsValues.append('\'');
                        filedsValues.append(String.valueOf(item.get(obj)));
                        filedsValues.append('\'');
                    }
                    else if (fieldType.equals("java.lang.Integer") || fieldType.equals("int")) {
                        filedsValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Long") || fieldType.equals("long")) {
                        filedsValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Byte") || fieldType.equals("byte")) {
                        filedsValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Boolean") || fieldType.equals("boolean")) {
                        filedsValues.append('\'');
                        filedsValues.append(String.valueOf(item.get(obj)));
                        filedsValues.append('\'');
                    }
                    else if (fieldType.equals("byte[]")) {
                        filedsValues.append('\'');
                        byte[] b = (byte[])item.get(obj);
                        if (b == null) {
                            b = new byte[0];
                        }
                        filedsValues.append(Base64.encodeToString(b, Base64.NO_WRAP));
//filedsValues.append(EncodingUtils.getString((byte[])item.get(obj), "utf8").replace("\'", "\\'"));
                        filedsValues.append('\'');
                    }
                    else {
                        filedsValues.append('\'');
                        filedsValues.append(String.valueOf(item.get(obj)));
                        filedsValues.append('\'');
                    }
                }
                query.append(" (");
                query.append(filedsName);
                query.append(") VALUES (");
                query.append(filedsValues);
                query.append(");");
                db.execSQL(query.toString());
            }
            return true;
        }
        catch (Throwable r) {
            r.printStackTrace();
            return false;
        }
        finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public synchronized <T> boolean Update(T record) {
        ArrayList<T> records = new ArrayList<T>();
        records.add(record);
        return Update(records, "[RowNumber]");
    }
    public synchronized <T> boolean Update(T record, String primaryKey) {
        ArrayList<T> records = new ArrayList<T>();
        records.add(record);
        return Update(records, primaryKey);
    }
    public synchronized <T> boolean Update(List<T> records) {
        return Update(records, "[RowNumber]");
    }
    public synchronized <T> boolean Update(List<T> records, String primaryKey) {
        if ((records == null) || records.isEmpty()) {
            return true;
        }
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Class<?> type = records.iterator().next().getClass();
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder query = new StringBuilder();
            StringBuilder filedsNameAndValues = new StringBuilder();
            Object primaryKeyValue = null;
            String fieldType;
            for (T obj : records) {
                query.setLength(0);
                filedsNameAndValues.setLength(0);
                query.append("UPDATE ");
                query.append(tableName);
                query.append(" SET ");
                for (java.lang.reflect.Field item: type.getFields()) {
                    if (primaryKey.equals(item.getName())) {
                        primaryKeyValue = item.get(obj);
                        continue;
                    }
                    else if (item.getName().equals("RowNumber")) {
                        continue;
                    }
                    fieldType = item.getType().getName();
                    if (item.getType().isArray()) {
                        fieldType = item.getType().getComponentType() + "[]";
                    }
                    if (filedsNameAndValues.length() != 0) {
                        filedsNameAndValues.append(',');
                    }
                    if (fieldType.equals("java.lang.String")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append('\'');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                        filedsNameAndValues.append('\'');
                    }
                    else if (fieldType.equals("java.lang.Integer") || fieldType.equals("int")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Long") || fieldType.equals("long")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Byte") || fieldType.equals("byte")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("java.lang.Boolean") || fieldType.equals("boolean")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                    }
                    else if (fieldType.equals("byte[]")) {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append("='");
//filedsNameAndValues.append(EncodingUtils.getString((byte[])item.get(obj), "utf8").replace("\'", "\\'"));
                        filedsNameAndValues.append(Base64.encodeToString((byte[])item.get(obj), Base64.NO_WRAP));
                        filedsNameAndValues.append('\'');
                    }
                    else {
                        filedsNameAndValues.append(item.getName());
                        filedsNameAndValues.append('\'');
                        filedsNameAndValues.append('=');
                        filedsNameAndValues.append(String.valueOf(item.get(obj)));
                        filedsNameAndValues.append('\'');
                    }
                }
                if (primaryKeyValue == null) {
                    throw new Exception("Primary Key Not Found");
                }
                query.append(filedsNameAndValues);
                query.append(" WHERE ");
                query.append(primaryKey);
                query.append(" = '");
                query.append(String.valueOf(primaryKeyValue));
                query.append('\'');
                db.execSQL(query.toString());
            }
            return true;
        }
        catch (Throwable r) {
            r.printStackTrace();
            return false;
        }
        finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public synchronized <T> boolean Delete(T record) {
        ArrayList<T> records = new ArrayList<T>();
        records.add(record);
        return Delete(records, "[RowNumber]");
    }
    public synchronized <T> boolean Delete(T record, String primaryKey) {
        ArrayList<T> records = new ArrayList<T>();
        records.add(record);
        return Delete(records, primaryKey);
    }
    public synchronized <T> boolean Delete(List<T> records) {
        return Delete(records, "[RowNumber]");
    }
    public synchronized <T> boolean Delete(List<T> records, String primaryKey) {
        if ((records == null) || records.isEmpty()) {
            return true;
        }
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Class<?> type = records.iterator().next().getClass();
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder query = new StringBuilder();
            for (T obj : records) {
                query.setLength(0);
                query.append("DELETE FROM ");
                query.append(tableName);
                query.append(" WHERE ");
                query.append(primaryKey);
                query.append(" = '");
                query.append(String.valueOf(type.getField(primaryKey).get(obj)));
                query.append('\'');
                db.execSQL(query.toString());
            }
            return true;
        }
        catch (Throwable r) {
            r.printStackTrace();
            return false;
        }
        finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public synchronized boolean TruncateTable(Class<?> type) {
        return TruncateTable(type, null);
    }
    public synchronized boolean TruncateTable(Class<?> type, String where) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ");
            query.append(tableName);
            if (where != null && where != "") {
                query.append(" WHERE ");
                query.append(where);
            }
            db.execSQL(query.toString());
            return true;
        }
        catch (Throwable r) {
            r.printStackTrace();
            return false;
        }
        finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public synchronized int GetPageCount(Class<?> type, String where) {
/*SQLiteDatabase db = null;
Cursor cur = null;
int count = 0;
try {
db = this.getWritableDatabase();
String tableName = type.getName().replace('.', '_');
MakeSureCreateTableIfNotExist(db, tableName, type);
StringBuilder qurty = new StringBuilder("SELECT COUNT(*) FROM ");
qurty.append(tableName);
if (!where.equals("")) {
qurty.append(" WHERE ");
qurty.append(where);
}
cur = db.rawQuery(qurty.toString(), new String[]{ });
cur.moveToNext();
count = cur.getInt(0);
}
catch (Throwable r) {
r.printStackTrace();
}
finally {
if (cur != null) {
cur.close();
}
if (db != null) {
db.close();
}
}*/
        int count = Count(type, where);
        return count / RecordCountPerPage + (count % RecordCountPerPage == 0 ? 0 : 1);
    }
    public synchronized int Count(Class<?> type, String where) {
        SQLiteDatabase db = null;
        Cursor cur = null;
        int count = 0;
        try {
            db = this.getWritableDatabase();
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder qurty = new StringBuilder("SELECT COUNT(*) FROM ");
            qurty.append(tableName);
            if (where != null && !where.equals("")) {
                qurty.append(" WHERE ");
                qurty.append(where);
            }
            cur = db.rawQuery(qurty.toString(), new String[]{ });
            cur.moveToNext();
            count = cur.getInt(0);
        }
        catch (Throwable r) {
            r.printStackTrace();
        }
        finally {
            if (cur != null) {
                cur.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return count;
    }
    public static int RecordCountPerPage = 20;
    @SuppressWarnings("unchecked")
    public synchronized <T> ArrayList<T> Select(Class<T> typeOfResult, int pageNumber, String where) {
        ArrayList<T> result = new ArrayList<T>();
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            db = this.getWritableDatabase();
            Class<?> type = typeOfResult;
            String tableName = type.getName().replace('.', '_');
            MakeSureCreateTableIfNotExist(db, tableName, type);
            StringBuilder query = new StringBuilder();
            query.append("SELECT ");
            StringBuilder fieldsString = new StringBuilder();
            for (java.lang.reflect.Field item: type.getFields()) {
                if (fieldsString.length() != 0) {
                    fieldsString.append(',');
                }
                fieldsString.append('[');
                fieldsString.append(item.getName());
                fieldsString.append(']');
            }
            query.append(fieldsString);
            query.append(" FROM ");
            query.append(tableName);
            if (where != null && !where.equals("")) {
                query.append(" WHERE ");
                query.append(where);
            }
            query.append(" ORDER BY [RowNumber] ASC");
            query.append(" LIMIT ");
            query.append(String.valueOf(RecordCountPerPage));
            query.append(" OFFSET ");
            query.append(String.valueOf(pageNumber * RecordCountPerPage));
            cur = db.rawQuery(query.toString(), new String[] {});
            String fieldType;
            T obj;
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    obj = (T)type.newInstance();
                    for (java.lang.reflect.Field item: type.getFields()) {
                        try {
                            fieldType = item.getType().getName();
                            if (item.getType().isArray()) {
                                fieldType = item.getType().getComponentType() + "[]";
                            }
                            if (fieldType.equals("java.lang.String")) {
                                item.set(obj, cur.getString(cur.getColumnIndex(item.getName())));
                            }
                            else if (fieldType.equals("java.lang.Integer") || fieldType.equals("int")) {
                                item.set(obj, cur.getInt(cur.getColumnIndex(item.getName())));
                            }
                            else if (fieldType.equals("java.lang.Long") || fieldType.equals("long")) {
                                item.set(obj, cur.getLong(cur.getColumnIndex(item.getName())));
                            }
                            else if (fieldType.equals("java.lang.Byte") || fieldType.equals("byte")) {
                                item.set(obj, (byte)cur.getInt(cur.getColumnIndex(item.getName())));
                            }
                            else if (fieldType.equals("java.lang.Boolean") || fieldType.equals("boolean")) {
                                item.set(obj, Boolean.valueOf(cur.getString(cur.getColumnIndex(item.getName()))));
                            }
                            else if (fieldType.equals("byte[]")) {
                                item.set(obj, Base64.decode(cur.getString(cur.getColumnIndex(item.getName())), Base64.NO_WRAP));
                            }
                            else {
                                item.set(obj, cur.getString(cur.getColumnIndex(item.getName())));
                            }
                        }
                        catch (Throwable r) {
                            r.printStackTrace();
                        }
                    }
                    result.add((T) obj);
                }
            }
            return result;
        }
        catch (Throwable r) {
            r.printStackTrace();
            return result;
        }
        finally {
            if (cur != null) {
                cur.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
    public synchronized <T> T Single(Class<T> typeOfResult, long valueOfPrimaryKey) {
        String primaryKey = "RowNumber";
        try {
            primaryKey = (String)typeOfResult.getMethod("GetPrimaryKeyColumn", new Class<?>[0]).invoke(null, new Object[0]);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return Select(typeOfResult, 0, primaryKey + "=" + valueOfPrimaryKey).get(0);
    }
    public synchronized <T> T SingleOrDefault(Class<T> typeOfResult, long valueOfPrimaryKey) {
        String primaryKey = "RowNumber";
        try {
            primaryKey = (String)typeOfResult.getMethod("GetPrimaryKeyColumn", new Class<?>[0]).invoke(typeOfResult.newInstance(), new Object[0]);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        ArrayList<T> result = Select(typeOfResult, 0, primaryKey + "=" + valueOfPrimaryKey);
        if (result.size() == 0) {
            return null;
        }
        else {
            return result.get(0);
        }
    }
    public synchronized <T> T Single(Class<T> typeOfResult, String where) {
        return Select(typeOfResult, 0, where).get(0);
    }
    public synchronized <T> T SingleOrDefault(Class<T> typeOfResult, String where) {
        ArrayList<T> result = Select(typeOfResult, 0, where);
        if (result.size() == 0) {
            return null;
        }
        else {
            return result.get(0);
        }
    }
}
