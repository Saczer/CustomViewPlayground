package pl.olszak.michal.customview

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.util.concurrent.TimeUnit

/**
 * @author molszak
 *         created on 23.01.2018.
 */
class AnimatedRectangle @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val initialPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val targetPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()

    private var iColor: Int = Color.GREEN
    private var tColor: Int = Color.RED

    private var radius: Float = INITIAL_RADIUS
    private var rotate: Float = INITIAL_RADIUS
    private var animator: ValueAnimator? = null

    private val animatorUpdateListener: ValueAnimator.AnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animator ->
        radius = animator.getAnimatedValue(PROPERTY_RADIUS) as Float
        rotate = animator.getAnimatedValue(PROPERTY_ROTATION) as Float
        initialPaint.apply {
            color = animator.getAnimatedValue(PROPERTY_COLOR) as Int
        }
        invalidate()
    }

    private val animatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            animation?.removeAllListeners()

            if (radius == INITIAL_RADIUS) {
                startAnimation()
            } else {
                reverseAnimation()
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

    }

    init {
        initializeParameters(attrs)
        initialPaint.apply {
            color = iColor
            style = Paint.Style.FILL
        }
        targetPaint.apply {
            color = tColor
            style = Paint.Style.FILL
        }
    }

    private fun initializeParameters(attrs: AttributeSet?) {

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            val viewWidth = width / 2
            val viewHeight = height / 2
            val leftTopX = viewWidth - 120f
            val leftTopY = viewHeight - 120f
            val rightBotX = viewWidth + 120f
            val rightBotY = viewWidth + 120f

            it.rotate(rotate, viewWidth.toFloat(), viewHeight.toFloat())
            it.drawRoundRect(leftTopX, leftTopY, rightBotX, rightBotY, radius, radius, initialPaint)
        }
    }

    fun startAnimation() {
        val propertyRadius: PropertyValuesHolder = PropertyValuesHolder.ofFloat(PROPERTY_RADIUS, 0f, 120f)
        val propertyRotate: PropertyValuesHolder = PropertyValuesHolder.ofFloat(PROPERTY_ROTATION, 0f, 360f)
        val propertyColor: PropertyValuesHolder = PropertyValuesHolder.ofObject(PROPERTY_COLOR, ArgbEvaluator(), iColor, tColor)
        setUpAnimator(propertyRadius, propertyRotate, propertyColor)
    }

    private fun reverseAnimation() {
        val propertyRadius: PropertyValuesHolder = PropertyValuesHolder.ofFloat(PROPERTY_RADIUS, 120f, 0f)
        val propertyRotate: PropertyValuesHolder = PropertyValuesHolder.ofFloat(PROPERTY_ROTATION, 360f, 0f)
        val propertyColor: PropertyValuesHolder = PropertyValuesHolder.ofObject(PROPERTY_COLOR, ArgbEvaluator(), tColor, iColor)
        setUpAnimator(propertyRadius, propertyRotate, propertyColor)
    }

    private fun setUpAnimator(vararg values: PropertyValuesHolder) {
        animator = ValueAnimator()
        animator?.let {
            it.setValues(*values)
            it.duration = TimeUnit.SECONDS.toMillis(2)
            it.interpolator = interpolator
            it.addUpdateListener(animatorUpdateListener)
            it.addListener(animatorListener)
            it.start()
        }
    }

    companion object {
        private const val INITIAL_RADIUS = 0f

        private const val PROPERTY_RADIUS = "PROPERTY_RADIUS"
        private const val PROPERTY_ROTATION = "PROPERTY_ROTATION"
        private const val PROPERTY_COLOR = "PROPERTY_COLOR"
    }
}