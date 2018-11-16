package ai.infrrd.permissionsplugin

import android.content.res.Resources
import android.graphics.drawable.Drawable

class PermissionDescription(internal val permission:String,internal var description:String?) {
    constructor(permission: String) : this(permission,null)
    init {
        setDescription()
    }

    private fun setDescription() {
        description?.let { description = Resources.getSystem().getString(R.string.bullet)+ it } ?:
        run{ description = Resources.getSystem().getString(R.string.bullet)+
                Resources.getSystem().getString(R.string.default_description) +
                permission.subSequence(permission.indexOfLast { it == '.' }+1,permission.lastIndex+1).toString().
                    replace('_',' ').
                    toLowerCase().capitalize()

        }
    }
}

internal class PermissionGroup(val group:String,var description:String,var icon: Drawable?)