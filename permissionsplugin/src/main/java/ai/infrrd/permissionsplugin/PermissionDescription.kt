package ai.infrrd.permissionsplugin

class PermissionDescription(val permission:String,var description:String?) {
    constructor(permission: String) : this(permission,null)
    init {
        setDescription()
    }

    private fun setDescription() {
        description?.let { description = "\u2022 $it" } ?:run{ description = "\u2022 "+"Enable permission to " +
                permission.subSequence(permission.indexOfLast { it == '.' }+1,permission.lastIndex+1).toString().
                    replace('_',' ').
                    toLowerCase().capitalize()
        }
    }
}