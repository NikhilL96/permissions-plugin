package com.example.inflap165.permissionsframework


import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ai.infrrd.permissionsplugin.PermissionCallBacks
import ai.infrrd.permissionsplugin.PermissionDescription
import ai.infrrd.permissionsplugin.PermissionsPlugin
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PermissionCallBacks {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    val permissions = mutableListOf<PermissionDescription>()
    lateinit var permissionsPlugin: PermissionsPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions.add(
            PermissionDescription(
                Manifest.permission.CAMERA,
                "Camera permission"
            )
        )
        permissions.add(PermissionDescription(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissionsPlugin =
                PermissionsPlugin(this, this, permissions, this)
        button.setOnClickListener {
            permissionsPlugin.checkPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        permissionsPlugin.onRequestPermissionResult(requestCode,permissions,grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionDenied(deniedPermissions:List<String>) {
        Log.d(TAG, (deniedPermissions).toString())
    }

    override fun onPermissionDisabled(disabledPermissions:List<String>) {
        Log.d(TAG,disabledPermissions.toString())
    }

    override fun onPermissionGranted() {
        Log.d(TAG,"Permissions Granted")
    }
}
