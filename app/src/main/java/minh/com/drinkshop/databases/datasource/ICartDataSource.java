package minh.com.drinkshop.databases.datasource;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Cart;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public interface ICartDataSource {

    Flowable<List<Cart>> getCartItems();//get all information trong cart

    Flowable<List<Cart>> getCartItemsByID(int cartItemId);//get cart information by cartID

    int countCartItems();//dem so luong cart

    void emptyCart();//xoa data cart

    void insertToCart(Cart... carts);//insert new cart

    void updateToCart(Cart... carts);//update cart

    void deleteCartItem(Cart... carts);//delete cart
}
