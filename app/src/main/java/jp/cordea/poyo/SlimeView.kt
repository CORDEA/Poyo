package jp.cordea.poyo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.absoluteValue

class SlimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), SlimeViewAnimatable {
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
    private val path = Path().apply {
        fillType = Path.FillType.EVEN_ODD
    }

    private val accelerateInterpolator = AccelerateInterpolator()
    private val accelerate4Interpolator = AccelerateInterpolator(0.4f)
    private val animator = SlimeViewAnimator(this)

    private var prevY = 0f
    private var debuggable = false
    private var velocityTracker: VelocityTracker? = null

    private val viewProgress = ViewProgress()
    private val maxHeight = context.resources.getDimension(R.dimen.max_height)
    private val bottomMaxHeight = context.resources.getDimension(R.dimen.bottom_max_height)
    private val cubicPoints = (0 until 7).map { CubicPoint() }

    fun setDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker?.clear()
                velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                prevY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.let {
                    it.addMovement(event)
                    it.computeCurrentVelocity(1000)
                    viewProgress.update(prevY, event.y, it.yVelocity)
                    invalidate()
                }
                prevY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.recycle()
                velocityTracker = null
                animator.bounce(viewProgress.progress, viewProgress.distance)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        if (viewProgress.progress > 0f) {
            applyPlusCubicPoints()
        } else {
            applyMinusCubicPoints()
        }

        val baseX = 0f
        val baseY = (height / 2f) - viewProgress.distance

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

    override fun updateDistance(distance: Float) {
        viewProgress.updateDistance(distance)
        invalidate()
    }

    override fun updateProgress(progress: Float) {
        viewProgress.updateProgress(progress)
        invalidate()
    }

    private fun applyPlusCubicPoints() {
        val progress = viewProgress.progress
        val width = width.toFloat()
        val x5 = width / 5f
        val x10 = width / 10f
        val y20 = maxHeight / 20f

        val accelerate = accelerateInterpolator.getInterpolation(progress)
        val accelerate4 = accelerate4Interpolator.getInterpolation(progress)

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
            x10 * 3.7f + (x10 * 0.8f * accelerate4), (y20 * 10 * progress) + (y20 * 5 * accelerate),
            x10 * 4.2f + (x10 * 0.3f * accelerate4), (y20 * 15 * progress) + (y20 * 2 * accelerate),
            x10 * 4.6f - (x10 * 0.1f * accelerate4), y20 * 19 * progress
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
            x10 * 5.4f + (x10 * 0.1f * accelerate4), y20 * 19 * progress,
            x10 * 5.8f - (x10 * 0.3f * accelerate4), (y20 * 15 * progress) + (y20 * 2 * accelerate),
            x10 * 6.3f - (x10 * 0.8f * accelerate4), (y20 * 10 * progress) + (y20 * 5 * accelerate)
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
    }

    private fun applyMinusCubicPoints() {
        val progress = viewProgress.progress.absoluteValue

        val width = width.toFloat()
        val x10 = width / 10f
        val y20 = -(bottomMaxHeight / 20f)

        cubicPoints[0].set(
            0f, 0f,
            0f, 0f,
            x10, y20 * 2f * progress
        )
        cubicPoints[1].set(
            x10 * 1.5f, y20 * 2f * progress,
            x10 * 2f, y20 * 2f * progress,
            x10 * 3.5f, y20 * 1.5f * progress
        )

        cubicPoints[2].set(
            x10 * 3.7f + (x10 * 0.3f * progress), 0f,
            x10 * 4.1f + (x10 * 0.2f * progress), y20 * 6f * progress,
            x10 * 4.5f, y20 * 11f * progress
        )
        cubicPoints[3].set(
            // 4.5
            (x10 * 4.7f) - (x10 * 0.2f * progress), -bottomMaxHeight * progress,
            width / 2f, -bottomMaxHeight * progress,
            // 5.5
            (x10 * 5.3f) + (x10 * 0.2f * progress), -bottomMaxHeight * progress
        )
        cubicPoints[4].set(
            x10 * 5.5f, y20 * 11f * progress,
            x10 * 5.9f - (x10 * 0.2f * progress), y20 * 6f * progress,
            x10 * 6.3f - (x10 * 0.3f * progress), 0f
        )

        cubicPoints[5].set(
            x10 * 6.5f, y20 * 1.5f * progress,
            x10 * 8f, y20 * 2f * progress,
            x10 * 8.5f, y20 * 2f * progress
        )
        cubicPoints[6].set(
            x10 * 9f, y20 * 2f * progress,
            width, 0f,
            0f, 0f
        )
    }
}
