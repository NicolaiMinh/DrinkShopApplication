package minh.com.drinkshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.RelativeLayout;
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
import minh.com.drinkshop.adapter.OrderAdapter;
import minh.com.drinkshop.model.Order;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;

public class ShowOrderActivity extends AppCompatActivity implements OrderAdapter.OrderAdapterViewHolder.ClickListener {

    @BindView(R.id.recycler_orders)
    RecyclerView recyclerOrders;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    IDrinkShopAPI mService;
    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private OrderAdapter mAdapter;
    private List<Order> localOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);
        ButterKnife.bind(this);

        //set up service
        mService = Common.getAPI();

        setupRecyclerOrder();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.order_new) {
                    loadListOrderItems("0");
                } else if (item.getItemId() == R.id.order_cancel) {
                    loadListOrderItems("-1");
                } else if (item.getItemId() == R.id.order_processing) {
                    loadListOrderItems("1");
                } else if (item.getItemId() == R.id.order_shipping) {
                    loadListOrderItems("2");
                } else if (item.getItemId() == R.id.order_shipped) {
                    loadListOrderItems("3");
                }
                return true;
            }
        });
    }

    private void setupRecyclerOrder() {
        localOrderList = new ArrayList<>();

        recyclerOrders.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerOrders.setLayoutManager(linearLayoutManager);

        loadListOrderItems("0");
    }

    private void loadListOrderItems(String statusCode) {
        if (Common.currentUser != null) {
            compositeDisposable.add(mService.getAllOrdersByStatus(Common.currentUser.getPhone(),
                    statusCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Order>>() {
                        @Override
                        public void accept(List<Order> orders) throws Exception {
                            displayOderItem(orders);
                        }
                    }));
        } else {
            Toast.makeText(this, "Please login!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void displayOderItem(List<Order> orders) {
        localOrderList = orders;
        mAdapter = new OrderAdapter(this, orders, this);
        recyclerOrders.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListOrderItems("0");
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onCLickItem(int position) {
        Common.currentOrder = mAdapter.getOrderList().get(position);
        startActivity(new Intent(ShowOrderActivity.this, OrderDetailActivity.class));
    }
}
