package minh.com.drinkshop.databases.datasource;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.modelDB.Cart;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public class CartRepository implements ICartDataSource {

    private ICartDataSource iCartDataSource;
    private static CartRepository instance;

    public CartRepository(ICartDataSource iCartDataSource) {
        this.iCartDataSource = iCartDataSource;
    }

    public static CartRepository getInstance(ICartDataSource iCartDataSource) {
        if (instance == null) {
            instance = new CartRepository(iCartDataSource);
        }
        return instance;
    }

    @Override
    public Flowable<List<Cart>> getCartItems() {
        return iCartDataSource.getCartItems();
    }

    @Override
    public Flowable<List<Cart>> getCartItemsByID(int cartItemId) {
        return iCartDataSource.getCartItemsByID(cartItemId);
    }

    @Override
    public int countCartItems() {
        return iCartDataSource.countCartItems();
    }

    @Override
    public void emptyCart() {
        iCartDataSource.emptyCart();
    }

    @Override
    public void insertToCart(Cart... carts) {
        iCartDataSource.insertToCart(carts);
    }

    @Override
    public void updateToCart(Cart... carts) {
        iCartDataSource.updateToCart(carts);
    }

    @Override
    public void deleteCartItem(Cart... carts) {
        iCartDataSource.deleteCartItem(carts);
    }
}
