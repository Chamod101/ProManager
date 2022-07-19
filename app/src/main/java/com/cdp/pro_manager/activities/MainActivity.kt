package com.cdp.pro_manager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    var toolbarmainactivity :Toolbar?=null
    var drawerlayout: DrawerLayout?=null
    var navview:NavigationView?=null
    var img:ImageView?=null
    var usertext:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarmainactivity = findViewById(R.id.toolbar_main_activity)
        drawerlayout = findViewById(R.id.drawer_layout)
        navview = findViewById(R.id.nav_view)
        img = findViewById(R.id.nav_user_image)
        usertext = findViewById(R.id.tv_username)

        setupActionBar()

        navview?.setNavigationItemSelectedListener(this)

      FirestoreClass().loadUserData(this)



    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarmainactivity)
        toolbarmainactivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarmainactivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (drawerlayout!!.isDrawerOpen(GravityCompat.START)){
            drawerlayout?.closeDrawer(GravityCompat.START)
        }else{
            drawerlayout?.openDrawer(GravityCompat.START)
        }
    }

    fun loadImageAndName(user: User){
        var userimg = findViewById<ImageView>(R.id.nav_user_image)
        var uname = findViewById<TextView>(R.id.tv_username)
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userimg)
        uname.setText(user.name)


    }

    override fun onBackPressed() {
        if(drawerlayout!!.isDrawerOpen(GravityCompat.START)){
            drawerlayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user:User){
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(img!!)
        usertext?.text = user.name

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                startActivity(Intent(this,MyProfileActivity::class.java))
            }

            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        drawerlayout?.closeDrawer(GravityCompat.START)

        return true
    }

}