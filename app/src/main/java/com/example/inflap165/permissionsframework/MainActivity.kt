package com.example.inflap165.permissionsframework


import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ai.infrrd.permissionsplugin.PermissionCallBacks
import ai.infrrd.permissionsplugin.PermissionDescription
import ai.infrrd.permissionsplugin.PermissionsPlugin
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PermissionCallBacks {

    companion object {
        private val TAG = "asdasd"
    }

    val permissions = mutableListOf<PermissionDescription>()
    lateinit var permissionsPlugin: PermissionsPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("look here",baseContext.resources.getString(ai.infrrd.permissionsplugin.R.string.positive_label))

        permissions.add(PermissionDescription(Manifest.permission.READ_EXTERNAL_STORAGE, "Camera permission"))
        permissions.add(PermissionDescription(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissions.add(PermissionDescription(Manifest.permission.CAMERA,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_WIFI_STATE,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.BLUETOOTH,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.RECORD_AUDIO,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_FINE_LOCATION,"blah blah"))

        permissionsPlugin = PermissionsPlugin(this, this)

        button.setOnClickListener {
            permissionsPlugin.checkPermissions(permissions)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        permissionsPlugin.onRequestPermissionResult(requestCode,permissions,grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == PermissionsPlugin.requestCode) {
            permissionsPlugin.activityResultCallback()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onPermissionDenied(deniedPermissions:List<String>) {
        Toast.makeText(baseContext,"Denied$deniedPermissions",Toast.LENGTH_SHORT).show()
        Log.d(TAG, (deniedPermissions).toString())
    }

    override fun onPermissionDisabled(disabledPermissions:List<String>) {
        Toast.makeText(baseContext,"Disabled$disabledPermissions",Toast.LENGTH_SHORT).show()
        Log.d(TAG,disabledPermissions.toString())
    }

    override fun onPermissionGranted() {
        Toast.makeText(baseContext,"Granted",Toast.LENGTH_SHORT).show()

        Log.d(TAG,"Permissions Granted")
    }
}
