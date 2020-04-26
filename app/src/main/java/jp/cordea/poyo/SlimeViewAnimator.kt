package jp.cordea.poyo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

interface SlimeViewAnimatable {
    fun updateDistance(distance: Float)
    fun updateProgress(progress: Float)
}

class SlimeViewAnimator(
    private val animatable: SlimeViewAnimatable
) {
    private var animating = false

    fun bounce(progress: Float, distance: Float) {
        if (animating) {
            return
        }
        animating = true
        val endProgress = -(progress * progress)
        AnimatorSet()
            .apply {
                doOnEnd {
                    animating = false
                }
                playTogether(
                    ObjectAnimator
                        .ofFloat(distance, 0f)
                        .apply {
                            interpolator = AccelerateInterpolator()
                            duration = 250L
                            addUpdateListener {
                                animatable.updateDistance(it.animatedValue as Float)
                            }
                        },
                    ObjectAnimator
                        .ofFloat(progress, endProgress, 0f)
                        .apply {
                            interpolator = AccelerateDecelerateInterpolator()
                            duration = 600L
                            addUpdateListener {
                                animatable.updateProgress(it.animatedValue as Float)
                            }
                        }
                )
            }
            .start()
    }
}
