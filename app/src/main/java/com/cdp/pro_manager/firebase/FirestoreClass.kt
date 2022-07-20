package com.cdp.pro_manager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.cdp.pro_manager.activities.MainActivity
import com.cdp.pro_manager.activities.MyProfileActivity
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

    fun loadUserData(activity: Activity){
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
                        activity.loadImageAndName(loggedInUser)
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