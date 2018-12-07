package ai.infrrd.permissionsplugin;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(RobolectricTestRunner::class)
//@Config(sdk = [Build.VERSION_CODES.O_MR1])

class ExampleUnitTest {
//    val permissions = mutableListOf<PermissionDescription>()
//    lateinit var activity: Activity
//    var deniedPermission = mutableListOf<String>()
//    var disabledPermissions = mutableListOf<String>()
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        activity = Robolectric.buildActivity(MainActivity::class.java)
//            .create()
//            .resume()
//            .get()
//    }
//
//    @Test
//    fun allGranted() {
//        permissions.add(PermissionDescription(Manifest.permission.READ_EXTERNAL_STORAGE, "Camera permission"))
//        permissions.add(PermissionDescription(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//        permissions.add(PermissionDescription(Manifest.permission.CAMERA,"blah blah"))
//        permissions.add(PermissionDescription(Manifest.permission.ACCESS_WIFI_STATE,"blah blah"))
//        permissions.add(PermissionDescription(Manifest.permission.BLUETOOTH,"blah blah"))
//        permissions.add(PermissionDescription(Manifest.permission.RECORD_AUDIO,"blah blah"))
//        permissions.add(PermissionDescription(Manifest.permission.ACCESS_FINE_LOCATION,"blah blah"))
//        val plugin = PermissionsPlugin(activity,MainActivity())
//        plugin.checkPermissions(permissions)
//        var permissions:Array<out String> = emptyArray()
//        Assert.assertEquals(4, 2 + 2)
//    }
}