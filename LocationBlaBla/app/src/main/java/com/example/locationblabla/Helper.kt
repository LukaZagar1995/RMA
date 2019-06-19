package com.example.locationblabla

import com.example.locationblabla.model.User

class Helper {
    fun isUserWithinDistanceRange(currentUser: User?, user: User?): Boolean {

        val radius = 6371

        val latDistance = deg2rad(Math.abs(user!!.lat-currentUser!!.lat))
        val longDistance = deg2rad(Math.abs(user.lng-currentUser.lng))
        val a =
            Math.sin(latDistance/2) * Math.sin(latDistance/2) +
                    Math.cos(deg2rad(currentUser.lng)) * Math.cos(deg2rad(user.lat)) *
                    Math.sin(longDistance/2) * Math.sin(longDistance/2)
        ;
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
        val d = radius * c



        return d <= currentUser.distance

    }

    private fun deg2rad(deg:Double):Double {
        return deg * (Math.PI/180)
    }
}