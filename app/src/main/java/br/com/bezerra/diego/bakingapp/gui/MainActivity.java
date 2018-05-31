package br.com.bezerra.diego.bakingapp.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.service.BakingAppService;
import br.com.bezerra.diego.bakingapp.data.service.model.RecipeModel;
import br.com.bezerra.diego.bakingapp.data.util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<List<RecipeModel>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BakingAppService bakingAppService = NetworkUtil.retrofit.create(BakingAppService.class);
        Call<List<RecipeModel>> listCall = bakingAppService.getRecipes();
        listCall.enqueue(this);
//        try {
//            listCall.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onResponse(Call<List<RecipeModel>> call, Response<List<RecipeModel>> response) {
        String nada = "";
    }

    @Override
    public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
        String nada = "";
    }
}
