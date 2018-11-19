package ai.infrrd.permissionsplugin


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.widget.TextView
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
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
    private lateinit var contextForApp: Context


    fun setContext(context: Context) {
        contextForApp = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewManager = LinearLayoutManager(context)
        viewAdapter = ExpandableRecycler(permissionDescription, context)


        packageManager = context?.packageManager
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = LayoutInflater.from(activity)


        val view: View = inflater.inflate(R.layout.permissions_description_dialog,null)

        recyclerView = view.findViewById<RecyclerView>(R.id.permissions_recycler).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter

        }
        resources.getString(R.string.negative_label)

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        builder.setView(view)

        builder.setPositiveButton(context?.resources?.getString(R.string.positive_label)) { DialogFragment, i ->
                positiveCallBack()
        }

        builder.setNegativeButton(
            context?.resources?.getString(R.string.negative_label
        )) { DialogFragment, i ->
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


