package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StartActivity : AppCompatActivity() {
    //
    private lateinit var auth :FirebaseAuth
    private lateinit var datebase:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        startApp()

        auth = FirebaseAuth.getInstance()
        datebase = FirebaseDatabase.getInstance().getReference("Users")
    }

    override fun onResume(){
        super.onResume()
        startApp()
    }

    //go to login screen
    private fun startApp(){
        Handler().postDelayed({
            datebase.child(auth.uid.toString()).get().addOnSuccessListener {
                if (it.exists()){
                    val login = it.child("Login").value.toString()
                    if (login == "Yes"){
                        startActivity(Intent(this,HomePageActivity::class.java))
                    }else{
                        startActivity(Intent(this,LoginActivity::class.java))
                    }
                }else{
                    startActivity(Intent(this,LoginActivity::class.java))
                }
            }
        },1500)


    }
}