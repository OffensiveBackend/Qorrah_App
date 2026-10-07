// app/src/main/java/com/example/qorrah_app/QorrahReviewActivity.kt

package com.example.qorrah_app

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class QorrahReviewActivity : AppCompatActivity() {

    private lateinit var currentCapture: ImageView
    private lateinit var previousCapture: ImageView
    private lateinit var nextCapture: ImageView
    private lateinit var videoIndicator: View
    private lateinit var buttonReplace: TextView

    private val captures = mutableListOf<String>()

    private var currentIndex = 0

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val EXTRA_CAPTURED_FILES =
            QorrahCaptureActivity.EXTRA_CAPTURED_FILES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qorrah_review)

        val incoming =
            intent.getStringArrayListExtra(EXTRA_CAPTURED_FILES)

        if (incoming.isNullOrEmpty()) {
            finish()
            return
        }

        captures.addAll(incoming)

        bindViews()
        configureUi()
        renderCurrentCapture()
    }

    private fun bindViews() {

        currentCapture =
            findViewById(R.id.currentCapture)

        previousCapture =
            findViewById(R.id.previousCapture)

        nextCapture =
            findViewById(R.id.nextCapture)

        videoIndicator =
            findViewById(R.id.videoIndicator)

        buttonReplace =
            findViewById(R.id.buttonReplace)
    }

    private fun configureUi() {

        findViewById<View>(R.id.buttonBackReview)
            .setOnClickListener {
                finish()
            }

        findViewById<View>(R.id.buttonPrevious)
            .setOnClickListener {
                showPrevious()
            }

        findViewById<View>(R.id.buttonNext)
            .setOnClickListener {
                showNext()
            }

        findViewById<View>(R.id.buttonDelete)
            .setOnClickListener {
                deleteCurrentCapture()
            }

        findViewById<View>(R.id.buttonConfirm)
            .setOnClickListener {
                confirmCaptures()
            }

        currentCapture.setOnLongClickListener {

            buttonReplace.visibility = View.VISIBLE

            buttonReplace.postDelayed(
                {
                    buttonReplace.visibility = View.GONE
                },
                2500L
            )

            true
        }

        buttonReplace.setOnClickListener {

            Toast.makeText(
                this,
                "Return to Capture to replace this item",
                Toast.LENGTH_SHORT
            ).show()

            /*
             * Replacement is intentionally routed back through the
             * same Qorrah capture chamber. The current implementation
             * establishes the interaction contract; the next pass
             * can preserve the exact target index across the capture
             * session.
             */
            buttonReplace.visibility = View.GONE
        }

        val gestureDetector =
            GestureDetector(
                this,
                object : GestureDetector.SimpleOnGestureListener() {

                    override fun onDown(
                        e: MotionEvent
                    ): Boolean {
                        return true
                    }

                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                    ): Boolean {

                        if (e1 == null) return false

                        val dx = e2.x - e1.x
                        val dy = e2.y - e1.y

                        if (
                            kotlin.math.abs(dx) >
                            kotlin.math.abs(dy)
                        ) {

                            if (dx < 0) {
                                showNext()
                            } else {
                                showPrevious()
                            }

                            return true
                        }

                        if (
                            dy < -150f &&
                            kotlin.math.abs(dy) >
                            kotlin.math.abs(dx)
                        ) {
                            deleteCurrentCapture()
                            return true
                        }

                        return false
                    }
                }
            )

        currentCapture.setOnTouchListener { _, event ->

            gestureDetector.onTouchEvent(event)

            true
        }
    }

    private fun renderCurrentCapture() {

        if (captures.isEmpty()) {
            finish()
            return
        }

        val path = captures[currentIndex]
        val file = File(path)

        if (!file.exists()) {
            captures.removeAt(currentIndex)

            if (captures.isEmpty()) {
                finish()
                return
            }

            currentIndex =
                currentIndex.coerceAtMost(
                    captures.lastIndex
                )

            renderCurrentCapture()
            return
        }

        val isVideo =
            path.endsWith(
                ".mp4",
                ignoreCase = true
            )

        stopMediaPlayer()

        if (isVideo) {

            currentCapture.setImageDrawable(null)
            currentCapture.setBackgroundResource(
                R.drawable.bg_review_capture
            )

            videoIndicator.visibility = View.VISIBLE

            videoIndicator.setOnClickListener {
                playVideo(path)
            }

        } else {

            val bitmap =
                BitmapFactory.decodeFile(path)

            currentCapture.setImageBitmap(bitmap)

            videoIndicator.visibility = View.GONE
            videoIndicator.setOnClickListener(null)
        }

        renderAdjacentCaptures()
    }

    private fun renderAdjacentCaptures() {

        if (currentIndex > 0) {

            previousCapture.visibility = View.VISIBLE

            val previousPath =
                captures[currentIndex - 1]

            if (
                previousPath.endsWith(
                    ".mp4",
                    ignoreCase = true
                )
            ) {
                previousCapture.setImageResource(
                    android.R.drawable.ic_media_play
                )
            } else {
                previousCapture.setImageBitmap(
                    BitmapFactory.decodeFile(previousPath)
                )
            }

        } else {

            previousCapture.visibility = View.GONE
        }

        if (currentIndex < captures.lastIndex) {

            nextCapture.visibility = View.VISIBLE

            val nextPath =
                captures[currentIndex + 1]

            if (
                nextPath.endsWith(
                    ".mp4",
                    ignoreCase = true
                )
            ) {
                nextCapture.setImageResource(
                    android.R.drawable.ic_media_play
                )
            } else {
                nextCapture.setImageBitmap(
                    BitmapFactory.decodeFile(nextPath)
                )
            }

        } else {

            nextCapture.visibility = View.GONE
        }
    }

    private fun showPrevious() {

        if (currentIndex <= 0) return

        currentIndex--
        renderCurrentCapture()
    }

    private fun showNext() {

        if (currentIndex >= captures.lastIndex) return

        currentIndex++
        renderCurrentCapture()
    }

    private fun deleteCurrentCapture() {

        if (captures.isEmpty()) return

        val deleted =
            File(captures[currentIndex])

        deleted.delete()

        captures.removeAt(currentIndex)

        if (captures.isEmpty()) {
            finish()
            return
        }

        currentIndex =
            currentIndex.coerceAtMost(
                captures.lastIndex
            )

        renderCurrentCapture()
    }

    private fun playVideo(path: String) {

        stopMediaPlayer()

        mediaPlayer =
            MediaPlayer().apply {

                setDataSource(path)

                setOnPreparedListener {
                    start()
                }

                setOnCompletionListener {
                    stopMediaPlayer()
                }

                prepareAsync()
            }
    }

    private fun stopMediaPlayer() {

        mediaPlayer?.let {

            try {
                if (it.isPlaying) {
                    it.stop()
                }
            } catch (_: Exception) {
            }

            it.release()
        }

        mediaPlayer = null
    }

    private fun confirmCaptures() {

        /*
         * At this stage the reviewed local captures are confirmed.
         *
         * The next material pipeline will:
         *
         * captures
         *     ↓
         * ordered material
         *     ↓
         * PDF / image / video resource
         *     ↓
         * course → module → objective → content node
         *
         * Keeping confirmation here prevents camera concerns from
         * leaking into the learning-content Activity.
         */

        Toast.makeText(
            this,
            "${captures.size} capture(s) ready for Qorrah",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    override fun onDestroy() {

        stopMediaPlayer()

        super.onDestroy()
    }
}