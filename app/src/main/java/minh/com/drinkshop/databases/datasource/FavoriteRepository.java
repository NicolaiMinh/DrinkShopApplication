package minh.com.drinkshop.databases.datasource;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Favorite;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public class FavoriteRepository implements IFavoriteDataSource {

    private IFavoriteDataSource iFavoriteDataSource;
    private static FavoriteRepository instance;

    public FavoriteRepository(IFavoriteDataSource iFavoriteDataSource) {
        this.iFavoriteDataSource = iFavoriteDataSource;
    }

    public static FavoriteRepository getInstance(IFavoriteDataSource iFavoriteDataSource) {
        if (instance == null) {
            instance = new FavoriteRepository(iFavoriteDataSource);
        }
        return instance;
    }


    @Override
    public Flowable<List<Favorite>> getFavoriteItems() {
        return iFavoriteDataSource.getFavoriteItems();
    }

    @Override
    public int isFavorite(int favoriteItemId) {
        return iFavoriteDataSource.isFavorite(favoriteItemId);
    }

    @Override
    public void deleteFavoriteItem(Favorite favorite) {
        iFavoriteDataSource.deleteFavoriteItem(favorite);
    }

    @Override
    public void insertFavorite(Favorite... favorites) {
        iFavoriteDataSource.insertFavorite(favorites);
    }
}
