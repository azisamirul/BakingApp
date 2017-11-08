package com.azisamirul.bakingapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.azisamirul.bakingapp.model.Step;
import com.azisamirul.bakingapp.network.Network;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.azisamirul.bakingapp.cons.Cons.STEP_PARCELABLE;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    //Exoplayer

    SimpleExoPlayer simpleExoPlayer;
    boolean playWhenReady;
    private long playbackPosition;
    private int currentWindow;
    private Unbinder unbinder;
    String shortDescription, description, videoURL, thumbnailURL;
    String CURRENT_POSITION = "current_position";
    @BindView(R.id.tv_short_description)
    TextView tvShortDescription;
    @BindView(R.id.tv_description)
    TextView tvDescription;

    @BindView(R.id.image_thumbnails)
    ImageView imageThumbnails;

    @BindView(R.id.video_player)
    SimpleExoPlayerView videoPlayer;
    Network network;
    /**
     * The dummy content this fragment is presenting.
     */
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playWhenReady = true;
        if (getArguments().containsKey(STEP_PARCELABLE)) {
            Step step = getArguments().getParcelable(STEP_PARCELABLE);
            shortDescription = step.getShortDescription();
            description = step.getDescription();
            videoURL = step.getVideoURL();
            thumbnailURL = step.getThumbnailURL();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_POSITION)) {
                playbackPosition = savedInstanceState.getLong(CURRENT_POSITION, 0);
                Log.d("playbackPosition", String.valueOf(playbackPosition));

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (Util.SDK_INT <= 23 || simpleExoPlayer == null) {
            initializePlayer();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
//        if (Util.SDK_INT <= 23) {
//
//        }
        releasePlayer();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        network = new Network(getActivity());
        tvShortDescription.setText(shortDescription);
        tvDescription.setText(description);
        if (thumbnailURL.isEmpty()) {
            imageThumbnails.setImageBitmap(getCakeImage());
        } else {
            downloadImage();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initializePlayer() {

        if (network.checkNetwork()) {
            if (simpleExoPlayer == null) {
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                        new DefaultTrackSelector(), new DefaultLoadControl());
                videoPlayer.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setPlayWhenReady(playWhenReady);
                simpleExoPlayer.seekTo(currentWindow, playbackPosition);
            }
            MediaSource mediaSource = build(Uri.parse(videoURL));
            simpleExoPlayer.prepare(mediaSource, true, false);
        } else {
            displayErrorConnection();
        }
    }

    private MediaSource build(Uri uri) {
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory("BakingApp"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }

    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        videoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void displayErrorConnection() {
        Toast.makeText(getActivity(), getString(R.string.error_connection_msg), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_POSITION, playbackPosition);
    }

    private void downloadImage() {
        if (network.checkNetwork()) {
            Picasso.with(getActivity()).load(thumbnailURL).into(imageThumbnails);
        }
    }

    private Bitmap getCakeImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cake);
        return bitmap;
    }

}
