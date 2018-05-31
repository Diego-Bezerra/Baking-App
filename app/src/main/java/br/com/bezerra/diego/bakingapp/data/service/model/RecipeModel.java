package br.com.bezerra.diego.bakingapp.data.service.model;

import java.util.List;

public class RecipeModel {

    private long id;
    private String name;
    private int servings;
    private String image;
    private List<IngredientsModel> ingredients;
    private List<StepsModel> steps;

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

    public List<IngredientsModel> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientsModel> ingredients) {
        this.ingredients = ingredients;
    }

    public List<StepsModel> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsModel> steps) {
        this.steps = steps;
    }
}
