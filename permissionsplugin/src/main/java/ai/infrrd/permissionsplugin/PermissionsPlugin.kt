package ai.infrrd.permissionsplugin

import ai.infrrd.permissionsplugin.utils.getApplicationName
import ai.infrrd.permissionsplugin.utils.getPermissionGroup
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast


class PermissionsPlugin(val activity: Activity, val context: Context, val permissionDescription: MutableList<PermissionDescription>,
                        var permissionCallBacks: PermissionCallBacks?) {

    private val PREFS_FILE_NAME = "First Time Permissions"
    private var permissions: Array<String> = Array(permissionDescription.size){""}
    private var permissionDisabled = false
    private var permissionDenied = false
    private var permissionsDisabled:MutableList<PermissionDescription> = mutableListOf()
    private var permissionsDenied:MutableList<PermissionDescription> = mutableListOf()


    init{
        for((i, permission) in permissionDescription.withIndex()) {
            permissions[i] = permission.permission
        }

    }
    private fun groupPermissions(permissionDescription:List<PermissionDescription>):List<PermissionDescription> {
        var permissionGroups:MutableList<PermissionDescription> = mutableListOf()

        for(permission in permissionDescription) {
            var groupPresent:Boolean = false
            for(group in permissionGroups) {
                if(getPermissionGroup(context,permission.permission) == getPermissionGroup(context,group.permission)) {
                    group.description = group.description+ "\n" + permission.description
                    groupPresent = true
                    break
                }
            }
            if(!groupPresent) {
                permissionGroups.add(permission)
            }
        }
        return permissionGroups
    }


    private var descriptionDialog = PermissionsDescriptionDialog()
    private var warningDialog = PermissionsDescriptionDialog()

    private fun firstTimeAskingPermission(permission: String) {

        var sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        if (!sharedPreference.contains(permission)) {
            sharedPreference.edit().putBoolean(permission, true).apply()
        }
        else {
            sharedPreference.edit().putBoolean(permission, false).apply()
        }
    }

    private fun removePreference(permission: String) {
        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        if (sharedPreference.contains(permission)) {
            sharedPreference.edit().remove(permission).apply()
        }
    }

    fun onRequestPermissionResult(requestCode:Int, permissions:Array<out String> , grantResults:IntArray) {

        var oneOrMoreDenied = false
        for((i,result) in grantResults.withIndex()) {
            if(result == PackageManager.PERMISSION_DENIED) {
                oneOrMoreDenied = true
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    permissionCallBacks?.onPermissionDisabled(getPermissionString(permissionsDisabled))
                    return
                }
            }
        }
        if(oneOrMoreDenied) {
            permissionCallBacks?.onPermissionDenied(getPermissionString(permissionsDisabled))
            return
        }
        permissionCallBacks?.onPermissionGranted()
    }

    private fun isFirstTimeAskingPermission(permission:String): Boolean{
        return context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(permission, true)
    }


    private fun getPermission() {
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }


    fun checkPermissions() {
        permissionDisabled = false
        permissionDenied =false
        permissionsDenied.clear()
        permissionsDisabled.clear()

        if(validatePermissions(permissions)) {

            for ((i,permission) in permissions.withIndex()) {
                firstTimeAskingPermission(permission)
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    removePreference(permission)
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                    && !isFirstTimeAskingPermission(permission)
                ) {
                    permissionsDisabled.add(permissionDescription[i])
                    permissionDisabled = true
                } else {
                    permissionsDenied.add(permissionDescription[i])
                    permissionDenied = true
                }
            }

            if (permissionDenied) {
                descriptionDialog.positiveCallBack = {
                    getPermission()
                }

                descriptionDialog.negativeCallBack = {
                    permissionCallBacks?.onPermissionDenied(getPermissionString(permissionDescription))

                }

                descriptionDialog.permissionDescription = groupPermissions(permissionsDenied)
                descriptionDialog.titleString = getApplicationName(context) +" needs Access to:"

                descriptionDialog.show((activity as FragmentActivity).supportFragmentManager, "permissions description")
            }


            if (permissionDisabled) {
                warningDialog.positiveCallBack = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    startActivity(context, intent, null)
                }
                warningDialog.negativeCallBack = {
                    permissionCallBacks?.onPermissionDenied(getPermissionString(permissionDescription))

                }
                warningDialog.permissionDescription = groupPermissions(permissionsDisabled)
                warningDialog.titleString = getApplicationName(context)+ " needs the following permissions for the app to function properly:"
                warningDialog.show((activity as FragmentActivity).supportFragmentManager, "permissions description")

            }
        }
        else {
            Toast.makeText(context,"Invalid permissions",Toast.LENGTH_LONG).show()
        }

    }

    fun getPermissionString(permissionDescriptions: MutableList<PermissionDescription>): List<String> {
        var permissions = mutableListOf<String>()
        for(permissionDescription in permissionDescriptions) {
            permissions.add(permissionDescription.permission)
        }
        return permissions
    }
    fun validatePermissions(permissions:Array<String>):Boolean {

        var manifestPermissions:List<String> = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions.toList()
        for(permission in permissions) {
            if(!manifestPermissions.contains(permission)) {
                return false
            }
        }
        return true
    }
}



interface PermissionCallBacks {

    fun onPermissionDenied(deniedPermissions:List<String>)

    fun onPermissionDisabled(disabledPermissions:List<String>)

    fun onPermissionGranted()
}