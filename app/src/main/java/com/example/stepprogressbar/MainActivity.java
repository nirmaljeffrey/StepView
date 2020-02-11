package com.example.stepprogressbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StepView stepView = findViewById(R.id.step_view_2);
//        ArrayList<Drawable> inCompleteStateDrawables = new ArrayList<>();
//        inCompleteStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_bank_in_complete));
//        inCompleteStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_document_in_complete));
//        inCompleteStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_video_in_complete));
//        stepView.setInCompleteStateDrawables(inCompleteStateDrawables);
//        ArrayList<Drawable> inProgressStateDrawables = new ArrayList<>();
//        inProgressStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_bank_in_progress));
//        inProgressStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_document_in_progress));
//        inProgressStateDrawables.add(ContextCompat.getDrawable(this,R.drawable.ic_video_in_progress));
//        stepView.setInProgressStateDrawables(inProgressStateDrawables);
//        stepView.setCompleteStateIcon(ContextCompat.getDrawable(this,R.drawable.ic_complete_red));
        findViewById(R.id.step_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stepView.setCurrentStep(2);
            }
        });
        findViewById(R.id.step_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             stepView.setCurrentStep(3);
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.reset();
            }
        });
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.done();
            }
        });
    }

}
