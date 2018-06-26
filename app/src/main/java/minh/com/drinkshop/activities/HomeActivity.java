package minh.com.drinkshop.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.CategoryAdapter;
import minh.com.drinkshop.databases.datasource.CartRepository;
import minh.com.drinkshop.databases.datasource.FavoriteRepository;
import minh.com.drinkshop.databases.local.CartDataSource;
import minh.com.drinkshop.databases.local.FavoriteDataSource;
import minh.com.drinkshop.databases.local.RoomDatabase;
import minh.com.drinkshop.model.Banner;
import minh.com.drinkshop.model.Category;
import minh.com.drinkshop.model.CheckUserResponse;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.model.User;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;
import minh.com.drinkshop.utils.ProgressRequestBody;
import minh.com.drinkshop.utils.UploadedCallback;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CategoryAdapter.CategoryAdapterViewHolder.ClickListener, UploadedCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int PICK_FILE_REQUEST = 1100;
    @BindView(R.id.recycler_menu)
    RecyclerView recyclerMenu;

    TextView txt_name, txt_phone;
    SliderLayout sliderLayout;

    CircleImageView imageAvatar;

    NotificationBadge badge;//so count tren shopping cart
    ImageView cart_icon;

    IDrinkShopAPI mService;

    Uri selectedFileUri;
    private CategoryAdapter mAdapter;
    private List<Category> categoryList;

    SwipeRefreshLayout swipeRefreshLayout;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = Common.getAPI();
        //setting slider
        sliderLayout = findViewById(R.id.slider);

        //swipe layout in content home
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header_view = navigationView.getHeaderView(0);
        txt_name = header_view.findViewById(R.id.txt_name);
        txt_phone = header_view.findViewById(R.id.txt_phone);
        imageAvatar = header_view.findViewById(R.id.imageAvatar);
        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.currentUser == null) {
                    chooseImage();//choose image from image avatar
                }
            }
        });


        //swipe refresh layout
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //get banners
                getBannersImage();
                setupRecyclerCategory();

                //get newest topping list
                getToppingList();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //get banners
                getBannersImage();
                getCategoryItem();

                //get newest topping list
                getToppingList();
            }
        });

        //init room persistence database
        initRoomPersistence();

