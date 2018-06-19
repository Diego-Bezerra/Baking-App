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
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsStepsFragment extends Fragment implements IngredientsStepsAdapter.CardItemClickListerner
        , AsyncTaskUtil.AsyncTaskListener<Long, Void, List<BaseModelAdapter>> {

    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";
    private static String LIST_STATE = "LIST_STATE";
    private static String DATA_STATE = "DATA_STATE";

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
    private ArrayList<BaseModelAdapter> adapterData;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;
    private AsyncTaskUtil<Long, Void, List<BaseModelAdapter>> asyncTaskUtil;

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

        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;

        if (adapterData == null) {
            asyncTaskUtil = new AsyncTaskUtil<>(this);
            asyncTaskUtil.execute(recipeId);
        } else {
            ingredientsStepsAdapter.swipeData(adapterData);
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
                adapterData = savedInstanceState.getParcelableArrayList(DATA_STATE);
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
        outState.putParcelableArrayList(DATA_STATE, adapterData);
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
        ingredientsStepsAdapter = new IngredientsStepsAdapter();
        ingredientsStepsAdapter.setCardItemClickListerner(this);
        ingredientsStepsList.setAdapter(ingredientsStepsAdapter);
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

        if (data.moveToFirst()) {

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

    @Override
    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        adapterData = new ArrayList<>();
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(List<BaseModelAdapter> baseModelAdapters) {
        ingredientsStepsAdapter.swipeData(baseModelAdapters);
        ingredientsStepsList.getLayoutManager().onRestoreInstanceState(listState);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(List<BaseModelAdapter> baseModelAdapters) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public List<BaseModelAdapter> doInBackground(Long... longs) {

        if (getContext() != null) {
            Cursor ingredientCursor = IngredientsProviderUtil.getIngredientsByRecipeId(recipeId, getContext());
            Cursor stepCursor = StepsProviderUtil.getStepsByRecipeId(recipeId, getContext());

            addIngredients(ingredientCursor);
            addSteps(stepCursor);
        }

        return adapterData;
    }
}
