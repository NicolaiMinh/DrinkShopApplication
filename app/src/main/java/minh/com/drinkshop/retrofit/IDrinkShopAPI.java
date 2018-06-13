package minh.com.drinkshop.retrofit;

import java.util.List;

import io.reactivex.Observable;
import minh.com.drinkshop.model.Banner;
import minh.com.drinkshop.model.Category;
import minh.com.drinkshop.model.CheckUserResponse;
import minh.com.drinkshop.model.Drink;
import minh.com.drinkshop.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by sgvn144 on 2018/06/13.
 */

public interface IDrinkShopAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User> createNewUser(@Field("phone") String phone,
                             @Field("name") String name,
                             @Field("birthday") String birthday,
                             @Field("address") String address);

    @FormUrlEncoded
    @POST("getdrinkbymenuid.php")
    Observable<List<Drink>> getDrinkByMenuId(@Field("menuid") String menuid);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    Observable<List<Banner>> getBanners();

    @GET("getmenu.php")
    Observable<List<Category>> getMenu();
}
