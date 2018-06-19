package br.com.bezerra.diego.bakingapp.gui.mainActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesFragment extends Fragment implements AsyncTaskUtil.AsyncTaskListener<Void, Void, Cursor>
        , BakingAppServiceUtil.NoConnectivityReceiverListener, ConnectivityReceiver.ConnectivityReceiverListener, RecipesListAdapter.RecipeAdapterItemClickListerner {

    private static String LIST_STATE = "LIST_STATE";
    private static String DATA_STATE = "DATA_STATE";

    @BindView(R.id.recipesList)
    RecyclerView recipesList;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private RecipesListAdapter listAdapter;
    private ConnectivityReceiver mConnectivityReceiver;
    private AsyncTaskUtil<Void, Void, Cursor> asyncTask;
    private RecipeModelAdapter[] recipesData;
    private Parcelable listState;

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
        setupRecipesList();

        if (savedInstanceState != null && savedInstanceState.containsKey(DATA_STATE)) {
            recipesData = (RecipeModelAdapter[]) savedInstanceState.getParcelableArray(DATA_STATE);
        }

        if (recipesData == null) {
            asyncTask = new AsyncTaskUtil<>(RecipesFragment.this);
            asyncTask.execute();
        } else {
            listAdapter.swipeData(recipesData);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, recipesList.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArray(DATA_STATE, recipesData);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE)) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
            recipesList.getLayoutManager().onRestoreInstanceState(listState);
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
        if (asyncTask != null) {
            asyncTask.cancel(true);
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

    private void setupRecipesList() {

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

    @Override
    public void onItemClick(long recipeId, String recipeTitle) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
        intent.putExtra(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }

    @Override
    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(Cursor data) {
        if (data != null && data.getCount() > 0) {
            recipesData = RecipeModelAdapter.recipesFromCursor(data);
            listAdapter.swipeData(recipesData);
            recipesList.getLayoutManager().onRestoreInstanceState(listState);
            recipesList.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        } else {
            recipesList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(Cursor cursor) {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public Cursor doInBackground(Void... voids) {
        return BakingAppServiceUtil.syncRecipesData(getContext(), this);
    }
}

