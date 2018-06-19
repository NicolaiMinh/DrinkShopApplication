package minh.com.drinkshop.databases.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Favorite;

/**
 * Created by sgvn144 on 2018/06/15.
 */

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM Favorite")
    Flowable<List<Favorite>> getFavoriteItems();//get all information trong Favorite

    @Query("SELECT EXISTS (SELECT 1 FROM Favorite WHERE id=:favoriteItemId) ")
    int isFavorite(int favoriteItemId);//get Favorite information by FavoriteID

    @Insert
    void insertFavorite(Favorite... favorites);

    @Delete
    void deleteFavoriteItem(Favorite favorite);//delete Favorite
}

/*
    Vậy bạn nên sử dụng Flowable khi:

        Thực thi 10k+ đối tượng khi mà chúng được generate ở nhiều nơi khác nhau mà cần liên kết để giảm số tài nguyên cần cung cấp
        Đọc file mà có dung lượng lớn cần hạn chế số line đọc để tránh việc treo bộ nhớ
        Đọc dữ liệu database thông qua kết nối JDBC, được điều khiển qua việc gọi phương thức ResultSet.next() cho mỗi yêu cầu xử lý
        Truyền tải dữ liệu network IO
        Sử dụng nhiều khối dữ liệu hoặc dữ liệu kiểu pull-based để lấy các dữ liệu dạng non-block qua các API reactive trong tương lai.
        */