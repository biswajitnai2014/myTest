package com.bis.mytest.permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import com.bis.mytest.R
import com.bis.mytest.camera.CameraActivity
import com.bis.mytest.permission.CommonMethod.Companion.createAlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        startActivity(Intent(this@MainActivity,CameraActivity::class.java))
    }


    fun checkPermission() {
        if (!PermissionUtility.hasVideoRecordingPermissions(this@MainActivity)) {


            PermissionUtility.requestVideoRecordingPermission(this, object : PermissionsCallback {
                override fun onPermissionRequest(granted: Boolean) {
                    if (!granted) {
                        dialogRecordingPermission()

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                dialogAllFileAccessPermissionAbove30()
                            }

                        }
                        else{

                        }

                    }

                }

            })

        }
    }

    private fun dialogRecordingPermission() {
        CommonMethod.createAlertDialog(
            this@MainActivity,
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

    fun dialogAllFileAccessPermissionAbove30() {
        createAlertDialog(
            this@MainActivity,
            "All file permissions",
            "Go to setting and enable all files permission",
            "OK", ""
        ) { value ->
            if (value) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
        }
    }
}