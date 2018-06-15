package br.com.bezerra.diego.bakingapp.gui.mainActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.bezerra.diego.bakingapp.BakingAppApplication;
import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.service.BakingAppServiceUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
        , BakingAppServiceUtil.NoConnectivityReceiverListener, ConnectivityReceiver.ConnectivityReceiverListener, RecipesListAdapter.RecipeAdapterItemClickListerner {

    private final int LOADER_ID = 3;

    @BindView(R.id.recipesList)
    RecyclerView recipesList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private RecipesListAdapter listAdapter;
    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecipesList(savedInstanceState);
        if (getActivity() != null && getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
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
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupRecipesList(@Nullable Bundle savedInstanceState) {

        boolean isSmallestWidth = getResources().getBoolean(R.bool.isSmallestWidth);

        if (isSmallestWidth) {
            recipesList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            recipesList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        recipesList.setHasFixedSize(true);
        listAdapter = new RecipesListAdapter();
        listAdapter.setRecipeAdapterItemClickListerner(this);
        recipesList.setAdapter(listAdapter);
    }

    @Override
    public void noInternetConnection() {
        if (getActivity() != null) {
            BakingAppApplication.getInstance().setConnectivityListener(this);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mConnectivityReceiver = new ConnectivityReceiver();
            getActivity().registerReceiver(mConnectivityReceiver, filter);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected && getActivity() != null) {
            BakingAppApplication.getInstance().setConnectivityListener(null);
            getActivity().unregisterReceiver(mConnectivityReceiver);
            mConnectivityReceiver = null;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return BakingAppServiceUtil.syncRecipesData(getContext(), this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            listAdapter.swipeCursor(data);
            recipesList.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        } else {
            recipesList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onItemClick(long recipeId, String recipeTitle) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
        intent.putExtra(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }
}

