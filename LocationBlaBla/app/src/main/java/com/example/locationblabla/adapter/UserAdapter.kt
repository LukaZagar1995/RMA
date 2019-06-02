import UserAdapter.ViewHolder
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE
import com.example.locationblabla.R
import com.example.locationblabla.model.User
import android.content.Intent
import com.example.locationblabla.Constants.DB_CHATS
import com.example.locationblabla.Constants.USER_ONLINE_STATUS
import com.example.locationblabla.activity.ChatActivity
import com.example.locationblabla.model.Chat
import com.example.locationblabla.module.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class UserAdapter(private val mContext: Context?, private val mUsers: ArrayList<User>, private val isChat: Boolean) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val USER_ID = "user_id"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = mUsers[position]
        holder.username.text = user.username
        if (user.profileImage == USER_DEFAULT_IMAGE) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            if (mContext != null) {
                GlideApp.with(mContext).load(FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImage))
                    .into(holder.profileImage)
            }
        }

        if(isChat){
            setLastMessage(user.id, holder.lastMessage )
        } else {
            holder.lastMessage.visibility = View.GONE
        }

        if (isChat) {
            if (user.status == USER_ONLINE_STATUS) {
                holder.onlineImage.visibility = View.VISIBLE
                holder.offlineImage.visibility = View.GONE
            } else {
                holder.onlineImage.visibility = View.GONE
                holder.offlineImage.visibility = View.VISIBLE
            }
        } else {
            holder.onlineImage.visibility = View.GONE
            holder.offlineImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra(USER_ID, user.id)
            mContext?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView = itemView.findViewById(R.id.tv_uItem_username)
        var profileImage: ImageView = itemView.findViewById(R.id.item_profile_image)
        var onlineImage: ImageView = itemView.findViewById(R.id.item_online_image)
        var offlineImage: ImageView = itemView.findViewById(R.id.item_offline_image)
        var lastMessage: TextView = itemView.findViewById(R.id.tv_uItem_lastMessage)

    }

    private fun setLastMessage(userID: String, lastMessage: TextView) {
        var theLastMessage = "default"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(DB_CHATS)
        db.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        if (firebaseUser != null) {
                            if (chat.receiver == firebaseUser.uid && chat.sender == userID ||
                                chat.receiver == userID && chat.sender == firebaseUser.uid) {
                                theLastMessage = chat.message
                            }
                        }
                    }
                }
                when(theLastMessage){
                    "default" ->
                        lastMessage.text = "No messages"
                    else ->
                        lastMessage.text = theLastMessage
                }
                theLastMessage = "default"

            }
        })
    }

}