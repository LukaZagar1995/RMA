package com.example.locationblabla.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.locationblabla.Constants.DB_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.view.View
import android.widget.TextView
import com.example.locationblabla.Constants.DB_CHATS
import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE
import com.example.locationblabla.Constants.USER_OFFLINE_STATUS
import com.example.locationblabla.Constants.USER_ONLINE_STATUS
import com.example.locationblabla.R
import com.example.locationblabla.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.locationblabla.fragments.ProfileFragment
import com.example.locationblabla.fragments.ChatFragment
import com.example.locationblabla.fragments.MapFragment
import com.example.locationblabla.fragments.UsersFragment
import com.example.locationblabla.model.Chat
import com.example.locationblabla.module.GlideApp
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val PERMISSION = "Permission"
        const val STORAGE_PERMISSION_CODE = 1
    }

    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkForLocationPermission())
        getUserLocation()
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

        val navView: NavigationView = findViewById(R.id.nav_view)
        setTv(navView.getHeaderView(0))


        val db = FirebaseDatabase.getInstance().getReference(DB_CHATS)
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var unread = 0
                for (snapshot in dataSnapshot.children) {
                    val chat: Chat? = snapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        if (firebaseUser != null) {
                            if (chat.receiver == firebaseUser.uid && !chat.isseen) {
                                unread++
                            }
                        }
                    }
                }

                if (unread == 0) {
                    nav_view.menu.getItem(1).title = getString(R.string.nav_chat_text)
                } else {
                    nav_view.menu.getItem(1).title = getString(R.string.nav_chat_text) + " (" + unread + ")"
                }
            }

        })

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
                val bundle = Bundle()
                if (!checkForStoragePermission()) {
                    requestStoragePermission()
                    bundle.putBoolean(PERMISSION, false)
                } else {
                    bundle.putBoolean(PERMISSION, true)
                }
                val profileFragment = ProfileFragment()
                profileFragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    profileFragment
                ).commit()
            }

            R.id.nav_map -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    MapFragment()
                ).commit()
            }

            R.id.nav_logout -> {
                status(USER_OFFLINE_STATUS)
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this@MainActivity,
                        StartActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
                return true
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.ask_for_permission_test))
                .setMessage(getString(R.string.permission_read_external_storage))
                .setPositiveButton(
                    getString(R.string.permission_accept_btn_txt)
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    getString(R.string.permission_denied_btn_tex)
                ) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun currentUserChat(userId: String) {
        val editor: SharedPreferences.Editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        editor.putString("currentuser", userId)
        editor.apply()

    }

    private fun checkForStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }

    private fun setTv(view: View) {

        val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(firebaseUser!!.uid)
        val civNavProfile: CircleImageView = view.findViewById(R.id.civ_nav_profile)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val tvNavUsername: TextView = view.findViewById(R.id.tv_nav_username)
                tvNavUsername.text = user?.username
                val tvNavEmail: TextView = view.findViewById(R.id.tv_nav_email)
                tvNavEmail.text = user?.email

                if (user != null) {
                    if (user.profileImage == USER_DEFAULT_IMAGE) {
                        civNavProfile.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        GlideApp.with(applicationContext)
                            .load(FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImage))
                            .into(civNavProfile)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation(){
        locationManager = ContextCompat.getSystemService(this, LocationManager::class.java)!!
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {



            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100.0F, locationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.0F, locationListener)
    }

    private fun status(status: String) {
        val db =
            FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        db.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        status(USER_ONLINE_STATUS)
        if(checkForLocationPermission())
        getUserLocation()
        if (firebaseUser != null) {
            currentUserChat(firebaseUser.uid)
        }
    }

    override fun onPause() {
        super.onPause()
        if (FirebaseAuth.getInstance().currentUser != null) {
            status(USER_OFFLINE_STATUS)
            val db =
                FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)
            val hashMap = HashMap<String, Any>()
            hashMap["lat"] = 0.0
            hashMap["lng"] = 0.0
            db.updateChildren(hashMap)
            locationManager.removeUpdates(locationListener)
            currentUserChat("none")
        }
    }

    private fun checkForLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
