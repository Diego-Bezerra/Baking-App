package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.content.Context;
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

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.util.IngredientsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.IngredientsAdapter;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.adapter.IngredientsStepsAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IngredientsStepsAdapter.IngredientCardItemClickListerner {

    public static final String RECIPE_ID_EXTRA = "recipeIdExtra";
    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";
    public static final int LOADER_ID = 1;

    @BindView(R.id.ingredientsList)
    RecyclerView ingredientsList;
    @BindView(R.id.progress)
    RecyclerView progress;
    @BindView(R.id.noResults)
    RecyclerView noResults;

    private long recipeId;
    private IngredientsAdapter adapter;

    public static IngredientsFragment newInstance(long recipeId) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(RECIPE_ID_EXTRA, recipeId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(RECIPE_ID_EXTRA)) {
            recipeId = bundle.getInt(RECIPE_ID_EXTRA);
        }
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
        if (getActivity() != null) {
            setupList();
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    private void setupList() {
        ingredientsList.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsList.setHasFixedSize(true);
        adapter = new IngredientsAdapter();
        ingredientsList.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return IngredientsProviderUtil.getIngredientsByRecipeId(recipeId, getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            adapter.swipeData(data);
        } else {
            ingredientsList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onIngredientCardItemClick() {

    }
}
