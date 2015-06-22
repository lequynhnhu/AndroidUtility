package android.helper.database.bean;

import android.helper.database.annotation.Column;
import android.helper.database.annotation.Table;
import android.helper.database.bean.ColumnDescription;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by lqnhu on 6/21/15.
 */
public class ModelDescription {
    private Class<?> type;
    private String tableName;
    private ColumnDescription keyColumn;
    private ArrayList<ColumnDescription> columnDescriptions;
    public ModelDescription(Class<?> type) {
        this.type = type;
        Table table = type.getAnnotation(Table.class);
        if (table == null) {
            this.tableName = type.getSimpleName();
        } else {
            this.tableName = table.name();
        }
        Field[] fields = type.getDeclaredFields();
        columnDescriptions = new ArrayList<ColumnDescription>(fields.length);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String columnName = column.name();
                if (columnName == null || columnName.length() == 0) {
                    columnName = field.getName();
                }
                ColumnDescription description = new ColumnDescription(columnName, field, column.index());
                columnDescriptions.add(description);
                if (column.key()) {
                    keyColumn = description;
                }
            }
        }
    }
    public Class<?> getType() {
        return type;
    }
    public String getTableName() {
        return tableName;
    }
    public ColumnDescription getKeyColumn() {
        return keyColumn;
    }
    public ArrayList<ColumnDescription> getColumnDescriptions() {
        return columnDescriptions;
    }
}
