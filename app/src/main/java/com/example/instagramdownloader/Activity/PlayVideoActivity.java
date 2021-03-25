package com.example.instagramdownloader.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramdownloader.R;

public class PlayVideoActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = findViewById(R.id.videoView);
        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        String url = getIntent().getStringExtra("Video URL");
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();

    }

    @Override
    protected void onResume() {
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }
}
