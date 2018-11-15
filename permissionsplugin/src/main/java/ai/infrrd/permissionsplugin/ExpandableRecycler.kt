package ai.infrrd.permissionsplugin

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ai.infrrd.permissionsplugin.R

class ExpandableRecycler(private val permissions: List<PermissionDescription>, val context: Context?) :

    RecyclerView.Adapter<ExpandableRecycler.MyViewHolder>() {
    var isExpanded = Array<Boolean>(permissions.size){_ -> false}
    class MyViewHolder(val linearLayout: CardView) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.permission_description, parent, false) as CardView

        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var animatorArrow = ObjectAnimator.ofFloat(holder.linearLayout.findViewById<ImageView>(R.id.arrow),
            "rotation", 0f,180f).apply {
            duration = 200
        }

        holder.linearLayout.findViewById<TextView>(R.id.permission_title).text = getPermissionGroup(permissions[position].permission)
        if(permissions[position].description==null) {
            holder.linearLayout.findViewById<TextView>(R.id.permission_description).text = getDescription(permissions[position].permission)
        }
        else {
            holder.linearLayout.findViewById<TextView>(R.id.permission_description).text = permissions[position].description

        }

        holder.linearLayout.findViewById<TextView>(R.id.permission_description).visibility = if(isExpanded[position])  View.VISIBLE else View.GONE

        holder.linearLayout.findViewById<ImageView>(R.id.permission_icon).setImageDrawable(getPermissionDrawable(permissions[position].permission))
        holder.linearLayout.findViewById<ImageView>(R.id.arrow).setOnClickListener {
            if(isExpanded[position]) {
                animatorArrow.reverse()
            }
            else {
                animatorArrow.start()
            }
            isExpanded[position] = !isExpanded[position]
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = permissions.size

    fun getDescription(permission: String):String {

        var description:String = "Enable permission to "

        description += permission.subSequence(permission.indexOfLast { it == '.' }+1,permission.lastIndex+1).toString().
            replace('_',' ').
            toLowerCase().capitalize()
        return description
    }



    private fun getPermissionDrawable(permission: String): Drawable? {
        var drawable: Drawable?
        val permissionInfo = context?.packageManager?.getPermissionInfo(permission, 0)
        val groupInfo = context?.packageManager?.getPermissionGroupInfo(permissionInfo?.group, 0)
        drawable = context?.packageManager?.getResourcesForApplication("android")?.getDrawable(groupInfo?.icon as Int,null)
        if (context!=null) {
            drawable?.setColorFilter(Color.DKGRAY,PorterDuff.Mode.SRC_ATOP)
        }
        return drawable
    }

    private fun getPermissionGroup(permission: String):String {
        val permissionInfo = context?.packageManager?.getPermissionInfo(permission, 0)
        val permissionGroupInfo = context?.packageManager?.getPermissionGroupInfo(permissionInfo?.group, 0)
        return permissionGroupInfo?.loadLabel(context?.packageManager).toString()
    }
}