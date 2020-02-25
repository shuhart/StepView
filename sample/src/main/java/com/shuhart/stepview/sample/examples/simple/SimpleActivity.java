package com.shuhart.stepview.sample.examples.simple;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.shuhart.stepview.StepView;
import com.shuhart.stepview.sample.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity {
    private int currentStep = 0;
    private int currentStep2 = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        final StepView stepView = findViewById(R.id.step_view);
        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                Toast.makeText(SimpleActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
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
        List<String> steps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            steps.add("Step " + (i + 1));
        }
        steps.set(steps.size() - 1, steps.get(steps.size() - 1) + " last one");
        stepView.setSteps(steps);

        final StepView stepView2 = findViewById(R.id.step_view_2);
        stepView2.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                Toast.makeText(SimpleActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.next_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep2 < stepView2.getStepCount() - 1) {
                    currentStep2++;
                    stepView2.go(currentStep2, true);
                } else {
                    stepView2.done(true);
                }
            }
        });
        findViewById(R.id.back_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep2 > 0) {
                    currentStep2--;
                }
                stepView2.done(false);
                stepView2.go(currentStep2, true);
            }
        });
    }
}
