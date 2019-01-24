package ai.infrrd.permissionsplugin

import android.animation.ObjectAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

internal class ExpandableRecycler(private val permissions: List<PermissionGroup>, val context: Context?) :
    RecyclerView.Adapter<ExpandableRecycler.MyViewHolder>()
{
    var isExpanded = Array<Boolean>(permissions.size){ false}
    class MyViewHolder(val layout: CardView) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.permission_description, parent, false) as CardView

        return MyViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var animatorArrow = ObjectAnimator.ofFloat(holder.layout.findViewById<ImageView>(R.id.arrow),
            "rotation", 0f,180f).apply {
            duration = 200
        }

        setMargins(position,holder)

        holder.layout.findViewById<TextView>(R.id.permission_title).text = permissions[position].group
        holder.layout.findViewById<TextView>(R.id.permission_description).text = permissions[position].description

        holder.layout.findViewById<TextView>(R.id.permission_description).visibility = if(isExpanded[position])  View.VISIBLE else View.GONE

        holder.layout.findViewById<ImageView>(R.id.permission_icon).setImageDrawable(permissions[position].icon)
        holder.layout.findViewById<View>(R.id.permission).setOnClickListener {
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

    private fun setMargins(position:Int,holder:MyViewHolder) {
        if(position == permissions.size-1) {
            var margins = holder.layout.layoutParams as? ViewGroup.MarginLayoutParams
            margins?.setMargins(30,30,30,80)
            holder.layout.requestLayout()
        }

        else if(position == 0) {
            var margins = holder.layout.layoutParams as? ViewGroup.MarginLayoutParams
            margins?.setMargins(30,80,30,30)
            holder.layout.requestLayout()
        }

        else {
            var margins = holder.layout.layoutParams as? ViewGroup.MarginLayoutParams
            margins?.setMargins(30,30,30,30)
            holder.layout.requestLayout()
        }
    }

    override fun getItemCount() = permissions.size

}