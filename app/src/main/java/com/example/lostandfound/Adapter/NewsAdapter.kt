package com.example.lostandfound.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.DetailUserActivity
import com.example.lostandfound.ReactionActivity
import com.example.lostandfound.databinding.NewsListViewBinding
import com.squareup.picasso.Picasso

class NewsAdapter(context: Context, resource: Int, list: MutableList<Post>) :
    ArrayAdapter<Post>(context, resource, list) {

    private val l = list
    private val c =context
    private lateinit var binding:NewsListViewBinding


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = NewsListViewBinding.inflate(layoutInflater,parent,false)





        //set name date type caption like and photo
        binding.TextCountLike.text = l[position].Like.toString()
        binding.TextName.text = l[position].Name
        binding.TextDate.text = l[position].Date
        binding.TextType.text = l[position].Type
        binding.TextCaption.text = l[position].Caption
        Picasso.get().load(l[position].Profile).into(binding.ProfileUser)
        Picasso.get().load(l[position].Photo).into(binding.Photo)

        binding.BtnLike.setOnClickListener {
            val i = Intent(context,ReactionActivity::class.java)
            i.putExtra("Profile",l[position].Profile)
            i.putExtra("Name",l[position].Name)
            i.putExtra("Caption",l[position].Caption)
            i.putExtra("Date",l[position].Date)
            i.putExtra("Photo",l[position].Photo)
            i.putExtra("Type",l[position].Type)
            i.putExtra("IDPost",l[position].IDPost)
            i.putExtra("Like",l[position].Like.toInt().toString())
            context.startActivity(i)
        }

        binding.ProfileUser.setOnClickListener {
            val i = Intent(context,DetailUserActivity::class.java)
            i.putExtra("Name",l[position].Name)
            i.putExtra("ID",l[position].UserID)
            i.putExtra("Email",l[position].Email)
            i.putExtra("Profile",l[position].Profile)
            c.startActivity(i)

        }

        return binding.root
    }




}