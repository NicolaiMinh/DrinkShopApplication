package minh.com.drinkshop.utils;

import minh.com.drinkshop.model.User;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.retrofit.RetrofitClient;

/**
 * Created by sgvn144 on 2018/06/13.
 */

public class Common {
    private static final String BASE_URL = "http://10.0.2.2/drinkshop/";//change localhost to 10.0.2.2

    public static User currentUser = null;

    public static IDrinkShopAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL)
                .create(IDrinkShopAPI.class);
    }
}
