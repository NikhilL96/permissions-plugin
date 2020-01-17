package ai.infrrd.permissionsplugin

import android.graphics.drawable.Drawable
import android.util.Log

//For Android 10 and above, please pass the permissionGroup parameter
class PermissionDescription(internal val permission:String,internal var description:String?,internal var permissionGroup: String? = null) {

    constructor(permission: String) : this(permission,null, null)

    init {
        setDescription()
    }

    private fun setDescription() {
        description?.let { description = "\u2022$it" } ?:
        run{ description = "\u2022"+
                "Enable permission to " +
                permission.subSequence(permission.indexOfLast { it == '.' }+1,permission.lastIndex+1).toString().
                    replace('_',' ').
                    toLowerCase().capitalize()

        }
    }
}

internal class PermissionGroup(val group:String,var description:String,var icon: Drawable?)