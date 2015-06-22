package android.utility.ui.gridview;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
/**
 * Created by lqnhu on 6/21/15.
 */
public class GridViewUtil {
    /**
     * Resizes the gridview.
     * @param gridView
     * @param items count
     * @param columns column number
     */
    public static void resizeGridView(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }
        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;
        int maxHeight = 0;
        for(int i = 0; i< listAdapter.getCount(); i++ ){
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight = listItem.getMeasuredHeight();
            if(maxHeight < totalHeight){
                maxHeight = totalHeight;
            }
        }
        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            maxHeight *= rows;
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = maxHeight;
        gridView.setLayoutParams(params);
    }
}
