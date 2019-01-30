package com.dr.realtimefirebase

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import com.bumptech.glide.Glide
import com.dr.realtimefirebase.extentions.PICK_IMAGE_REQUEST
import com.dr.realtimefirebase.extentions.chooseImageFromGallery
import com.dr.realtimefirebase.extentions.makeProgressDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var ref: DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var auth: FirebaseAuth? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRealTimeDataBase()

        initStorage()

        initReceiveComment()

    }

    fun onSendClick(v: View) {
        if (etEnterComment.text.isNotEmpty()) ref?.setValue(etEnterComment.text.toString())
    }

    private fun initRealTimeDataBase() {
        val database = FirebaseDatabase.getInstance()
        ref = database.getReference("message")
    }

    private fun initReceiveComment() {
        ref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                value?.let {
                    if (it.isNotEmpty()) {
                        tvComment.text = value
                        Glide.with(this@MainActivity).load(value).into(ivImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("fcm", error.toException().toString())
            }
        })
    }

    private fun uploadFile(file: Uri) {
        val riversRef = storageRef?.child("images/rivers.jpg")

        progressDialog.show()

        riversRef?.putFile(file)
            ?.addOnProgressListener {
                val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                progressDialog.progress = progress.toInt()
                if(progress.toInt() == 100){
                    progressDialog.dismiss()
                    progressDialog.progress = 0
                }
            }
            ?.addOnSuccessListener {
                riversRef.downloadUrl.addOnSuccessListener { uri ->
                    ref?.setValue(uri.toString())
                }
            }?.addOnFailureListener {
                // Handle unsuccessful uploads
                // ...
            }
    }

    //upload file
    private fun initStorage() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously()
        }

        storageRef = FirebaseStorage.getInstance().reference

        progressDialog = makeProgressDialog(R.string.dialog_title_upload_your_Image)

    }

    private fun signInAnonymously() {
        auth?.signInAnonymously()?.addOnSuccessListener(this) {
            // do your stuff
        }
            ?.addOnFailureListener(
                this
            ) { exception -> Log.e("fcm", "signInAnonymously:FAILURE", exception) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            data?.data?.let { uploadFile(it) }
        }
    }

    fun onPickImageClick(v: View) {
        chooseImageFromGallery()
    }
}