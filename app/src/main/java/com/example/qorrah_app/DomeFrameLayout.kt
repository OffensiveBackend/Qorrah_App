package com.example.qorrah_app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout

class DomeFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val domePath = Path()

    /*
     * Controls the top corner radius for the camera card sheet.
     * 28dp top-left and top-right rounded corners (matching ChatGPT/Instagram style).
     */
    private val topCornerRadiusDp = 28f

    init {
        setWillNotDraw(true)

        clipChildren = true
        clipToPadding = true
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(
            width,
            height,
            oldWidth,
            oldHeight
        )

        buildDomePath(
            width,
            height
        )
    }

    private fun buildDomePath(
        width: Int,
        height: Int
    ) {
        domePath.reset()

        val radiusPx = topCornerRadiusDp * resources.displayMetrics.density

        /*
         * FloatArray for top-left and top-right rounded corners:
         * [rxTL, ryTL, rxTR, ryTR, rxBR, ryBR, rxBL, ryBL]
         */
        val radii = floatArrayOf(
            radiusPx, radiusPx, // Top-Left corner radius
            radiusPx, radiusPx, // Top-Right corner radius
            0f, 0f,             // Bottom-Right corner (square)
            0f, 0f              // Bottom-Left corner (square)
        )

        domePath.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            radii,
            Path.Direction.CW
        )
    }

    override fun dispatchDraw(
        canvas: Canvas
    ) {
        val saveCount = canvas.save()

        /*
         * Clips live CameraX PreviewView pixels to the top-rounded card sheet shape.
         */
        canvas.clipPath(domePath)

        super.dispatchDraw(canvas)

        canvas.restoreToCount(saveCount)
    }
}