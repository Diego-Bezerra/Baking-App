package br.com.bezerra.diego.bakingapp.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtil {

    private static final String BAKING_APP_SERVICE_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    public static final Retrofit retrofit = newRetrofitInstance();

    private static Retrofit newRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BAKING_APP_SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
