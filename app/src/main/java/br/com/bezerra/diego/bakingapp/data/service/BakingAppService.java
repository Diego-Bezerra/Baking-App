package br.com.bezerra.diego.bakingapp.data.service;

import java.util.List;

import br.com.bezerra.diego.bakingapp.data.service.model.RecipeJsonModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingAppService {
    @GET("baking.json")
    Call<List<RecipeJsonModel>> getRecipes();
}
