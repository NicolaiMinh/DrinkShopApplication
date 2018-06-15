package minh.com.drinkshop.databases.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import minh.com.drinkshop.databases.modelDB.Cart;

/**
 * Created by sgvn144 on 2018/06/15.
 */

@Database(entities = {Cart.class}, version = 1)
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();
    private static CartDatabase instance;

    public static CartDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, CartDatabase.class, "Drinkshop")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

}
