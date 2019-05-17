package com.shuhart.stepview.sample.examples.simple

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import com.shuhart.stepview.StepView
import com.shuhart.stepview.sample.R

import java.util.ArrayList

class SimpleActivity : AppCompatActivity() {
    private var currentStep = 0
    private var currentStep2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)
        val stepView = findViewById<StepView>(R.id.step_view)
        stepView.setOnStepClickListener(object : StepView.OnStepClickListener {
            override fun onStepClick(step: Int) {
                Toast.makeText(this@SimpleActivity, "Step $step", Toast.LENGTH_SHORT).show()
            }
        })
        findViewById<View>(R.id.next).setOnClickListener {
            if (currentStep < stepView.stepCount - 1) {
                currentStep++
                stepView.go(currentStep, true)
            } else {
                stepView.done(true)
            }
        }
        findViewById<View>(R.id.back).setOnClickListener {
            if (currentStep > 0) {
                currentStep--
            }
            stepView.done(false)
            stepView.go(currentStep, true)
        }
        val steps = ArrayList<String>()
        for (i in 0..4) {
            steps.add("Step " + (i + 1))
        }
        steps[steps.size - 1] = steps[steps.size - 1] + " last one"
        stepView.setSteps(steps)

        val stepView2 = findViewById<StepView>(R.id.step_view_2)
        stepView2.setOnStepClickListener(object : StepView.OnStepClickListener {
            override fun onStepClick(step: Int) {
                Toast.makeText(this@SimpleActivity, "Step $step", Toast.LENGTH_SHORT).show()
            }
        })
        findViewById<View>(R.id.next_2).setOnClickListener {
            if (currentStep2 < stepView2.stepCount - 1) {
                currentStep2++
                stepView2.go(currentStep2, true)
            } else {
                stepView2.done(true)
            }
        }
        findViewById<View>(R.id.back_2).setOnClickListener {
            if (currentStep2 > 0) {
                currentStep2--
            }
            stepView2.done(false)
            stepView2.go(currentStep2, true)
        }
    }
}
