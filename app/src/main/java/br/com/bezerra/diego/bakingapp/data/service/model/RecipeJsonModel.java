package br.com.bezerra.diego.bakingapp.data.service.model;

import java.util.List;

public class RecipeJsonModel {

    private long id;
    private String name;
    private int servings;
    private String image;
    private List<IngredientJsonModel> ingredients;
    private List<StepJsonModel> steps;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<IngredientJsonModel> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientJsonModel> ingredients) {
        this.ingredients = ingredients;
    }

    public List<StepJsonModel> getSteps() {
        return steps;
    }

    public void setSteps(List<StepJsonModel> steps) {
        this.steps = steps;
    }
}
