package com.shuhart.stepview.sample.examples.recyclerview

interface CurrentStepListener {
    fun update(adapterPosition: Int, step: Int)
}