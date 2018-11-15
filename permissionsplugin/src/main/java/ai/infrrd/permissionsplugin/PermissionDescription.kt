package ai.infrrd.permissionsplugin

class PermissionDescription(val permission:String,var description:String?) {
    constructor(permission: String) : this(permission,null)
}