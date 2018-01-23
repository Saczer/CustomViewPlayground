package pl.olszak.michal.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.NonNull
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import java.lang.ref.WeakReference

/**
 * @author molszak
 *         created on 22.01.2018.
 */
class Indicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val defaultPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val chosenPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val initialCircleRadius = dpToPx(context, DEFAULT_CIRCLE_RADIUS_DP)
    private val initialCircleColor = Color.WHITE
    private val initialChosenCircleColor = Color.GREEN
    private val initialDistanceBetween = dpToPx(context, DEFAULT_DISTANCE_BETWEEN)

    private var circleRadius: Float = initialCircleRadius
    private var defCircleColor: Int = initialCircleColor
    private var chosenCircleColor: Int = initialChosenCircleColor
    private var distanceBetween: Float = initialDistanceBetween
    private var numberOfCircles: Int = DEFAULT_NUMBER_OF_CIRCLES
    private var currentIndex: Int = DEFAULT_CURRENT_SELECTION
    private var locationY: Float = 0f

    private var boundPager: WeakReference<ViewPager>? = null

    private var circleLocations: List<Float>? = null

    init {
        initializeParameters(attrs)
    }

    private fun initializeParameters(attributeSet: AttributeSet?) {
        initializeAttributes(attributeSet)
        defaultPaint.apply {
            color = defCircleColor
            style = Paint.Style.FILL
        }
        chosenPaint.apply {
            color = chosenCircleColor
            style = Paint.Style.FILL
        }
    }

    private fun initializeAttributes(attributeSet: AttributeSet?) {
        attributeSet?.let { attrSet ->
            val attributeArray: TypedArray = context.obtainStyledAttributes(attrSet, R.styleable.Indicator)

            circleRadius = attributeArray.getDimension(R.styleable.Indicator_ind_radius,
                    initialCircleRadius)
            defCircleColor = attributeArray.getInt(R.styleable.Indicator_ind_default_circle_color,
                    initialCircleColor)
            chosenCircleColor = attributeArray.getInt(R.styleable.Indicator_ind_chosen_circle_color,
                    initialChosenCircleColor)
            distanceBetween = attributeArray.getDimension(R.styleable.Indicator_ind_circle_distance,
                    initialDistanceBetween)
            numberOfCircles = attributeArray.getInt(R.styleable.Indicator_ind_number_of_circles,
                    DEFAULT_NUMBER_OF_CIRCLES)

            attributeArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val isMatchParentFlagOn = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
        measureIndicesOfIndicator(isMatchParentFlagOn)

        val measuredWidth: Int = if (numberOfCircles > 0) {
            measureWidth(widthMeasureSpec)
        } else {
            0
        }
        val measuredHeight: Int = if (numberOfCircles > 0) {
            measureHeight(heightMeasureSpec)
        } else {
            0
        }

        locationY = (measuredHeight / 2).toFloat()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        circleLocations?.forEachIndexed { i, location ->
            if (currentIndex == i) {
                canvas?.drawCircle(location, locationY, circleRadius, chosenPaint)
            } else {
                canvas?.drawCircle(location, locationY, circleRadius, defaultPaint)
            }
        }
    }

    override fun requestLayout() {
        circleLocations = null
        super.requestLayout()
    }

    private fun measureIndicesOfIndicator(shouldAddScreenOffset: Boolean) {
        if (numberOfCircles > 0 && circleLocations == null) {
            var counter: Float = paddingLeft + circleRadius
            val spaceBetweenCircles: Float = circleRadius * 2 + distanceBetween

            circleLocations = List(numberOfCircles, {
                val location = counter
                counter += spaceBetweenCircles
                location
            })

            if (shouldAddScreenOffset) {
                val screenWidth: Int = context.resources.displayMetrics.widthPixels
                circleLocations?.let {
                    val widthOfView: Int = (it[numberOfCircles - 1] + circleRadius + paddingRight).toInt()
                    val spaceToAdd: Int = (screenWidth - widthOfView) / 2
                    circleLocations = it.map { location -> location + spaceToAdd }
                }
            }
        }
    }

    private fun measureHeight(measureSpec: Int): Int {
        val specMode: Int = MeasureSpec.getMode(measureSpec)
        val specSize: Int = MeasureSpec.getSize(measureSpec)
        val result: Int
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = (circleRadius * 2 + paddingTop + paddingBottom).toInt()
        }
        return result
    }

    private fun measureWidth(measureSpec: Int): Int {
        val specMode: Int = MeasureSpec.getMode(measureSpec)
        val specSize: Int = MeasureSpec.getSize(measureSpec)
        var result = 0
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            circleLocations?.let { locations ->
                if (numberOfCircles > 0) {
                    result = (locations[numberOfCircles - 1] + circleRadius + paddingRight).toInt()
                }
            }
        }

        return result
    }

    fun attach(@NonNull pager: ViewPager) {
        if (numberOfCircles != 0) {
            boundPager = WeakReference(pager)
            pager.addOnPageChangeListener(pageChangeObserver)
        }
    }

    fun attachDynamically(@NonNull pager: ViewPager) {
        if (pager.adapter != null && pager.adapter.count != numberOfCircles) {
            numberOfCircles = pager.adapter.count
            requestLayout()
        }

        attach(pager)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        boundPager?.let {
            it.get()?.removeOnPageChangeListener(pageChangeObserver)
        }
    }

    private val pageChangeObserver = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            currentIndex = position
            invalidate()
        }

    }

    companion object {
        private const val DEFAULT_CIRCLE_RADIUS_DP = 12f
        private const val DEFAULT_DISTANCE_BETWEEN = 6f
        private const val DEFAULT_NUMBER_OF_CIRCLES = 0
        private const val DEFAULT_CURRENT_SELECTION = 0
    }
}