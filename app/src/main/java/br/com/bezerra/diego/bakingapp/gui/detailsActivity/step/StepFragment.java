package br.com.bezerra.diego.bakingapp.gui.detailsActivity.step;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseFragment;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivityFragmentListener;
import br.com.bezerra.diego.bakingapp.util.AsyncTaskUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class StepFragment extends BaseFragment implements ExoPlayer.EventListener, AsyncTaskUtil.AsyncTaskListener<Long, Void, StepModelData> {

    public static final String DATA_STATE = "data_state";
    private static final String TAG = StepFragment.class.getSimpleName();

    @Nullable
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @Nullable
    @BindView(R.id.stepDescription)
    TextView stepDescription;
    @Nullable
    @BindView(R.id.progress)
    ProgressBar progress;
    @Nullable
    @BindView(R.id.noResults)
    TextView noResults;
    @Nullable
    @BindView(R.id.nextButton)
    TextView nextButton;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;
    private MediaReceiver mediaReceiver;

    private StepFragmentBundle bundle;
    private AsyncTaskUtil<Long, Void, StepModelData> asyncTaskUtil;
    private StepModelData stepData;
    private DetailsActivityFragmentListener detailsActivityFragmentListener;

    public static StepFragment newInstance(StepFragmentBundle stepFragmentBundle, DetailsActivityFragmentListener detailsActivityFragmentListener) {
        StepFragment stepFragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailsActivity.STEP_FRAGMENT_EXTRA, stepFragmentBundle);
        bundle.putSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA, detailsActivityFragmentListener);
        stepFragment.setArguments(bundle);

        return stepFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(DetailsActivity.STEP_FRAGMENT_EXTRA)) {
                bundle = args.getParcelable(DetailsActivity.STEP_FRAGMENT_EXTRA);
            }
            if (args.containsKey(DetailsActivity.FRAGMENT_LISTENER_EXTRA)) {
                detailsActivityFragmentListener = (DetailsActivityFragmentListener) args.getSerializable(DetailsActivity.FRAGMENT_LISTENER_EXTRA);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (!getResources().getBoolean(R.bool.isSmallestWidth) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            detailsActivityFragmentListener.setFitSystemWindow(false);
            view = inflater.inflate(R.layout.fragment_step_land, container, false);
            hideSystemUI();
        } else {
            view = inflater.inflate(R.layout.fragment_step, container, false);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity != null) {
            if (!getResources().getBoolean(R.bool.isSmallestWidth) && activity.getSupportActionBar() != null) {
                String title = String.format(getString(R.string.recipe_step_title_format), bundle.getRecipeTitle(), bundle.getStepPosition());
                activity.getSupportActionBar().setTitle(title);
            }
        }
        if (nextButton != null && bundle.getNextStepId() == null) {
            nextButton.setVisibility(View.GONE);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(DATA_STATE)) {
            stepData = savedInstanceState.getParcelable(DATA_STATE);
        }

        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.ic_play_video));
        initializeMediaSession();

        if (stepData == null) {
            asyncTaskUtil = new AsyncTaskUtil<>(this);
            asyncTaskUtil.execute(bundle.getStepId());
        } else {
            bindData(stepData);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(activity, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    activity, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(activity, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void hideSystemUI() {

        View decorView = activity.getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void showSystemUI() {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
        if (!getResources().getBoolean(R.bool.isSmallestWidth)) {
            showSystemUI();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
        detailsActivityFragmentListener.setFitSystemWindow(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void bindData(StepModelData data) {
        if (data != null) {
            stepData = data;

            if (this.stepDescription != null) {
                String stepDescription = data.getDescription();
                this.stepDescription.setText(stepDescription);
            }

            String videoUrl = data.getVideoUrl();
            initializePlayer(Uri.parse(videoUrl));
        } else {
            if (container != null) {
                container.setVisibility(View.GONE);
            }
        }
    }

    private void setProgress(boolean show) {
        if (progress != null) {
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

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
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPreExecute() {
        setProgress(true);
    }

    @Override
    public void onProgressUpdate(Void... values) {

    }

    @Override
    public void onPostExecute(StepModelData stepModelData) {
        bindData(stepModelData);
        setProgress(false);
    }

    @Override
    public void onCancelled(StepModelData stepModelData) {
        setProgress(false);
    }

    @Override
    public void onCancelled() {
        setProgress(false);
    }

    @Override
    public StepModelData doInBackground(Long... longs) {

        if (getContext() != null) {
            Cursor cursor = StepsProviderUtil.getStepsById(bundle.getStepId(), getContext());
            stepData = new StepModelData(cursor);

            return stepData;
        }

        return null;
    }

    @Optional
    @OnClick(R.id.nextButton)
    public void nextButtonAction(Button button) {
        if (bundle.getNextStepPosition() != null) {
            detailsActivityFragmentListener.clickNextStep(bundle.getNextStepPosition());
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
