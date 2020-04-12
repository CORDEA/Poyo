package jp.cordea.poyo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
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
    private val path = Path().apply {
        fillType = Path.FillType.EVEN_ODD
    }

    private val accelerateInterpolator = AccelerateInterpolator()

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
        val width = width.toFloat()
        val height = height.toFloat()
        val x5 = width / 5f
        val x10 = width / 10f
        val y20 = maxHeight / 20f

        val accelerate = accelerateInterpolator.getInterpolation(progress)

        cubicPoints[0].set(
            0f, 0f,
            0f, 0f,
            x5 + progress, y20 * 3 * accelerate
        )
        cubicPoints[1].set(
            x10 * 2f + (x10 * 2f * progress), y20 * 7 * accelerate,
            x10 * 2.5f + (x10 * 2f * progress), y20 * 9 * accelerate,
            x10 * 3.2f + (x10 * 1.8f * progress), y20 * 11 * accelerate
        )

        cubicPoints[2].set(
            // 4.5
            x10 * 3.7f + (x10 * 0.8f * progress), (y20 * 10 * progress) + (y20 * 5 * accelerate),
            x10 * 4.2f + (x10 * 0.3f * progress), (y20 * 15 * progress) + (y20 * 2 * accelerate),
            x10 * 4.6f - (x10 * 0.1f * progress), y20 * 19 * progress
        )
        cubicPoints[3].set(
            // 4.75
            (x10 * 4.7f) + (x10 * 0.05f * progress), maxHeight * progress,
            width / 2f, maxHeight * progress,
            // 5.25
            (x10 * 5.3f) - (x10 * 0.05f * progress), maxHeight * progress
        )
        cubicPoints[4].set(
            // 5.5
            x10 * 5.4f + (x10 * 0.1f * progress), y20 * 19 * progress,
            x10 * 5.8f - (x10 * 0.3f * progress), (y20 * 15 * progress) + (y20 * 2 * accelerate),
            x10 * 6.3f - (x10 * 0.8f * progress), (y20 * 10 * progress) + (y20 * 5 * accelerate)
        )

        cubicPoints[5].set(
            x10 * 6.8f - (x10 * 1.8f * progress), y20 * 11 * accelerate,
            x10 * 7.5f - (x10 * 2f * progress), y20 * 9 * accelerate,
            x10 * 8f - (x10 * 2f * progress), y20 * 7 * accelerate
        )
        cubicPoints[6].set(
            x5 * 4 - progress, y20 * 3 * accelerate,
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
        path.moveTo(baseX, baseY)
        path.lineTo(0f, height)
        path.lineTo(width, height)
        path.lineTo(width, baseY)
        path.close()
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
