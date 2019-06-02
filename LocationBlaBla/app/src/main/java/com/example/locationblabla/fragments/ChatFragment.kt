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
import com.example.locationblabla.model.User
import UserAdapter
import com.example.locationblabla.Constants.DB_CHAT_LIST
import com.example.locationblabla.model.Chatlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val usersList = ArrayList<User>()
    val userChatList = ArrayList<Chatlist>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_frg_chat)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val db = FirebaseDatabase.getInstance().getReference(DB_CHAT_LIST).child(firebaseUser!!.uid)
        db.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children){
                    val chatList = snapshot.getValue(Chatlist::class.java)
                    if (chatList != null) {
                        userChatList.add(chatList)
                    }
                }
                setChatList(recyclerView)
            }
        })
        return view
    }

    private fun setChatList(recyclerView: RecyclerView) {

        val db = FirebaseDatabase.getInstance().getReference(DB_USERS)
        db.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (snapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)
                        for(chatlist: Chatlist in userChatList){
                            if (user!!.id == chatlist.id){
                                usersList.add(user)
                            }
                        }
                    }
                val userAdapter = UserAdapter(context, usersList, true)
                recyclerView.adapter = userAdapter


                }
        })

    }


}