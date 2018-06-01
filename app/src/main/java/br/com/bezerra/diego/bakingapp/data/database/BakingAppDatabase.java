package br.com.bezerra.diego.bakingapp.data.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;

@Database(version = BakingAppDatabase.VERSION)
public class BakingAppDatabase {

    public static final int VERSION = 1;

    @Table(RecipeContract.class)
    public static final String RECIPE = "recipe";

    @Table(StepContract.class)
    public static final String STEP = "step";

    @Table(IngredientContract.class)
    public static final String INGREDIENT = "ingredient";
}
