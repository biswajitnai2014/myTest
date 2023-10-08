package com.bis.mytest.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtility {
    private val CAMERA_PERMISSIONS:MutableList<String> =

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                }
            }

        }
        else {
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,

                Manifest.permission.RECORD_AUDIO,
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                }
            }
        }

    fun hasVideoRecordingPermissions(context: Context): Boolean =
        CAMERA_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun requestVideoRecordingPermission(context: Context, callback: PermissionsCallback) {

        Dexter.withContext(context)
            .withPermissions(CAMERA_PERMISSIONS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if user has granted all
                    if (report?.areAllPermissionsGranted() == true) {
                        callback.onPermissionRequest(true)
                    } else {
                        callback.onPermissionRequest(false)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // User has denied a permission, proceed and ask them again
                    token?.continuePermissionRequest()
                }
            }).check()
    }
}