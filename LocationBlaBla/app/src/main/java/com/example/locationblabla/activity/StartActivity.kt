package com.example.locationblabla.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.locationblabla.R
import com.example.locationblabla.activity.authentication.LoginActivity
import com.example.locationblabla.activity.authentication.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onStart() {

        if (FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isEmailVerified ) {
            startActivity(Intent (this, MainActivity::class.java))
            finish()
        }
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        btn_start_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_start_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}
