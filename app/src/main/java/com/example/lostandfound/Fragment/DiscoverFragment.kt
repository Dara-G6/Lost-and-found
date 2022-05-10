package com.example.lostandfound.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lostandfound.Adapter.UserAdapter
import com.example.lostandfound.Customize.hideKeyboard
import com.example.lostandfound.DataClass.User
import com.example.lostandfound.R
import com.example.lostandfound.databinding.FragmentDiscoverBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DiscoverFragment() : Fragment() {


    //binding
    private lateinit var binding: FragmentDiscoverBinding

    //Firebase
    private lateinit var database:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiscoverBinding.inflate(inflater,container,false)
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.BtnSearch.setOnClickListener {
            activity?.hideKeyboard(binding.root)
            if (binding.TextName.text.toString()!=""){
                searchUser(binding.TextName.text.toString())
            }else{
                Toast.makeText(activity,"Please enter user name you want to find",Toast.LENGTH_LONG).show()
            }

        }

        binding.root.setOnClickListener {
            activity?.hideKeyboard(binding.root)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.TextName.setText("")
    }

    private fun searchUser(n: String){
        val list =ArrayList<User>()
        database.get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val name = ds.child("Name").value.toString()
                    val ID   = ds.child("ID").value.toString()
                    val email = ds.child("Email").value.toString()
                    val gender = ds.child("Gender").value.toString()
                    val profile = ds.child("Profile").value.toString()

                    if (name == n || name.contentEquals(n)
                        || name[0].lowercase()==n[0].lowercase()){
                        list.add(
                            User(
                                name,
                                ID,
                                email,
                                gender,
                                profile,
                            )
                        )
                    }
                }

                val adapter =UserAdapter(
                    requireContext(),
                    R.layout.users_list_view,
                    list
                )

                if (list.size == 0){
                    Toast.makeText(activity,"No user name : $n",Toast.LENGTH_LONG).show()
                }
                binding.ListUsers.adapter = adapter
            }
        }
    }



}