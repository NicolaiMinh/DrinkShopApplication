package minh.com.drinkshop.databases.modelDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by sgvn144 on 6/19/2018.
 */

@Entity(tableName = "Favorite")
public class Favorite {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    public String id;//id of favorite

    @ColumnInfo(name = "name")
    public String name;// cart name

    @ColumnInfo(name = "link")
    public String link;// link image name

    @ColumnInfo(name = "price")
    public String price;//total price of cart

    @ColumnInfo(name = "menuID")
    public String menuID;// luong sugar chon
}
