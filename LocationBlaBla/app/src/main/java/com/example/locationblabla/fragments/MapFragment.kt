package com.example.locationblabla.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.locationblabla.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.Toast
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.Helper
import com.example.locationblabla.model.User
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapFragment : Fragment(), OnMapReadyCallback {


    private lateinit var googleMap: GoogleMap
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        var mapFragment = childFragmentManager.findFragmentById(R.id.frg_frg_map) as SupportMapFragment?
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (mapFragment == null) {

            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            fragmentTransaction?.replace(R.id.frg_frg_map, mapFragment)?.commit()
        }

        mapFragment?.getMapAsync(this)
        locationManager = getSystemService(context!!, LocationManager::class.java)!!
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(firebaseUser!!.uid)
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

        return view
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map!!
        val zoomLocation = LatLng(45.55111, 18.69389)
        val hashMapMarker = HashMap<String, Marker?>()
        val user = User()
        val usersLocations = FirebaseDatabase.getInstance().getReference(DB_USERS)
         val db =
            FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)

        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lat:Double = dataSnapshot.child("lat").getValue(Double::class.java)!!
                val long:Double = dataSnapshot.child("lng").getValue(Double::class.java)!!
                val distance: Int = dataSnapshot.child("distance").getValue(Int::class.java)!!

                user.lat = lat
                user.lng = long
                user.distance = distance

            }

        })

        val helper = Helper()
        usersLocations.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val databaseUser: User? = snapshot.getValue(User::class.java)
                    databaseUser!!.lng  = snapshot.child("lng").getValue(Double::class.java)!!
                    databaseUser.lat  = snapshot.child("lat").getValue(Double::class.java)!!
                    val latitudeUser = databaseUser.lat
                    val longitudeUser = databaseUser.lng
                    if (databaseUser.lat != 0.0 && databaseUser.lng != 0.0) {
                        if(helper.isUserWithinDistanceRange(user, databaseUser)) {
                            val latLng = LatLng(latitudeUser, longitudeUser)
                            if (hashMapMarker[databaseUser.id] != null) {
                                hashMapMarker[databaseUser.id]!!.remove()
                                hashMapMarker[databaseUser.id] =
                                    googleMap.addMarker(MarkerOptions().position(latLng).title(databaseUser.username))
                            } else {
                                hashMapMarker[databaseUser.id] =
                                    googleMap.addMarker(MarkerOptions().position(latLng).title(databaseUser.username))

                            }
                        }
                    }
                }
            }
        })

        googleMap.setMaxZoomPreference(20F)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomLocation, 11.0f))
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationListener)

    }

}