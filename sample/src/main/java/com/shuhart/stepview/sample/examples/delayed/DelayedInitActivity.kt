package com.shuhart.stepview.sample.examples.delayed

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import com.shuhart.stepview.StepView
import com.shuhart.stepview.sample.R
import com.shuhart.stepview.sample.examples.simple.SimpleActivity

import java.util.ArrayList

class DelayedInitActivity : AppCompatActivity() {
    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delayed_init)
        val stepView = findViewById<StepView>(R.id.step_view)
        stepView.setOnStepClickListener(object : StepView.OnStepClickListener {
            override fun onStepClick(step: Int) {
                Toast.makeText(this@DelayedInitActivity, "Step $step", Toast.LENGTH_SHORT).show()
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
        findViewById<View>(R.id.btn_init).setOnClickListener {
            val steps = ArrayList<String>()
            for (i in 0..4) {
                steps.add("Step " + (i + 1))
            }
            stepView.setSteps(steps)
        }
    }
}
