package com.pebbles.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.pebbles.ui.fragments.*

class PagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return  HomeFragment.newInstance("sd","1")
            }
            1-> {
                return  DeviceFragment.newInstance(0)
            }
            2-> {
                return ChatFragment.newInstance(0)
            }
            3-> {
                return  OthersDevicesFragment.newInstance(0)
            }
            else -> {
                return  OthersDevicesFragment.newInstance(0)
            }
        }
    }


    override fun getCount(): Int {
        return 4
    }
}