package minh.com.drinkshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import minh.com.drinkshop.R;
import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.databases.modelDB.Favorite;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.utils.Common;

/**
 * Created by May Chu on 6/13/2018.
 */

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkAdapterViewHolder> implements CompoundButton.OnCheckedChangeListener {

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
    public void onBindViewHolder(@NonNull final DrinkAdapterViewHolder holder, final int position) {
        final Drink drink = drinkList.get(position);
        Picasso.with(context)
                .load(drink.getLink())
                .into(holder.image_product);
        holder.txt_drink_name.setText(drink.getName());
        holder.drink_price.setText(drink.getPrice());

        //onclick add to cart
        holder.btn_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToCartDialog(position);
            }
        });

        //on favorite
        if (Common.favoriteRepository.isFavorite(Integer.parseInt(drink.getID())) == 1) {
            holder.btn_favorite_cart.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            holder.btn_favorite_cart.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        holder.btn_favorite_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.favoriteRepository.isFavorite(Integer.parseInt(drink.getID())) != 1) {
                    addOrRemoveFavorite(drink, true);
                    holder.btn_favorite_cart.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    addOrRemoveFavorite(drink, false);
                    holder.btn_favorite_cart.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });
    }

    private void addOrRemoveFavorite(Drink drink, boolean isAdd) {
        Favorite favorite = new Favorite();
        favorite.id = drink.getID();
        favorite.link = drink.getLink();
        favorite.name = drink.getName();
        favorite.price = drink.getPrice();
        favorite.menuID = drink.getMenuId();

        if (isAdd) {
            //add
            Common.favoriteRepository.insertFavorite(favorite);
        }else{
            //remove
            Common.favoriteRepository.deleteFavoriteItem(favorite);
        }
    }

    private void showAddToCartDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context).inflate(R.layout.add_to_cart_layout, null);

        //image view element
        ImageView image_cart_product = itemView.findViewById(R.id.image_cart_product);

        //count view element
        final ElegantNumberButton txt_count = itemView.findViewById(R.id.txt_count);

        //txt_cart_product_name view element
        TextView txt_cart_product_name = itemView.findViewById(R.id.txt_cart_product_name);

        //edt_comment view element
        EditText edt_comment = itemView.findViewById(R.id.edt_comment);

        //size of cup view element
        RadioButton radio_sizeM = itemView.findViewById(R.id.radio_sizeM);
        RadioButton radio_sizeL = itemView.findViewById(R.id.radio_sizeL);
        radio_sizeM.setOnCheckedChangeListener(this);
        radio_sizeL.setOnCheckedChangeListener(this);


        //choose sugar view element
        RadioButton radio_sugar_100 = itemView.findViewById(R.id.radio_sugar_100);
        RadioButton radio_sugar_70 = itemView.findViewById(R.id.radio_sugar_70);
        RadioButton radio_sugar_50 = itemView.findViewById(R.id.radio_sugar_50);
        RadioButton radio_sugar_30 = itemView.findViewById(R.id.radio_sugar_30);
        RadioButton radio_sugar_none = itemView.findViewById(R.id.radio_sugar_none);

        radio_sugar_100.setOnCheckedChangeListener(this);
        radio_sugar_70.setOnCheckedChangeListener(this);
        radio_sugar_50.setOnCheckedChangeListener(this);
        radio_sugar_30.setOnCheckedChangeListener(this);
        radio_sugar_none.setOnCheckedChangeListener(this);


        //choose ice view element
        RadioButton radio_ice_100 = itemView.findViewById(R.id.radio_ice_100);
        RadioButton radio_ice_70 = itemView.findViewById(R.id.radio_ice_70);
        RadioButton radio_ice_50 = itemView.findViewById(R.id.radio_ice_50);
        RadioButton radio_ice_30 = itemView.findViewById(R.id.radio_ice_30);
        RadioButton radio_ice_none = itemView.findViewById(R.id.radio_ice_none);

        radio_ice_100.setOnCheckedChangeListener(this);
        radio_ice_70.setOnCheckedChangeListener(this);
        radio_ice_50.setOnCheckedChangeListener(this);
        radio_ice_30.setOnCheckedChangeListener(this);
        radio_ice_none.setOnCheckedChangeListener(this);

        //recycler topping
        RecyclerView recycler_topping = itemView.findViewById(R.id.recycler_topping);
        recycler_topping.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recycler_topping.setHasFixedSize(true);

        //get topping adapter
        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context, Common.toppingList);
        recycler_topping.setAdapter(adapter);


        //set data
        Picasso.with(context)
                .load(drinkList.get(position).getLink())
                .into(image_cart_product);
        txt_cart_product_name.setText(drinkList.get(position).getName());

        builder.setView(itemView);
        builder.setNegativeButton("Add to cart", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //validate data
                if (Common.sizeOfCup == -1) {//error
                    Toast.makeText(context, "Please choose size of cup.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.sugar == -1) {//error
                    Toast.makeText(context, "Please choose sugar.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Common.ice == -1) {//error
                    Toast.makeText(context, "Please choose ice.", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(position, txt_count.getNumber());
                dialog.dismiss();
            }
        });
        builder.show();


    }

    private void showConfirmDialog(final int position, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context).inflate(R.layout.confirm_add_to_cart_layout, null);


        //view element
        ImageView image_product = itemView.findViewById(R.id.image_product);
        final TextView txt_cart_product_name = itemView.findViewById(R.id.txt_cart_product_name);
        final TextView txt_cart_product_price = itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_cart_product_sugar = itemView.findViewById(R.id.txt_cart_product_sugar);
        TextView txt_cart_product_ice = itemView.findViewById(R.id.txt_cart_product_ice);
        final TextView txt_topping_extra = itemView.findViewById(R.id.txt_topping_extra);

        //set data
        Picasso.with(context)
                .load(drinkList.get(position).getLink())
                .into(image_product);
        txt_cart_product_name.setText(new StringBuffer(drinkList.get(position).getName()).append(" x")
                        .append(Common.sizeOfCup == 0 ? " Size M" : " Size L")
                .append(number).toString()// product x 3
                );//size of cup

        txt_cart_product_ice.setText(new StringBuffer("Ice: ").append(Common.ice).append("%").toString());
        txt_cart_product_sugar.setText(new StringBuffer("Sugar: ").append(Common.sugar).append("%").toString());

        double totalPrice = (Double.parseDouble(drinkList.get(position).getPrice()) * Double.parseDouble(number)) + Common.toppingPrice;

        if (Common.sizeOfCup == 1) {
            totalPrice += (0.5 * Double.parseDouble(number)); //size lon+ them 0.5$
        }



        StringBuilder topping_final_comment = new StringBuilder("");
        for (String line : Common.toppingAdded) {
            topping_final_comment.append(line).append("\n");
        }
        txt_topping_extra.setText(topping_final_comment);

        final double finalTotalPrice = Math.round(totalPrice);

        txt_cart_product_price.setText(new StringBuffer("$").append(finalTotalPrice));

        builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                try {
                    //add to room  database
                    //Create new Cart item
                    Cart cartItem = new Cart();
                    cartItem.name = drinkList.get(position).getName();
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.sugar = Common.sugar;
                    cartItem.ice = Common.ice;
                    cartItem.price = finalTotalPrice;
                    cartItem.toppingExtras = txt_topping_extra.getText().toString();
                    cartItem.link = drinkList.get(position).getLink();
                    cartItem.size = Common.sizeOfCup;

                    //add to database
                    Common.cartRepository.insertToCart(cartItem);

                    Log.d("DrinkAdapter", new Gson().toJson(cartItem));
                    Toast.makeText(context, "Save item to cart successful!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(itemView);
        builder.show();
    }

    private void resetData() {
        Common.sizeOfCup = -1;
        Common.ice = -1;
        Common.sizeOfCup = -1;
        Common.toppingPrice = 0.0;
        Common.toppingAdded = new ArrayList<>();
    }


    @Override
    public int getItemCount() {
        return drinkList == null ? 0 : drinkList.size();
    }

    public List<Drink> getDrinkList() {
        return drinkList;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_sizeM:
                    Common.sizeOfCup = 0;
                    break;
                case R.id.radio_sizeL:
                    Common.sizeOfCup = 1;
                    break;
                case R.id.radio_sugar_100:
                    Common.sugar = 100;
                    break;
                case R.id.radio_sugar_70:
                    Common.sugar = 70;
                    break;
                case R.id.radio_sugar_50:
                    Common.sugar = 50;
                    break;
                case R.id.radio_sugar_30:
                    Common.sugar = 30;
                    break;
                case R.id.radio_sugar_none:
                    Common.sugar = 0;
                    break;
                case R.id.radio_ice_100:
                    Common.ice = 100;
                    break;
                case R.id.radio_ice_70:
                    Common.ice = 70;
                    break;
                case R.id.radio_ice_50:
                    Common.ice = 50;
                    break;
                case R.id.radio_ice_30:
                    Common.ice = 30;
                    break;
                case R.id.radio_ice_none:
                    Common.ice = 0;
                    break;
            }
        }
    }

    public static class DrinkAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private ImageView image_product;
        private ImageView btn_add_cart, btn_favorite_cart;
        private TextView txt_drink_name, drink_price;


        public DrinkAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            image_product = itemView.findViewById(R.id.image_product);
            txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
            drink_price = itemView.findViewById(R.id.drink_price);
            btn_add_cart = itemView.findViewById(R.id.btn_add_cart);
            btn_favorite_cart = itemView.findViewById(R.id.btn_favorite_cart);
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
