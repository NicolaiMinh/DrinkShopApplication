package minh.com.drinkshop.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import minh.com.drinkshop.R;
import minh.com.drinkshop.adapter.OrderDetailAdapter;
import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.model.Order;
import minh.com.drinkshop.utils.Common;

public class OrderDetailActivity extends AppCompatActivity implements OrderDetailAdapter.OrderDetailAdapterViewHolder.ClickListener {

    @BindView(R.id.txt_order_id)
    TextView txtOrderId;
    @BindView(R.id.txt_order_price)
    TextView txtOrderPrice;
    @BindView(R.id.txt_order_address)
    TextView txtOrderAddress;
    @BindView(R.id.txt_order_comment)
    TextView txtOrderComment;
    @BindView(R.id.txt_order_status)
    TextView txtOrderStatus;
    @BindView(R.id.recycler_order_detail)
    RecyclerView recyclerOrderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        setUpData();

        displayOrderDetail();
    }

    private void displayOrderDetail() {
        recyclerOrderDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderDetail.setHasFixedSize(true);

        List<Cart> orderDetail = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>() {
                }.getType());

        recyclerOrderDetail.setAdapter(new OrderDetailAdapter(this, orderDetail, this));
    }

    private void setUpData() {
        txtOrderId.setText(new StringBuilder("#").append(Common.currentOrder.getOrderID()));

        txtOrderPrice.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()));
        txtOrderAddress.setText(Common.currentOrder.getOrderAddress());
        txtOrderComment.setText(Common.currentOrder.getOrderComment());
        txtOrderStatus.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(Common.currentOrder.getOrderStatus())));
    }

    @Override
    public void onCLickItem(int position) {

    }
}
