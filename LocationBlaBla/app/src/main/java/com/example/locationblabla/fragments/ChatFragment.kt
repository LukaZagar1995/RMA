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
import android.annotation.SuppressLint
import android.widget.Toast
import com.example.locationblabla.Constants.DB_CHAT_LIST
import com.example.locationblabla.Constants.DB_TOKEN
import com.example.locationblabla.model.Chatlist
import com.example.locationblabla.notifications.MyFirebaseMessaging
import com.example.locationblabla.notifications.Token
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class ChatFragment : Fragment() {

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val usersList = ArrayList<User>()
    val userChatList = ArrayList<Chatlist>()

    @SuppressLint("StringFormatInvalid")
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
        MyFirebaseMessaging.toString()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
               val newToken = task.result?.token
                updateToken(newToken)
            })



        return view
    }

    private fun updateToken(token:String?) {
        val db = FirebaseDatabase.getInstance().getReference(DB_TOKEN)
        val token1 = token?.let { Token(it) }
        if (firebaseUser != null) {
            db.child(firebaseUser.uid).setValue(token1)
        }
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