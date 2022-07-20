package com.cdp.pro_manager.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.User
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }

    private var mSelectedImageFileUri: Uri?=null
    var userimg : ImageView?=null
    var toolbarmyprofileactivity : Toolbar?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbarmyprofileactivity = findViewById(R.id.toolbar_my_profile_activity)
        userimg = findViewById(R.id.iv_profile_user_image)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        userimg?.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                shoImageChooser()

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                shoImageChooser()
            }
        }else{
            Toast.makeText(this,"You just denied the permission for storage.You can allow it from settings",Toast.LENGTH_LONG).show()
        }
    }

    private fun shoImageChooser(){
        var galleyIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleyIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try{
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(userimg!!)
            }catch (e: IOException){
                e.printStackTrace()
            }


        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarmyprofileactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        toolbarmyprofileactivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }



    fun setUserDataInUI(user: User){

        var uname = findViewById<AppCompatEditText>(R.id.et_name)
        val uemail = findViewById<AppCompatEditText>(R.id.et_email)
        var mobile = findViewById<AppCompatEditText>(R.id.et_mobile)
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userimg!!)

        uname.setText(user.name)
        uemail.setText(user.email)
        if(user.mobile != 0L){
            mobile.setText(user.mobile.toString())
        }
    }
}