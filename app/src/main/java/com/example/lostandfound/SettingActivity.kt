package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    private var language : String=""
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnClose.setOnClickListener {
            finish()
        }

         binding.English.setOnClickListener {
             language = "English"
         }

        binding.Khmer.setOnClickListener {
            language = "Khmer"
        }

        binding.BtnSetting.setOnClickListener {
            setSetting()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        getLang()
    }

    //set setting
    private fun setSetting(){
        if (language.isNotEmpty()){
            database.child(auth.uid.toString()).child("Language").setValue(language)
            startActivity( Intent(this,StartActivity::class.java))
        }

    }

    private fun getLang(){
        database.child(auth.uid.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val l = it.child("Language").value.toString()
                if (l[0]=='E'){
                    binding.Language.check(R.id.English)
                }else{
                    binding.Language.check(R.id.Khmer)
                }
            }
        }
    }
}