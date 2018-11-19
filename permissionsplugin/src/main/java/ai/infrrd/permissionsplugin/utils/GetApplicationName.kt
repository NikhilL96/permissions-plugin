package ai.infrrd.permissionsplugin.utils

import android.content.Context

internal fun getApplicationName(context: Context?): String {
    return context?.packageManager?.getApplicationLabel(context.packageManager?.getApplicationInfo(context.packageName,0)).toString()
}