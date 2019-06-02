package com.example.locationblabla.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE
import com.example.locationblabla.R
import com.example.locationblabla.model.Chat
import com.example.locationblabla.module.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ChatAdapter(private val mContext: Context?, private val mChat: List<Chat>, private val profileURL: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    companion object {
        const val MSG_TYPE_RIGHT = 1
        const val MSG_TYPE_LEFT = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChat[position]

        holder.showMessage.text = chat.message

        if (profileURL == USER_DEFAULT_IMAGE) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            if (mContext != null) {
                GlideApp.with(mContext).load(FirebaseStorage.getInstance().getReferenceFromUrl(profileURL)).into(holder.profileImage)
            }
        }

    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var showMessage: TextView = itemView.findViewById(R.id.tv_chat_MessageItem)
        var profileImage: ImageView = itemView.findViewById(R.id.civ_profile_chat_Item)

    }

    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        return if (mChat[position].sender == firebaseUser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }

    }

}