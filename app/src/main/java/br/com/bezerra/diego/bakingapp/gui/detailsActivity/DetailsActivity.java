package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep.IngredientsStepsFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.step.StepFragment;

public class DetailsActivity extends AppCompatActivity implements DetailsActivityFragmentListener, FragmentManager.OnBackStackChangedListener {

    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";
    public static final String RECIPE_TITLE_EXTRA = "recipe_title_extra";
    public static final String FRAGMENT_LISTENER_EXTRA = "fragment_listener_extra";
    public static final String STEP_POSITION_EXTRA = "step_position_extra";
    public static final String STEP_ID_EXTRA = "step_id_extra";
    public static final String STEP_FRAGMENT_EXTRA = "step_fragament_extra";

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
            boolean isSmallestWidth = getResources().getBoolean(R.bool.isSmallestWidth);
            final String tag = getString(R.string.ingredients_steps_fragment_tag);

            if (!isSmallestWidth) {

                BaseFragment fragment;
                if (savedInstanceState == null) {
                    fragment = IngredientsStepsFragment.newInstance(recipeId, recipeTitle, this);
                } else {
                    int lastPosition = getSupportFragmentManager().getFragments().size() - 1;
                    fragment = (BaseFragment) getSupportFragmentManager().getFragments().get(lastPosition);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, tag)
                        .commit();

            } else {
                IngredientsStepsFragment ingredientsStepsFragment = (IngredientsStepsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.ingredientsStepsFragment);
                ingredientsStepsFragment.setDetailsActivityFragmentListener(this);
                ingredientsStepsFragment.loadRecipe(recipeId, recipeTitle);
            }
        }
    }

    @Override
    public void clickNextStep(@NonNull Integer position) {

        final String tag = getString(R.string.ingredients_steps_fragment_tag);

        IngredientsStepsFragment ingredientsStepsFragment = (IngredientsStepsFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (ingredientsStepsFragment != null) {
            ingredientsStepsFragment.clickStepPosition(position);
        }
    }

    @Override
    public void clickIngredient() {
        IngredientsStepsFragment ingredientsStepsFragment = (IngredientsStepsFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.ingredients_steps_fragment_tag));
        if (ingredientsStepsFragment != null) {
            ingredientsStepsFragment.clickIngredientPosition();
        }
    }

    @Override
    public void setFitSystemWindow(boolean fit) {
        ViewGroup viewGroup = findViewById(R.id.fragmentContainer);
        viewGroup.setFitsSystemWindows(fit);
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean isSmallestWidth = getResources().getBoolean(R.bool.isSmallestWidth);
        if (!isSmallestWidth) {
            int lastIndex = getSupportFragmentManager().getFragments().size() - 1;
            if (getSupportFragmentManager().getFragments().get(lastIndex) instanceof StepFragment) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        } else {
            finish();
        }

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
