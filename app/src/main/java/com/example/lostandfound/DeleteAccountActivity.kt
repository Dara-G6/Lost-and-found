package com.example.lostandfound

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.databinding.ActivityDeleteAccountBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DeleteAccountActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityDeleteAccountBinding

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database:DatabaseReference
    private lateinit var storage: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //back button
        binding.BtnClose.setOnClickListener {
            finish()
        }

        //button delete
        binding.BtnDeleteAccount.setOnClickListener {
            hideKeyboard(binding.root)
            showDialog()
        }

        //
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("Profile Users")
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
        textHeader.text = "Do you want to delete your account?"

        val btnYes = dialog.findViewById<Button>(R.id.BtnYes)
        btnYes.setOnClickListener {
            dialog.dismiss()
            checkInput(
                binding.InputEmail.text.toString(),
                binding.InputPassword.text.toString()
            )
        }

        val btnNo = dialog.findViewById<Button>(R.id.BtnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        val iconDialog = dialog.findViewById<ImageView>(R.id.IconDialog)
        iconDialog.setImageResource(R.drawable.fail_toast)
        dialog.show()
    }

    //check input
    private fun checkInput(email:String,password:String){
        if (email == ""|| password==""){
            Toast.makeText(this,"Please enter all the information",Toast.LENGTH_LONG).show()
        }else{
             delete(email,password)
        }
    }

    //delete
    private fun delete(email:String,password: String){
        val c = EmailAuthProvider.getCredential(email,password)
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible=true
        user.reauthenticate(c).addOnCompleteListener {
            if (it.isSuccessful){
                deletePost()
                deleteProfile()
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        binding.Form.isVisible = true
                        binding.SHOWPROGRESS.isVisible=false
                        Toast.makeText(this,"Account Deleted",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,StartActivity::class.java))
                    }else{
                        binding.Form.isVisible = true
                        binding.SHOWPROGRESS.isVisible=false
                        Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible=false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }

    //delete post
    private fun deletePost(){
        database.database.getReference("Post").get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val userID = ds.child("UserID").value.toString()
                    val idPost = ds.child("IDPost").value.toString()
                    val photo  = ds.child("Photo").value.toString()
                    if (auth.uid.toString() == userID){
                        if (photo == "https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Post%2Fphoto.png?alt=media&token=170b077f-2b43-467b-886b-55d19097f033"){
                            database.database.getReference("Post").child(idPost).removeValue()
                        }else{
                            storage.storage.getReference("Post").child(idPost).delete()
                            database.database.getReference("Post").child(idPost).removeValue()
                        }

                    }
                }
            }
        }
    }

    //delete profile
    private fun deleteProfile(){
        val other = "https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Profile%20Users%2Fothers.png?alt=media&token=ad0cbd17-3d35-483c-b967-ab2350c50641"
        val male ="https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Profile%20Users%2Fmale.png?alt=media&token=90b311be-d7ff-4392-84b6-338039c184fc"
        val female = "https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Profile%20Users%2Ffemale.png?alt=media&token=4817e5e2-8661-4fad-9bde-167ef075dd5d"
        database.database.getReference("Users").child(auth.uid.toString()).get().addOnSuccessListener{
            if (it.exists()){
                val profile=it.child("Profile").value.toString()
                if (profile==female || profile == male || profile==other){
                    database.database.getReference("Users").child(auth.uid.toString()).removeValue()
                }else{
                    storage.storage.getReference("Profile Users").child(auth.uid.toString()).delete()
                    database.database.getReference("Users").child(auth.uid.toString()).removeValue()
                }
            }
        }
    }

}