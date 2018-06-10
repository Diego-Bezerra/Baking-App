package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep.IngredientsStepsFragment;

public class DetailsActivity extends AppCompatActivity implements DetailsActivityFragmentListener, FragmentManager.OnBackStackChangedListener {

    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";
    public static final String RECIPE_TITLE_EXTRA = "recipe_title_extra";
    public static final String FRAGMENT_LISTENER_EXTRA = "fragment_listener_extra";
    public static final String STEP_POSITION_EXTRA = "step_position_extra";
    public static final String STEP_ID_EXTRA = "step_id_extra";

    private String recipeTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_ID_EXTRA)) {

            recipeTitle = bundle.getString(RECIPE_TITLE_EXTRA, "");
            long recipeId = bundle.getLong(RECIPE_ID_EXTRA);

            if (!getResources().getBoolean(R.bool.isSmallestWidth)) {
                Fragment fragment;
                if (savedInstanceState == null) {
                    fragment = IngredientsStepsFragment.newInstance(recipeId, recipeTitle, this);
                } else {
                    fragment = getSupportFragmentManager().findFragmentByTag(IngredientsStepsFragment.FRAGMENT_TAG);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, IngredientsStepsFragment.FRAGMENT_TAG)
                        .commit();
            } else {
                IngredientsStepsFragment ingredientsStepsFragment = (IngredientsStepsFragment) getSupportFragmentManager().findFragmentById(R.id.ingredientsStepsFragment);
                ingredientsStepsFragment.loadRecipe(recipeId, recipeTitle);
            }
        }
    }

    @Override
    public void setDefaultTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(recipeTitle);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;

    }

    @Override
    public void onBackStackChanged() {
        if (getSupportActionBar() != null) {
            boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
            getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        }
    }
}
