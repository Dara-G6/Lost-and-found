package com.example.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.Adapter.PostListAdapter
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.databinding.ActivityDetailUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class DetailUserActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityDetailUserBinding

    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnClose.setOnClickListener {
            finish()
        }

        database = FirebaseDatabase.getInstance().getReference("Post")
        setDetailUser()
    }

    override fun onResume() {
        super.onResume()
        setDetailUser()
    }

    //set detail user
    private fun setDetailUser(){
        val ID = intent.getStringExtra("ID")
        val name = intent.getStringExtra("Name")
        val profile = intent.getStringExtra("Profile")
        val email = intent.getStringExtra("Email")
        binding.TextEmail.text = email
        binding.TextName.text = name
        binding.TextHeader.text = "${getString(R.string.user)} : $name"
        Picasso.get().load(profile).into(binding.ProfileUser)


       setListPost(ID!!,name!!,email!!,profile!!)

    }

    private fun setListPost(ID:String,name:String,email:String,profile:String){
        val list = ArrayList<Post>()
        database.orderByChild("Date").get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val userID = ds.child("UserID").value.toString()
                    val idPost =ds.child("IDPost").value.toString()
                    val photo = ds.child("Photo").value.toString()
                    val date  = ds.child("Date").value.toString()
                    val caption = ds.child("Caption").value.toString()
                    val type = ds.child("Type").value.toString()
                    val like = ds.child("Like").value.toString().toLong()
                    if (userID==ID){
                        list.add(
                            Post(
                                name,
                                email,
                                userID,
                                profile,
                                idPost,
                                photo,
                                date,
                                caption,
                                type,
                                like
                            )
                        )
                    }
                }
                list.reverse()
                val adapter = PostListAdapter(this,R.layout.post_list_view,list)
                binding.ListPost.adapter = adapter
            }
        }
    }
}