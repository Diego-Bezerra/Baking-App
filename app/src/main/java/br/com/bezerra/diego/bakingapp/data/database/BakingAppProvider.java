package br.com.bezerra.diego.bakingapp.data.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;

@ContentProvider(authority = BakingAppProvider.AUTHORITY, database = BakingAppDatabase.class)
public class BakingAppProvider {

    public static final String AUTHORITY = "br.com.bezerra.diego.bakingapp.provider";

    @TableEndpoint(table = BakingAppDatabase.RECIPE)
    public static class Recipes {

        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes",
                defaultSort = RecipeContract._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    }

    @TableEndpoint(table = BakingAppDatabase.INGREDIENT)
    public static class Ingredients {

        @InexactContentUri(
                path = "ingredients/#",
                name = "INGREDIENTS_ID",
                type = "vnd.android.cursor.item/ingredients",
                whereColumn = IngredientContract.RECIPE,
                pathSegment = 1)
        public static Uri withRecipeId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/ingredients/" + id);
        }
    }

    @TableEndpoint(table = BakingAppDatabase.STEP)
    public static class Steps {

        @InexactContentUri(
                path = "steps/#",
                name = "STEPS_ID",
                type = "vnd.android.cursor.item/steps",
                whereColumn = IngredientContract.RECIPE,
                pathSegment = 1)
        public static Uri withRecipeId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/steps/" + id);
        }
    }
}
