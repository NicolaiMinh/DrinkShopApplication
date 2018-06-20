package minh.com.drinkshop.databases.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Cart;

/**
 * Created by sgvn144 on 2018/06/15.
 */

@Dao
public interface CartDAO {

    @Query("SELECT * FROM Cart")
    Flowable<List<Cart>> getCartItems();//get all information trong cart

    @Query("SELECT * FROM Cart WHERE id=:cartItemId")
    Flowable<List<Cart>> getCartItemsByID(int cartItemId);//get cart information by cartID

    @Query("SELECT COUNT(*) FROM Cart")
    int countCartItems();//dem so luong cart


    @Query("SELECT SUM(price) FROM Cart")
    double sumPrice();//dem tong so tien trong cart


    @Query("DELETE FROM Cart")
    void emptyCart();//xoa data cart

    @Insert
    void insertToCart(Cart... carts);//insert new cart

    @Update
    void updateToCart(Cart... carts);//update cart

    @Delete
    void deleteCartItem(Cart... carts);//delete cart


}

/*
    Vậy bạn nên sử dụng Flowable khi:

        Thực thi 10k+ đối tượng khi mà chúng được generate ở nhiều nơi khác nhau mà cần liên kết để giảm số tài nguyên cần cung cấp
        Đọc file mà có dung lượng lớn cần hạn chế số line đọc để tránh việc treo bộ nhớ
        Đọc dữ liệu database thông qua kết nối JDBC, được điều khiển qua việc gọi phương thức ResultSet.next() cho mỗi yêu cầu xử lý
        Truyền tải dữ liệu network IO
        Sử dụng nhiều khối dữ liệu hoặc dữ liệu kiểu pull-based để lấy các dữ liệu dạng non-block qua các API reactive trong tương lai.
        */