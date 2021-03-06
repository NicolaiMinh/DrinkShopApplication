package minh.com.drinkshop.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import minh.com.drinkshop.R;
import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.databases.modelDB.Favorite;
import minh.com.drinkshop.utils.Common;

/**
 * Created by May Chu on 6/13/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder> {

    private Context context;
    private List<Cart> cartList;

    private CartAdapterViewHolder.ClickListener mClickListener;

    public CartAdapter(Context context, List<Cart> cartList, CartAdapterViewHolder.ClickListener mClickListener) {
        this.context = context;
        this.cartList = cartList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public CartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.cart_item_layout, parent, false);


        return new CartAdapterViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapterViewHolder holder, final int position) {
        Cart cart = cartList.get(position);
        Picasso.with(context)
                .load(cart.link)
                .into(holder.image_product);
        holder.txt_product_name.setText(new StringBuilder(cart.name)
                .append(" x")
                .append(cart.amount)
                .append(cart.size == 0 ? " Size M" : " Size L"));
        holder.txt_amount.setNumber(String.valueOf(cart.amount));

        holder.txt_sugar_ice.setText(new StringBuffer("Sugar: ").append(cart.sugar)
                .append("%").append("\n")
                .append("Ice: ").append(cart.ice)
                .append("%")
                .toString());

        holder.txt_price.setText(new StringBuffer("$").append(cart.price));

        //get price of once cup with all options
        final double priceOfCup = cart.price / cart.amount;

        //auto save item when user change amount
        holder.txt_amount.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart = cartList.get(position);
                cart.amount = newValue;

                cart.price = Math.round(priceOfCup * newValue);

                Common.cartRepository.updateToCart(cart);

                holder.txt_price.setText(new StringBuilder("$").append(cart.price));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList == null ? 0 : cartList.size();
    }

    //deleted item from list with deletedIndex
    public void removeItem(int position) {
        cartList.remove(position);
        notifyItemRemoved(position);
    }

    //restore item from list with deletedIndex
    public void restoreItem(Cart item, int position) {
        cartList.add(position, item);
        notifyItemInserted(position);
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public static class CartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private ImageView image_product;
        private TextView txt_product_name, txt_sugar_ice, txt_price;
        private ElegantNumberButton txt_amount;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public CartAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            image_product = itemView.findViewById(R.id.image_product);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_amount = itemView.findViewById(R.id.txt_amount);

            view_background = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);
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
