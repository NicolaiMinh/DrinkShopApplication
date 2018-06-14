package minh.com.drinkshop.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import minh.com.drinkshop.R;
import minh.com.drinkshop.model.Category;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.utils.Common;

/**
 * Created by May Chu on 6/13/2018.
 */

//dung cho khi multi choice chon topping
public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.MultiChoiceViewHolder> {

    private Context context;
    private List<Drink> optiionList;//option list topping


    public MultiChoiceAdapter(Context context, List<Drink> optiionList) {
        this.context = context;
        this.optiionList = optiionList;
    }

    @NonNull
    @Override
    public MultiChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //layout custom card meal
        View itemView = layoutInflater.inflate(R.layout.multi_check_layout, parent, false);

        return new MultiChoiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiChoiceViewHolder holder, int position) {
        final Drink drink = optiionList.get(position);
        holder.check_topping.setText(drink.getName());

        holder.check_topping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Common.toppingAdded.add(buttonView.getText().toString());// add vao list data khi chon nhieu item
                    Common.toppingPrice += Double.parseDouble(drink.getPrice());//tinh sum tat ca cac item trong list
                } else {
                    Common.toppingAdded.remove(buttonView.getText().toString());// xoa position list data khi bo chon nhieu item
                    Common.toppingPrice -= Double.parseDouble(drink.getPrice());//tinh substract khi cac item bi remove trong list
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return optiionList == null ? 0 : optiionList.size();
    }

    public List<Drink> getOptionList() {
        return optiionList;
    }

    public static class MultiChoiceViewHolder extends RecyclerView.ViewHolder {
        private CheckBox check_topping;

        public MultiChoiceViewHolder(View itemView) {
            super(itemView);
            //view in multi_check_layout
            check_topping = itemView.findViewById(R.id.check_topping);
        }


    }

}
