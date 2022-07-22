package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.MemberListItemsAdapter
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    var toolbarmembersactivity : Toolbar?=null
    var recycleViewmember : RecyclerView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        toolbarmembersactivity = findViewById(R.id.toolbar_members_activity)
        recycleViewmember = findViewById(R.id.rv_members_list)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMemberslistDetails(this, mBoardDetails.assignedTo)

    }

    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialog()

        recycleViewmember?.layoutManager = LinearLayoutManager(this)
        recycleViewmember?.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this,list)
        recycleViewmember?.adapter = adapter
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarmembersactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbarmembersactivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}