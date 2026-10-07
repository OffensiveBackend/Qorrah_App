// app/src/main/java/com/example/qorrah_app/QorrahCaptureActivity.kt

package com.example.qorrah_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.video.AudioConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class QorrahCaptureActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraController: LifecycleCameraController

    private lateinit var buttonDone: TextView
    private lateinit var shutterButton: View
    private lateinit var photoMode: TextView
    private lateinit var videoMode: TextView
    private lateinit var captureModeLabel: TextView
    private lateinit var captureCount: TextView

    private var selectedMode = CaptureMode.PHOTO

    private val capturedFiles = mutableListOf<String>()

    private var recording: androidx.camera.video.Recording? = null

    private enum class CaptureMode {
        PHOTO,
        VIDEO
    }

    companion object {
        private const val REQUEST_CAMERA = 2101
        private const val REQUEST_CAMERA_AND_AUDIO = 2102

        const val EXTRA_CAPTURED_FILES = "extra_captured_files"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qorrah_capture)

        bindViews()
        configureUi()
        requestRequiredPermissions()
    }

    private fun bindViews() {

        previewView =
            findViewById(R.id.cameraPreview)

        buttonDone =
            findViewById(R.id.buttonDone)

        shutterButton =
            findViewById(R.id.shutterButton)

        photoMode =
            findViewById(R.id.photoMode)

        videoMode =
            findViewById(R.id.videoMode)

        captureModeLabel =
            findViewById(R.id.captureModeLabel)

        captureCount =
            findViewById(R.id.captureCount)
    }

    private fun configureUi() {

        shutterButton.setOnClickListener {

            when (selectedMode) {

                CaptureMode.PHOTO -> {
                    capturePhoto()
                }

                CaptureMode.VIDEO -> {
                    toggleVideoRecording()
                }
            }
        }

        buttonDone.setOnClickListener {
            finishCaptureSession()
        }

        findViewById<View>(R.id.buttonClose).setOnClickListener {

            if (recording != null) {
                stopVideoRecording()
            } else {
                finish()
            }
        }

        photoMode.setOnClickListener {

            if (recording == null) {
                selectPhotoMode()
            }
        }

        videoMode.setOnClickListener {

            if (recording == null) {
                selectVideoMode()
            }
        }

        selectPhotoMode()
    }

    private fun requestRequiredPermissions() {

        val cameraGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (!cameraGranted) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )

            return
        }

        startCamera()
    }

    private fun startCamera() {

        cameraController =
            LifecycleCameraController(this).apply {

                cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA

                setEnabledUseCases(
                    androidx.camera.view.CameraController.IMAGE_CAPTURE or
                            androidx.camera.view.CameraController.VIDEO_CAPTURE
                )

                bindToLifecycle(
                    this@QorrahCaptureActivity
                )
            }

        previewView.controller =
            cameraController
    }

    private fun selectPhotoMode() {

        selectedMode =
            CaptureMode.PHOTO

        captureModeLabel.text =
            "Capture"

        photoMode.setTextColor(
            0xFF24211D.toInt()
        )

        videoMode.setTextColor(
            0xFF8B857B.toInt()
        )
    }

    private fun selectVideoMode() {

        selectedMode =
            CaptureMode.VIDEO

        captureModeLabel.text =
            "Video"

        photoMode.setTextColor(
            0xFF8B857B.toInt()
        )

        videoMode.setTextColor(
            0xFF24211D.toInt()
        )
    }

    private fun capturePhoto() {

        val directory =
            File(
                cacheDir,
                "qorrah_captures"
            )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val filename =
            "qorrah_photo_" +
                    SimpleDateFormat(
                        "yyyyMMdd_HHmmss_SSS",
                        Locale.US
                    ).format(
                        System.currentTimeMillis()
                    ) +
                    ".jpg"

        val file =
            File(
                directory,
                filename
            )

        val outputOptions =
            androidx.camera.core.ImageCapture
                .OutputFileOptions
                .Builder(file)
                .build()

        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object :
                androidx.camera.core.ImageCapture
                .OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults:
                    androidx.camera.core.ImageCapture
                    .OutputFileResults
                ) {

                    capturedFiles.add(
                        file.absolutePath
                    )

                    updateCaptureCount()

                    Toast.makeText(
                        this@QorrahCaptureActivity,
                        "Captured",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(
                    exception:
                    androidx.camera.core.ImageCaptureException
                ) {

                    Toast.makeText(
                        this@QorrahCaptureActivity,
                        "Capture failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun toggleVideoRecording() {

        if (recording != null) {

            stopVideoRecording()

        } else {

            startVideoRecording()
        }
    }

    private fun startVideoRecording() {

        val audioGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        if (!audioGranted) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                REQUEST_CAMERA_AND_AUDIO
            )

            return
        }

        val directory =
            File(
                cacheDir,
                "qorrah_captures"
            )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val filename =
            "qorrah_video_" +
                    SimpleDateFormat(
                        "yyyyMMdd_HHmmss_SSS",
                        Locale.US
                    ).format(
                        System.currentTimeMillis()
                    ) +
                    ".mp4"

        val file =
            File(
                directory,
                filename
            )

        val outputOptions =
            FileOutputOptions
                .Builder(file)
                .build()

        val audioConfig =
            AudioConfig.create(true)

        recording =
            cameraController.startRecording(
                outputOptions,
                audioConfig,
                ContextCompat.getMainExecutor(this)
            ) { event ->

                when (event) {

                    is VideoRecordEvent.Start -> {

                        captureModeLabel.text =
                            "Recording"

                        shutterButton.alpha =
                            0.82f
                    }

                    is VideoRecordEvent.Finalize -> {

                        recording = null

                        shutterButton.alpha =
                            1f

                        if (!event.hasError()) {

                            capturedFiles.add(
                                file.absolutePath
                            )

                            updateCaptureCount()

                            captureModeLabel.text =
                                "Video"

                            Toast.makeText(
                                this@QorrahCaptureActivity,
                                "Video captured",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            file.delete()

                            Toast.makeText(
                                this@QorrahCaptureActivity,
                                "Video recording failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun stopVideoRecording() {

        recording?.stop()

        recording = null

        captureModeLabel.text =
            "Video"

        shutterButton.alpha =
            1f
    }

    private fun updateCaptureCount() {

        val count =
            capturedFiles.size

        if (count > 0) {

            buttonDone.visibility =
                View.VISIBLE

            captureCount.visibility =
                View.VISIBLE

            captureCount.text =
                count.toString()
        }
    }

    private fun finishCaptureSession() {

        if (recording != null) {
            stopVideoRecording()
        }

        if (capturedFiles.isEmpty()) {
            finish()
            return
        }

        val intent =
            Intent(
                this,
                QorrahReviewActivity::class.java
            ).apply {

                putStringArrayListExtra(
                    EXTRA_CAPTURED_FILES,
                    ArrayList(capturedFiles)
                )
            }

        startActivity(intent)

        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        when (requestCode) {

            REQUEST_CAMERA -> {

                val granted =
                    grantResults.isNotEmpty() &&
                            grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED

                if (granted) {

                    startCamera()

                } else {

                    Toast.makeText(
                        this,
                        "Camera permission is required for Capture",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }

            REQUEST_CAMERA_AND_AUDIO -> {

                val cameraGranted =
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                val audioGranted =
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                if (cameraGranted) {

                    if (!::cameraController.isInitialized) {
                        startCamera()
                    }

                    if (audioGranted) {

                        startVideoRecording()

                    } else {

                        Toast.makeText(
                            this,
                            "Microphone permission is required for video audio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {

        recording?.stop()

        recording = null

        super.onDestroy()
    }
}