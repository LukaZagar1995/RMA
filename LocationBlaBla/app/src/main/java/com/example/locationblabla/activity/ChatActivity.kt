package com.example.locationblabla.activity

import UserAdapter.Companion.USER_ID
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.locationblabla.Constants
import com.example.locationblabla.Constants.DB_CHATS
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.R
import com.example.locationblabla.adapter.ChatAdapter
import com.example.locationblabla.model.Chat
import com.example.locationblabla.model.User
import com.example.locationblabla.module.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*



class ChatActivity : AppCompatActivity() {

    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setupUI(intent)

        btn_chat_send.setOnClickListener{

            if(et_chat_message.text.isNotEmpty()){
                sendMessage(firebaseUser!!.uid, intent.getStringExtra(USER_ID), et_chat_message.text.toString())
            } else {
                Toast.makeText(
                    this,
                    R.string.send_message_error,
                    Toast.LENGTH_SHORT
                ).show()
            }

            et_chat_message.setText("")

        }

    }

    private fun setupUI(intent: Intent) {

        setSupportActionBar(chat_toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rv_chat_messages.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        rv_chat_messages.layoutManager = linearLayoutManager

        chat_toolbar.setNavigationOnClickListener {
            finish()
        }

        val chatPartnerID = intent.getStringExtra(USER_ID)
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(chatPartnerID)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               val user: User? = dataSnapshot.getValue(User::class.java)
                tv_chat_username.text = user?.username

                if (user!!.profileImage == Constants.USER_DEFAULT_IMAGE) {
                    civ_chat_profile.setImageResource(R.mipmap.ic_launcher)
                } else {
                    GlideApp.with(applicationContext).load(FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImage)).into(civ_chat_profile)
                }
                readMessages(firebaseUser!!.uid, chatPartnerID, user.profileImage)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun sendMessage(sender:String, receiver:String, message:String){

        val db = FirebaseDatabase.getInstance().reference

        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message

        db.child(DB_CHATS).push().setValue(hashMap)

    }

    private fun readMessages(senderId:String, receiverId: String, profileImage:String){

        val mChat = ArrayList<Chat>()

        val db = FirebaseDatabase.getInstance().getReference(DB_CHATS)
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)!!

                    assert(firebaseUser != null)
                    if (chat.receiver == senderId && chat.sender == receiverId ||
                    chat.receiver == receiverId && chat.sender == senderId) {
                        mChat.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(applicationContext, mChat, profileImage)
                rv_chat_messages.adapter = chatAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun status(status:String){
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(FirebaseAuth.getInstance().currentUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        db.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        status(Constants.USER_ONLINE_STATUS)
    }

    override fun onPause() {
        super.onPause()
        status(Constants.USER_OFFLINE_STATUS)
    }
}

