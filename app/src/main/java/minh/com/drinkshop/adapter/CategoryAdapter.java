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

/**
 * Created by May Chu on 6/13/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder> {

    private Context context;
    private List<Category> categoryList;

    private CategoryAdapterViewHolder.ClickListener mClickListener;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryAdapterViewHolder.ClickListener mClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.menu_item_layout, parent, false);


        return new CategoryAdapterViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterViewHolder holder, int position) {
        Category category = categoryList.get(position);
        Picasso.with(context)
                .load(category.getLink())
                .into(holder.image_product);
        holder.txt_menu_name.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public static class CategoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        private ImageView image_product;
        private TextView txt_menu_name;


        public CategoryAdapterViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            //view in custom_card_meal
            image_product = itemView.findViewById(R.id.image_product);
            txt_menu_name = itemView.findViewById(R.id.txt_menu_name);
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
