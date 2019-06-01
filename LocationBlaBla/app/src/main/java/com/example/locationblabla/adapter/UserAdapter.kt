
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
import com.example.locationblabla.activity.ChatActivity


class UserAdapter(private val mContext: Context?, private val mUsers: List<User>) :
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
            //Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage)
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


    }

}