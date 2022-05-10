package com.example.lostandfound.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lostandfound.Adapter.NewsAdapter
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.R
import com.example.lostandfound.databinding.FragmentNewsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class NewsFragment(): Fragment() {

    private lateinit var database :DatabaseReference


    private lateinit var binding: FragmentNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentNewsBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().getReference("Post")

        binding.RefreshNew.setOnRefreshListener {
            binding.RefreshNew.isRefreshing=false
            setList()
        }



        setList()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        setList()
    }





    //set list view
    private fun setList(){
        val list = ArrayList<Post>()
        database.orderByChild("Date").get().addOnSuccessListener {
            if (it.exists()){
                for (ds in it.children){
                    val name    = ds.child("Name").value.toString()
                    val email   = ds.child("Email").value.toString()
                    val userID  = ds.child("UserID").value.toString()
                    val profile =ds.child("Profile").value.toString()
                    val idPost  =ds.child("IDPost").value.toString()
                    val photo   =ds.child("Photo").value.toString()
                    val date    =ds.child("Date").value.toString()
                    val caption =ds.child("Caption").value.toString()
                    val type    =ds.child("Type").value.toString()
                    val like    =ds.child("Like").value.toString()

                    list.add(
                        Post(
                            name,
                            email,
                            userID,
                            profile,
                            idPost,
                            photo,
                            date,
                            caption,
                            type,
                            like.toLong()
                        )
                    )
                }

                list.reverse()
                val adapter = NewsAdapter(
                     requireContext()
                    ,R.layout.news_list_view,
                    list
                )
                binding.ListNews.adapter= adapter

            }
        }

    }


}