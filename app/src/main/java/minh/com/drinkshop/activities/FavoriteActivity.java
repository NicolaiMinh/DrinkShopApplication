package minh.com.drinkshop.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.FavoriteAdapter;
import minh.com.drinkshop.databases.modelDB.Favorite;
import minh.com.drinkshop.utils.Common;
import minh.com.drinkshop.utils.RecyclerItemTouchHelper;
import minh.com.drinkshop.utils.RecyclerItemTouchHelperListener;

public class FavoriteActivity extends AppCompatActivity implements FavoriteAdapter.FavoriteAdapterViewHolder.ClickListener, RecyclerItemTouchHelperListener {

    @BindView(R.id.recycler_favorite)
    RecyclerView recycler_favorite;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;


    private FavoriteAdapter mAdapter;
    private List<Favorite> localFavoriteList;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);

        //setup itemtouchhelper for swipe to delete
        setupItemTouchHelper();


        setupRecyclerFavorite();
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_favorite);
    }

    private void setupRecyclerFavorite() {
        localFavoriteList = new ArrayList<>();

        recycler_favorite.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_favorite.setLayoutManager(linearLayoutManager);

        loadListFavoriteItems();

    }

    private void loadListFavoriteItems() {
        compositeDisposable.add(Common.favoriteRepository.getFavoriteItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Favorite>>() {
                    @Override
                    public void accept(List<Favorite> favorites) throws Exception {
                        displayFavoriteItem(favorites);
                    }
                }));
    }

    private void displayFavoriteItem(List<Favorite> favorites) {
        localFavoriteList = favorites;
        mAdapter = new FavoriteAdapter(this, favorites, this);
        recycler_favorite.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListFavoriteItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onCLickItem(int position) {

    }


    //swiped to left for delete
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteAdapterViewHolder) {
            String name = localFavoriteList.get(viewHolder.getAdapterPosition()).name;//get name of Favorite

            final Favorite deletedItem = localFavoriteList.get(viewHolder.getAdapterPosition());// item favorite position deleted

            final int deletedIndex = viewHolder.getAdapterPosition();//position index of item deleted
            //deleted item from adapter
            mAdapter.removeItem(deletedIndex);
            //deleted from room database
            Common.favoriteRepository.deleteFavoriteItem(deletedItem);


            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" removed from Favorite!").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //restore item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    //update room data
                    Common.favoriteRepository.insertFavorite(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
