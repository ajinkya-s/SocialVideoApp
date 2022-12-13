package com.example.socialvideoapp

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialvideoapp.data.localData.Video
import com.example.socialvideoapp.databinding.ActivityMainBinding
import com.example.socialvideoapp.ui.adapters.VideoListAdapter
import com.example.socialvideoapp.utils.SortOptions.SORT_OPTIONS
import com.example.socialvideoapp.viewmodels.MainViewModel
import com.example.socialvideoapp.viewmodels.MainViewModelFactory
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mainViewModel: MainViewModel

    // For Video Playback on Exo Player
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    // For Player Audio
    private var currentVolume: Int = 0
    private var isMute: Boolean = false

    // For Player Analytics
    private val timer = Timer()
    var timeSkipped: Long = 0
    var lastPosition: Long = 0

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        (application as SocialVideoApplication).applicationComponent.inject(this)
        mainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]

        setupViews()
        setupObservers()
        setUpVideoProgressTimer()
    }

    @OptIn(UnstableApi::class)
    private fun setupViews() {
        viewBinding.playerView.apply {
            setShowNextButton(false)
            setShowPreviousButton(false)
        }

        // Setup the video list
        viewBinding.videoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewBinding.videoList.adapter = VideoListAdapter(videoListItemClickListener = { video ->
            mainViewModel._selectedVideo.value = video
        })

        // Setup on click listener for maximize and minimize
        viewBinding.fullscreenImage.setOnClickListener {
            handleMaximizeMinimize()
        }

        // Setup on click listener for handling mute and unmute
        viewBinding.muteUnMuteImage.setOnClickListener {
            handleAudio()
        }

        // Setup on click listener for Sort functionality
        viewBinding.sortImage.setOnClickListener {
            openSortingDialog()
        }
    }

    private fun openSortingDialog() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Sort Options")
            .setItems(SORT_OPTIONS, DialogInterface.OnClickListener { dialog, which ->
                // Handle the selected sort option
                mainViewModel.sortVideos(which)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                // Handle the cancel button
                dialog.dismiss()
            })
            .create()

        // Show the dialog
        dialog.show()
    }

    private fun handleAudio() {
        if (isMute) {
            isMute = false
            player?.volume = currentVolume.toFloat()
            viewBinding.muteUnMuteImage.setImageResource(R.drawable.ic_un_mute)
        } else {
            isMute = true
            player?.volume = 0f
            viewBinding.muteUnMuteImage.setImageResource(R.drawable.ic_mute)
        }
    }

    private fun handleMaximizeMinimize() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun setupObservers() {
        // Observe changes to the video list
        mainViewModel.videos.observe(this) { videos ->
            (viewBinding.videoList.adapter as VideoListAdapter).submitList(videos)
        }

        // Observe changes to the selected video
        mainViewModel.selectedVideo.observe(this, Observer { video ->
            mainViewModel.onVideoClicked(video)
            // Update the player with the new video
            initializePlayer(video)
        })
    }

    private fun setUpVideoProgressTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (player?.isPlaying == true) {
                        calculateViewedPercentageAndShow()
                    }
                }
            }
        }, 0, 100)
    }

    private fun calculateViewedPercentageAndShow() {
        lastPosition = player!!.currentPosition
        val duration = player!!.duration
        val adjustedWatchedDuration = player!!.currentPosition - timeSkipped + 100
        val percentage = (adjustedWatchedDuration / duration.toFloat()) * 100
        viewBinding.percentageViewed.text = String.format("%d%%", percentage.toInt())
    }

    public override fun onResume() {
        super.onResume()
        if (player == null) {
            mainViewModel.selectedVideo.value?.let { initializePlayer(it) }
        }
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    public override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewBinding.videoList.visibility = GONE
            viewBinding.sortImage.visibility = GONE
            viewBinding.fullscreenImage.setImageResource(R.drawable.ic_minimize)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewBinding.videoList.visibility = VISIBLE
            viewBinding.sortImage.visibility = VISIBLE
            viewBinding.fullscreenImage.setImageResource(R.drawable.ic_maximize)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initializePlayer(video: Video) {
        // Initializations for new video
        lastPosition = 0;
        timeSkipped = 0;

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                viewBinding.playerView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(Uri.parse(video.url))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
        currentVolume = player!!.volume.toInt()
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }

        player = null
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                val currentPosition = player!!.currentPosition

                // Calculate how much time has been skipped since the last check
                val timeDelta = currentPosition - lastPosition
                timeSkipped += timeDelta

                // Update the last playback position
                lastPosition = currentPosition
            }
        }
    }
}