package jp.cordea.poyo

import android.content.Context
import android.graphics.Canvas
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
) : View(context, attrs, defStyleAttr) {
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
    private val path = Path()

    private var debuggable = true

    private val cubicPoints = (0 until 8).map { CubicPoint() }

    private val circleSize = context.resources.getDimension(R.dimen.circle_size)

    fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val half = circleSize / 2f
        val middle = sqrt((half * half) / 2f)

        cubicPoints[0].set(
            0f, half,
            0f, half,
            0f, half
        )
        cubicPoints[1].set(
            middle, middle,
            middle, middle,
            middle, middle
        )
        cubicPoints[2].set(
            half, 0f,
            half, 0f,
            half, 0f
        )
        cubicPoints[3].set(
            middle, -middle,
            middle, -middle,
            middle, -middle
        )
        cubicPoints[4].set(
            0f, -half,
            0f, -half,
            0f, -half
        )
        cubicPoints[5].set(
            -middle, -middle,
            -middle, -middle,
            -middle, -middle
        )
        cubicPoints[6].set(
            -half, 0f,
            -half, 0f,
            -half, 0f
        )
        cubicPoints[7].set(
            -middle, middle,
            -middle, middle,
            -middle, middle
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
}
