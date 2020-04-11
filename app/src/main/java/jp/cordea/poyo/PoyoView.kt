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
    private val path = Path()

    private var progress = 0f
    private val maxHeight = context.resources.getDimension(R.dimen.max_height)
    private val cubicPoints = (0 until 5).map { CubicPoint() }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x5 = width / 5f
        val x10 = width / 10f

        cubicPoints[0].set(
            0f, 0f,
            0f, 0f,
            x5, 0f
        ).applyProgress(progress)
        cubicPoints[1].set(
            x10 * 3.5f, 0f,
            x10 * 4f, maxHeight / 3f,
            x10 * 4.5f, (maxHeight / 3f) * 2
        ).applyProgress(progress)
        cubicPoints[2].set(
            x10 * 4.5f, maxHeight,
            width / 2f, maxHeight,
            x10 * 5.5f, maxHeight
        ).applyProgress(progress)
        cubicPoints[3].set(
            x10 * 5.5f, (maxHeight / 3f) * 2,
            x10 * 6f, maxHeight / 3f,
            x10 * 6.5f, 0f
        ).applyProgress(progress)
        cubicPoints[4].set(
            x5 * 4, 0f,
            x5 * 5, 0f,
            0f, 0f
        ).applyProgress(progress)

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
    }
}
