package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;

public class IngredientModelAdapter extends BaseModelAdapter {

    private long recipeId;
    private String ingredient;

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}