package br.com.bezerra.diego.bakingapp.data.database.contract;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import br.com.bezerra.diego.bakingapp.data.database.BakingAppDatabase;

public interface IngredientContract {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @References(table = BakingAppDatabase.RECIPE, column = RecipeContract._ID)
    String RECIPE = "recipe";

    @DataType(DataType.Type.REAL) @NotNull
    String QUANTITY = "quantity";

    @DataType(DataType.Type.TEXT) @NotNull
    String MEASURE = "measure";

    @DataType(DataType.Type.TEXT) @NotNull
    String INGREDIENT = "ingredient";
}
