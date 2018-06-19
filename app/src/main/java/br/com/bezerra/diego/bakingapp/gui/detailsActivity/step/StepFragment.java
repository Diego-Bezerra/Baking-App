package br.com.bezerra.diego.bakingapp.gui.detailsActivity.step;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.util.StepsProviderUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StepFragment extends Fragment implements ExoPlayer.EventListener, AsyncTaskUtil.AsyncTaskListener<Long, Void, StepModelData> {

    public static final String FRAGMENT_TAG = "StepFragment";
    public static final String DATA_STATE = "data_state";

    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.stepDescription)
    TextView stepDescription;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.noResults)
    TextView noResults;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    private long stepId;
    private String recipeTitle;
    private int stepPosition;
    private AsyncTaskUtil<Long, Void, StepModelData> asyncTaskUtil;
    private StepModelData stepData;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;

    public static StepFragment newInstance(long stepId, int stepPosition, String recipeTitle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        StepFragment stepFragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.STEP_ID_EXTRA, stepId);
        bundle.putString(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);
        bundle.putInt(DetailsActivity.STEP_POSITION_EXTRA, stepPosition);
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
                recipeTitle = bundle.getString(DetailsActivity.RECIPE_TITLE_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.FRAGMENT_LISTENER_EXTRA)) {
                detailsActivityFragmentListener = (DetailsActivityFragmentListener) bundle.getSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA);
            }
            if (bundle.containsKey(DetailsActivity.STEP_POSITION_EXTRA)) {
                stepPosition = bundle.getInt(DetailsActivity.STEP_POSITION_EXTRA);
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
                String title = String.format(getString(R.string.recipe_step_title_format), recipeTitle, stepPosition);
                activity.getSupportActionBar().setTitle(title);
            }
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(DATA_STATE)) {
            stepData = savedInstanceState.getParcelable(DATA_STATE);
        }

        if (stepData == null) {
            asyncTaskUtil = new AsyncTaskUtil<>(this);
            asyncTaskUtil.execute(stepId);
        } else {
            bindData(stepData);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null && getContext() != null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA_STATE, stepData);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (asyncTaskUtil != null) {
            asyncTaskUtil.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void bindData(StepModelData data) {
        if (data != null) {
            stepData = data;
            String stepDescription = data.getDescription();
            String videoUrl = data.getVideoUrl();
            this.stepDescription.setText(stepDescription);
            initializePlayer(Uri.parse(videoUrl));
        } else {
            container.setVisibility(View.GONE);
        }
    }

//    @NonNull
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        progress.setVisibility(View.VISIBLE);
//        return StepsProviderUtil.getStepsById(stepId, getContext());
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//        if (data != null && data.getCount() > 0 && data.moveToFirst()) {
//
//            String stepDescription = data.getString(data.getColumnIndex(StepContract.DESCRIPTION));
//            String videoUrl = data.getString(data.getColumnIndex(StepContract.VIDEO_URL));
//
//            this.stepDescription.setText(stepDescription);
//            initializePlayer(Uri.parse(videoUrl));
//
//        } else {
//            container.setVisibility(View.GONE);
//        }
//
//        progress.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//
//    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(StepModelData stepModelData) {
        bindData(stepModelData);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(StepModelData stepModelData) {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public StepModelData doInBackground(Long... longs) {

        if (getContext() != null) {
            Cursor cursor = StepsProviderUtil.getStepsById(stepId, getContext());
            stepData = new StepModelData(cursor);

            return stepData;
        }

        return null;
    }
}
