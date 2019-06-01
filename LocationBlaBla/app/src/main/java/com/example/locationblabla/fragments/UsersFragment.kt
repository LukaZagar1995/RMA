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
import android.content.Context
import com.example.locationblabla.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener




class UsersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_users, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_frg_users)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val mUsers = ArrayList<User>()

        getAllUsersExceptCurrent(mUsers,context,recyclerView)

        return view
    }

    private fun getAllUsersExceptCurrent(mUsers: ArrayList<User>, context: Context?, recyclerView: RecyclerView){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!

                    assert(firebaseUser != null)
                    if (user.id != firebaseUser!!.uid) {
                        mUsers.add(user)
                    }
                }

               val userAdapter = UserAdapter(context, mUsers)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }
}