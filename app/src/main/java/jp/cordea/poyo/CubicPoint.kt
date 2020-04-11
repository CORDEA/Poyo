package jp.cordea.poyo

import android.graphics.PointF

class CubicPoint {
    var leftControlPoint = PointF()
        private set
    var rightControlPoint = PointF()
        private set
    var point = PointF()
        private set

    fun set(
        leftX: Float,
        leftY: Float,
        pointX: Float,
        pointY: Float,
        rightX: Float,
        rightY: Float
    ) = apply {
        leftControlPoint.set(leftX, -leftY)
        point.set(pointX, -pointY)
        rightControlPoint.set(rightX, -rightY)
    }

    fun applyProgress(progress: Float) {
        leftControlPoint *= progress
        point *= progress
        rightControlPoint *= progress
    }
}

operator fun PointF.timesAssign(progress: Float) = set(x, y * progress)
