package com.example.videosummarise.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.videosummarise.R
import kotlin.math.abs

class AnimatedThemeToggle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isChecked = false
    private var animationProgress = 0f
    private var thumbPosition = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var trackWidth = 0f
    private var trackHeight = 0f
    private var thumbRadius = 0f
    private var trackRadius = 0f

    private var onToggleListener: ((Boolean) -> Unit)? = null

    // Colors
    private val trackColorOff = ContextCompat.getColor(context, R.color.gray_300)
    private val trackColorOn = ContextCompat.getColor(context, R.color.blue_600)
    private val thumbColorOff = ContextCompat.getColor(context, R.color.white)
    private val thumbColorOn = ContextCompat.getColor(context, R.color.white)
    private val iconColorOff = ContextCompat.getColor(context, R.color.gray_600)
    private val iconColorOn = ContextCompat.getColor(context, R.color.blue_600)

    // Sun and Moon paths
    private val sunPath = Path()
    private val moonPath = Path()

    init {
        setupPaints()
        createIconPaths()
    }

    private fun setupPaints() {
        paint.style = Paint.Style.FILL
        thumbPaint.style = Paint.Style.FILL
        thumbPaint.setShadowLayer(8f, 0f, 4f, Color.argb(50, 0, 0, 0))
        iconPaint.style = Paint.Style.FILL
    }

    private fun createIconPaths() {
        // Sun icon path (simplified)
        sunPath.apply {
            reset()
            addCircle(0f, 0f, 8f, Path.Direction.CW)
            // Add sun rays
            for (i in 0 until 8) {
                val angle = i * 45f * Math.PI / 180
                val startX = (12 * kotlin.math.cos(angle)).toFloat()
                val startY = (12 * kotlin.math.sin(angle)).toFloat()
                val endX = (16 * kotlin.math.cos(angle)).toFloat()
                val endY = (16 * kotlin.math.sin(angle)).toFloat()
                moveTo(startX, startY)
                lineTo(endX, endY)
            }
        }

        // Moon icon path (crescent)
        moonPath.apply {
            reset()
            addCircle(0f, 0f, 10f, Path.Direction.CW)
            addCircle(4f, -2f, 8f, Path.Direction.CCW)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (80 * resources.displayMetrics.density).toInt()
        val desiredHeight = (40 * resources.displayMetrics.density).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)

        trackWidth = width.toFloat()
        trackHeight = height.toFloat()
        trackRadius = trackHeight / 2f
        thumbRadius = trackRadius - 4f

        updateThumbPosition()
    }

    private fun updateThumbPosition() {
        thumbPosition = if (isChecked) {
            trackWidth - trackRadius
        } else {
            trackRadius
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw track
        val trackColor = interpolateColor(trackColorOff, trackColorOn, animationProgress)
        paint.color = trackColor
        canvas.drawRoundRect(0f, 0f, trackWidth, trackHeight, trackRadius, trackRadius, paint)

        // Calculate current thumb position
        val currentThumbX = trackRadius + (trackWidth - 2 * trackRadius) * animationProgress

        // Draw thumb shadow
        thumbPaint.color = Color.argb(30, 0, 0, 0)
        canvas.drawCircle(currentThumbX + 2f, trackHeight / 2f + 2f, thumbRadius, thumbPaint)

        // Draw thumb
        val thumbColor = interpolateColor(thumbColorOff, thumbColorOn, animationProgress)
        thumbPaint.color = thumbColor
        canvas.drawCircle(currentThumbX, trackHeight / 2f, thumbRadius, thumbPaint)

        // Draw icon
        canvas.save()
        canvas.translate(currentThumbX, trackHeight / 2f)

        val iconColor = interpolateColor(iconColorOff, iconColorOn, animationProgress)
        iconPaint.color = iconColor

        // Animate between sun and moon
        val iconAlpha = (255 * (1f - abs(animationProgress - 0.5f) * 2f)).toInt()
        iconPaint.alpha = iconAlpha

        if (animationProgress < 0.5f) {
            // Draw sun
            canvas.scale(0.8f, 0.8f)
            canvas.drawPath(sunPath, iconPaint)
        } else {
            // Draw moon
            canvas.scale(0.6f, 0.6f)
            canvas.drawPath(moonPath, iconPaint)
        }

        canvas.restore()
    }

    private fun interpolateColor(startColor: Int, endColor: Int, fraction: Float): Int {
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)

        val endA = Color.alpha(endColor)
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)

        return Color.argb(
            (startA + (endA - startA) * fraction).toInt(),
            (startR + (endR - startR) * fraction).toInt(),
            (startG + (endG - startG) * fraction).toInt(),
            (startB + (endB - startB) * fraction).toInt()
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                toggle()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun toggle() {
        setChecked(!isChecked, true)
    }

    fun setChecked(checked: Boolean, animate: Boolean = true) {
        if (isChecked == checked) return

        isChecked = checked

        if (animate) {
            animateToPosition()
        } else {
            animationProgress = if (checked) 1f else 0f
            updateThumbPosition()
            invalidate()
        }

        onToggleListener?.invoke(isChecked)
    }

    private fun animateToPosition() {
        val targetProgress = if (isChecked) 1f else 0f

        ValueAnimator.ofFloat(animationProgress, targetProgress).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                animationProgress = animator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun setOnToggleListener(listener: (Boolean) -> Unit) {
        onToggleListener = listener
    }

    fun isChecked(): Boolean = isChecked
}
