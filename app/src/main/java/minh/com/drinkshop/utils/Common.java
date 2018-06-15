package minh.com.drinkshop.utils;

import java.util.ArrayList;
import java.util.List;

import minh.com.drinkshop.databases.datasource.CartRepository;
import minh.com.drinkshop.databases.local.CartDatabase;
import minh.com.drinkshop.model.Category;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.model.User;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.retrofit.RetrofitClient;

/**
 * Created by sgvn144 on 2018/06/13.
 */

public class Common {
    public static final String BASE_URL = "http://10.0.2.2/drinkshop/";//change localhost to 10.0.2.2

    public static final String TOPPING_MENU_ID = "3";// ID =3 TRONG MYSQL
    public static User currentUser = null;
    public static Category currentCategory = null;

    //save data khi fetch tu server
    public static List<Drink> toppingList = new ArrayList<>();

    //hold data khi chon nhieu loai topping o cart, hold sum of topping price
    public static double toppingPrice = 0.0;
    //hold data khi chon nhieu topping. List hold topping value
    public static List<String> toppingAdded = new ArrayList<>();

    //hold field khi chon trong addtocart view
    public static int sizeOfCup = -1;//-1: no choose -> error, 0: Medium, 1: Large
    public static int sugar = -1;//-1: no choose -> error
    public static int ice = -1;//-1: no choose -> error

    //create retrofit instance
    public static IDrinkShopAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL)
                .create(IDrinkShopAPI.class);
    }

    //create room instance
    public static CartDatabase cartDatabase;
    public static CartRepository cartRepository;


}
