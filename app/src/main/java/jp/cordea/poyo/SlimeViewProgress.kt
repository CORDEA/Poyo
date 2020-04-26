package jp.cordea.poyo

import kotlin.math.absoluteValue

class SlimeViewProgress {
    private var oldVelocity = 0f

    var distance: Float = 0f
        private set
    var progress: Float = 0f
        private set

    fun update(oldY: Float, y: Float, velocity: Float) {
        val absVelocity = velocity.absoluteValue
        if (absVelocity > 10000f) {
            return
        }
        if (absVelocity > 0f) {
            if (progress < 1f) {
                val diff = absVelocity / 20000f
                if (diff > 0.005f) {
                    progress += diff
                }
                if (progress > 1f) {
                    progress = 1f
                }
                if (progress < 0f) {
                    progress = 0f
                }
            }
        }
        oldVelocity = absVelocity

        if (distance < 0f) {
            return
        }
        distance += oldY - y
        if (distance < 0f) {
            distance = 0f
        }
    }

    fun updateDistance(distance: Float) {
        this.distance = distance
    }

    fun updateProgress(progress: Float) {
        this.progress = progress
    }
}
