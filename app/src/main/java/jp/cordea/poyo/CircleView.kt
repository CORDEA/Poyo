package jp.cordea.poyo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CircleViewAnimatable {
    private val paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.main)
        style = Paint.Style.FILL
    }
    private val debugPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.debug)
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    private val debugControlPointPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.debugAccent)
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    private val pathMatrix = Matrix()
    private val path = Path()

    private var debuggable = false
    private var progress = 0f
    private var degrees = 0

    private val circleSize = context.resources.getDimension(R.dimen.circle_size)
    private val cubicPoints = (0 until 9).map { CubicPoint() }

    fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val half = circleSize / 2f
        val middle = sqrt((half * half) / 2f)

        val length = circleSize / 8f
        val length45 = sqrt((length * length) / 2f)

        val size7 = (circleSize / 7f) * progress
        val size15 = (circleSize / 15f) * progress
        val size30 = (circleSize / 30f) * progress
        cubicPoints[0].set(
            0f, 0f,
            0f, 0f,
            length, half - size15
        )
        cubicPoints[1].set(
            middle - length45 - size7, middle + length45 - size7,
            middle - size7, middle - size7,
            middle + length45 - size7, middle - length45 - size7
        )
        cubicPoints[2].set(
            half - size15, length,
            half, 0f,
            half + size15, -length
        )
        cubicPoints[3].set(
            middle + length45 + size15 + size30, -middle + length45 - size15 + size30,
            middle + size15, -middle - size15,
            middle - length45 + size15 - size30, -middle - length45 - size15 - size30
        )
        cubicPoints[4].set(
            length, -half - size15,
            0f, -half,
            -length, -half + size15
        )
        cubicPoints[5].set(
            -middle + length45 + size7, -middle - length45 + size7,
            -middle + size7, -middle + size7,
            -middle - length45 + size7, -middle + length45 + size7
        )
        cubicPoints[6].set(
            -half + size15, -length,
            -half, 0f,
            -half - size15, length
        )
        cubicPoints[7].set(
            -middle - length45 - size15 - size30, middle - length45 + size15 - size30,
            -middle - size15, middle + size15,
            -middle + length45 - size15 + size30, middle + length45 + size15 + size30
        )
        cubicPoints[8].set(
            -length, half + size15,
            0f, half,
            0f, 0f
        )

        val centerX = width / 2f
        val centerY = height / 2f
        path.reset()
        path.moveTo(centerX, centerY - half)
        for (index in 1 until cubicPoints.size) {
            val prev = cubicPoints[index - 1]
            val current = cubicPoints[index]
            path.cubicTo(
                centerX + prev.rightControlPoint.x,
                centerY + prev.rightControlPoint.y,
                centerX + current.leftControlPoint.x,
                centerY + current.leftControlPoint.y,
                centerX + current.point.x,
                centerY + current.point.y
            )

        }
        path.close()

        pathMatrix.reset()
        pathMatrix.postRotate(degrees.toFloat(), centerX, centerY)
        path.transform(pathMatrix)

        canvas.drawPath(path, paint)

        if (debuggable) {
            cubicPoints.forEach {
                canvas.drawPoint(
                    centerX + it.point.x,
                    centerY + it.point.y,
                    debugPaint
                )
                canvas.drawPoint(
                    centerX + it.rightControlPoint.x,
                    centerY + it.rightControlPoint.y,
                    debugControlPointPaint
                )
                canvas.drawPoint(
                    centerX + it.leftControlPoint.x,
                    centerY + it.leftControlPoint.y,
                    debugControlPointPaint
                )
            }
        }
    }

    override fun updateDegrees(degrees: Int) {
        this.degrees = degrees
        invalidate()
    }

    override fun updateProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }
}
