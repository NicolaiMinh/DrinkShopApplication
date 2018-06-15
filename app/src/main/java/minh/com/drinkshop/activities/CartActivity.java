package minh.com.drinkshop.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.CartAdapter;
import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.utils.Common;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterViewHolder.ClickListener {

    @BindView(R.id.btn_place_oder)
    Button btnPlaceOder;
    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;

    private CartAdapter mAdapter;
    private List<Cart> cartList;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        setupRecyclerCart();

    }

    private void setupRecyclerCart() {
        cartList = new ArrayList<>();

        recyclerCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerCart.setLayoutManager(linearLayoutManager);

        loadListCartItems();

    }

    private void loadListCartItems() {
        compositeDisposable.add(Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayCartItem(carts);
                    }
                }));
    }

    private void displayCartItem(List<Cart> carts) {
        mAdapter = new CartAdapter(this, carts, this);
        recyclerCart.setAdapter(mAdapter);
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

    //onclick item on card
    @Override
    public void onCLickItem(int position) {

    }
}
