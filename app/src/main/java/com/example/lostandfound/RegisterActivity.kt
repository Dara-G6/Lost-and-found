package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth:FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage:StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.BtnRegister.setOnClickListener {
            hideKeyboard(binding.BtnRegister)
            checkInput(
                binding.InputFullName.text.toString(),
                binding.InputEmail.text.toString(),
                binding.InputPassword.text.toString(),
                binding.InputConfirmPassword.text.toString(),
                binding.InputGender.text.toString()
            )
        }


        val adapter = ArrayAdapter.createFromResource(this,
            R.array.gender,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )

        this.binding.InputGender.setAdapter(adapter)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("Profile Users")

        binding.root.setOnClickListener {
            hideKeyboard(binding.Form)
        }

        binding.Form.setOnClickListener{
            hideKeyboard(binding.root)
        }

    }


    //check input
    private fun  checkInput(fullName:String , email:String , password:String , confirmPassword:String,gender:String){
        if (email == "" || fullName=="" || password=="" || confirmPassword==""||gender==""){
            Toast.makeText(this,"Please enter all the information",Toast.LENGTH_SHORT).show()
        }
        else if (fullName.length<4){
            Toast.makeText(this,"Full name at least have 4 characters",Toast.LENGTH_SHORT).show()
        }
        else if (password!=confirmPassword){
            binding.InputConfirmPassword.error = "Password not match"
        }else{
            createAuth(email,password)
        }
    }

    //create auth
    private fun createAuth(email: String,password: String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                 createProfile(
                     binding.InputFullName.text.toString(),
                     email,
                     auth.uid.toString(),
                     binding.InputGender.text.toString()
                 )
            }else{
                Toast.makeText(this,"${it.exception}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProfile(fullName: String,email: String,ID:String,gender: String){
        binding.Form.isVisible = false
        binding.SHOWPROGRESS.isVisible = true
        when {
            gender[0] =='M' -> {
                storage.child("male.png").downloadUrl.addOnSuccessListener {
                    val map = HashMap<String,String>()
                    map.put("Name",fullName)
                    map.put("Email",email)
                    map.put("ID",ID)
                    map.put("Gender",gender)
                    map.put("Profile",it.toString())
                    map.put("Login","Yes")
                    map.put("Language","English")
                    database.child(ID).setValue(map).addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"Register Success",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomePageActivity::class.java))
                        }else{
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"${it.exception}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }
            gender[0] =='F' -> {
                storage.child("female.png").downloadUrl.addOnSuccessListener {
                    val map = HashMap<String,String>()
                    map.put("Name",fullName)
                    map.put("Email",email)
                    map.put("ID",ID)
                    map.put("Gender",gender)
                    map.put("Profile",it.toString())
                    map.put("Login","Yes")
                    map.put("Language","English")
                    database.child(ID).setValue(map).addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"Register Success",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomePageActivity::class.java))
                        }else{
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"${it.exception}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else -> {
                storage.child("others.png").downloadUrl.addOnSuccessListener {
                    val map = HashMap<String,String>()
                    map.put("Name",fullName)
                    map.put("Email",email)
                    map.put("ID",ID)
                    map.put("Gender",gender)
                    map.put("Profile",it.toString())
                    map.put("Login","Yes")
                    map.put("Language","English")
                    database.child(ID).setValue(map).addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"Register Success",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomePageActivity::class.java))

                        }else{
                            binding.Form.isVisible = true
                            binding.SHOWPROGRESS.isVisible = false
                            Toast.makeText(this,"${it.exception}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}