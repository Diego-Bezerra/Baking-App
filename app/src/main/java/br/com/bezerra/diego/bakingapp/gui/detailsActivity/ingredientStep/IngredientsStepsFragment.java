package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;
import br.com.bezerra.diego.bakingapp.data.database.util.IngredientsProviderUtil;
import br.com.bezerra.diego.bakingapp.data.database.util.StepsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient.IngredientsFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.step.StepFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
        , IngredientsStepsAdapter.CardItemClickListerner {

    private final int LOADER_INGREDIENTS_ID = 4;
    private final int LOADER_STEPS_ID = 5;
    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";

    @BindView(R.id.ingredientsStepsList)
    RecyclerView ingredientsStepsList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private IngredientsStepsAdapter ingredientsStepsAdapter;
    private long recipeId;
    private String recipeTitle;
    private List<BaseModelAdapter> adapterData = new ArrayList<>();
    private DetailsActivityFragmentListener detailsActivityFragmentListener;

    public static IngredientsStepsFragment newInstance(long recipeId, String recipeTitle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        IngredientsStepsFragment fragment = new IngredientsStepsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
        bundle.putString(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        bundle.putSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA, detailsActivityFragmentListener);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void loadRecipe(long recipeId, String recipeTitle) {

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(recipeTitle);
        }

        if (getActivity() != null && getLoaderManager().getLoader(LOADER_INGREDIENTS_ID) == null) {

            this.recipeId = recipeId;
            this.recipeTitle = recipeTitle;

            getLoaderManager().initLoader(LOADER_INGREDIENTS_ID, null, this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(DetailsActivity.RECIPE_ID_EXTRA)) {
                recipeId = bundle.getLong(DetailsActivity.RECIPE_ID_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.FRAGMENT_LISTENER_EXTRA)) {
                detailsActivityFragmentListener = (DetailsActivityFragmentListener) bundle.getSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.RECIPE_TITLE_EXTRA)) {
                recipeTitle = bundle.getString(DetailsActivity.RECIPE_TITLE_EXTRA);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients_steps, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupIngredientsStepsList();

        if (!getResources().getBoolean(R.bool.isSmallestWidth)) {
            loadRecipe(recipeId, recipeTitle);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LOADER_INGREDIENTS_ID);
        getLoaderManager().destroyLoader(LOADER_STEPS_ID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupIngredientsStepsList() {
        ingredientsStepsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsStepsList.setHasFixedSize(true);
        ingredientsStepsAdapter = new IngredientsStepsAdapter();
        ingredientsStepsAdapter.setCardItemClickListerner(this);
        ingredientsStepsList.setAdapter(ingredientsStepsAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_INGREDIENTS_ID:
                progress.setVisibility(View.VISIBLE);
                return IngredientsProviderUtil.getIngredientsByRecipeId(recipeId, getContext());
            case LOADER_STEPS_ID:
                return StepsProviderUtil.getStepsByRecipeId(recipeId, getContext());
            default:
                throw new IllegalArgumentException("No loader id found");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_INGREDIENTS_ID:
                adapterData.clear();
                addIngredients(data);
                if (getLoaderManager().getLoader(LOADER_STEPS_ID) == null) {
                    getLoaderManager().initLoader(LOADER_STEPS_ID, null, this);
                }
                break;
            case LOADER_STEPS_ID:
                addSteps(data);
                ingredientsStepsAdapter.swipeData(adapterData);
                ingredientsStepsList.setAdapter(ingredientsStepsAdapter);
                progress.setVisibility(View.GONE);

                if (adapterData.size() == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    ingredientsStepsList.setVisibility(View.GONE);
                }
                if (getResources().getBoolean(R.bool.isSmallestWidth)) {
                    IngredientsFragment fragment = IngredientsFragment.newInstance(recipeId, recipeTitle, null);
                    loadFragment(fragment);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private void addIngredients(Cursor data) {

        if (data.moveToFirst()) {

            IngredientModelAdapter ingredientModelAdapter = new IngredientModelAdapter();
            ingredientModelAdapter.setId(data.getLong(data.getColumnIndex(IngredientContract._ID)));
            ingredientModelAdapter.setViewType(IngredientsStepsAdapter.INGREDIENT_VIEW_TYPE);
            ingredientModelAdapter.setRecipeId(data.getLong(data.getColumnIndex(IngredientContract.RECIPE)));

            final String initStr = "- ";
            StringBuilder ingredientStrBuilder = new StringBuilder(initStr);
            ingredientStrBuilder.append(data.getString(data.getColumnIndex(IngredientContract.INGREDIENT)));
            int i = 0;
            while (data.moveToNext()) {
                ingredientStrBuilder.append(initStr);
                ingredientStrBuilder.append(data.getString(data.getColumnIndex(IngredientContract.INGREDIENT)));
                if (i != data.getCount() - 1) {
                    ingredientStrBuilder.append("\n");
                }
                i++;
            }
            ingredientModelAdapter.setIngredient(ingredientStrBuilder.toString());
            adapterData.add(ingredientModelAdapter);
        }
    }

    private void addSteps(Cursor data) {

        data.moveToFirst();

        while (data.moveToNext()) {
            StepModelAdapter stepModelAdapter = new StepModelAdapter();
            stepModelAdapter.setId(data.getInt(data.getColumnIndex(StepContract._ID)));
            stepModelAdapter.setViewType(IngredientsStepsAdapter.STEP_VIEW_TYPE);
            stepModelAdapter.setDescription(data.getString(data.getColumnIndex(StepContract.DESCRIPTION)));
            stepModelAdapter.setShortDescription(data.getString(data.getColumnIndex(StepContract.SHORT_DESCRIPTION)));
            stepModelAdapter.setThumbnailURL(data.getString(data.getColumnIndex(StepContract.THUMBNAIL_URL)));
            stepModelAdapter.setVideoURL(data.getString(data.getColumnIndex(StepContract.VIDEO_URL)));
            adapterData.add(stepModelAdapter);
        }
    }

    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if (!getResources().getBoolean(R.bool.isSmallestWidth)) {
                transaction.addToBackStack(null);
            }
            transaction.replace(R.id.fragmentContainer, fragment).commit();
        }
    }

    @Override
    public void onIngredientCardItemClick(long recipeId) {
        IngredientsFragment fragment = IngredientsFragment.newInstance(recipeId, recipeTitle, null);
        loadFragment(fragment);
    }

    @Override
    public void onStepCardItemClick(long stepId, int position) {
        StepFragment fragment = StepFragment.newInstance(stepId, position, recipeTitle, detailsActivityFragmentListener);
        loadFragment(fragment);
    }
}
