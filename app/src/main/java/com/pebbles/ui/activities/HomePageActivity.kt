package com.pebbles.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.pebbles.Utils.ResourceUtils.getStringResource
import com.pebbles.backgroundServices.PebblesService
import com.pebbles.core.*
import com.pebbles.databinding.ActivityHomeBinding
import com.pebbles.ui.Custom.Tab
import com.pebbles.ui.PagerAdapter
import com.pebbles.ui.fragments.DeviceFragment


import java.util.*


class HomePageActivity : BaseActivity(), DeviceFragment.OnDeviceTabInteractionListener {

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigationView()
        initToolBar()
        initializeTabs()

        initDeviceShortcutViews()
        initTempStateListener()

        PebblesService.startService(this, "message")

        askForPushNotificationPermission()
        fetchTokens()

        Run.after(5000) {
            showBiometricSetupIfNeeded()
        }


        //throw RuntimeException("Test Crash in build mode: ${BuildConfig.BUILD_TYPE}")

    }

    private fun initToolBar() {
        val title = "Hi there ${Repo.user?.name?.split(" ")?.component1() ?: "fish keeper"} !"
        binding.homepageUiLayout.include.toolbarTitle.text = title
        binding.homepageUiLayout.include.toolbarSubtitle.text = "Planted tank in Hall room"
    }


    private fun showBiometricSetupIfNeeded() {
        if(!sessionUtils.getBiometricSetupShownFlag() && BiometricUtils.checkBiometricsAvailable(this) && !sessionUtils.getBiometricEnabledFlag()) {
            // show dialog and navigate to biometric setup
//            showEnableBioMetricDialog ({
//                sessionUtils.setBiometricSetupShownFlag()
//            }) {
//                sessionUtils.setBiometricSetupShownFlag()
//                navigateToSettings()
//            }
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

        binding.homepageUiLayout.myTabBar.addTab(Tab(0, R.drawable.ic_my_tanks, "Tank"))
        binding.homepageUiLayout.myTabBar.addTab(Tab(1, R.drawable.ic_other_devices, "Devices"))
        binding.homepageUiLayout.myTabBar.addTab(Tab(2 ,R.drawable.ic_tasks, "Tasks"))
        binding.homepageUiLayout.myTabBar.addTab(Tab(3, R.drawable.ic_settings, "Settings"))

        binding.homepageUiLayout.myTabBar.onTabClicked = { tab ->
          binding.homepageUiLayout.mainViewPager.currentItem = tab.id
        }

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        binding.homepageUiLayout.mainViewPager.adapter = pagerAdapter

        binding.homepageUiLayout.mainViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                Log.d("Pebbles_debug", "scroll state $state")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int ) {
                Log.d("Pebbles_debug", "scroll position offset:$positionOffset , pixels:$positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
                binding.homepageUiLayout.myTabBar.animateToTab(binding.homepageUiLayout.myTabBar.tabList[position])
                binding.homepageUiLayout.myTabBar.animateTextPosition(binding.homepageUiLayout.myTabBar.tabList[position])
                binding.homepageUiLayout.myTabBar.animateTextAlpha(binding.homepageUiLayout.myTabBar.tabList[position])
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

//        val shortcutViews: ArrayList<ShortCutView> = arrayListOf()
//        val viewsToRemove: ArrayList<ShortCutView> = arrayListOf()
//        shortcut1Layout.tag = "s1"
//        shortcut2Layout.tag = "s2"
//        shortcut3Layout.tag = "s3"
//        shortcut4Layout.tag = "s4"
//        shortcutViews.add(shortcut1Layout)
//        shortcutViews.add(shortcut2Layout)
//        shortcutViews.add(shortcut3Layout)
//        shortcutViews.add(shortcut4Layout)
//
//        Repo.deviceShortCuts.forEach { sc ->
//            shortcutViews.find { view -> view.tag.toString() == sc.tag }?.let { view ->
//                Repo.devices.find { it.id?.toLong() == sc.deviceID }?.let {
//                    view.setDevice(it)
//                    viewsToRemove.add(view)
//                    view.onRemoveClicked = { device, tag ->
//                        DatabaseHelper.removeShortcut(tag, device) {
//                            initializeShortCutDevices()
//                        }
//                    }
//                    view.onSwitch = { device ->
//                        DatabaseHelper.switchDevice(device, { }) {
//                            //error
//                        }
//                    }
//                }
//            }
//        }
//
//        shortcutViews.removeAll(viewsToRemove)
//        shortcutViews.takeIf { it.isNotEmpty() }?.let {
//            Repo.selectedShortCutAddPosition = it.first().tag.toString()
//        }
//        shortcutViews.forEach {
//            it.setAddDeviceLayout()
//
//            it.onAddClicked = {tag->
//                Repo.selectedShortCutAddPosition = tag
//            }
//        }

    }

    private fun initDeviceStateListener() {
//        val messageListener = object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val portData = dataSnapshot.value as HashMap<String,Long>
//                    portData.forEach { (port, state) ->
//                        Repo.devices.find { device -> device.port.toString() == port[1].toString()}?.state = state.toInt()
//                    }
//                    initShortcutViews()
//                    initIndicatorViews(portData)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Failed to read value
//            }
//        }
//
//        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("portData")?.child(it)?.addValueEventListener(messageListener) }
    }

    private fun initTempStateListener() {
//        val messageListener = object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val portData = dataSnapshot.value as HashMap<String,Float>
//                    portData.forEach { (port, state) ->
//                       if(port == "D5") {
//                           progressText.text = state.toString()
//                       }
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Failed to read value
//            }
//        }
//        Repo.user?.deviceSetId?.let { DatabaseHelper.databaseReference?.child("tempData")?.child(it)?.addValueEventListener(messageListener) }
    }

    private fun initIndicatorViews(portData: HashMap<String, Long>) {
//        portData["D1"]?.let {
//            if(it == 1L) {
//                filterIndicator.setImageDrawable(getDrawableResource(R.drawable.filter_indicator_on))
//            } else {
//                filterIndicator.setImageDrawable(getDrawableResource(R.drawable.filter_indicator_off))
//            }
//        }
//
//        portData["D2"]?.let {
//            if(it == 1L) {
//                lightIndicator.setImageDrawable(getDrawableResource(R.drawable.light_indicatior_on))
//            } else {
//                lightIndicator.setImageDrawable(getDrawableResource(R.drawable.light_indicatior_off))
//            }
//        }



    }


    private fun initDeviceShortcutViews() {
//        Repo.user?.deviceSetId?.let {
//            DatabaseHelper.returnDevicesForUid(it, {
//                initializeShortCutDevices()
//            }, {
//                showDismissiveAlertDialog(getStringResource(R.string.error_title),getStringResource(R.string.error_api))
//            })
//        }

    }


    private fun initNavigationView() {
        binding.childLayout.profileNameTextView.text = Repo.user?.name
        binding.childLayout.profileEmailTextView.text = Repo.user?.email
        binding.childLayout.profileImageView.assignImageFromUrl(Repo.user?.profilePhotoUrl.toString(),true, isCircleCrop = true)
        binding.homepageUiLayout.include.sideMenuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        binding.childLayout.signOutTextView.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            logout()
        }
        binding.childLayout.SettingsTextView.setOnClickListener {
            navigateToSettings()
            binding.drawerLayout.closeDrawer(GravityCompat.END)

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