package com.example.locationblabla.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.locationblabla.Constants.DB_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE
import com.example.locationblabla.R
import com.example.locationblabla.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_navigation.*
import com.example.locationblabla.fragments.ProfileFragment
import com.example.locationblabla.fragments.ChatFragment
import com.example.locationblabla.fragments.UsersFragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                UsersFragment()
            ).commit()
            nav_view.setCheckedItem(R.id.nav_users)
        }

    }

    private fun setupUI(activity: MainActivity) {

        val toggle = ActionBarDrawerToggle(
            activity, drawer_layout, main_toolbar,
            R.string.nav_drawer_open, R.string.nav_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_users -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    UsersFragment()
                ).commit()
            }
            R.id.nav_chat -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    ChatFragment()
                ).commit()
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    ProfileFragment()
                ).commit()
            }
            R.id.nav_logout -> {

                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this@MainActivity,
                        StartActivity::class.java
                    )
                )
                return true
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setTv(){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(firebaseUser!!.uid)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                tv_nav_username.text = user?.username
                tv_nav_email.text = user?.email

                if (user != null) {
                    if (user.profileImage == USER_DEFAULT_IMAGE) {
                        iv_nav_profile.setImageResource(R.mipmap.ic_launcher)
                    } else {

                        //Glide.with(this@MainActivity).load(user.profileImage).into(civ_main_profileImage)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }
}
