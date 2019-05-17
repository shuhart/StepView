package com.shuhart.stepview.animation

import android.animation.Animator

open class AnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animator: Animator) {}

    override fun onAnimationEnd(animator: Animator) {}

    override fun onAnimationCancel(animator: Animator) {}

    override fun onAnimationRepeat(animator: Animator) {}
}
