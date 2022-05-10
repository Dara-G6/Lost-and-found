package com.example.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.databinding.ActivityReactionBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ReactionActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityReactionBinding

    //
    private lateinit var name:String
    private lateinit var profile:String
    private lateinit var date:String
    private lateinit var type:String
    private lateinit var caption:String
    private lateinit var photo:String
    private lateinit var idPost:String
    private var countLike=0

    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        countLike = intent.getStringExtra("Like").toString().toInt()
        set()

        database = FirebaseDatabase.getInstance().getReference("Post")
        binding.BtnClose.setOnClickListener {
            finish()
        }
        binding.BtnLike.setOnClickListener {
            updateLike()
        }




    }

    override fun onStart() {
        super.onStart()
        set()
    }
    override fun onResume() {
        super.onResume()
        set()
    }

    //set to view
    private fun set(){
        name = intent.getStringExtra("Name").toString()
        profile = intent.getStringExtra("Profile").toString()
        date = intent.getStringExtra("Date").toString()
        type = intent.getStringExtra("Type").toString()
        caption = intent.getStringExtra("Caption").toString()
        photo = intent.getStringExtra("Photo").toString()
        idPost = intent.getStringExtra("IDPost").toString()

        binding.TextName.text = name
        binding.TextDate.text=date
        binding.TextType.text=type
        binding.TextCaption.text = caption
        Picasso.get().load(profile).into(binding.ProfileUser)
        Picasso.get().load(photo).into(binding.Photo)
        binding.TextCountLike.text = "$countLike"

    }
    //update like
    private fun updateLike(){
            if (binding.BtnLike.text =="React"){
                binding.BtnLike.setText("Reacted")
                binding.BtnLike.setTextColor(getColor(R.color.white))
                binding.BtnLike.setBackgroundColor(getColor(R.color.primary))
                countLike++
                database.child(idPost).child("Like").setValue(countLike)
                binding.TextCountLike.text ="$countLike"

            }else{
                binding.BtnLike.setTextColor(getColor(R.color.black_60))
                binding.BtnLike.setBackgroundColor(getColor(R.color.white))
                binding.BtnLike.setText("React")
                countLike--
                database.child(idPost).child("Like").setValue(countLike)
                binding.TextCountLike.text ="$countLike"
            }

    }
}