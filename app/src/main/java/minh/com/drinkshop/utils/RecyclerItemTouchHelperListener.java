package minh.com.drinkshop.utils;

/**
 * Created by sgvn144 on 6/19/2018.
 */

import android.support.v7.widget.RecyclerView;

/**
 * Work for swipe to delete item in recycler view
 */
public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
