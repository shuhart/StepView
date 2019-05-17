package com.shuhart.stepview.sample.examples.rtl

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import com.shuhart.stepview.StepView
import com.shuhart.stepview.sample.R
import com.shuhart.stepview.sample.examples.simple.SimpleActivity

import java.util.ArrayList

class RTLActivity : AppCompatActivity() {
    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_dummy)
        val stepView = findViewById<StepView>(R.id.step_view)
        stepView.setOnStepClickListener(object : StepView.OnStepClickListener {
            override fun onStepClick(step: Int) {
                Toast.makeText(this@RTLActivity, "Step $step", Toast.LENGTH_SHORT).show()
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
        val steps = object : ArrayList<String>() {
            init {
                add("الأول")
                add("الثاني ، نص طويل نوعا ما")
                add("الثالث")
                add("الرابع")
                add("الخامس")
            }
        }
        stepView.setSteps(steps)
    }
}
