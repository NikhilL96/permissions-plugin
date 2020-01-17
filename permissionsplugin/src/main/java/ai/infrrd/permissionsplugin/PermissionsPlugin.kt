package ai.infrrd.permissionsplugin

import ai.infrrd.permissionsplugin.utils.getApplicationName
import ai.infrrd.permissionsplugin.utils.getPermissionDrawable
import ai.infrrd.permissionsplugin.utils.getPermissionGroup
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast


class PermissionsPlugin(
    private val fragment: Fragment?,
    private val activity: Activity?,
    private val permissionCallBacks: PermissionCallBacks?
) {

    constructor(fragment: Fragment, permissionCallBacks: PermissionCallBacks?) : this(
        fragment,
        null,
        permissionCallBacks
    ) {
        fragment.context?.let {
            this.context = it
            init()
        }
    }

    constructor(activity: Activity, permissionCallBacks: PermissionCallBacks?) : this(
        null,
        activity,
        permissionCallBacks
    ) {
        this.context = activity
        init()
    }

    private lateinit var context: Context
    private lateinit var permissions: Array<String>
    private var permissionDisabled = false
    private var permissionDenied = false
    private val permissionsDisabled: MutableList<PermissionDescription> = mutableListOf()
    private val permissionsDenied: MutableList<PermissionDescription> = mutableListOf()
    private var descriptionDialog = PermissionsDescriptionDialog()
    private var warningDialog = PermissionsDescriptionDialog()

    private fun init() {
        warningDialog.setContext(context)
        descriptionDialog.setContext(context)
    }

    companion object {
        const val requestCode: Int = 112
        private const val PREFS_FILE_NAME = "First Time Permission"
        var permissionInFocus = false
    }


    private fun initializePermissionsArray(permissionDescription: List<PermissionDescription>) {
        permissions = Array(permissionDescription.size) { "" }
        for ((i, permission) in permissionDescription.withIndex()) {
            permissions[i] = permission.permission
        }
    }

    private fun groupPermissions(permissionDescription: List<PermissionDescription>): List<PermissionGroup> {
        val permissionGroups: MutableList<PermissionGroup> = mutableListOf()

        for (permission in permissionDescription) {

            val newPermission: PermissionGroup? = permission.description?.let {
                PermissionGroup(
                    getPermissionGroup(context, permission.permission, permission.permissionGroup), it,
                    getPermissionDrawable(context, permission.permission, permission.permissionGroup)
                )
            }
            var groupPresent = false
            for (group in permissionGroups) {
                if (getPermissionGroup(context, permission.permission, permission.permissionGroup) == group.group) {
                    group.description = group.description + "\n" + permission.description
                    groupPresent = true
                    break
                }
            }
            if (!groupPresent) {
                newPermission?.let { permissionGroups.add(newPermission) }
            }
        }
        return permissionGroups
    }


    private fun firstTimeAskingPermission(permission: String) {

        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        if (!sharedPreference.contains(permission)) {
            sharedPreference.edit().putBoolean(permission, true).apply()
        } else {
            sharedPreference.edit().putBoolean(permission, false).apply()
        }
    }

    private fun removePreference(permission: String) {
        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        if (sharedPreference.contains(permission)) {
            sharedPreference.edit().remove(permission).apply()
        }
    }

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (!permissionDisabled) {
            permissionInFocus = false
            val permissionsDisabled: MutableList<String> = mutableListOf()
            val permissionsDenied: MutableList<String> = mutableListOf()

            checkPermissionStatus(this.permissions.asList(), permissionsDenied, permissionsDisabled)

            if (permissionDenied) {
                permissionCallBacks?.onPermissionDenied(permissionsDenied)
            }
            if (permissionDisabled) {
                permissionCallBacks?.onPermissionDisabled(permissionsDisabled)
            } else if (!permissionDenied) {
                permissionCallBacks?.onPermissionGranted()
            }
        } else {
            setWarningDialog(permissionsDisabled)
        }
    }

    fun activityResultCallback() {
        val permissionsDisabled: MutableList<String> = mutableListOf()
        val permissionsDenied: MutableList<String> = mutableListOf()
        checkPermissionStatus(permissions.toList(), permissionsDenied, permissionsDisabled)
        if (permissionDenied) {
            permissionCallBacks?.onPermissionDenied(permissionsDenied)
        }
        if (permissionDisabled) {
            permissionCallBacks?.onPermissionDisabled(permissionsDisabled)
        } else if (!permissionDenied) {
            permissionCallBacks?.onPermissionGranted()
        }
    }

    private fun <X> checkPermissionStatus(
        permissionsArray: List<X>,
        permissionsDenied: MutableList<X>,
        permissionsDisabled: MutableList<X>
    ) {

        permissionDisabled = false
        permissionDenied = false
        for ((i, permission) in permissions.withIndex()) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {

                performActionForActivityOrFragment(activityAction = { guardedActivity ->
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(guardedActivity, permission)
                        && !isFirstTimeAskingPermission(permission)
                    ) {
                        permissionDisabled = true
                        permissionsDisabled.add(permissionsArray[i])
                    } else {
                        permissionDenied = true
                        permissionsDenied.add(permissionsArray[i])
                    }
                },
                    fragmentAction = { guardedFragment ->
                        if (!guardedFragment.shouldShowRequestPermissionRationale(permission)
                            && !isFirstTimeAskingPermission(permission)
                        ) {
                            permissionDisabled = true
                            permissionsDisabled.add(permissionsArray[i])
                        } else {
                            permissionDenied = true
                            permissionsDenied.add(permissionsArray[i])
                        }
                    })
            }
        }
    }

    private fun isFirstTimeAskingPermission(permission: String): Boolean {
        return context.getSharedPreferences(PREFS_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
            .getBoolean(permission, true)
    }


    private fun getPermission() {
        permissionInFocus = true
        for (permission in permissions) {
            firstTimeAskingPermission(permission)
        }
        performActionForActivityOrFragment(activityAction = {
            ActivityCompat.requestPermissions(it, permissions, 1)
        }, fragmentAction = {
            it.requestPermissions(permissions, 1)
        })
    }


    fun checkPermissions(permissionDescription: List<PermissionDescription>) {
        initializePermissionsArray(permissionDescription)

        permissionsDenied.clear()
        permissionsDisabled.clear()

        if (validatePermissions(permissions)) {

            checkPermissionStatus(permissionDescription, permissionsDenied, permissionsDisabled)

            if (permissionDenied) {
                setDescriptionDialog(permissionsDenied)
            } else if (permissionDisabled) {
                setWarningDialog(permissionsDisabled)
            } else {
                permissionCallBacks?.onPermissionGranted()
            }
        } else {
            Toast.makeText(context, "Invalid permissions", Toast.LENGTH_LONG).show()
        }
    }

    private fun setDescriptionDialog(permissionsDenied: MutableList<PermissionDescription>) {
        descriptionDialog.positiveCallBack = {
            getPermission()
        }

        descriptionDialog.negativeCallBack = {
            permissionCallBacks?.onPermissionDenied(getPermissionString(permissionsDenied))
        }
        descriptionDialog.permissionDescription = groupPermissions(permissionsDenied)
        descriptionDialog.titleString =
            getApplicationName(context) + " " + context.resources.getString(R.string.dialog_title)

        performActionForActivityOrFragment(activityAction = {
            descriptionDialog.show(
                (it as FragmentActivity).supportFragmentManager,
                "permissions description"
            )
        }, fragmentAction = {
            descriptionDialog.show(
                it.childFragmentManager,
                "permissions description"
            )
        })

    }

    private fun setWarningDialog(permissionDisabled: MutableList<PermissionDescription>) {
        warningDialog.positiveCallBack = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

            performActionForActivityOrFragment(activityAction = {
                val uri = Uri.fromParts("package", it.packageName, null)
                intent.data = uri
                startActivityForResult(it, intent, requestCode, null)

            }, fragmentAction = {
                val uri = Uri.fromParts("package", it.activity?.packageName, null)
                intent.data = uri
                it.startActivityForResult(intent, requestCode, null)
            })
        }
        warningDialog.negativeCallBack = {
            if (permissionDenied) {
                activityResultCallback()
            } else {
                permissionCallBacks?.onPermissionDisabled(getPermissionString(permissionDisabled))
            }
        }
        warningDialog.permissionDescription = groupPermissions(permissionDisabled)
        warningDialog.titleString =
            getApplicationName(context) + " " + context.resources.getString(R.string.dialog_warning_title)

        performActionForActivityOrFragment(activityAction = {
            warningDialog.show(
                (it as FragmentActivity).supportFragmentManager,
                "permissions description"
            )
        }, fragmentAction = {
            warningDialog.show(
                it.childFragmentManager,
                "permissions description"
            )
        })
    }

    private fun getPermissionString(permissionDescriptions: MutableList<PermissionDescription>): List<String> {
        val permissions = mutableListOf<String>()
        for (permissionDescription in permissionDescriptions) {
            permissions.add(permissionDescription.permission)
        }
        return permissions
    }

    private fun validatePermissions(permissions: Array<String>): Boolean {

        val manifestPermissions: List<String> =
            context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions.toList()
        for (permission in permissions) {
            if (!manifestPermissions.contains(permission)) {
                return false
            }
        }
        return true
    }

    private fun performActionForActivityOrFragment(
        activityAction: ((Activity) -> Unit)? = null,
        fragmentAction: ((Fragment) -> Unit)? = null
    ) {
        activity?.let {
            activityAction?.invoke(it)
        }

        fragment?.let {
            fragmentAction?.invoke(it)
        }

    }
}


interface PermissionCallBacks {

    fun onPermissionDenied(deniedPermissions: List<String>)

    fun onPermissionDisabled(disabledPermissions: List<String>)

    fun onPermissionGranted()
}