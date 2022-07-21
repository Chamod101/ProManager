package com.cdp.pro_manager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.cdp.pro_manager.activities.*
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity,userInfo : User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo,
            SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }

    }

    fun getBoardDetails(activity: TaskListActivity,documentId: String){

        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName,document.toString())

                activity.boardDetails((document.toObject(Board::class.java)!!))


            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }

    }


    fun createBoard(activity: CreateBoardActivity,board:Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board created successfully.")

                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_LONG).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    exception
                )
            }
    }

    fun getBoardsList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for (i in document.documents) {
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board. ", e)
            }

    }


    fun updateUserProfileData(activity: MyProfileActivity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                Log.i(activity.javaClass.simpleName,"Profile data updated successfully!")
                Toast.makeText(activity,"Profile updated successfully!",Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,"Error while creating a board.",e
                )
                Toast.makeText(activity,"Error when updating the profile",Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        //activity.updateNavigationUserDetails(loggedInUser)
                        activity.loadImageAndName(loggedInUser,readBoardList)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)

                    }
                }

            }.addOnFailureListener {
                      e->
                    when(activity){
                        is SignInActivity ->{
                            activity.hideProgressDialog()
                        }
                        is MainActivity ->{
                            activity.hideProgressDialog()
                        }
                    }

                Log.e("SignInUser","Error writing document")
            }
    }

    fun getCurrentUserId(): String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID =""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}