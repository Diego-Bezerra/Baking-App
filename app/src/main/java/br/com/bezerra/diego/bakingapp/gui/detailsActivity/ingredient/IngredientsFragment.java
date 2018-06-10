package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FRAGMENT_TAG = "IngredientsStepsFragment";
    public final int LOADER_ID = 2;

    @BindView(R.id.ingredientsList)
    RecyclerView ingredientsList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private long recipeId;
    private IngredientsAdapter adapter;
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
                AppCompatActivity activity = (AppCompatActivity)getActivity();
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
        if (getActivity() != null && getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
}
