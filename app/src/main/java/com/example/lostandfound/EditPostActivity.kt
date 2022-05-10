package com.example.lostandfound

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.lostandfound.databinding.ActivityEditPostBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class EditPostActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityEditPostBinding

    //
    private lateinit var idPost :String
    private lateinit var photo:String
    private lateinit var caption:String
    private lateinit var type:String
    private val PICK_IMAGE=1
    private var path: Uri?=null

    private lateinit var database:DatabaseReference
    private lateinit var storage:StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnClose.setOnClickListener {
            finish()
        }

        binding.Photo.setOnClickListener {
            selectPhoto()
        }

        binding.BtnPost.setOnClickListener {
            showDialog()
        }






        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type,
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item
            )
        binding.InputType.setAdapter(adapter)

        idPost = intent.getStringExtra("IDPost").toString()
        photo  = intent.getStringExtra("Photo").toString()
        caption = intent.getStringExtra("Caption").toString()
        type  = intent.getStringExtra("Type").toString()

        Picasso.get().load(photo).into(binding.Photo)
        binding.InputCaption.setText(caption)


        database = FirebaseDatabase.getInstance().getReference("Post")
        storage  = FirebaseStorage.getInstance().getReference("Post")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ){
                path = data.data
                binding.Photo.setImageURI(path)
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
        textHeader.text = "Do you want to update this post?"

        val btnYes = dialog.findViewById<Button>(R.id.BtnYes)
        btnYes.setOnClickListener {
            checkInput(
                binding.InputCaption.text.toString(),
                binding.InputType.text.toString()
            )
            dialog.dismiss()
        }

        val btnNo = dialog.findViewById<Button>(R.id.BtnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        val iconDialog = dialog.findViewById<ImageView>(R.id.IconDialog)
        if (photo!="https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Post%2Fphoto.png?alt=media&token=170b077f-2b43-467b-886b-55d19097f033"){
            Picasso.get().load(photo).into(iconDialog)
        }else{
            iconDialog.setImageResource(R.drawable.question_mark)
        }
        dialog.show()
    }

    //select image from gallery
    private fun selectPhoto(){
        val i  = Intent(Intent.ACTION_PICK);
        i.type = "image/*"
        startActivityForResult(i,PICK_IMAGE)
    }

    //check input
    private fun checkInput(caption :String, type:String){
        if (caption == "" || type ==""){
            Toast.makeText(this,"Please enter all the information", Toast.LENGTH_LONG).show()
        }else if(path==null){
            noPhoto(caption,type)
        }else{
            withPhoto(caption,type,path!!)
        }
    }

    //change all
    private fun withPhoto(caption: String, type: String, path: Uri) {
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible = true
        storage.child(idPost).putFile(path).addOnCompleteListener{
            if (it.isSuccessful){
                storage.child(idPost).downloadUrl.addOnSuccessListener{t->
                    Log.d("Link ",t.toString())
                    database.child(idPost).child("Photo").setValue(t.toString())
                    database.child(idPost).child("Caption").setValue(caption)
                    database.child(idPost).child("Type").setValue(type).addOnCompleteListener {x->
                        if (x.isSuccessful){
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"Edit Post success",Toast.LENGTH_LONG).show()
                        }else{
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"${x.exception}",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible = false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }

    }

    //change caption
    private fun noPhoto(caption: String, type: String) {
            database.child(idPost).child("Type").setValue(type)
            binding.Form.isVisible = false
            binding.SHOWPROGRESS.isVisible = true
            database.child(idPost).child("Caption").setValue(caption).addOnCompleteListener {
                if (it.isSuccessful){
                    binding.Form.isVisible = true
                    binding.SHOWPROGRESS.isVisible = false
                    Toast.makeText(this,"Edit Post success",Toast.LENGTH_LONG).show()
                }else{
                    binding.Form.isVisible = true
                    binding.SHOWPROGRESS.isVisible = false
                    Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
                }
            }
    }

}