package minh.com.drinkshop.utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import minh.com.drinkshop.adapter.CartAdapter;
import minh.com.drinkshop.adapter.FavoriteAdapter;

/**
 * Created by sgvn144 on 6/19/2018.
 */

/**
 * Support recycler view onSwiped, onMove
 */
public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    //declare
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    //crtl O

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //tro toi view holder cua FavoriteAdapter.FavoriteAdapterViewHolder layout chua view foreground, background
        if (viewHolder instanceof FavoriteAdapter.FavoriteAdapterViewHolder) {
            View foregroundView = ((FavoriteAdapter.FavoriteAdapterViewHolder) viewHolder).view_foreground;

            getDefaultUIUtil().clearView(foregroundView);
        } else if (viewHolder instanceof CartAdapter.CartAdapterViewHolder) {
            //tro toi view holder cua CartAdapter.CartAdapterViewHolder layout chua view foreground, background
            View foregroundView = ((CartAdapter.CartAdapterViewHolder) viewHolder).view_foreground;

            getDefaultUIUtil().clearView(foregroundView);
        }

    }

    //must be declare
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            if (viewHolder instanceof FavoriteAdapter.FavoriteAdapterViewHolder) {
                View foregroundView = ((FavoriteAdapter.FavoriteAdapterViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);
            } else if (viewHolder instanceof CartAdapter.CartAdapterViewHolder) {
                View foregroundView = ((CartAdapter.CartAdapterViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteAdapterViewHolder) {
            View foregroundView = ((FavoriteAdapter.FavoriteAdapterViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof CartAdapter.CartAdapterViewHolder) {
            View foregroundView = ((CartAdapter.CartAdapterViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteAdapterViewHolder) {
            View foregroundView = ((FavoriteAdapter.FavoriteAdapterViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof CartAdapter.CartAdapterViewHolder) {
            View foregroundView = ((CartAdapter.CartAdapterViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

    }
}
