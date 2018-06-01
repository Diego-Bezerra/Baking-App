package br.com.bezerra.diego.bakingapp.data.database.contract;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import br.com.bezerra.diego.bakingapp.data.database.BakingAppDatabase;

public interface StepContract {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @NotNull
    String ORDER_API = "orderApi";

    @DataType(DataType.Type.INTEGER) @References(table = BakingAppDatabase.RECIPE, column = RecipeContract._ID)
    String RECIPE = "recipe";

    @DataType(DataType.Type.INTEGER) @NotNull
    String SHORT_DESCRIPTION = "shortDescription";

    @DataType(DataType.Type.TEXT) @NotNull
    String DESCRIPTION = "description";

    @DataType(DataType.Type.TEXT) @NotNull
    String VIDEO_URL = "videoURL";

    @DataType(DataType.Type.TEXT) @NotNull
    String THUMBNAIL_URL = "thumbnailURL";
}
