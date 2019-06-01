package com.example.locationblabla.authentication

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import android.content.Intent
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.R
import com.example.locationblabla.activity.MainActivity
import com.example.locationblabla.activity.authentication.LoginActivity
import com.example.locationblabla.model.User
import com.google.firebase.database.DatabaseReference


@Suppress("NAME_SHADOWING")
class Firebase {

    companion object{
       const val USER_EMAIL = "user_email"
    }

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var db: DatabaseReference

    fun createUser(user:User, password:String, context: Context) {

        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    val userID = firebaseUser!!.uid

                    db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(userID)

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userID
                    hashMap["username"] = user.username
                    hashMap["email"] = user.email
                    hashMap["profileImage"] = user.profileImage

                    db.setValue(hashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ task ->
                                    if (task.isSuccessful){
                                    val intent = Intent(context, LoginActivity::class.java)
                                        intent.putExtra(USER_EMAIL, user.email)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }else {
                                        Toast.makeText(
                                            context,
                                            R.string.verification_email_error,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                            }

                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        R.string.registration_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun loginUser(email: String, password: String, context: Context) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if(firebaseAuth.currentUser!!.isEmailVerified) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            R.string.verified_email_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        R.string.login_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
}