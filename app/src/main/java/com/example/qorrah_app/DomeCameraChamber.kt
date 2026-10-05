package com.example.qorrah_app

import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class DomeCameraChamber @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val domePath = Path()

    override fun dispatchDraw(canvas: android.graphics.Canvas) {

        domePath.reset()

        val width = width.toFloat()
        val height = height.toFloat()

        /*
         * The top edge is deliberately curved rather than
         * simply using rounded top corners.
         *
         * This creates the "camera chamber" feeling:
         * the camera emerges upward into the learning environment.
         */

        domePath.moveTo(0f, 80f)

        domePath.quadTo(
            width / 2f,
            -55f,
            width,
            80f
        )

        domePath.lineTo(width, height)
        domePath.lineTo(0f, height)
        domePath.close()

        canvas.save()
        canvas.clipPath(domePath)

        super.dispatchDraw(canvas)

        canvas.restore()
    }
}