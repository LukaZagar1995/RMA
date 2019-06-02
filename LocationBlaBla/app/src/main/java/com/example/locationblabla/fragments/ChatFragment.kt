package com.example.locationblabla.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.example.locationblabla.Constants.DB_CHATS
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.R
import com.example.locationblabla.model.Chat
import com.example.locationblabla.model.User
import UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val usersList = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_frg_chat)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val db = FirebaseDatabase.getInstance().getReference(DB_CHATS)
        db.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)!!

                    assert(firebaseUser != null)
                    if (chat.sender == firebaseUser!!.uid) {
                        usersList.add(chat.receiver)
                    }
                    if (chat.receiver == firebaseUser.uid) {
                        usersList.add(chat.sender)
                    }
                }
                readChats(recyclerView)
            }
        })
        return view
    }

    private fun readChats(recyclerView: RecyclerView) {

        val mUsers = ArrayList<User>()
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS)

        db.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!
                    for (id in usersList) {
                        if (user.id == id) {
                            if (mUsers.size != 0) {
                                for (user1 in mUsers) {
                                    if (user.id != user1.id) {
                                        mUsers.add(user)
                                    }
                                }
                            } else {
                                mUsers.add(user)
                            }
                        }
                    }
                }

               val userAdapter = UserAdapter(context, mUsers, true)
                recyclerView.adapter = userAdapter
            }

        })

    }
}