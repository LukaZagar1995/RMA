package com.example.locationblabla.activity.authentication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.locationblabla.R
import com.example.locationblabla.authentication.Firebase
import com.example.locationblabla.authentication.Firebase.Companion.USER_EMAIL
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_bar.toolbar

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupUI()

        if(intent.getStringExtra(USER_EMAIL) != null) {
            et_login_email.editText?.setText(intent.getStringExtra(USER_EMAIL))
        }

       btn_login_login.setOnClickListener{
           login()
       }

    }

    private fun login(){

        validate()
        if (!validatePassword() || !validateUsername()){
            return
        }

        val firebase = Firebase()
        firebase.loginUser( et_login_email.editText?.text.toString(),et_login_password.editText?.text.toString(), this)

    }

    private fun validate(){

        validatePassword()
        validateUsername()

    }

    private fun validateUsername():Boolean {

        if(et_login_email.editText?.text.toString().isEmpty()){
            et_login_email.error = getString(R.string.et_email_empty_error)
            return false
        } else {
            et_login_email.error = null
            return true
        }

    }

    private fun validatePassword():Boolean {

        if(et_login_password.editText?.text.toString().isEmpty()){
            et_login_password.error = getString(R.string.et_password_empty_error)
            return false
        } else {
            et_login_password.error = null
            return true
        }

    }

    private fun setupUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.login_toolbar_text)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

}