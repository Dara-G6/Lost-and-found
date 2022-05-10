package com.example.lostandfound

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityChangePasswordBinding


    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //back button
        binding.BtnClose.setOnClickListener {
            finish()
        }

        //button change profile
        binding.BtnChangePassword.setOnClickListener {
            hideKeyboard(binding.root)
            showDialog()
        }

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
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
        textHeader.text = "Do you want to update your password?"

        val btnYes = dialog.findViewById<Button>(R.id.BtnYes)
        btnYes.setOnClickListener {
            dialog.dismiss()
            checkInput(
                binding.InputCurrentPassword.text.toString(),
                binding.InputNewPassword.text.toString(),
                binding.InputNewConfirmPassword.text.toString()
            )
        }

        val btnNo = dialog.findViewById<Button>(R.id.BtnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //check input
    private fun checkInput(currentPassword:String, newPassword:String, newConfirmPassword:String){
        if (currentPassword == "" || newConfirmPassword=="" || newConfirmPassword==""){
            Toast.makeText(this,"Please enter all the information",Toast.LENGTH_LONG).show()
        }else if (newPassword!=newConfirmPassword){
            binding.InputNewConfirmPassword.error ="Password not match"
        }else{
            changePassword(currentPassword,newConfirmPassword)
        }
    }

    //
    private fun changePassword(currentPassword: String,newConfirmPassword: String){
        val credential = EmailAuthProvider.getCredential(user.email!!,currentPassword)


        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible = true
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful){
                user.updatePassword(newConfirmPassword).addOnCompleteListener {
                    if (it.isSuccessful){
                        binding.Form.isVisible = true
                        binding.SHOWPROGRESS.isVisible = false
                        Toast.makeText(this,"Change Password success",Toast.LENGTH_LONG).show()
                    }else{
                        binding.Form.isVisible = true
                        binding.SHOWPROGRESS.isVisible = false
                        Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                binding.Form.isVisible = true
                binding.SHOWPROGRESS.isVisible = false
                Toast.makeText(this,"${task.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }

}