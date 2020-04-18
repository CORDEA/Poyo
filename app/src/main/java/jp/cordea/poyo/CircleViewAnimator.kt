package jp.cordea.poyo

import android.animation.AnimatorSet
import android.animation.ValueAnimator

interface CircleViewAnimatable {
    fun updateDegrees(degrees: Int)
    fun updateProgress(progress: Float)
}

class CircleViewAnimator(
    private val animatable: CircleViewAnimatable
) {
    fun animate() {
        AnimatorSet()
            .apply {
                playTogether(
                    ValueAnimator.ofInt(0, 360)
                        .apply {
                            duration = 10000L
                            repeatCount = ValueAnimator.INFINITE
                            addUpdateListener {
                                animatable.updateDegrees(it.animatedValue as Int)
                            }
                        },
                    ValueAnimator.ofFloat(0f, 1f, 0f)
                        .apply {
                            duration = 5000L
                            repeatCount = ValueAnimator.INFINITE
                            addUpdateListener {
                                animatable.updateProgress(it.animatedValue as Float)
                            }
                        }
                )
            }
            .start()
    }
}
