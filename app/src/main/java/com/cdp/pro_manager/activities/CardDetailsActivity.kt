package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.cdp.pro_manager.R
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.utils.Constants

class CardDetailsActivity : AppCompatActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    var toolbarCardDetailsActivity : Toolbar?=null
    var editTextCard: AppCompatEditText?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        toolbarCardDetailsActivity = findViewById(R.id.toolbar_card_details_activity)
        editTextCard = findViewById(R.id.et_name_card_details)


        getIntentData()
        setupActionBar()
        editTextCard?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        editTextCard?.setSelection(editTextCard?.text.toString().length)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition]
                .cards[mCardPosition].name

        }
        toolbarCardDetailsActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }
}