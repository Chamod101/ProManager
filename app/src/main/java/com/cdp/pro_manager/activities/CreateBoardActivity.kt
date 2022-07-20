package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.cdp.pro_manager.R

class CreateBoardActivity : AppCompatActivity() {
    var toolbarboardactivity : Toolbar?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        toolbarboardactivity = findViewById(R.id.toolbar_create_board_activity)
        setupActionBar()
    }


    private fun setupActionBar(){
        setSupportActionBar(toolbarboardactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)

        }
        toolbarboardactivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}