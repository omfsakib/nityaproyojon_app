package com.myapp.nityaproyojon;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Loading extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.text_view);
        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressAnimation();
    }
    public void progressAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar, textView,0f,100f);
        anim.setDuration(8000);
        progressBar.setAnimation(anim);
    }
}