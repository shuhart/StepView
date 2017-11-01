package com.shuhart.stepview.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StepView stepView = findViewById(R.id.step_view);
        stepView.setSteps(new ArrayList<String>() {{
            add("Beginning");
            add("Intermediate\npage");
            add("End");
        }});
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.go(stepView.getCurrentStep() + 1, true);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.go(stepView.getCurrentStep() - 1, false);
            }
        });
    }
}
