package minh.com.drinkshop.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by sgvn144 on 6/20/2018.
 */

//use for braintree payment
public class RetrofitScalarsClient {
    private static Retrofit retrofit = null;

    public static Retrofit getScalarsClient(String baseURL) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
