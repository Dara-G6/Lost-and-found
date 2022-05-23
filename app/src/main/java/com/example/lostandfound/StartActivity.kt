package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class StartActivity : AppCompatActivity() {
    //
    private lateinit var auth :FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        startApp()
    }

    override fun onResume(){
        super.onResume()
        startApp()
    }

    //go to login screen
    private fun startApp(){
        Handler().postDelayed({
            database.child(auth.uid.toString()).get().addOnSuccessListener {
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

         getLang()
    }

    // get language user
    private fun getLang() {
        database.child(auth.uid.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                val lang = it.child("Language").value.toString()
                setLang(lang[0].toString() + lang[1].toString())
            } else {
                setLang("en")
            }
        }
    }

    private fun setLang(lang:String){
        val r = resources
        val dm = r.displayMetrics
        val config = r.configuration
        config.locale = Locale(lang.toLowerCase())
        r.updateConfiguration(config,dm)
    }
}