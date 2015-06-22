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
public @interface Indexed {
    /**
     * Name of the index. Use &lt;table name&gt;_&lt;column_name&gt; if name is
     * not defined.
     */
    String name() default "";
    /**
     * Specified if the index must be unique. False by default.
     */
    boolean unique() default false;
}