//        checkSessionLogin();// if user already logged, just login again
//        checkSessionLoginWithSGVN();
    }

    private void checkSessionLoginWithSGVN() {
        final AlertDialog alertDialog = new SpotsDialog(HomeActivity.this);
        alertDialog.show();
        alertDialog.setMessage("Please wait...");
        mService.checkUserExists("123456789")
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse userResponse = response.body();
                        if (userResponse.isExists()) {
                            //request information of current user
                            mService.getUserInformation("01")
                                    .enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            Common.currentUser = response.body();
                                            if (Common.currentUser != null) {
                                                alertDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            alertDialog.dismiss();
                                            Log.d("Error", t.getMessage());
                                        }
                                    });
                        } else {
                            //if user not exist on database, just make login
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
    }

    private void checkSessionLogin() {
        if (AccountKit.getCurrentAccessToken() != null) {
            final AlertDialog alertDialog = new SpotsDialog(HomeActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please wait...");

            swipeRefreshLayout.setRefreshing(true);

            //Check exist user on server (My sql)
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    mService.checkUserExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if (userResponse.isExists()) {
                                        //request information of current user
                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        Common.currentUser = response.body();
                                                        if (Common.currentUser != null) {
                                                            alertDialog.dismiss();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                            //set user information
                                                            txt_name.setText(Common.currentUser.getName());
                                                            txt_phone.setText(Common.currentUser.getPhone());

                                                            //set imageAvatar
                                                            setImageAvatar();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        alertDialog.dismiss();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                        Log.d("Error", t.getMessage());
                                                    }
                                                });
                                    } else {
                                        //if user not exist on database, just make login
                                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("Error", accountKitError.getErrorType().getMessage());
                }
            });
        } else {
            AccountKit.logOut();
            //clear all activity
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    private void initRoomPersistence() {
        Common.roomDatabase = RoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.roomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.roomDatabase.favoriteDAO()));
    }

    private void setImageAvatar() {
        if (!TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
            Picasso.with(getBaseContext())
                    .load(new StringBuilder(Common.BASE_URL)
                            .append("user_avarta/")
                            .append(Common.currentUser.getAvatarUrl()).toString())
                    .into(imageAvatar);
        }
    }

    private void getToppingList() {
        compositeDisposable.add(mService.getDrinkByMenuId(Common.TOPPING_MENU_ID)//get topping list by id
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {

                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        Common.toppingList = drinks;
                    }
                }));
    }

    private void setupRecyclerCategory() {
        categoryList = new ArrayList<>();

        recyclerMenu.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerMenu.setLayoutManager(linearLayoutManager);

        getCategoryItem();
    }

    private void getCategoryItem() {//get menu
        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {

                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenu(categories);
                    }
                }));
    }

    private void displayMenu(List<Category> categories) {
        mAdapter = new CategoryAdapter(this, categories, this);
        recyclerMenu.setAdapter(mAdapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    private void getBannersImage() {
        compositeDisposable.add(mService.getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {

                    @Override
                    public void accept(List<Banner> banners) throws Exception {
                        displayImage(banners);
                    }
                }));
    }

    private void displayImage(List<Banner> banners) {
        HashMap<String, String> bannerMap = new HashMap<>();
        for (Banner item : banners) {
            bannerMap.put(item.getName(), item.getLink());
        }
        for (String name : bannerMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


    //exit application when press back
    boolean isBackButtonPress = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isBackButtonPress) {
                super.onBackPressed();
                return;
            }
            this.isBackButtonPress = true;
            Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (badge != null) {
            updateCartCount();
        }
        isBackButtonPress = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View view = menu.findItem(R.id.cart_menu).getActionView();//tro toi file menu_action_bar.
        badge = view.findViewById(R.id.badge);// so count trên hinh shopping

        cart_icon = view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to CartActivity
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });


        updateCartCount();

        return true;
    }

    private void updateCartCount() {
        if (badge == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.cartRepository.countCartItems() == 0) {//dem so cart item trong database
                    badge.setVisibility(View.INVISIBLE);
                } else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItems()));//set count cart item
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        } else if (id == R.id.search_menu) {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            // Handle the nav_sign_out action
            //create confirm dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit application");
            builder.setMessage("Do you want to exit application!");

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //logout account kit
                    AccountKit.logOut();
                    //clear all activity
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            builder.show();

        } else if (id == R.id.nav_favorite) {
            if(Common.currentUser != null){
                startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
            }else{
                Toast.makeText(this, "Please login to use this feature.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_show_order) {
            if(Common.currentUser != null){
                startActivity(new Intent(HomeActivity.this, ShowOrderActivity.class));
            }else{
                Toast.makeText(this, "Please login to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //implement onclik item in category list
    @Override
    public void onCLickItem(int position) {
        Common.currentCategory = mAdapter.getCategoryList().get(position);
        startActivity(new Intent(HomeActivity.this, DrinkActivity.class));

        Log.d(TAG, "position " + mAdapter.getCategoryList().get(position));
    }

    private void chooseImage() {
        //FileUtils cua aFileChooser
        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(), "Select a image"),
                PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data != null) {
                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !selectedFileUri.getPath().isEmpty()) {
                        imageAvatar.setImageURI(selectedFileUri);
                        uploadFileToServer();

                    } else {
                        Toast.makeText(this, "Cannot upload file to server!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private void uploadFileToServer() {
        if (selectedFileUri != null) {
            File file = FileUtils.getFile(this, selectedFileUri);
            String filename = new StringBuilder(Common.currentUser.getPhone())
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file, this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", filename, requestFile);
            final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone", Common.currentUser.getPhone());

            new Thread(new Runnable() {
                @Override


                public void run() {
                    mService.uploadFile(userPhone, body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }

    //implement upload callback

    @Override
    public void onProgressUpdate(int percentage) {

    }

}
