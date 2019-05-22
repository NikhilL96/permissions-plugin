package com.example.inflap165.permissionsframework

import ai.infrrd.permissionsplugin.PermissionCallBacks
import ai.infrrd.permissionsplugin.PermissionDescription
import ai.infrrd.permissionsplugin.PermissionsPlugin
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_permissions.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PermissionsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PermissionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PermissionsFragment : Fragment(), PermissionCallBacks {

    private val permissions = mutableListOf<PermissionDescription>()
    lateinit var permissionsPlugin: PermissionsPlugin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("look here",context?.resources?.getString(ai.infrrd.permissionsplugin.R.string.positive_label))

        permissions.add(PermissionDescription(Manifest.permission.READ_EXTERNAL_STORAGE, "Camera permission"))
        permissions.add(PermissionDescription(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        permissions.add(PermissionDescription(Manifest.permission.CAMERA,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_WIFI_STATE,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.BLUETOOTH,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.RECORD_AUDIO,"blah blah"))
        permissions.add(PermissionDescription(Manifest.permission.ACCESS_FINE_LOCATION,"blah blah"))

        permissionsPlugin = PermissionsPlugin(this, this)

        fragment_button.setOnClickListener {
            permissionsPlugin.checkPermissions(permissions)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        permissionsPlugin.onRequestPermissionResult(requestCode,permissions,grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == PermissionsPlugin.requestCode) {
            permissionsPlugin.activityResultCallback()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onPermissionDenied(deniedPermissions:List<String>) {
        Toast.makeText(context,"Denied in fragment$deniedPermissions", Toast.LENGTH_SHORT).show()
        Log.d(TAG, (deniedPermissions).toString())
    }

    override fun onPermissionDisabled(disabledPermissions:List<String>) {
        Toast.makeText(context,"Disabled in fragment$disabledPermissions", Toast.LENGTH_SHORT).show()
        Log.d(TAG,disabledPermissions.toString())
    }

    override fun onPermissionGranted() {
        Toast.makeText(context,"Granted in fragment", Toast.LENGTH_SHORT).show()

        Log.d(TAG,"Permissions Granted in fragment")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }


    companion object {
        fun newInstance() = PermissionsFragment()
        var TAG = "Fragment"
    }
}
