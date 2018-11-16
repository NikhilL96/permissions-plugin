package ai.infrrd.permissionsplugin

import ai.infrrd.permissionsplugin.utils.getApplicationName
import ai.infrrd.permissionsplugin.utils.getPermissionDrawable
import ai.infrrd.permissionsplugin.utils.getPermissionGroup
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.widget.Toast


class PermissionsPlugin(private val activity: Activity,private val context: Context, private var permissionCallBacks: PermissionCallBacks?) {

    private val PREFS_FILE_NAME = Resources.getSystem().getString(R.string.shared_preference_filaname)
    private lateinit var permissions: Array<String>
    private var permissionDisabled = false
    private var permissionDenied = false
    private var permissionsDisabled:MutableList<PermissionDescription> = mutableListOf()
    private var permissionsDenied:MutableList<PermissionDescription> = mutableListOf()


    private fun initializePermissionsArray(permissionDescription: MutableList<PermissionDescription>){
        permissions = Array(permissionDescription.size){""}
        for((i, permission) in permissionDescription.withIndex()) {
            permissions[i] = permission.permission
        }

    }
    private fun groupPermissions(permissionDescription:List<PermissionDescription>):List<PermissionGroup> {
        val permissionGroups:MutableList<PermissionGroup> = mutableListOf()

        for(permission in permissionDescription) {

            var newPermission:PermissionGroup? = permission.description?.let {PermissionGroup(getPermissionGroup(context,permission.permission),it,
                getPermissionDrawable(context,permission.permission))  }
            var groupPresent = false
            for(group in permissionGroups) {
                if(getPermissionGroup(context,permission.permission) == group.group ){
                    group.description = group.description+ "\n" + permission.description
                    groupPresent = true
                    break
                }
            }
            if(!groupPresent) {
                newPermission?.let { permissionGroups.add(newPermission) }
            }
        }
        return permissionGroups
    }


    private var descriptionDialog = PermissionsDescriptionDialog()
    private var warningDialog = PermissionsDescriptionDialog()

    private fun firstTimeAskingPermission(permission: String) {

        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
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
        var oneOrMoreDisabled = false
        var permissionsDisabled: MutableList<String> = mutableListOf()
        var permissionsDenied: MutableList<String> = mutableListOf()

        for((i,result) in grantResults.withIndex()) {
            if(result == PackageManager.PERMISSION_DENIED) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    oneOrMoreDisabled = true
                    permissionsDisabled.add(permissions[i])
                }
                else {
                    oneOrMoreDenied = true
                    permissionsDenied.add(permissions[i])

                }
            }
        }
        if(oneOrMoreDenied) {
            permissionCallBacks?.onPermissionDenied(permissionsDenied)
        }
        if(oneOrMoreDisabled) {
            permissionCallBacks?.onPermissionDisabled(permissionsDisabled)
        }
        else if(!oneOrMoreDenied) {
            permissionCallBacks?.onPermissionGranted()
        }
    }

    private fun isFirstTimeAskingPermission(permission:String): Boolean{
        return context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(permission, true)
    }


    private fun getPermission() {
        for(permission in permissions) {
            firstTimeAskingPermission(permission)
        }
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }


    fun checkPermissions(permissionDescription: MutableList<PermissionDescription>) {
        initializePermissionsArray(permissionDescription)
        permissionDisabled = false
        permissionDenied = false
        permissionsDenied.clear()
        permissionsDisabled.clear()

        if (validatePermissions(permissions)) {

            for ((i, permission) in permissions.withIndex()) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
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
                    if (permissionDisabled) {
                        setWarningDialog(permissionsDisabled)
                    }
                }

                descriptionDialog.negativeCallBack = {
                    permissionCallBacks?.onPermissionDenied(getPermissionString(permissionsDenied))

                }

                descriptionDialog.permissionDescription = groupPermissions(permissionsDenied)
                descriptionDialog.titleString = getApplicationName(context) + Resources.getSystem().getString(R.string.dialog_title)

                descriptionDialog.show(
                    (activity as FragmentActivity).supportFragmentManager,
                    "permissions description"
                )
            }
            else if (permissionDisabled) {
                    setWarningDialog(permissionsDisabled)

                }
            } else {
                Toast.makeText(context, "Invalid permissions", Toast.LENGTH_LONG).show()
            }
        }

    private fun setWarningDialog(permissionDisabled: MutableList<PermissionDescription>) {
        warningDialog.positiveCallBack = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            startActivity(context, intent, null)
        }
        warningDialog.negativeCallBack = {
            permissionCallBacks?.onPermissionDisabled(getPermissionString(permissionDisabled))

        }
        warningDialog.permissionDescription = groupPermissions(permissionsDisabled)
        warningDialog.titleString = getApplicationName(context)+ Resources.getSystem().getString(R.string.dialog_warning_title)
        warningDialog.show((activity as FragmentActivity).supportFragmentManager, "permissions description")
    }


    private fun getPermissionString(permissionDescriptions: MutableList<PermissionDescription>): List<String> {
        val permissions = mutableListOf<String>()
        for(permissionDescription in permissionDescriptions) {
            permissions.add(permissionDescription.permission)
        }
        return permissions
    }
    
    private fun validatePermissions(permissions:Array<String>):Boolean {

        val manifestPermissions:List<String> = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions.toList()
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