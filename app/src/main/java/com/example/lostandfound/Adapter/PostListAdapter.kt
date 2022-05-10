package com.example.lostandfound.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.databinding.PostListViewBinding
import com.squareup.picasso.Picasso

class PostListAdapter(context: Context,resource:Int,val list: List<Post>
) : ArrayAdapter<Post>(context, resource, list) {


    //binding
    private lateinit var binding: PostListViewBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= PostListViewBinding.inflate(layoutInflater,parent,false)

        binding.TextCaption.text = list[position].Caption
        binding.TextDate.text ="Date : "+ list[position].Date
        binding.TextType.text ="Type : "+ list[position].Type
        Picasso.get().load(list[position].Photo).into(binding.Photo)

        return binding.root;
    }


}