package br.com.bezerra.diego.bakingapp.data.database.contract;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface RecipeContract {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT) @NotNull
    String SERVINGS = "servings";

    @DataType(DataType.Type.TEXT)
    String IMAGE = "image";

}
