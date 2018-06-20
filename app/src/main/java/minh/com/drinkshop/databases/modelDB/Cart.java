package minh.com.drinkshop.databases.modelDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by sgvn144 on 2018/06/15.
 */

@Entity(tableName = "Cart")
public class Cart {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;//id of cart

    @ColumnInfo(name = "name")
    public String name;// cart name

    @ColumnInfo(name = "link")
    public String link;// link image name

    @ColumnInfo(name = "amount")
    public int amount;//so luong san pham

    @ColumnInfo(name = "price")
    public double price;//total price of cart

    @ColumnInfo(name = "sugar")
    public int sugar;// luong sugar chon

    @ColumnInfo(name = "ice")
    public int ice;//luong da chon

    @ColumnInfo(name = "size")
    public int size;//size da chon

    @ColumnInfo(name = "toppingExtras")
    public String toppingExtras;//luong topping extra chon

}
