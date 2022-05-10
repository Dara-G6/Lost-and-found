package com.example.lostandfound

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.Adapter.HistoryAdapter
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.databinding.ActivityHistoryPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HistoryPostActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityHistoryPostBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.BtnClose.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Post")

    }

    override fun onStart() {
        super.onStart()
        setList()
    }

    override fun onResume() {
        super.onResume()
        setList()
    }


    //get list
    private fun setList(){
        Log.d("App ",auth.app.toString())
        val list = ArrayList<Post>()
        database.orderByChild("Date").get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val name   = ds.child("Name").value.toString()
                    val email  = ds.child("Email").value.toString()
                    val userID = ds.child("UserID").value.toString()
                    val profile = ds.child("Profile").value.toString()
                    val idPost =ds.child("IDPost").value.toString()
                    val photo = ds.child("Photo").value.toString()
                    val date  = ds.child("Date").value.toString()
                    val caption = ds.child("Caption").value.toString()
                    val type = ds.child("Type").value.toString()
                    val like = ds.child("Like").value.toString().toLong()
                    if (userID==auth.uid.toString()){
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
                val adapter = HistoryAdapter(this,R.layout.history_list_view,list,this)
                binding.ListHistory.adapter = adapter
            }
        }
    }
}