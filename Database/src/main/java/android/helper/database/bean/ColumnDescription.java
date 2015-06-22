package android.helper.database.bean;
import java.lang.reflect.Field;

/**
 * Created by lqnhu on 6/21/15.
 */
public class ColumnDescription {
    private String columnName;
    private Field columnField;
    private String sqliteType;
    private boolean isIndex;
    public ColumnDescription(String columnName, Field columnField, boolean isIndex) {
        this.columnName = columnName;
        this.columnField = columnField;
        this.isIndex = isIndex;
        this.columnField.setAccessible(true);
        Class<?> fieldType = columnField.getType();
        if (fieldType.equals(String.class)) {
            sqliteType = "TEXT";
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            sqliteType = "INTEGER";
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            sqliteType = "REAL";
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            sqliteType = "REAL";
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            sqliteType = "INTEGER";
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            sqliteType = "INTEGER";
        } else {
            sqliteType = "TEXT";
        }
    }
    public String getColumnName() {
        return columnName;
    }
    public Field getColumnField() {
        return columnField;
    }
    public String getSqliteType() {
        return sqliteType;
    }
    public boolean isIndex() {
        return isIndex;
    }
}
