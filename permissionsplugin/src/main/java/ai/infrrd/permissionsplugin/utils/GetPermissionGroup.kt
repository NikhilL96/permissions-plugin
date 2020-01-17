package ai.infrrd.permissionsplugin.utils

import android.content.Context

internal fun getPermissionGroup(context: Context?,permission:String, permissionGroup: String?):String {
    val permissionInfo = context?.packageManager?.getPermissionInfo(permission, 0)
    val permissionGroupInfo = context?.packageManager?.getPermissionGroupInfo(permissionGroup?:permissionInfo?.group, 0)
    return permissionGroupInfo?.loadLabel(context.packageManager).toString()
}