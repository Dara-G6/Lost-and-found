package com.example.lostandfound.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.lostandfound.DataClass.User
import com.example.lostandfound.DetailUserActivity
import com.example.lostandfound.databinding.UsersListViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserAdapter(context: Context, resource: Int, list: List<User>) :
    ArrayAdapter<User>(context, resource, list) {

    private val c = context
    private val l = list

    private lateinit var binding: UsersListViewBinding
    private lateinit var auth:FirebaseAuth
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = UsersListViewBinding.inflate(layoutInflater,parent,false)
        auth = FirebaseAuth.getInstance()


        Picasso.get().load(l[position].Profile).into(binding.ProfileUser)
        binding.TextName.text = l[position].Name

        if (auth.uid.toString() == l[position].ID){
            binding.TextName.text = "You"
        }
        binding.Card.setOnClickListener {
            val i = Intent(context,DetailUserActivity::class.java)
            i.putExtra("Name",l[position].Name)
            i.putExtra("ID",l[position].ID)
            i.putExtra("Email",l[position].Email)
            i.putExtra("Profile",l[position].Profile)
            c.startActivity(i)
        }

        return binding.root
    }
}