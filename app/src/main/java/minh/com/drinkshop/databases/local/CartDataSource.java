package minh.com.drinkshop.databases.local;

import java.util.List;

import io.reactivex.Flowable;
import minh.com.drinkshop.databases.datasource.ICartDataSource;
import minh.com.drinkshop.databases.modelDB.Cart;

/**
 * Created by sgvn144 on 2018/06/15.
 */

public class CartDataSource implements ICartDataSource {

    private CartDAO cartDAO;
    private static CartDataSource instance;

    public CartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    public static CartDataSource getInstance(CartDAO cartDAO) {
        if (instance == null) {
            instance = new CartDataSource(cartDAO);
        }
        return instance;
    }

    @Override
    public Flowable<List<Cart>> getCartItems() {
        return cartDAO.getCartItems();
    }

    @Override
    public Flowable<List<Cart>> getCartItemsByID(int cartItemId) {
        return cartDAO.getCartItemsByID(cartItemId);
    }

    @Override
    public int countCartItems() {
        return cartDAO.countCartItems();
    }

    @Override
    public void emptyCart() {
        cartDAO.emptyCart();
    }

    @Override
    public void insertToCart(Cart... carts) {
        cartDAO.insertToCart(carts);
    }

    @Override
    public void updateToCart(Cart... carts) {
        cartDAO.updateToCart(carts);
    }

    @Override
    public void deleteCartItem(Cart... carts) {
        cartDAO.deleteCartItem(carts);
    }

    @Override
    public double sumPrice() {
        return cartDAO.sumPrice();
    }
}
