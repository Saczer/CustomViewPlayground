package pl.olszak.michal.customview

import android.content.Context

/**
 * @author molszak
 *         created on 22.01.2018.
 */
fun dpToPx(context: Context, value: Float): Float {
    return value * context.resources.displayMetrics.density
}