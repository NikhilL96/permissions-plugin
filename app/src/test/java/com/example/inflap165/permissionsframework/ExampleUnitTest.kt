package com.example.inflap165.permissionsframework

import ai.infrrd.permissionsplugin.PermissionDescription
import ai.infrrd.permissionsplugin.PermissionsPlugin
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Robolectric
import org.junit.Before



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])

class ExampleUnitTest {
    val permissions = mutableListOf<PermissionDescription>()
    lateinit var activity:Activity
    var deniedPermission = mutableListOf<String>()
    var disabledPermissions = mutableListOf<String>()
    @Before
    @Throws(Exception::class)
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .resume()
            .get()
    }

    @Test
    fun allGranted() {
        permissions.add(PermissionDescription(Manifest.permission.READ_EXTERNAL_STORAGE, "Camera permission"))
        permissions.add(PermissionDescription(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissions.add(PermissionDescription(Manifest.permission.CAMERA,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_WIFI_STATE,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.BLUETOOTH,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.RECORD_AUDIO,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_FINE_LOCATION,"blah blah"))
        val plugin = PermissionsPlugin(activity,MainActivity())
        plugin.checkPermissions(permissions)

        var permissions:Array<String> = arrayOf()
        permissions[0] = "Manifest.permission.READ_EXTERNAL_STORAGE"
        permissions[1] = "Manifest.permission.WRITE_EXTERNAL_STORAGE"
        permissions[2] = "Manifest.permission.CAMERA"
        permissions[3] = "Manifest.permission.ACCESS_WIFI_STATE"
        permissions[4] = "Manifest.permission.BLUETOOTH"
        permissions[5] = "Manifest.permission.RECORD_AUDIO"
        permissions[6] = "Manifest.permission.ACCESS_FINE_LOCATION"

        var grantTypes = intArrayOf()
        grantTypes[0] = PackageManager.PERMISSION_GRANTED
        grantTypes[1] = PackageManager.PERMISSION_GRANTED
        grantTypes[2] = PackageManager.PERMISSION_GRANTED
        grantTypes[3] = PackageManager.PERMISSION_GRANTED
        grantTypes[4] = PackageManager.PERMISSION_GRANTED
        grantTypes[5] = PackageManager.PERMISSION_GRANTED
        grantTypes[6] = PackageManager.PERMISSION_GRANTED


        assertEquals(4, 2 + 2)
    }
}
