package minh.com.drinkshop.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sgvn144 on 6/26/2018.
 */

public class MyFirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        if (Common.currentUser != null) {
            updateTokenToFirebase();
        }
    }

    private void updateTokenToFirebase() {
        IDrinkShopAPI mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(), FirebaseInstanceId.getInstance().getToken(), "0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("MyFirebaseIDService ", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("MyFirebaseIDService ", t.getMessage());
                    }
                });
    }

}
