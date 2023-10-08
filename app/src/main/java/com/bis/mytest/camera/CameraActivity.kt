package com.bis.mytest.camera

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import com.bis.mytest.R
import com.bis.mytest.databinding.ActivityCameraBinding
import com.bis.mytest.permission.CommonMethod.Companion.createAlertDialog
import com.bis.mytest.permission.PermissionUtility
import com.bis.mytest.permission.PermissionsCallback
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    lateinit var binding:ActivityCameraBinding
    var isVideo = false
    var contentValues: ContentValues? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_camera)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        init()
    }

    fun init(){
        PermissionUtility.requestVideoRecordingPermission(this@CameraActivity, object :
            PermissionsCallback {
            override fun onPermissionRequest(granted: Boolean) {

                if (granted) {
                    startCamera()
                    onViewClick()
                } else {
                    dialogRecordingPermission()
                }
            }
        })
    }

    private fun onViewClick() {
       binding.btnCaptureImg.setOnClickListener {

                takePhoto()



        }

        binding.btnCaptureVideo.setOnClickListener {

            videoCapture()
        }
        binding.btn.setOnClickListener {

            recording?.stop()
        }

    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and higher, use RELATIVE_PATH
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Biswajit")
                //put(MediaStore.Video.Media.RELATIVE_PATH, "Biswajit/image")
            } else {
                // For versions prior to Android 10, manage the file operations manually
                val directoryPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // For Android Nougat and higher, use getExternalStoragePublicDirectory
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                } else {
                    // For versions prior to Nougat, use a hardcoded path
                    File(Environment.getExternalStorageDirectory(), "Biswajit/image")
                }

                // Ensure the directory exists, and create it if necessary
                if (!directoryPath.exists()) {
                    directoryPath.mkdirs()
                }

                // Set the full file path
                val filePath = File(directoryPath, name).absolutePath
                put(MediaStore.MediaColumns.DATA, filePath)
            }
        }
        contentValues?.let {contentValues->


            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            imageCapture.takePicture(

                outputOptions,
                ContextCompat.getMainExecutor(binding.root.context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Toast.makeText(this@CameraActivity, ""+output, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

    }
    fun startCamera() {
        binding.root.context?.let { ctx ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also { mPreview ->
                        mPreview.setSurfaceProvider(
                            binding.preview.surfaceProvider
                        )

                    }
                imageCapture = ImageCapture.Builder().build()


                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)


                val cameraSelector = if (isVideo) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this@CameraActivity,
                        cameraSelector,
                        preview,
                        imageCapture,
                        videoCapture
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "start camera erroe" + e.message)
                }
            }, ContextCompat.getMainExecutor(ctx))
        }

    }

    private fun dialogRecordingPermission() {
        createAlertDialog(
            this,
            "Permission Denied!",
            "Go to setting and enable recording permission",
            "OK", ""
        ) { value ->
            if (value) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }


    // video

    fun videoCapture() {
        try {
            val videoCapture = this.videoCapture ?: return

            //startCountdown()

            val curRecording = recording
            if (curRecording != null) {

                curRecording.stop()
                //recording = null
                return
            }


            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
            contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".mp4")
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10 and higher, use RELATIVE_PATH
                    //  put(MediaStore.Video.Media.RELATIVE_PATH, "Biswajit/video")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Biswajit")
                } else {
                    // For versions prior to Android 10, manage the file operations manually
                    val directoryPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // For Android Nougat and higher, use getExternalStoragePublicDirectory
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                    } else {
                        // For versions prior to Nougat, use a hardcoded path
                        File(Environment.getExternalStorageDirectory(), "Biswajit/video")
                    }

                    // Ensure the directory exists, and create it if necessary
                    if (!directoryPath.exists()) {
                        directoryPath.mkdirs()
                    }

                    // Set the full file path
                    val filePath = File(directoryPath, name + ".mp4").absolutePath
                    put(MediaStore.MediaColumns.DATA, filePath)
                }
            }
            contentValues?.let { contentValues ->


                val mediaStoreOutputOptions = MediaStoreOutputOptions
                    .Builder(
                        contentResolver,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    )
                    .setContentValues(contentValues)
                    .build()
                recording = videoCapture.output
                    .prepareRecording(binding.root.context, mediaStoreOutputOptions)
                    .apply {
                        if (PermissionChecker.checkSelfPermission(
                                binding.root.context,
                                Manifest.permission.RECORD_AUDIO
                            ) ==
                            PermissionChecker.PERMISSION_GRANTED
                        ) {
                            withAudioEnabled()
                        }
                    }
                    .start(ContextCompat.getMainExecutor(binding.root.context)) { recordEvent ->
                        when (recordEvent) {
                            is VideoRecordEvent.Start -> {
                                Toast.makeText(
                                    binding.root.context,
                                    "Start Record",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            is VideoRecordEvent.Finalize -> {

                                if (!recordEvent.hasError()) {


                                } else {
                                    recording?.close()
                                    recording = null

                                }

                            }
                        }
                    }
            }
        } catch (e: Exception) {
        }
    }
}