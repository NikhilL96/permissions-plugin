package ai.infrrd.permissionsplugin.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

internal fun getPermissionDrawable(
    context: Context?,
    permission: String,
    permissionGroup: String?
): Drawable? {
    return try {
        val drawable: Drawable?

        val permissionInfo = context?.packageManager?.getPermissionInfo(permission, 0)
        val groupInfo = context?.packageManager?.getPermissionGroupInfo(permissionGroup?:permissionInfo?.group, 0)
        drawable = context?.packageManager?.getResourcesForApplication("android")
            ?.getDrawable(groupInfo?.icon as Int, null)
        if (context != null) {
            drawable?.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP)
        }
        drawable
    } catch (ex: Exception) {
        null
    }

}