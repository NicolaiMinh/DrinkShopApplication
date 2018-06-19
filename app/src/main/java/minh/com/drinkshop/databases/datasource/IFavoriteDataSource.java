package minh.com.drinkshop.databases.datasource;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Favorite;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public interface IFavoriteDataSource {

    Flowable<List<Favorite>> getFavoriteItems();//get all information trong Favorite

    int isFavorite(int favoriteItemId);//get Favorite information by FavoriteID

    void deleteFavoriteItem(Favorite favorite);//delete Favorite

    void insertFavorite(Favorite... favorites);
}
