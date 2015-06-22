package android.utility.ui.layout;
import android.app.Activity;
import android.utility.ui.annotation.LayoutView;
import android.utility.ui.annotation.OnClick;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by lqnhu on 6/19/15.
 */
public class LayoutViewHelper
{
    private static HashMap<String, HashMap<String, Integer>> idsCache = new HashMap<String, HashMap<String, Integer>>();
    public static void initLayout(final Activity activity) {
        HashMap<Integer, View> viewCache = new HashMap<Integer, View>();
        Field fields[] = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            LayoutView layoutView = field.getAnnotation(LayoutView.class);
            if (layoutView != null) {
                try {
                    field.setAccessible(true);
                    View view = activity.findViewById(layoutView.value());
                    field.set(activity, view);
                    viewCache.put(layoutView.value(), view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Method methods[] = activity.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                method.setAccessible(true);
                int viewId = onClick.value();
                View view = null;
                if (viewCache.containsKey(viewId)) {
                    view = viewCache.get(viewId);
                } else {
                    view = activity.findViewById(viewId);
                }
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                method.invoke(activity, v);
                            } catch (IllegalAccessException e) {
                                v.setOnClickListener(null);
                            } catch (InvocationTargetException e) {
                                v.setOnClickListener(null);
                            }
                        }
                    });
                }
            }
        }
    }
    public static void initLayout(View view, Object owner) {
        Field fields[] = owner.getClass().getDeclaredFields();
        for (Field field : fields) {
            LayoutView layoutView = field.getAnnotation(LayoutView.class);
            if (layoutView != null) {
                try {
                    field.setAccessible(true);
                    field.set(owner, view.findViewById(layoutView.value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void initLayoutWithName(final Activity activity) {
        if (!idsCache.containsKey(activity.getPackageName())) {
            String rIdName = activity.getPackageName() + ".R$id";
            try {
                Class<?> rIdClass = activity.getClassLoader().loadClass(rIdName);
                HashMap<String, Integer> ids = new HashMap<String, Integer>();
                Field fields[] = rIdClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType().isAssignableFrom(int.class) && Modifier.isStatic(field.getModifiers())) {
                        ids.put(field.getName(), field.getInt(null));
                    }
                }
                idsCache.put(activity.getPackageName(), ids);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Integer> ids = idsCache.get(activity.getPackageName());
        HashMap<Integer, View> viewCache = new HashMap<Integer, View>();
        if (ids.size() > 0) {
            Field fields[] = activity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (View.class.isAssignableFrom(field.getType())) {
                    if (ids.containsKey(field.getName())) {
                        int viewId = ids.get(field.getName());
                        try {
                            field.setAccessible(true);
                            View view = activity.findViewById(viewId);
                            field.set(activity, view);
                            viewCache.put(viewId, view);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        Method methods[] = activity.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                method.setAccessible(true);
                int viewId = onClick.value();
                View view = null;
                if (viewCache.containsKey(viewId)) {
                    view = viewCache.get(viewId);
                } else {
                    view = activity.findViewById(viewId);
                }
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                method.invoke(activity, v);
                            } catch (IllegalAccessException e) {
                                v.setOnClickListener(null);
                            } catch (InvocationTargetException e) {
                                v.setOnClickListener(null);
                            }
                        }
                    });
                }
            }
        }
    }
    public static void clear(){
        idsCache.clear();
    }
}
