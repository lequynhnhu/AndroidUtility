package android.utility.ui.listview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

/**
 * Created by lqnhu on 6/19/15.
 */
public class ListViewUtil
{
    private static final String TAG = "ListViewUtil";
    public static void setListViewHeightBasedOnChildrenBasic(ListView listView, int rowHeight) {
        ArrayAdapter<?> listAdapter = (ArrayAdapter<?>) listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }
        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                Log.d(TAG, "lvu VIEW height: " + listItem.getMeasuredHeight() + " i: " + i);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter<?> listAdapter = (ArrayAdapter<?>) listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }
        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                Log.d(TAG, "lvu VIEW height: " + listItem.getMeasuredHeight() + " i: " + i);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
        Log.d(TAG, "listviewutil total height:: " + totalHeight);
        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static int getSingleRowHeight(ListView listView, int position) {
        ArrayAdapter<?> listAdapter = (ArrayAdapter<?>) listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return 0;
        }
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        View listItem = null;
        try {
            listItem = listAdapter.getView(position, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return listItem != null ? listItem.getMeasuredHeight() : 0;
    }

    public static int getListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return -1;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            Log.i("measure", " i: " + i + " size: " + listItem.getMeasuredHeight());
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight;
    }
    public static void setListViewHeight( ListView listView, int totalHeight )
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setViewWidths(View view, View[] views) {
        int w = view.getWidth();
        int h = view.getHeight();
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.layout((i + 1) * w, 0, (i + 2) * w, h);
            printView("view[" + i + "]", v);
        }
    }

    public static void printView(String msg, View v) {
        System.out.println(msg + "=" + v);
        if (null == v) {
            return;
        }
        System.out.print("[" + v.getLeft());
        System.out.print(", " + v.getTop());
        System.out.print(", w=" + v.getWidth());
        System.out.println(", h=" + v.getHeight() + "]");
        System.out.println("mw=" + v.getMeasuredWidth() + ", mh=" + v.getMeasuredHeight());
        System.out.println("scroll [" + v.getScrollX() + "," + v.getScrollY() + "]");
    }

    public static void initListView(Context context, ListView listView, String prefix, int numItems, int layout) {
        // By using setAdpater method in listview we an add string array in list.
        String[] arr = new String[numItems];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = prefix + (i + 1);
        }
        listView.setAdapter(new ArrayAdapter<String>(context, layout, arr));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                String msg = "item[" + position + "]=" + parent.getItemAtPosition(position);
                //Toast.makeText(context, msg, 1000).show();
                System.out.println(msg);
            }
        });
    }




}
