package com.example.locationblabla.activity.authentication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import com.example.locationblabla.R
import com.example.locationblabla.authentication.Firebase
import com.example.locationblabla.model.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_bar.toolbar


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupUI()

        btn_register_register.setOnClickListener{
            register()
        }

    }

    private fun register() {

        validate()
        if(!validateEmail() || !validateUsername() || !validatePassword()){
            return
        } else {
            val user = User()
            user.email = et_register_email.editText?.text.toString()
            user.username = et_register_username.editText?.text.toString()

            val firebase = Firebase()
            firebase.createUser(user, et_register_password.editText?.text.toString(), this)

        }
    }

    private fun validate(){
        validateEmail()
        validateUsername()
        validatePassword()
    }

    private fun validateUsername():Boolean {

        if(et_register_username.editText?.text.toString().isEmpty()){
            et_register_username.error = getString(R.string.et_username_empty_error)
            return false
        } else {
            et_register_username.error = null
            return true
        }

    }

    private fun validateEmail():Boolean {

        if(et_register_email.editText?.text.toString().isEmpty()){
            et_register_email.error = getString(R.string.et_email_empty_error)
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(et_register_email.editText?.text.toString()).matches()) {
            et_register_email.error = getString(R.string.et_email_pattern_error)
            return false
        } else {
            et_register_email.error = null
            return true
        }

    }

    private fun validatePassword():Boolean {

        if(et_register_password.editText?.text.toString().isEmpty()){
            et_register_password.error = getString(R.string.et_password_empty_error)
            return false
        } else if(et_register_password.editText?.text.toString().length < 6) {
            et_register_password.error = getString(R.string.et_password_length_error)
            return false
        } else if (et_register_password.editText?.text.toString() != et_register_repeatPassword.editText?.text.toString()) {
            et_register_password.error = getString(R.string.et_repeatPassword_match_error)
            return false
        } else {
            et_register_password.error = null
            et_register_repeatPassword.error = null
            return true
        }

    }

    private fun setupUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.register_toolbar_text)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


}
