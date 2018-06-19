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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteAdapterViewHolder> {

    private Context context;
    private List<Favorite> favoriteList;

    private FavoriteAdapterViewHolder.ClickListener mClickListener;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList, FavoriteAdapterViewHolder.ClickListener mClickListener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public FavoriteAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.favorite_item_layout, parent, false);


        return new FavoriteAdapterViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapterViewHolder holder, final int position) {
        Favorite favorite = favoriteList.get(position);
        Picasso.with(context)
                .load(favorite.link)
                .into(holder.image_product);
        holder.txt_product_name.setText(favorite.name);
        holder.txt_price.setText(new StringBuffer("$").append(favorite.price));
    }

    @Override
    public int getItemCount() {
        return favoriteList == null ? 0 : favoriteList.size();
    }

    public List<Favorite> getFavoriteList() {
        return favoriteList;
    }

    //deleted item from list with deletedIndex
    public void removeItem(int position) {
        favoriteList.remove(position);
        notifyItemRemoved(position);
    }

    //restore item from list with deletedIndex
    public void restoreItem(Favorite item, int position) {
        favoriteList.add(position, item);
        notifyItemInserted(position);
    }

    public static class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private ImageView image_product;
        private TextView txt_product_name, txt_price;


        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public FavoriteAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            image_product = itemView.findViewById(R.id.image_product);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);

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
