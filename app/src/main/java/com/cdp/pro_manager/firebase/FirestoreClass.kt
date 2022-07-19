package com.cdp.pro_manager.firebase

import android.app.Activity
import android.util.Log
import com.cdp.pro_manager.activities.MainActivity
import com.cdp.pro_manager.activities.SignInActivity
import com.cdp.pro_manager.activities.SignUpActivity
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants
import com.google.firebase.auth.FirebaseAuth
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

    fun signinUser(activity: Activity){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
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