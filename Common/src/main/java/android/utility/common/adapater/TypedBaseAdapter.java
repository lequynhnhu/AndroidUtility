package android.utility.common.adapater;

import android.widget.BaseAdapter;

/**
 * Created by lqnhu on 6/20/15.
 */
public abstract class TypedBaseAdapter<T> extends BaseAdapter {
    @Override
    public abstract T getItem(int position);
}
