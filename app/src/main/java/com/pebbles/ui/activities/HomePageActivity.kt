package com.pebbles.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.pebbles.R
import com.pebbles.Utils.ResourceUtils.getStringResource
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.assignImageFromUrl
import com.pebbles.ui.Appwidgets.ShortCutView
import com.pebbles.ui.fragments.DeviceFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.nav_layout.view.*


class HomePageActivity : BaseActivity(), DeviceFragment.OnDeviceTabInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initNavigationView()


        initializeFragments()
        intiDevicesView()
    }

    private fun initializeFragments() {
        val fragment = DeviceFragment.newInstance(0)
        loadFragment(fragment)
    }

    private fun initializeShortCutDevices() {
        DatabaseHelper.returnUserShortCuts({
            val shortcutViews: ArrayList<ShortCutView> = arrayListOf()
            val viewsToRemove: ArrayList<ShortCutView> = arrayListOf()
            shortcut1Layout.tag = "s1"
            shortcut2Layout.tag = "s2"
            shortcut3Layout.tag = "s3"
            shortcut4Layout.tag = "s4"
            shortcutViews.add(shortcut1Layout)
            shortcutViews.add(shortcut2Layout)
            shortcutViews.add(shortcut3Layout)
            shortcutViews.add(shortcut4Layout)

            Repo.deviceShortCuts.forEach {sc->
                shortcutViews.find { view-> view.tag.toString() == sc.tag }?.let {view ->
                    Repo.devices.find { it.id?.toLong() == sc.deviceID }?.let {
                        view.setDevice(it)
                        viewsToRemove.add(view)
                        view.onRemoveClicked = { device,tag ->
                            DatabaseHelper.removeShortcut(tag, device) {
                                initializeShortCutDevices()
                            }
                        }
                    }
                }
            }

            shortcutViews.removeAll(viewsToRemove)
            shortcutViews.takeIf { it.isNotEmpty() }?.let {
                Repo.selectedShortCutAddPosition = it.first().tag.toString()
            }
            shortcutViews.forEach {
                it.setAddDeviceLayout()

                it.onAddClicked = {tag->
                    Repo.selectedShortCutAddPosition = tag
                    parentLayout.transitionToState(R.id.startClickMyTanks)
                    Snackbar.make(cl,"Add a device from device list", Snackbar.LENGTH_SHORT).show()
                }
            }

        },{
            showDismissiveAlertDialog(getStringResource(R.string.error_title),getStringResource(R.string.error_api))
        })

    }


    private fun intiDevicesView() {
        Repo.user?.id?.let {
            DatabaseHelper.returnDevicesForUid(it, {
                val fragment = supportFragmentManager.findFragmentById(R.id.bottomFragment)
                if (fragment is DeviceFragment) {
                    fragment.reloadDeviceList()
                }
                initializeShortCutDevices()
            }, {
                showDismissiveAlertDialog(getStringResource(R.string.error_title),getStringResource(R.string.error_api))
            })
        }

    }



    private fun initNavigationView() {
        nav_view.childLayout.profileNameTextView.text = Repo.user?.name
        nav_view.childLayout.profileEmailTextView.text = Repo.user?.email
        nav_view.childLayout.profileImageView.assignImageFromUrl(Repo.user?.profilePhotoUrl.toString(),true, isCircleCrop = true)
        sideMenuIcon.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
        nav_view.signOutTextView.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    navigateToSplashScreen()
                }
    }

    private fun navigateToSplashScreen() {
        Repo.user = null
        Repo.firebaseLoginResponse = null
        startActivity(Intent(this, SplashScreenActivity::class.java))
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.bottomFragment, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun shortcutAdded() {
        initializeShortCutDevices()
    }

}