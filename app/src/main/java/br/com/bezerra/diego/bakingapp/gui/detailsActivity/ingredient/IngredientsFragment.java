package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.util.IngredientsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsFragment extends Fragment implements AsyncTaskUtil.AsyncTaskListener<Long, Void, IngredientModelAdapter[]> {

    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";
    private static String LIST_STATE = "LIST_STATE";
    private static String DATA_STATE = "DATA_STATE";

    @BindView(R.id.ingredientsList)
    RecyclerView ingredientsList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private long recipeId;
    private IngredientsAdapter adapter;
    private Parcelable listState;
    private AsyncTaskUtil<Long, Void, IngredientModelAdapter[]> asyncTaskUtil;
    private IngredientModelAdapter[] ingredientModelData;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;

    public static IngredientsFragment newInstance(long recipeId, String recipeTitle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
        bundle.putString(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        bundle.putSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA, detailsActivityFragmentListener);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DetailsActivity.RECIPE_ID_EXTRA)) {
            recipeId = bundle.getLong(DetailsActivity.RECIPE_ID_EXTRA);

            if (bundle.containsKey(DetailsActivity.FRAGMENT_LISTENER_EXTRA)) {
                detailsActivityFragmentListener = (DetailsActivityFragmentListener) bundle.getSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA);
            }
            if (!getResources().getBoolean(R.bool.isSmallestWidth) && bundle.containsKey(DetailsActivity.RECIPE_TITLE_EXTRA)) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null && activity.getSupportActionBar() != null) {
                    String recipeTitle = bundle.getString(DetailsActivity.RECIPE_TITLE_EXTRA);
                    String title = String.format(getString(R.string.recipe_ingredient_title_format), recipeTitle);
                    activity.getSupportActionBar().setTitle(title);
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupList();

        if (savedInstanceState != null && savedInstanceState.containsKey(DATA_STATE)) {
            ingredientModelData = (IngredientModelAdapter[]) savedInstanceState.getParcelableArray(DATA_STATE);
        }

        if (ingredientModelData == null) {
            asyncTaskUtil = new AsyncTaskUtil<>(this);
            asyncTaskUtil.execute(recipeId);
        } else {
            adapter.swipeData(ingredientModelData);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, ingredientsList.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArray(DATA_STATE, ingredientModelData);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE)) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
            ingredientsList.getLayoutManager().onRestoreInstanceState(listState);
        }
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
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupList() {
        ingredientsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsList.setHasFixedSize(true);
        adapter = new IngredientsAdapter();
        ingredientsList.setAdapter(adapter);
    }

    @Override
    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(IngredientModelAdapter[] ingredientModelAdapters) {
        if (ingredientModelAdapters != null && ingredientModelAdapters.length > 0) {
            ingredientModelData = ingredientModelAdapters;
            adapter.swipeData(ingredientModelAdapters);
        } else {
            ingredientsList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(IngredientModelAdapter[] ingredientModelAdapters) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public IngredientModelAdapter[] doInBackground(Long... longs) {

        if (getContext() != null) {
            Cursor cursor = IngredientsProviderUtil.getIngredientsByRecipeId(recipeId, getContext());
            return IngredientModelAdapter.getIngredientsFromCursor(cursor);
        }

        return new IngredientModelAdapter[0];
    }
}
