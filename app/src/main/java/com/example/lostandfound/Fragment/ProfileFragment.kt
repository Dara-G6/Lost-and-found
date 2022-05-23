package com.example.lostandfound.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lostandfound.*
import com.example.lostandfound.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {


    //binding
    private lateinit var binding: FragmentProfileBinding

    //firebase
    private lateinit var auth:FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")


        //logout button
        binding.BtnLogout.setOnClickListener {
            logout()
        }

        //edit profile
        binding.BtnEditProfile.setOnClickListener {
            startActivity(Intent(activity,EditProfileActivity::class.java))
        }

        //post
        binding.BtnPost.setOnClickListener {
            gotoPost()
        }

        //change password
        binding.BtnChangePassword.setOnClickListener {
            startActivity(Intent(activity,ChangePasswordActivity::class.java))
        }

        //delete account
        binding.BtnDeleteAccount.setOnClickListener {
            startActivity(Intent(activity,DeleteAccountActivity::class.java))
        }

        binding.BtnHistory.setOnClickListener {
            val i = Intent(activity,HistoryPostActivity::class.java)
            startActivity(i)
        }

        binding.BtnSetting.setOnClickListener {
            val i = Intent(activity,SettingActivity::class.java)
            startActivity(i)
        }

        setProfile()
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        setProfile()
    }
    override fun onResume() {
        super.onResume()
        setProfile()
    }






    //set profile
    private lateinit var photo:String
    private fun setProfile(){
        database.child(auth.uid.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val name = it.child("Name").value.toString()
                val email = it.child("Email").value.toString()
                val photo = it.child("Profile").value.toString()
                binding.TextName.text = name
                binding.TextEmail.text= email
                Picasso.get().load(photo).into(binding.ProfileUser)
                this.photo = photo
            }
        }
    }

    //logout
    private fun logout(){
        database.child(auth.uid.toString()).child("Login").setValue("No")
        auth.signOut()
        startActivity(Intent( activity,LoginActivity::class.java))
    }

    //go to post page
    private fun gotoPost(){
        val i  = Intent(activity,PostActivity::class.java)
        i.putExtra("Name",binding.TextName.text.toString())
        i.putExtra("Email",binding.TextEmail.text.toString())
        i.putExtra("UserID",auth.uid.toString())
        i.putExtra("Profile",photo)
        startActivity(i)
    }


}