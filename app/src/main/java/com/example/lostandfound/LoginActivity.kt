package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.lostandfound.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    //View
    private lateinit var binding: ActivityLoginBinding

    //Firebase
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnLogin.setOnClickListener {
            hideKeyboard(binding.BtnLogin)
             checkInput(
                 binding.InputEmail.text.toString(),
                 binding.InputPassword.text.toString()
             )
        }

        binding.BtnRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
        }

        autoLogin()
    }

    override fun onStart() {
        super.onStart()
        autoLogin()
    }


    //check input user
    private fun checkInput(email:String , password:String){
        if(email=="" || password==""){
            Toast.makeText(this,"Please enter all the information",Toast.LENGTH_LONG).show()
        }else{
            loginByForm(email,password)
        }
    }

    //login by form
    private fun loginByForm(email: String,password: String){
        binding.Form.isVisible=false
        binding.SHOWPROGRESS.isVisible = true
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                binding.Form.isVisible=true
                binding.SHOWPROGRESS.isVisible = false
                Toast.makeText(this,"Login success",Toast.LENGTH_LONG).show()
                database.child(auth.uid.toString()).child("Login").setValue("Yes")
                startActivity(Intent(this,HomePageActivity::class.java))
            }else{
                binding.Form.isVisible=true
                binding.SHOWPROGRESS.isVisible = false
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }

    //case user already login
    private fun autoLogin(){
        database.child(auth.uid.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val login = it.child("Login").value.toString()
                if (login == "Yes"){
                    startActivity(Intent(this,HomePageActivity::class.java))
                }
            }
        }
    }
}