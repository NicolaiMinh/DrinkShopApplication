package minh.com.drinkshop.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.DrinkAdapter;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;

public class SearchActivity extends AppCompatActivity implements DrinkAdapter.DrinkAdapterViewHolder.ClickListener {

    @BindView(R.id.search_bar)
    MaterialSearchBar searchBar;
    @BindView(R.id.recycler_search)
    RecyclerView recyclerSearch;

    List<String> suggestList = new ArrayList<>();
    List<Drink> localDataSource = new ArrayList<>();

    IDrinkShopAPI mService;
    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DrinkAdapter searchAdapter, mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mService = Common.getAPI();
        setupRecyclerDrink();

        searchBar.setHint("Enter your drink");
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toString().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                searchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    recyclerSearch.setAdapter(mAdapter);//restore full list of drink
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        List<Drink> result = new ArrayList<>();
        for (Drink drink : localDataSource) {
            if (drink.getName().contains(text)) {
                result.add(drink);
            }
        }
        searchAdapter = new DrinkAdapter(this, result, this);
        recyclerSearch.setAdapter(searchAdapter);
    }

    private void setupRecyclerDrink() {
        recyclerSearch.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerSearch.setLayoutManager(linearLayoutManager);

        loadAllDrink();
    }

    private void loadAllDrink() {
        compositeDisposable.add(mService.getAllDrinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrinkList(drinks);
                        buildSuggestLink(drinks);
                    }
                }));
    }

    private void buildSuggestLink(List<Drink> drinks) {
        for (Drink drink : drinks) {
            suggestList.add(drink.getName());
        }
        //set search bar
        searchBar.setLastSuggestions(suggestList);
    }

    private void displayDrinkList(List<Drink> drinkList) {
        localDataSource = drinkList;
        mAdapter = new DrinkAdapter(this, drinkList, this);
        recyclerSearch.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onCLickItem(int position) {

    }
}
