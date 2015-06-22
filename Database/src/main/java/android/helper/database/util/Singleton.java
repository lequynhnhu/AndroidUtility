package android.helper.database.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lqnhu on 6/21/15.
 */
public class Singleton {

    private static final Singleton instance = new Singleton();

    @SuppressWarnings("rawtypes")
    private Map<Class, Object> mapHolder = new HashMap<Class, Object>();

    private Singleton() {
    }


    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> classOf) {

        try {
            synchronized (instance) {

                if (!instance.mapHolder.containsKey(classOf)) {

                    T obj = classOf.newInstance();

                    instance.mapHolder.put(classOf, obj);
                }


            }
        } catch (InstantiationException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return (T) instance.mapHolder.get(classOf);
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
