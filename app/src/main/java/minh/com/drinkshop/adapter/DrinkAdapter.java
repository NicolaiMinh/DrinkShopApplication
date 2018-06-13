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
import minh.com.drinkshop.model.Drink;

/**
 * Created by May Chu on 6/13/2018.
 */

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkAdapterViewHolder> {

    private Context context;
    private List<Drink> drinkList;

    private DrinkAdapterViewHolder.ClickListener mClickListener;

    public DrinkAdapter(Context context, List<Drink> categoryList, DrinkAdapter.DrinkAdapterViewHolder.ClickListener mClickListener) {
        this.context = context;
        this.drinkList = categoryList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public DrinkAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.drink_item_layout, parent, false);


        return new DrinkAdapterViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkAdapterViewHolder holder, int position) {
        Drink drink = drinkList.get(position);
        Picasso.with(context)
                .load(drink.getLink())
                .into(holder.image_product);
        holder.txt_drink_name.setText(drink.getName());
        holder.drink_price.setText(drink.getPrice());
    }

    @Override
    public int getItemCount() {
        return drinkList == null ? 0 : drinkList.size();
    }

    public List<Drink> getDrinkList() {
        return drinkList;
    }
    public static class DrinkAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private ImageView image_product;
        private TextView txt_drink_name, drink_price;


        public DrinkAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            image_product = itemView.findViewById(R.id.image_product);
            txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
            drink_price = itemView.findViewById(R.id.drink_price);
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
