package com.example.lostandfound

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityEditProfileBinding

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var storage: StorageReference


    private var path:Uri?=null
    private val PICK_IMAGE=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //back
        binding.BtnClose.setOnClickListener {
            finish()
        }

        //select image
        binding.ProfileUser.setOnClickListener {
          selectPhoto()
        }

        //set new Profile
        binding.BtnEditProfile.setOnClickListener {
            hideKeyboard(binding.root)
            showDialog()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("Profile Users")

        setProfile()
    }






    //Load Image to Profile view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ){
                path = data.data
                binding.ProfileUser.setImageURI(path)
            }
        }
    }


    //select image from gallery
    private fun selectPhoto(){
        var i  = Intent(Intent.ACTION_PICK);
        i.type = "image/*"
        startActivityForResult(i,PICK_IMAGE)
    }

    //set up profile
    private fun setProfile(){
        database.child(auth.uid.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val name =  it.child("Name").value.toString()
                val profile = it.child("Profile").value.toString()

                binding.InputFullName.setText(name)
                Picasso.get().load(profile).into(binding.ProfileUser)
            }
        }
    }

    //show dialog
    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_ask)
        val ip = WindowManager.LayoutParams()
        ip.copyFrom(dialog.window!!.attributes)
        ip.width = WindowManager.LayoutParams.MATCH_PARENT
        ip.height = WindowManager.LayoutParams.WRAP_CONTENT
        ip.gravity = Gravity.CENTER
        dialog.window!!.attributes=ip

        val textHeader = dialog.findViewById<TextView>(R.id.TextHeader)
        textHeader.text = getString(R.string.ask_update_profile)

        val btnYes = dialog.findViewById<Button>(R.id.BtnYes)
        btnYes.setOnClickListener {
            checkInput(
                binding.InputFullName.text.toString()
            )
            dialog.dismiss()
        }

        val btnNo = dialog.findViewById<Button>(R.id.BtnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //check input
    private fun checkInput(name:String){
        when {
            name =="" -> {
                Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show()
            }
            name.length<4->{
                Toast.makeText(this,"Please full name have at least 4 character",Toast.LENGTH_SHORT).show()
            }
            path==null -> {
                noPhoto(name)
            }
            else -> {
                withPhoto(name,path!!)
            }
        }
    }

    //only change name
    private fun noPhoto(name: String){
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible=true
        database.child(auth.uid.toString()).child("Name").setValue(name).addOnCompleteListener {
            if (it.isSuccessful){
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"Edit profile success",Toast.LENGTH_LONG).show()

                database.database.getReference("Users").child(auth.uid.toString()).get().addOnSuccessListener {
                    if (it.exists()){
                        val name = it.child("Name").value.toString()
                        val photo = it.child("Profile").value.toString()
                        updatePost(name,photo,auth.uid.toString())
                    }
                }
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }

    //change both
    private fun withPhoto(name: String,path:Uri){
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible=true
        storage.child(auth.uid.toString()).putFile(path).addOnCompleteListener{
            if (it.isSuccessful){
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                storage.child(auth.uid.toString()).downloadUrl.addOnSuccessListener {
                    Toast.makeText(this,"Edit profile success",Toast.LENGTH_LONG).show()
                    database.child(auth.uid.toString()).child("Name").setValue(name)
                    database.child(auth.uid.toString()).child("Profile").setValue(it.toString())
                    database.database.getReference("Users").child(auth.uid.toString()).get().addOnSuccessListener {
                        if (it.exists()){
                            val name = it.child("Name").value.toString()
                            val photo = it.child("Profile").value.toString()
                            updatePost(name,photo,auth.uid.toString())
                        }
                    }
                }
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //update post
    private fun updatePost(name: String,photo:String,ID:String){
        database.database.getReference("Post").get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val idPost = ds.child("IDPost").value.toString()
                    val userID = ds.child("UserID").value.toString()
                    if (ID == userID){
                        database.database.getReference("Post").child(idPost).child("Name").setValue(name)
                        database.database.getReference("Post").child(idPost).child("Profile").setValue(photo)
                    }
                }
            }
        }
    }
}