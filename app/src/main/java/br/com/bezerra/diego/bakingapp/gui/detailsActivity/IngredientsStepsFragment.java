package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.BaseModelAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.IngredientModelAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.IngredientsStepsAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.StepModelAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_INGREDIENTS_ID = 2;
    private static final int LOADER_STEPS_ID = 3;
    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";
    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";

    @BindView(R.id.ingredientsStepsList)
    RecyclerView ingredientsStepsList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private IngredientsStepsAdapter ingredientsStepsAdapter;
    private int recipeId;
    private List<BaseModelAdapter> adapterData = new ArrayList<>();

    public static IngredientsStepsFragment newInstance(int recipeId) {
        IngredientsStepsFragment fragment = new IngredientsStepsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RECIPE_ID_EXTRA, recipeId);
        fragment.setArguments(bundle);

        return fragment;
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

        Bundle bundle = getArguments();
        if (getActivity() != null && bundle != null && bundle.containsKey(RECIPE_ID_EXTRA)) {
            recipeId = bundle.getInt(RECIPE_ID_EXTRA);
            getActivity().getSupportLoaderManager().initLoader(LOADER_INGREDIENTS_ID, null, this);
        }
    }

    private void setupIngredientsStepsList() {
        ingredientsStepsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsStepsList.setHasFixedSize(true);
        ingredientsStepsAdapter = new IngredientsStepsAdapter();
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
                addIngredients(data);
                if (getActivity() != null) {
                    getActivity().getSupportLoaderManager().initLoader(LOADER_STEPS_ID, null, this);
                }
                break;
            case LOADER_STEPS_ID:
                addSteps(data);
                ingredientsStepsAdapter.swipeData(adapterData);
                progress.setVisibility(View.GONE);

                if (adapterData.size() == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    ingredientsStepsList.setVisibility(View.GONE);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private void addIngredients(Cursor data) {

        data.moveToFirst();

        IngredientModelAdapter ingredientModelAdapter = new IngredientModelAdapter();
        ingredientModelAdapter.setId(data.getInt(data.getColumnIndex(IngredientContract._ID)));
        ingredientModelAdapter.setViewType(IngredientsStepsAdapter.INGREDIENT_VIEW_TYPE);
        ingredientModelAdapter.setMeasure(data.getString(data.getColumnIndex(IngredientContract.MEASURE)));
        ingredientModelAdapter.setQuantity(data.getDouble(data.getColumnIndex(IngredientContract.QUANTITY)));

        StringBuilder ingredientStrBuilder = new StringBuilder("1. ");
        ingredientStrBuilder.append(data.getString(data.getColumnIndex(IngredientContract.INGREDIENT)));
        int i = 0;
        while (data.moveToNext()) {
            ingredientStrBuilder.append(i + 1);
            ingredientStrBuilder.append(". ");
            ingredientStrBuilder.append(data.getString(data.getColumnIndex(IngredientContract.INGREDIENT)));
            if (i != data.getCount() - 1) {
                ingredientStrBuilder.append("\n");
            }
            i++;
        }
        ingredientModelAdapter.setIngredient(ingredientStrBuilder.toString());
        adapterData.add(ingredientModelAdapter);
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
}
