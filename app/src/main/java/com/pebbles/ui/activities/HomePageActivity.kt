package com.pebbles.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pebbles.R
import com.pebbles.Utils.BiometricUtils
import com.pebbles.Utils.NotificationUtils
import com.pebbles.Utils.ResourceUtils.getDrawableResource
import com.pebbles.Utils.ResourceUtils.getStringResource
import com.pebbles.backgroundServices.PebblesService
import com.pebbles.core.*
import com.pebbles.ui.Appwidgets.ShortCutView
import com.pebbles.ui.PagerAdapter
import com.pebbles.ui.fragments.DeviceFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.nav_layout.view.*
import java.util.*


class HomePageActivity : BaseActivity(), DeviceFragment.OnDeviceTabInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initNavigationView()

        initializeTabs()
        initDeviceShortcutViews()
        initTempStateListener()

        PebblesService.startService(this, "message")

        askForPushNotificationPermission()
        fetchTokens()



        initClickListeners()

        Run.after(5000) {
            showBiometricSetupIfNeeded()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListeners() {

        myTanksIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Pebbles_debug", "my tanks clicked")
                mainViewPager.currentItem = 0
            }
            false
        }
        otherDevicesIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Pebbles_debug", " otherDevicesIcon clicked")
                mainViewPager.currentItem = 1
            }
            false
        }

        taskIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Pebbles_debug", " taskIcon clicked")
                mainViewPager.currentItem = 2
            }
            false
        }


        settingsIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("Pebbles_debug",  "settingsIcon clicked")
                mainViewPager.currentItem = 3
            }
            false
        }


    }

    private fun showBiometricSetupIfNeeded() {
        if(!sessionUtils.getBiometricSetupShownFlag() && BiometricUtils.checkBiometricsAvailable(this) && !sessionUtils.getBiometricEnabledFlag()) {
            // show dialog and navigate to biometric setup
            showEnableBioMetricDialog ({
                sessionUtils.setBiometricSetupShownFlag()
            }) {
                sessionUtils.setBiometricSetupShownFlag()
                navigateToSettings()
            }
        }
    }







    private fun askForPushNotificationPermission() {
        NotificationUtils.updateTokenLiveData.observe(this, Observer { token->
            DatabaseHelper.updateFCMToken(token) {
                Repo.myToken = token
                NotificationUtils.setShouldRegenerateToken()
            }
        })

        if(NotificationUtils.shouldRegenerateToken()) {
            NotificationUtils.initFireBase(onTokenGenerated = { token ->
                NotificationUtils.updateTokenLiveData.postValue(token)
            })
        }
    }

    private fun fetchTokens() {
        val messageListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Repo.tokens = dataSnapshot.value as HashMap<String,String>
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }
        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("fcmTokens")?.addValueEventListener(messageListener) }
    }


    private fun initializeTabs() {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        mainViewPager.adapter = pagerAdapter

        mainViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int ) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

    }


    private fun initializeShortCutDevices() {
        DatabaseHelper.returnUserShortCuts({
            initShortcutViews()
            initDeviceStateListener()
        },{
            showDismissiveAlertDialog(getStringResource(R.string.error_title),getStringResource(R.string.error_api))
        })
    }

    private fun initShortcutViews() {

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

        Repo.deviceShortCuts.forEach { sc ->
            shortcutViews.find { view -> view.tag.toString() == sc.tag }?.let { view ->
                Repo.devices.find { it.id?.toLong() == sc.deviceID }?.let {
                    view.setDevice(it)
                    viewsToRemove.add(view)
                    view.onRemoveClicked = { device, tag ->
                        DatabaseHelper.removeShortcut(tag, device) {
                            initializeShortCutDevices()
                        }
                    }
                    view.onSwitch = { device ->
                        DatabaseHelper.switchDevice(device, { }) {
                            //error
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
            }
        }

    }

    private fun initDeviceStateListener() {
        val messageListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val portData = dataSnapshot.value as HashMap<String,Long>
                    portData.forEach { (port, state) ->
                        Repo.devices.find { device -> device.port.toString() == port[1].toString()}?.state = state.toInt()
                    }
                    initShortcutViews()
                    initIndicatorViews(portData)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }

        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("portData")?.child(it)?.addValueEventListener(messageListener) }
    }

    private fun initTempStateListener() {
        val messageListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val portData = dataSnapshot.value as HashMap<String,Float>
                    portData.forEach { (port, state) ->
                       if(port == "D5") {
                           progressText.text = state.toString()
                       }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }
        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("tempData")?.child(it)?.addValueEventListener(messageListener) }
    }

    private fun initIndicatorViews(portData: HashMap<String, Long>) {
        portData["D1"]?.let {
            if(it == 1L) {
                filterIndicator.setImageDrawable(getDrawableResource(R.drawable.filter_indicator_on))
            } else {
                filterIndicator.setImageDrawable(getDrawableResource(R.drawable.filter_indicator_off))
            }
        }

        portData["D2"]?.let {
            if(it == 1L) {
                lightIndicator.setImageDrawable(getDrawableResource(R.drawable.light_indicatior_on))
            } else {
                lightIndicator.setImageDrawable(getDrawableResource(R.drawable.light_indicatior_off))
            }
        }



    }


    private fun initDeviceShortcutViews() {
        Repo.user?.deviceSetId?.let {
            DatabaseHelper.returnDevicesForUid(it, {
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
            drawer_layout.closeDrawer(GravityCompat.START)
            logout()
        }
        nav_view.SettingsTextView.setOnClickListener {
            navigateToSettings()
            drawer_layout.closeDrawer(GravityCompat.START)

        }
    }


    private fun logout() {
        PebblesService.stopService(this)
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

    override fun shortcutAdded() {
        initializeShortCutDevices()
    }

}