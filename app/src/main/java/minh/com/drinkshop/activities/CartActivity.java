package minh.com.drinkshop.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.CartAdapter;
import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;
import minh.com.drinkshop.utils.RecyclerItemTouchHelper;
import minh.com.drinkshop.utils.RecyclerItemTouchHelperListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterViewHolder.ClickListener, RecyclerItemTouchHelperListener {


    private static final int PAYMENT_REQUEST_CODE = 1234;
    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.btn_place_oder)
    Button btnPlaceOder;

    private CartAdapter mAdapter;
    private List<Cart> localCartList;

    String[] token;
    String amount, orderAddress, orderComment;
    HashMap<String, String> paramsHash;

    IDrinkShopAPI mService;
    IDrinkShopAPI mScalarsService;

    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        //set up service
        mService = Common.getAPI();
        mScalarsService = Common.getScalarAPI();

        //setup itemtouchhelper for swipe to delete
        setupItemTouchHelper();

        setupRecyclerCart();

        new getToken().execute();
    }

    //get token using braintree API
    private class getToken extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CartActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(Common.API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //set token
                            token = responseBody.split("\r\n");
                            //set button enable
                            btnPlaceOder.setEnabled(true);
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    //set button enable
                    btnPlaceOder.setEnabled(false);
                    Log.d("doInBackground ", exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
        }
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerCart);
    }

    private void setupRecyclerCart() {
        localCartList = new ArrayList<>();

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
        localCartList = carts;
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

    //on view click
    @OnClick(R.id.btn_place_oder)
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_place_oder:
                placeOrderToServer();
                break;
            default:
                break;
        }
    }

    private void placeOrderToServer() {
        if (Common.currentUser != null) {


            //create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Place Order");

            //setup view
            View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout, null);

            final EditText edt_comment = submit_order_layout.findViewById(R.id.edt_comment);
            final EditText edt_other_address = submit_order_layout.findViewById(R.id.edt_other_address);

            final RadioButton rdi_user_address = submit_order_layout.findViewById(R.id.rdi_user_address);
            final RadioButton rdi_other_address = submit_order_layout.findViewById(R.id.rdi_other_address);

            //set event
            rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        edt_other_address.setEnabled(false);
                    }
                }
            });
            rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        edt_other_address.setEnabled(true);
                    }
                }
            });

            builder.setView(submit_order_layout);

            //set button dialog
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get data input
                    orderComment = edt_comment.getText().toString();
                    if (rdi_user_address.isChecked()) {
                        orderAddress = Common.currentUser.getAddress().toString();
                    } else if (rdi_other_address.isChecked()) {
                        orderAddress = edt_other_address.getText().toString();
                    } else {
                        orderAddress = "";
                    }

                    //submit payment
                    DropInRequest dropInRequest = new DropInRequest().clientToken(String.valueOf(token[1]));
                    startActivityForResult(dropInRequest.getIntent(CartActivity.this), PAYMENT_REQUEST_CODE);

                    //submit order to server
                /*compositeDisposable.add(Common.cartRepository.getCartItems()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                if (!TextUtils.isEmpty(orderAddress)) {
                                    sendOrderToServer(Common.cartRepository.sumPrice(),
                                            carts,
                                            orderComment,
                                            orderAddress);
                                } else {
                                    Toast.makeText(CartActivity.this, "Order address can null! Please input...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }));*/


                }
            });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("Not Login");
            builder.setMessage("Please login or register account to submit order!");

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //required login
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();
                }
            });

            builder.show();
        }

    }

    private void sendOrderToServer(double sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        if (carts.size() > 0) {
            String oderDetail = new Gson().toJson(carts);
            mService.submitOrder(oderDetail, Common.currentUser.getPhone(), orderAddress, orderComment, sumPrice)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(CartActivity.this, "Order submit to server! ", Toast.LENGTH_SHORT).show();

                                //clear cart when submit success
                                Common.cartRepository.emptyCart();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("sendOrderToServer fail", t.getMessage());
                        }
                    });
        }
    }


    //swiped to left for delete
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartAdapterViewHolder) {
            String name = localCartList.get(viewHolder.getAdapterPosition()).name;//get name of Favorite

            final Cart deletedItem = localCartList.get(viewHolder.getAdapterPosition());// item favorite position deleted

            final int deletedIndex = viewHolder.getAdapterPosition();//position index of item deleted
            //deleted item from adapter
            mAdapter.removeItem(deletedIndex);
            //deleted from room database
            Common.cartRepository.deleteCartItem(deletedItem);


            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" removed from Cart!").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //restore item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    //update room data
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();
                if (Common.cartRepository.sumPrice() > 0) {
                    amount = String.valueOf(Common.cartRepository.sumPrice());
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount", amount);
                    paramsHash.put("nonce", strNonce);

                    sendPayments();
                } else {
                    Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User Cancel", Toast.LENGTH_SHORT).show();
            } else {
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("MainActivity Error ", exception.toString());
            }
        }

    }

    private void sendPayments() {
        //get data from params hash
        String amount = paramsHash.get("amount");
        String nonce = paramsHash.get("nonce");
        mScalarsService.payment(nonce, amount)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().toString().contains("Successful")) {
                            Toast.makeText(CartActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();

                            //submit order to server
                            compositeDisposable.add(Common.cartRepository.getCartItems()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {
                                            if (!TextUtils.isEmpty(orderAddress)) {
                                                sendOrderToServer(Common.cartRepository.sumPrice(),
                                                        carts,
                                                        orderComment,
                                                        orderAddress);
                                            } else {
                                                Toast.makeText(CartActivity.this, "Order address can null! Please input...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }));


                        } else {
                            Toast.makeText(CartActivity.this, "Transaction fail!", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("payment ", response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("payment ", t.getMessage().toString());
                    }
                });
    }

}
