package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import br.com.bezerra.diego.bakingapp.R;

public class DetailsActivity extends AppCompatActivity implements DetailsActivityFragmentListener {

    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";
    public static final String RECIPE_TITLE_EXTRA = "recipe_title_extra";

    private long recipeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_ID_EXTRA)) {

            String recipeTitle = bundle.getString(RECIPE_TITLE_EXTRA, "");
            setTitle(recipeTitle);
            recipeId = bundle.getLong(RECIPE_ID_EXTRA);
            Fragment fragment;

            if (savedInstanceState == null) {
                fragment = IngredientsStepsFragment.newInstance(recipeId, this);
            } else {
                fragment = getSupportFragmentManager().findFragmentByTag(IngredientsStepsFragment.FRAGMENT_TAG);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, IngredientsStepsFragment.FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onIngredientsCardClicked() {
        IngredientsFragment fragment = IngredientsFragment.newInstance(recipeId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, IngredientsFragment.FRAGMENT_TAG)
                .commit();
    }
}
