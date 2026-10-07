// app/src/main/java/com/example/qorrah_app/ActualSequentialLearningContentActivity.kt

package com.example.qorrah_app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActualSequentialLearningContentActivity : AppCompatActivity() {

    private var qorrahPopup: PopupWindow? = null

    companion object {
        private const val REQUEST_IMPORT = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_actual_sequential_learning_content
        )

        val buttonBack =
            findViewById<ImageButton>(R.id.buttonBack)

        buttonBack.setOnClickListener {
            finish()
        }

        val qorrahActionBubble =
            findViewById<View>(R.id.qorrahActionBubble)

        qorrahActionBubble.setOnClickListener {
            showQorrahActions(qorrahActionBubble)
        }
    }

    private fun showQorrahActions(anchor: View) {

        if (qorrahPopup?.isShowing == true) {
            qorrahPopup?.dismiss()
            return
        }

        val popupView =
            LayoutInflater.from(this)
                .inflate(
                    R.layout.popup_qorrah_actions,
                    null
                )

        val popupWidth = 250.dp()

        qorrahPopup =
            PopupWindow(
                popupView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

        qorrahPopup?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        qorrahPopup?.isOutsideTouchable = true
        qorrahPopup?.isFocusable = true
        qorrahPopup?.elevation = 20f

        popupView
            .findViewById<View>(R.id.actionWrite)
            .setOnClickListener {
                dismissQorrahPopup()
                onWriteSelected()
            }

        popupView
            .findViewById<View>(R.id.actionCapture)
            .setOnClickListener {
                dismissQorrahPopup()
                onCaptureSelected()
            }

        popupView
            .findViewById<View>(R.id.actionImport)
            .setOnClickListener {
                dismissQorrahPopup()
                onImportSelected()
            }

        popupView
            .findViewById<View>(R.id.actionRecord)
            .setOnClickListener {
                dismissQorrahPopup()
                onRecordSelected()
            }

        popupView
            .findViewById<View>(R.id.actionShareLink)
            .setOnClickListener {
                dismissQorrahPopup()
                onShareSelected()
            }

        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(
                popupWidth,
                View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )

        val popupHeight =
            popupView.measuredHeight

        val anchorLocation = IntArray(2)

        anchor.getLocationOnScreen(anchorLocation)

        val anchorTop =
            anchorLocation[1]

        val decorView =
            window.decorView

        val decorLocation = IntArray(2)

        decorView.getLocationOnScreen(
            decorLocation
        )

        val anchorTopRelativeToDecor =
            anchorTop - decorLocation[1]

        val decorHeight =
            decorView.height

        val bottomOffset =
            decorHeight -
                    anchorTopRelativeToDecor +
                    12.dp()

        qorrahPopup?.showAtLocation(
            anchor,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            bottomOffset
        )
    }

    private fun dismissQorrahPopup() {

        qorrahPopup?.dismiss()
        qorrahPopup = null
    }

    private fun onWriteSelected() {

        Toast.makeText(
            this,
            "Write mode",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onCaptureSelected() {

        val intent =
            Intent(
                this,
                QorrahCaptureActivity::class.java
            ).apply {

                /*
                 * These become real database IDs once the
                 * learning-content screen is data-driven.
                 */
                putExtra(
                    "course_id",
                    "BCS_113"
                )

                putExtra(
                    "module_id",
                    "MODULE_001"
                )

                putExtra(
                    "objective_id",
                    "OBJECTIVE_001"
                )
            }

        startActivity(intent)
    }

    private fun onImportSelected() {

        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {

                addCategory(
                    Intent.CATEGORY_OPENABLE
                )

                type = "*/*"

                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf(
                        "image/*",
                        "audio/*"
                    )
                )
            }

        startActivityForResult(
            intent,
            REQUEST_IMPORT
        )
    }

    private fun onRecordSelected() {

        /*
         * Record remains separate from Capture.
         *
         * Capture = camera-based visual material.
         * Record = semantic audio annotation anchored
         * to selected learning text.
         */
        Toast.makeText(
            this,
            "Record mode",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onShareSelected() {

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    "Shared from Qorrah — Atomic Structure and Energy Bands"
                )
            }

        startActivity(
            Intent.createChooser(
                shareIntent,
                "Share from Qorrah"
            )
        )
    }

    @Deprecated(
        "Deprecated in Android API; retained for prototype compatibility."
    )
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            resultCode != RESULT_OK ||
            data?.data == null
        ) {
            return
        }

        if (requestCode == REQUEST_IMPORT) {

            val importedUri: Uri =
                data.data!!

            Toast.makeText(
                this,
                "Import received",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}