package com.cdp.pro_manager.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.CardMemberListItemsAdapter
import com.cdp.pro_manager.dialogs.LabelColorListDialog
import com.cdp.pro_manager.dialogs.MembersListDialog
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.*
import com.cdp.pro_manager.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    private lateinit var mMemberDetailList: ArrayList<User>

    var toolbarCardDetailsActivity : Toolbar?=null
    var editTextCard: AppCompatEditText?=null
    var btnUpdate :Button?=null
    var colorText:TextView?=null
    var selectMembers :TextView? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        toolbarCardDetailsActivity = findViewById(R.id.toolbar_card_details_activity)
        editTextCard = findViewById(R.id.et_name_card_details)
        btnUpdate = findViewById(R.id.btn_update_card_details)
        colorText = findViewById(R.id.tv_select_label_color)
        selectMembers = findViewById(R.id.tv_select_members)


        getIntentData()
        setupActionBar()
        editTextCard?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        editTextCard?.setSelection(editTextCard?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor

        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        btnUpdate?.setOnClickListener {
            if(editTextCard?.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this@CardDetailsActivity,"Enter a card name.",Toast.LENGTH_LONG).show()
            }
        }

        colorText?.setOnClickListener {
            labelColorsListDialog()
        }

        selectMembers?.setOnClickListener {
            membersListDialog()
        }


        setSelectedMembersList()


    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

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

    private fun colorsList():ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#CD5C5C")
        colorsList.add("#F08080")
        colorsList.add("#FA8072")
        colorsList.add("#E9967A")
        colorsList.add("#FFA07A")
        colorsList.add("#C0C0C0")
        colorsList.add("#808080")
        colorsList.add("#000000")
        colorsList.add("#FF0000")
        colorsList.add("#800000")
        colorsList.add("#FFFF00")
        colorsList.add("#808000")
        colorsList.add("#00FFFF")
        colorsList.add("#0000FF")
        return colorsList
    }

    private fun setColor(){
        colorText?.text=""
        colorText?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){R.id.action_delete_member->{
            alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
            return true
        }


        }

        return super.onOptionsItemSelected(item)
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMemberDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size >0){
            for(i in mMemberDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMemberDetailList[i].id == j){
                        mMemberDetailList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMemberDetailList.indices){
                mMemberDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMemberDetailList,
            resources.getString((R.string.str_select_members))

        ){

            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails
                            .taskList[mTaskListPosition].cards[mCardPosition]
                            .assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails
                        .taskList[mTaskListPosition].cards[mCardPosition]
                        .assignedTo.remove(user.id)

                    for(i in mMemberDetailList.indices){
                        if(mMemberDetailList[i].id == user.id){
                            mMemberDetailList[i].selected = false
                        }


                    }
                }

                setSelectedMembersList()


            }

        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        val card = Card(
            editTextCard?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){ dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton(resources.getString(R.string.no)){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }

        listDialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMemberDetailList.indices){
            for(j in cardAssignedMemberList){
                if(mMemberDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMemberDetailList[i].id,
                        mMemberDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            requireViewById<TextView>(R.id.tv_select_members).visibility = View.GONE
            requireViewById<RecyclerView>(R.id.rv_selected_members_list).visibility =View.VISIBLE

            requireViewById<RecyclerView>(R.id.rv_selected_members_list).layoutManager = GridLayoutManager(
                this,6
            )
            val adapter = CardMemberListItemsAdapter(this,selectedMembersList)

            requireViewById<RecyclerView>(R.id.rv_selected_members_list).adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }else{
            requireViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            requireViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.GONE
        }
    }

}