package minh.com.drinkshop.databases.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.Context;

import minh.com.drinkshop.databases.modelDB.Cart;
import minh.com.drinkshop.databases.modelDB.Favorite;

/**
 * Created by sgvn144 on 2018/06/15.
 */

@Database(entities = {Cart.class, Favorite.class}, version = 1)
public abstract class RoomDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();
    private static RoomDatabase instance;

    public static RoomDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, RoomDatabase.class, "Drinkshop")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

}
