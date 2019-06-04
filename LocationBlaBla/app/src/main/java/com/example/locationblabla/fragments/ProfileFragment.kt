package com.example.locationblabla.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.SeekBar
import android.widget.TextView
import com.example.locationblabla.Constants.DB_USERS
import com.example.locationblabla.Constants.STORAGE_UPLOADS
import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE
import com.example.locationblabla.R
import com.example.locationblabla.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import com.example.locationblabla.Constants.IMAGE_LOCATION
import com.example.locationblabla.activity.MainActivity.Companion.PERMISSION
import com.example.locationblabla.module.GlideApp
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.Continuation
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    companion object {
        const val IMAGE_REQUEST = 1

    }

    private var storageTask: UploadTask? = null
    private lateinit var imageUri: Uri
    private val userID: String? = FirebaseAuth.getInstance().uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(firebaseUser!!.uid)
        val imageReference = FirebaseStorage.getInstance()
        val tvFrgDistance: TextView = view.findViewById(R.id.tv_frg_distance)
        val sbFrgDistance: SeekBar = view.findViewById(R.id.sb_frg_distance)
        val civFrgProfileImage: CircleImageView = view.findViewById(R.id.civ_frg_profile_image)
        val tvFrgProfileUsername: TextView = view.findViewById(R.id.tv_frg_profile_username)
        var distance = 0
        val permission = arguments?.get(PERMISSION)

        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    sbFrgDistance.progress = user.distance
                    tvFrgProfileUsername.text = user.username
                    if (user.profileImage == USER_DEFAULT_IMAGE) {
                        civFrgProfileImage.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        context?.let {
                            GlideApp.with(it).load(imageReference.getReferenceFromUrl(user.profileImage))
                                .into(civFrgProfileImage)
                        }
                    }
                }
            }
        })
        if (permission == true) {
            civFrgProfileImage.setOnClickListener {

                openImage()
            }
        }

        sbFrgDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                distance = progress
                tvFrgDistance.text = getString(R.string.tv_frg_distance_text) + " " + progress + " km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val hashMap = HashMap<String, Any>()
                if (distance == 0){
                    hashMap["distance"] = 1
                } else {
                    hashMap["distance"] = distance
                }

                db.updateChildren(hashMap)
            }

        })

        return view
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = context?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadImage() {
        val storage = FirebaseStorage.getInstance().getReference(STORAGE_UPLOADS)

        val pd = ProgressDialog(context)
        pd.setMessage(getString(R.string.upload_image_status))
        pd.show()

        if (imageUri != null) {
            val map = HashMap<String, Any>()
            val imageReference = storage.child(
                userID + Calendar.getInstance().time + "." + getFileExtension(imageUri)
            )
            map["profileImage"] =
                IMAGE_LOCATION + userID + Calendar.getInstance().time + "." + getFileExtension(imageUri)
            storageTask = imageReference.putFile(imageUri)


            storageTask!!.continueWith {
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    imageReference.downloadUrl
                }
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(firebaseUser!!.uid)


                    db.updateChildren(map)

                    pd.dismiss()
                } else {
                    Toast.makeText(context, getString(R.string.upload_image_error), Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data!!

            if (storageTask != null) {
                if (storageTask!!.isInProgress) {
                    Toast.makeText(context, getString(R.string.upload_image_info), Toast.LENGTH_SHORT).show()
                }
            } else {
                uploadImage()
            }
        }
    }
}
