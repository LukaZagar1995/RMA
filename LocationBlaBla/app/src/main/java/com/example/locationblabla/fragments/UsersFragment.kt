package com.example.locationblabla.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import UserAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import com.example.locationblabla.Helper
import com.example.locationblabla.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener




class UsersFragment : Fragment() {

    private val mUsers = ArrayList<User>()
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_users, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_frg_users)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        locationManager = ContextCompat.getSystemService(context!!, LocationManager::class.java)!!
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)
                val hashMap = HashMap<String, Any>()
                hashMap["lat"] = latitude
                hashMap["lng"] = longitude
                db.updateChildren(hashMap)

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5 * 60 * 1000,
            100.0F,
            locationListener
        )
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 100.0F, locationListener)

        getAllUsersExceptCurrent(mUsers, context, recyclerView)

        return view
    }

    private fun getAllUsersExceptCurrent(mUsers: ArrayList<User>, context: Context?, recyclerView: RecyclerView){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS)
        val currentUserRef =
            FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)
        val helper = Helper()
        val currentUser = User()
        currentUserRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lat:Double = dataSnapshot.child("lat").getValue(Double::class.java)!!
                val long:Double = dataSnapshot.child("lng").getValue(Double::class.java)!!
                val distance: Int = dataSnapshot.child("distance").getValue(Int::class.java)!!

                currentUser.lat = lat
                currentUser.lng = long
                currentUser.distance = distance

            }

        })

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!

                    assert(firebaseUser != null)
                    if (user.id != firebaseUser!!.uid ) {
                        if(helper.isUserWithinDistanceRange(currentUser, user)) {
                            mUsers.add(user)
                        }
                    }
                }

               val userAdapter = UserAdapter(context, mUsers, false)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }
}