package ai.infrrd.permissionsplugin


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.widget.TextView
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import ai.infrrd.permissionsplugin.R
import android.util.Log
import android.util.TypedValue
import android.view.*


internal class PermissionsDescriptionDialog: AppCompatDialogFragment() {


    var positiveCallBack: () -> Unit = {}
    var negativeCallBack: () -> Unit = {}
    lateinit var permissionDescription: List<PermissionGroup>
    lateinit var titleString:String
    var packageManager: PackageManager? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ExpandableRecycler
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var width:Int = 0
    private var height:Int = 0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewManager = LinearLayoutManager(context)
        viewAdapter = ExpandableRecycler(permissionDescription, context)


        packageManager = context?.packageManager
        var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        var inflater: LayoutInflater = LayoutInflater.from(activity)


        var view: View = inflater.inflate(R.layout.permissions_description_dialog,null)

        recyclerView = view.findViewById<RecyclerView>(R.id.permissions_recycler).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter

        }

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false


//
//        for(permission in permissionDescription) {
//            var permissionView:View = inflater.inflate(R.layout.permission_description,null)
//            var permissionLayout:LinearLayout = view.findViewById(R.id.permission_layout)
//            var textView = permissionView.findViewById<TextView>(R.id.permission_title)
//            var startRotationAngle = 0f
//            var endRotationAngle = 180f
//            var dropDownExpanded = false
//            textView.text = getPermissionGroup(permission.permission)
//            if(permission.description==null) {
//                permission.description = getDescription(permission.permission)
//            }
//            textView.bringToFront()
//
//            var textView1 = permissionView.findViewById<TextView>(R.id.permission_description)
//            textView1.text = permission.description
//            var arrow = permissionView.findViewById<ImageView>(R.id.arrow)
//            var animatorArrow = ObjectAnimator.ofFloat(arrow, "rotation", startRotationAngle,endRotationAngle).apply {
//                duration = 200
//            }
//
////            var animatorDescription = ObjectAnimator.ofFloat(textView1, "translationY", 70f).apply {
////                duration = 200
////            }
////
////            animatorDescription.addListener(
////                object : Animator.AnimatorListener {
////                    override fun onAnimationRepeat(animation: Animator?) {
////                    }
////
////                    override fun onAnimationCancel(animation: Animator?) {
////                    }
////
////                    override fun onAnimationStart(animation: Animator?) {
////                        if(!dropDownExpanded) {
////                            textView1.visibility = View.VISIBLE
////                        }
////                    }
////
////                    override fun onAnimationEnd(animation: Animator?) {
////                        if(!dropDownExpanded) {
////                        }
////                    }
////
////                })
//
//            arrow.setOnClickListener {
//                if(!dropDownExpanded) {
//                    textView1.visibility = View.VISIBLE
//                    animatorArrow.start()
//                }
//                else {
//                    textView1.visibility = View.GONE
//                    animatorArrow.reverse()
//                }
//                dropDownExpanded=!dropDownExpanded
//
//            }
//            val drawable = getPermissionDrawable(permission.permission)
//            var image = permissionView.findViewById<ImageView>(R.id.permission_icon)
//            image.setImageDrawable(drawable)
//            permissionLayout.addView(permissionView)
//
//        }
        builder.setView(view)

        view.measure(0,
            0)

        width=view.measuredWidth
        height = view.measuredHeight

        Log.d("Inside",""+width+","+height)

        builder.setPositiveButton("Enable") { DialogFragment, i ->
                positiveCallBack()
        }

        builder.setNegativeButton("Deny") { DialogFragment, i ->
            negativeCallBack()
        }

        var titleText = TextView(context)
        titleText.text = titleString
        titleText.textSize = 17f
        titleText.setTextColor(Color.DKGRAY)
        titleText.setPadding(30,30,30,30)

        builder.setCustomTitle(titleText)
        val alert = builder.create()
        return alert
    }


}


