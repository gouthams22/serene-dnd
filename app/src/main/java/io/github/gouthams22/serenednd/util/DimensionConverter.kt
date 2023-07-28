package io.github.gouthams22.serenednd.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

class DimensionConverter {
    companion object {
        /**
         * Converts dp to px
         * @param context context of activity
         * @param dip: value of dp
         * @return corresponding px value
         */
        fun toPx(context: Context, dip: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                context.resources.displayMetrics
            )
        }

        /**
         * Converts px to dp
         * @param context context of activity
         * @param px: value of px
         * @return corresponding dp value
         */
        fun toDp(context: Context, px: Float): Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            /*
            // This method is to be introduced in Android 14 according to official docs
            return TypedValue.deriveDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px,
                context.resources.displayMetrics
            )
             */
        }
    }
}