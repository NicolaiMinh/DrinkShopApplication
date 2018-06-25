package minh.com.drinkshop.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import minh.com.drinkshop.R;
import minh.com.drinkshop.model.Category;
import minh.com.drinkshop.model.Order;
import minh.com.drinkshop.utils.Common;

/**
 * Created by May Chu on 6/13/2018.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderAdapterViewHolder> {

    private Context context;
    private List<Order> orderList;

    private OrderAdapterViewHolder.ClickListener mClickListener;

    public OrderAdapter(Context context, List<Order> orderList, OrderAdapterViewHolder.ClickListener mClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public OrderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.orders_item_layout, parent, false);


        return new OrderAdapterViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapterViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txt_order_id.setText(new StringBuilder("#").append(order.getOrderID()));

        holder.txt_order_price.setText(new StringBuilder("$").append(order.getOrderPrice()));
        holder.txt_order_address.setText(order.getOrderAddress());
        holder.txt_order_comment.setText(order.getOrderComment());
        holder.txt_order_status.setText(new StringBuilder("Order Status: ").append(Common.convertCodeToStatus(order.getOrderStatus())));
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public static class OrderAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private TextView txt_order_id,txt_order_price,txt_order_address,txt_order_comment,txt_order_status;


        public OrderAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_price = itemView.findViewById(R.id.txt_order_price);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_order_comment = itemView.findViewById(R.id.txt_order_comment);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onCLickItem(getAdapterPosition());
            }
        }

        //click on each item
        public interface ClickListener {
            void onCLickItem(int position);
        }

    }

}
