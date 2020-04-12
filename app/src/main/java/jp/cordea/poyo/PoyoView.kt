package jp.cordea.poyo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class PoyoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.colorAccent)
        style = Paint.Style.FILL
    }
    private val debugPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    private val debugControlPointPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    private val path = Path()

    private var progress = 0f
    private var debuggable = false

    private val maxHeight = context.resources.getDimension(R.dimen.max_height)
    private val cubicPoints = (0 until 7).map { CubicPoint() }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x5 = width / 5f
        val x10 = width / 10f
        val y4 = maxHeight / 4f
        val y7 = maxHeight / 7f
        val y10 = maxHeight / 10f

        cubicPoints[0].set(
            0f, 0f,
            0f, 0f,
            x5, y10 * progress
        )
        cubicPoints[1].set(
            x10 * 1.5f + (x10 * 2.5f * progress), 0f,
            x10 * 2f + (x10 * 2.5f * progress), y4 * progress,
            x10 * 2.5f + (x10 * 2.5f * progress), y4 * 2 * progress
        )

        cubicPoints[2].set(
            x10 * 4.3f + (x10 * 0.2f * progress), y10 * 7 * progress,
            x10 * 4.5f, y10 * 8 * progress,
            x10 * 4.7f - (x10 * 0.2f * progress), y10 * 9 * progress
        )
        cubicPoints[3].set(
            (x10 * 4.5f) + (x10 * 0.25f * progress), maxHeight * progress,
            width / 2f, maxHeight * progress,
            (x10 * 5.5f) - (x10 * 0.25f * progress), maxHeight * progress
        )
        cubicPoints[4].set(
            x10 * 5.3f + (x10 * 0.2f * progress), y10 * 9 * progress,
            x10 * 5.5f, y10 * 8 * progress,
            x10 * 5.7f - (x10 * 0.2f * progress), y10 * 7 * progress
        )

        cubicPoints[5].set(
            x10 * 7.5f - (x10 * 2.5f * progress), y4 * 2 * progress,
            x10 * 8f - (x10 * 2.5f * progress), y4 * progress,
            x10 * 8.5f - (x10 * 2.5f * progress), 0f
        )
        cubicPoints[6].set(
            x5 * 4, y10 * progress,
            x5 * 5, 0f,
            0f, 0f
        )

        val baseX = 0f
        val baseY = height / 2f
        path.reset()
        path.moveTo(baseX, baseY)
        for (index in 1 until cubicPoints.size) {
            val prev = cubicPoints[index - 1]
            val current = cubicPoints[index]
            path.cubicTo(
                baseX + prev.rightControlPoint.x,
                baseY + prev.rightControlPoint.y,
                baseX + current.leftControlPoint.x,
                baseY + current.leftControlPoint.y,
                baseX + current.point.x,
                baseY + current.point.y
            )

        }
        canvas.drawPath(path, paint)

        if (debuggable) {
            cubicPoints.forEach {
                canvas.drawPoint(
                    baseX + it.point.x,
                    baseY + it.point.y,
                    debugPaint
                )
                canvas.drawPoint(
                    baseX + it.rightControlPoint.x,
                    baseY + it.rightControlPoint.y,
                    debugControlPointPaint
                )
                canvas.drawPoint(
                    baseX + it.leftControlPoint.x,
                    baseY + it.leftControlPoint.y,
                    debugControlPointPaint
                )
            }
        }
    }
}
