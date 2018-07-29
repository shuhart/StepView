package com.shuhart.stepview.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int currentStep = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StepView stepView = findViewById(R.id.step_view);
//        stepView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // still, it's very unlikely you are going to see this animation
//                stepView.go(1, true);
//            }
//        });
        stepView.go(1, true);
        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                Toast.makeText(MainActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < stepView.getStepCount() - 1) {
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep > 0) {
                    currentStep--;
                }
                stepView.done(false);
                stepView.go(currentStep, true);
            }
        });
    }
}
