package android.helper.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lqnhu on 6/21/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Columns {
    enum TypeColumn {
        INTEGER("INTEGER"), TEXT("TEXT"), NUMERIC("NUMERIC"), BOOLEAN("INTEGER(1)"), DATE("INTEGER"), LIST_TEXT("TEXT");
        private final String sqlType;
        TypeColumn(String sqlType) {
            this.sqlType = sqlType;
        }
        public String getSqlType() {
            return sqlType;
        }
    }
    /**
     * Name of the column, use a field name if the name is not defined.
     */
    String name() default "";
    /**
     * Type of the column. TEXT by default.
     */
    Columns.TypeColumn type() default Columns.TypeColumn.TEXT;
    /**
     * True if the column must be not null. FALSE by default.
     */
    boolean notNull() default false;
}
