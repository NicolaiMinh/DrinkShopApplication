package minh.com.drinkshop.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.DrinkAdapter;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;

public class DrinkActivity extends AppCompatActivity implements DrinkAdapter.DrinkAdapterViewHolder.ClickListener {

    @BindView(R.id.recycler_drink)
    RecyclerView recyclerDrink;
    @BindView(R.id.txt_menu_name)
    TextView txtMenuName;

    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeToRefresh;

    IDrinkShopAPI mService;

    private DrinkAdapter mAdapter;
    private List<Drink> drinkList;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        ButterKnife.bind(this);

        mService = Common.getAPI();


        swipeToRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeToRefresh.setRefreshing(true);
                setupRecyclerDrink();
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListDrink(Common.currentCategory.getID());
            }
        });

    }

    private void setupRecyclerDrink() {
        drinkList = new ArrayList<>();

        recyclerDrink.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerDrink.setLayoutManager(linearLayoutManager);

        loadListDrink(Common.currentCategory.getID());

        //set Header name
        txtMenuName.setText(Common.currentCategory.getName());
    }

    private void loadListDrink(String menuID) {
        compositeDisposable.add(mService.getDrinkByMenuId(menuID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrinkList(drinks);
                    }
                }));
    }

    private void displayDrinkList(List<Drink> drinkList) {
        mAdapter = new DrinkAdapter(this, drinkList, this);
        recyclerDrink.setAdapter(mAdapter);

        swipeToRefresh.setRefreshing(false);
    }


    @Override
    public void onCLickItem(int position) {

    }

    //exit application when press back
    boolean isBackButtonPress = false;

    @Override
    public void onBackPressed() {
        if (isBackButtonPress) {
            super.onBackPressed();
            return;
        }
        this.isBackButtonPress = true;
        Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isBackButtonPress = false;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

}
