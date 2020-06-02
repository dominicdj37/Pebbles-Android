package com.pebbles.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.pebbles.R
import com.pebbles.core.Repo
import com.pebbles.core.assignImageFromUrl
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.nav_layout.*
import kotlinx.android.synthetic.main.nav_layout.view.*
import kotlinx.android.synthetic.main.nav_layout.view.signOutTextView

class HomePageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initNavigationView()
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




}