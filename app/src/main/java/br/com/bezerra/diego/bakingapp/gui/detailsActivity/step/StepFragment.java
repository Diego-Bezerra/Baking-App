package br.com.bezerra.diego.bakingapp.gui.detailsActivity.step;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;
import br.com.bezerra.diego.bakingapp.data.database.util.StepsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StepFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FRAGMENT_TAG = "StepFragment";
    private final int LOADER_ID = 1;

    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.playerView)
    SimpleExoPlayerView playerView;
    @BindView(R.id.stepDescription)
    TextView stepDescription;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private long stepId;
    private String recipeTitle;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;

    public static StepFragment newInstance(long stepId, String recipeTitle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        StepFragment stepFragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.STEP_ID_EXTRA, stepId);
        bundle.putString(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        bundle.putSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA, detailsActivityFragmentListener);
        stepFragment.setArguments(bundle);

        return stepFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(DetailsActivity.STEP_ID_EXTRA)) {
                stepId = bundle.getLong(DetailsActivity.STEP_ID_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.RECIPE_TITLE_EXTRA)) {
                recipeTitle = bundle.getString(DetailsActivity.STEP_ID_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.FRAGMENT_LISTENER_EXTRA)) {
                detailsActivityFragmentListener = (DetailsActivityFragmentListener) bundle.getSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            if (!getResources().getBoolean(R.bool.isSmallestWidth) && activity.getSupportActionBar() != null) {
                String title = String.format(getString(R.string.recipe_step_title_format), recipeTitle);
                activity.getSupportActionBar().setTitle(title);
            }

            activity.getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return StepsProviderUtil.getStepsById(stepId, getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0 && data.moveToFirst()) {

            String stepDescription = data.getString(data.getColumnIndex(StepContract.DESCRIPTION));
            String videoUrl = data.getString(data.getColumnIndex(StepContract.VIDEO_URL));

            this.stepDescription.setText(stepDescription);

        } else {
            container.setVisibility(View.GONE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
