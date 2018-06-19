package minh.com.drinkshop.databases.local;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.datasource.IFavoriteDataSource;
import minh.com.drinkshop.databases.modelDB.Favorite;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public class FavoriteDataSource implements IFavoriteDataSource {

    private FavoriteDAO favoriteDAO;
    private static FavoriteDataSource instance;

    public FavoriteDataSource(FavoriteDAO favoriteDAO) {
        this.favoriteDAO = favoriteDAO;
    }

    public static FavoriteDataSource getInstance(FavoriteDAO favoriteDAO) {
        if (instance == null) {
            instance = new FavoriteDataSource(favoriteDAO);
        }
        return instance;
    }


    @Override
    public Flowable<List<Favorite>> getFavoriteItems() {
        return favoriteDAO.getFavoriteItems();
    }

    @Override
    public int isFavorite(int favoriteItemId) {
        return favoriteDAO.isFavorite(favoriteItemId);
    }

    @Override
    public void deleteFavoriteItem(Favorite favorite) {
        favoriteDAO.deleteFavoriteItem(favorite);
    }

    @Override
    public void insertFavorite(Favorite... favorites) {
        favoriteDAO.insertFavorite(favorites);
    }
}
