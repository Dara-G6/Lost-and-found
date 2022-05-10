package com.example.lostandfound

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.databinding.ActivityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class PostActivity(

) : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityPostBinding

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var storage:StorageReference

    //
    private lateinit var  name :String
    private lateinit var ID  :String
    private lateinit var email :String
    private lateinit var profile :String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnClose.setOnClickListener {
            finish()
        }

        //select photo
        binding.Photo.setOnClickListener {
            selectPhoto()
        }

        //button post
        binding.BtnPost.setOnClickListener {
            hideKeyboard(binding.root)
            checkInput(
                binding.InputCaption.text.toString(),
                binding.InputType.text.toString()
            )
        }

        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
        }




        val adapter = ArrayAdapter.createFromResource(
            this,
                   R.array.type,
                   androidx.appcompat.R.layout.select_dialog_item_material
        )

        binding.InputType.setAdapter(adapter)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Post")
        storage = FirebaseStorage.getInstance().getReference("Post")


        name=intent.getStringExtra("Name").toString()
        email = intent.getStringExtra("Email").toString()
        ID = intent.getStringExtra("UserID").toString()
        profile = intent.getStringExtra("Profile").toString()
    }

    //Load Image to Profile view
    private val PICK_IMAGE=1
    private var path:Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ){
                path = data.data
                binding.Photo.setImageURI(path)
            }
        }
    }


    //select image from gallery
    private fun selectPhoto(){
        var i  = Intent(Intent.ACTION_PICK);
        i.type = "image/*"
        startActivityForResult(i,PICK_IMAGE)
    }

    //check input
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkInput(caption :String, type:String){
        if (caption == "" || type ==""){
            Toast.makeText(this,"Please enter all the information",Toast.LENGTH_LONG).show()
        }else if(path==null){
                noPhoto(caption,type)
        }else{
               withPhoto(caption,type,path!!)
        }
    }

    //only caption
    @RequiresApi(Build.VERSION_CODES.O)
    private fun noPhoto(caption: String, type: String){
        //generate ID and DatePost
        val idPost = Random.nextLong(1000000000)*9 - Random.nextInt(1000000)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm: a")
        val date = current.format(formatter)

        val map = HashMap<String,String>()
        map.put("Date",date)
        map.put("Name",name!!)
        map.put("UserID",ID!!)
        map.put("Profile",profile!!)
        map.put("Email",email!!)
        map.put("Photo","https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Post%2Fphoto.png?alt=media&token=170b077f-2b43-467b-886b-55d19097f033")
        map.put("IDPost",idPost.toString())
        map.put("Caption",caption)
        map.put("Type",type)
        map.put("Like","0")

        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible=true
        database.child(idPost.toString()).setValue(map).addOnCompleteListener {
            if (it.isSuccessful){
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"Upload success",Toast.LENGTH_LONG).show()
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun withPhoto(caption: String, type: String, path:Uri){
        //generate ID and DatePost
        val idPost = Random.nextLong(1000000000)*9 - Random.nextInt(1000000)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm: a")
        val date = current.format(formatter)
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible=true
        storage.child(idPost.toString()).putFile(path).addOnCompleteListener {
            if (it.isSuccessful){
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                storage.child(idPost.toString()).downloadUrl.addOnSuccessListener { i ->
                    val map = HashMap<String,String>()
                    map.put("Date",date)
                    map.put("Name",name!!)
                    map.put("UserID",ID!!)
                    map.put("Profile",profile!!)
                    map.put("Email",email!!)
                    map.put("Photo",i.toString())
                    map.put("IDPost",idPost.toString())
                    map.put("Caption",caption)
                    map.put("Type",type)
                    map.put("Like","0")

                    database.child(idPost.toString()).setValue(map).addOnCompleteListener { it ->
                        if (it.isSuccessful){
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible=false
                            Toast.makeText(this,"Upload success",Toast.LENGTH_LONG).show()
                            }
                        else{
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible=false
                            Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
                        }
                    }
                }


            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }

}