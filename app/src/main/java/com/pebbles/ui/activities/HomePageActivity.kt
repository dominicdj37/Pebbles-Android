package com.pebbles.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.auth.AuthUI
import com.pebbles.R
import com.pebbles.core.Repo
import com.pebbles.core.assignImageFromUrl
import com.pebbles.data.Device
import com.pebbles.ui.adapters.AddDeviceDataHolder
import com.pebbles.ui.adapters.DeviceDataHolder
import com.pebbles.ui.adapters.DevicesAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.nav_layout.view.*


class HomePageActivity : BaseActivity(), DevicesAdapter.DeviceListClickListener {

    private var adapter: DevicesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initNavigationView()
        intiDevicesView()
    }

    private fun intiDevicesView() {
        /*val list = arrayListOf<Any>()
        list.add(DeviceDataHolder(Device(0, "LED Light 1", isAutomated = true, isDeviceOn = false, isDeviceConnected = true, deviceImageUrl = "https://firebasestorage.googleapis.com/v0/b/nodemcutest-eba2f.appspot.com/o/intro_beleuchtungs-steuerung_0618.jpg?alt=media&token=6a25a904-82bb-4d63-946f-f62fb44624dc" )))
        list.add(DeviceDataHolder(Device(0, "LED Light 2", isAutomated = false, isDeviceOn = true, isDeviceConnected = true, deviceImageUrl = "https://firebasestorage.googleapis.com/v0/b/nodemcutest-eba2f.appspot.com/o/led-aquarium-light.jpg?alt=media&token=e5e005d3-dc19-4cc6-a4f5-d837ee6b917c" )))
        list.add(DeviceDataHolder(Device(0, "Filter Submersible  ", isAutomated = false, isDeviceOn = false, isDeviceConnected = true, deviceImageUrl = "https://firebasestorage.googleapis.com/v0/b/nodemcutest-eba2f.appspot.com/o/aquarium-water-filter-500x500.jpg?alt=media&token=0faed990-8c36-40b4-ad0a-340e128dc23e " )))
        list.add(DeviceDataHolder(Device(0, "LED Light", isAutomated = true, isDeviceOn = true, isDeviceConnected = true, deviceImageUrl = "https://firebasestorage.googleapis.com/v0/b/nodemcutest-eba2f.appspot.com/o/best-small-aquarium-filter.jpg?alt=media&token=67c9d055-029c-455b-9400-d62466cb5e9b" )))
        list.add(DeviceDataHolder(Device(0, "LED Light", isAutomated = true, isDeviceOn = false, isDeviceConnected = true, deviceImageUrl = "https://firebasestorage.googleapis.com/v0/b/nodemcutest-eba2f.appspot.com/o/filter1.jpg?alt=media&token=fa05caaf-0fcb-47a9-886f-ce00f56aa096" )))
        list.add(AddDeviceDataHolder())
        deviceRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        adapter = DevicesAdapter(list,this)
        deviceRecyclerView.adapter = adapter*/
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

    override fun onDeviceSwitchClicked(device: Device) {
        device.isDeviceOn = !device.isDeviceOn
        adapter?.notifyDataSetChanged()
    }

    override fun onAddDeviceClicked() {

    }


}