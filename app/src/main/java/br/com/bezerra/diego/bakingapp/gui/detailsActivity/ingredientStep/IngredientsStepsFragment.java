package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;
import br.com.bezerra.diego.bakingapp.data.database.util.IngredientsProviderUtil;
import br.com.bezerra.diego.bakingapp.data.database.util.StepsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient.IngredientsFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.step.StepFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.step.StepFragmentBundle;
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsFragment extends BaseFragment implements IngredientsStepsAdapter.CardItemClickListener
        , AsyncTaskUtil.AsyncTaskListener<Long, Void, BaseModelAdapter[]> {

    private static String LIST_STATE = "LIST_STATE";
    private static String DATA_STATE = "DATA_STATE";
    private static String LAST_SELECTED_ITEM_STATE = "LAST_SELECTED_ITEM_STATE";

    @BindView(R.id.ingredientsStepsList)
    RecyclerView ingredientsStepsList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private IngredientsStepsAdapter ingredientsStepsAdapter;
    private long recipeId;
    private String recipeTitle;
    private Parcelable listState;
    private Integer lastSelectedItem;
    private BaseModelAdapter[] adapterData;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;
    private AsyncTaskUtil<Long, Void, BaseModelAdapter[]> asyncTaskUtil;

    public static IngredientsStepsFragment newInstance(long recipeId, String recipeTitle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        IngredientsStepsFragment fragment = new IngredientsStepsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
        bundle.putString(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        bundle.putSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA, detailsActivityFragmentListener);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);

        return fragment;
    }

    public void loadRecipe(long recipeId, String recipeTitle) {

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(recipeTitle);
        }

        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;

        if (adapterData == null) {
            asyncTaskUtil = new AsyncTaskUtil<>(this);
            asyncTaskUtil.execute(recipeId);
        } else {
            ingredientsStepsAdapter.swipeData(adapterData);
            loadFisrtStep();
        }
    }

    private void loadFisrtStep() {
        if (getResources().getBoolean(R.bool.isSmallestWidth)) {
            if (lastSelectedItem != null) {
                detailsActivityFragmentListener.clickNextStep(lastSelectedItem);
            } else {
                detailsActivityFragmentListener.clickIngredient();
            }
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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIST_STATE)) {
                listState = savedInstanceState.getParcelable(LIST_STATE);
            }
            if (savedInstanceState.containsKey(DATA_STATE)) {
                adapterData = (BaseModelAdapter[]) savedInstanceState.getParcelableArray(DATA_STATE);
            }
            if (savedInstanceState.containsKey(LAST_SELECTED_ITEM_STATE)) {
                lastSelectedItem = savedInstanceState.getInt(LAST_SELECTED_ITEM_STATE);
            }
        }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, ingredientsStepsList.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArray(DATA_STATE, adapterData);
        outState.putInt(LAST_SELECTED_ITEM_STATE, ingredientsStepsAdapter.getLastClickedViewPosition());
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
        if (asyncTaskUtil != null) {
            asyncTaskUtil.cancel(true);
        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupIngredientsStepsList() {
        ingredientsStepsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsStepsList.setHasFixedSize(true);
        ingredientsStepsAdapter = new IngredientsStepsAdapter(getResources().getBoolean(R.bool.isSmallestWidth));
        ingredientsStepsAdapter.setCardItemClickListener(this);
        ingredientsStepsList.setAdapter(ingredientsStepsAdapter);
    }

    public void setDetailsActivityFragmentListener(DetailsActivityFragmentListener detailsActivityFragmentListener) {
        this.detailsActivityFragmentListener = detailsActivityFragmentListener;
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
            adapterData[0] = ingredientModelAdapter;
        }
    }

    private void addSteps(Cursor data) {

        int i = 1;
        while (data.moveToNext()) {
            StepModelAdapter stepModelAdapter = new StepModelAdapter();
            stepModelAdapter.setId(data.getInt(data.getColumnIndex(StepContract._ID)));
            stepModelAdapter.setViewType(IngredientsStepsAdapter.STEP_VIEW_TYPE);
            stepModelAdapter.setDescription(data.getString(data.getColumnIndex(StepContract.DESCRIPTION)));
            stepModelAdapter.setShortDescription(data.getString(data.getColumnIndex(StepContract.SHORT_DESCRIPTION)));
            stepModelAdapter.setThumbnailURL(data.getString(data.getColumnIndex(StepContract.THUMBNAIL_URL)));
            stepModelAdapter.setVideoURL(data.getString(data.getColumnIndex(StepContract.VIDEO_URL)));
            adapterData[i] = stepModelAdapter;
            i++;
        }
    }

    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if (!getResources().getBoolean(R.bool.isSmallestWidth)) {
                getActivity().getSupportFragmentManager().popBackStack();
                transaction.addToBackStack(null);
            }
            transaction.replace(R.id.fragmentContainer, fragment).commit();
        }
    }

    public void clickStepPosition(int position) {
        ingredientsStepsAdapter.clickStepPosition(position);
    }

    public void clickIngredientPosition() {
        ingredientsStepsAdapter.clickIngredientPosition();
    }

    @Override
    public void onIngredientCardItemClick(long recipeId) {
        IngredientsFragment fragment = IngredientsFragment.newInstance(recipeId, recipeTitle, null);
        loadFragment(fragment);
    }

    @Override
    public void onStepCardItemClick(long stepId, int stepPosition, Long nextStepId, Integer nextStepPosition) {
        StepFragmentBundle bundle = new StepFragmentBundle(stepId, stepPosition, nextStepId, nextStepPosition, recipeTitle);
        StepFragment fragment = StepFragment.newInstance(bundle, detailsActivityFragmentListener);
        loadFragment(fragment);
    }

    @Override
    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(BaseModelAdapter[] baseModelAdapters) {
        ingredientsStepsAdapter.swipeData(baseModelAdapters);
        ingredientsStepsList.getLayoutManager().onRestoreInstanceState(listState);
        loadFisrtStep();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(BaseModelAdapter[] baseModelAdapters) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public BaseModelAdapter[] doInBackground(Long... longs) {

        if (getContext() != null) {
            Cursor ingredientCursor = IngredientsProviderUtil.getIngredientsByRecipeId(recipeId, getContext());
            Cursor stepCursor = StepsProviderUtil.getStepsByRecipeId(recipeId, getContext());

            adapterData = new BaseModelAdapter[1 + stepCursor.getCount()];
            addIngredients(ingredientCursor);
            addSteps(stepCursor);
        }

        return adapterData;
    }
}
