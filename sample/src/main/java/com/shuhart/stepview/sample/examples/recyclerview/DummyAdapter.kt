package com.shuhart.stepview.sample.examples.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.shuhart.stepview.StepView
import com.shuhart.stepview.sample.R

import java.util.ArrayList

class DummyAdapter : RecyclerView.Adapter<DummyAdapter.DummyHolder>(), CurrentStepListener {
    private val currentSteps = object : ArrayList<Int>() {
        init {
            for (i in 0..19) {
                add(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyAdapter.DummyHolder {
        return DummyHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_dummy, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DummyAdapter.DummyHolder, position: Int) {
        if (position % 2 == 0) {
            val steps = ArrayList<String>()
            for (i in 0..4) {
                steps.add("Step " + (i + 1))
            }
            holder.stepView.setSteps(steps)
        } else {
            holder.stepView.setStepsNumber(5)
        }
        holder.stepView.go(currentSteps[position], false)
    }

    override fun onViewAttachedToWindow(holder: DummyHolder) {
        super.onViewAttachedToWindow(holder)
        holder.listener = this
    }

    override fun onViewDetachedFromWindow(holder: DummyHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.listener = null
    }

    override fun getItemCount(): Int {
        return currentSteps.size
    }

    override fun update(adapterPosition: Int, step: Int) {
        currentSteps[adapterPosition] = step
    }

    class DummyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stepView: StepView = itemView.findViewById(R.id.step_view)
        var listener: CurrentStepListener? = null

        private var currentStep = 0

        init {
            stepView.setOnStepClickListener(object : StepView.OnStepClickListener {
                override fun onStepClick(step: Int) {
                    Toast.makeText(itemView.context, "Step $step", Toast.LENGTH_SHORT).show()
                }
            })
            itemView.findViewById<View>(R.id.next).setOnClickListener {
                if (currentStep < stepView.stepCount - 1) {
                    currentStep++
                    stepView.go(currentStep, true)
                    listener!!.update(adapterPosition, currentStep)
                } else {
                    stepView.done(true)
                }
            }
            itemView.findViewById<View>(R.id.back).setOnClickListener {
                if (currentStep > 0) {
                    currentStep--
                    listener!!.update(adapterPosition, currentStep)
                }
                stepView.done(false)
                stepView.go(currentStep, true)
            }
        }
    }
}
