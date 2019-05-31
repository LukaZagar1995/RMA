package com.example.locationblabla.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.locationblabla.R
import com.example.locationblabla.activity.authentication.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null && firebaseUser.isEmailVerified) {
          startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}
