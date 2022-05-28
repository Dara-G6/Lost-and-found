package com.example.lostandfound.Adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import com.example.lostandfound.DataClass.Post
import com.example.lostandfound.EditPostActivity
import com.example.lostandfound.R
import com.example.lostandfound.databinding.HistoryListViewBinding

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class HistoryAdapter(context: Context, resource: Int,list:ArrayList<Post>,activity: Activity) :
    ArrayAdapter<Post>(context, resource,list) {

    //binding
    private lateinit var binding: HistoryListViewBinding

    //
    private val l = list
    private val a = activity
    private val defaultPhoto = "https://firebasestorage.googleapis.com/v0/b/lost-and-found-3a38b.appspot.com/o/Post%2Fphoto.png?alt=media&token=170b077f-2b43-467b-886b-55d19097f033"
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= HistoryListViewBinding.inflate(layoutInflater,parent,false)

        binding.TextDate.text ="Date : "+ l[position].Date
        binding.TextType.text ="Type : "+ l[position].Type
        binding.TextCaption.text = l[position].Caption
        Picasso.get().load(l[position].Photo).into(binding.Photo)
        if (l[position].Photo == defaultPhoto){
            binding.Photo.visibility = View.GONE
        }

        binding.BtnDelete.setOnClickListener {
            showDialog(
                l[position].IDPost,
                position,
                l[position].Photo,
                l
            )
        }

        binding.BtnEdit.setOnClickListener {
                val i = Intent(context,EditPostActivity::class.java)
                i.putExtra("IDPost",l[position].IDPost)
                i.putExtra("Caption",l[position].Caption)
                i.putExtra("Type",l[position].Type)
                i.putExtra("Photo",l[position].Photo)
                context.startActivity(i)
        }

        if (l[position].Like!=0L){
            binding.TextLike.text = "Like : "+l[position].Like
        }else{
            binding.TextLike.isVisible=false
        }


        return binding.root
    }

    override fun remove(`object`: Post?) {
        super.remove(`object`)
    }


    //show dialog
    private fun showDialog(idPost:String,index:Int,photo:String,list: ArrayList<Post>){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_ask)
        val ip = WindowManager.LayoutParams()
        ip.copyFrom(dialog.window!!.attributes)
        ip.width = WindowManager.LayoutParams.MATCH_PARENT
        ip.height = WindowManager.LayoutParams.WRAP_CONTENT
        ip.gravity = Gravity.CENTER
        dialog.window!!.attributes=ip

        val textHeader = dialog.findViewById<TextView>(R.id.TextHeader)
        textHeader.text = context.getString(R.string.ask_delete_post)

        val btnYes = dialog.findViewById<Button>(R.id.BtnYes)
        btnYes.setOnClickListener {
            dialog.dismiss()
            deletePost(idPost,index,photo,list)
        }

        val btnNo = dialog.findViewById<Button>(R.id.BtnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        val iconDialog = dialog.findViewById<ImageView>(R.id.IconDialog)
        if (photo!=defaultPhoto){
            Picasso.get().load(photo).into(iconDialog)
        }
        else{
            iconDialog.setImageResource(R.drawable.fail_toast)
        }
        dialog.show()
    }

    // delete post
    private fun deletePost(idPost: String,index: Int,photo: String,list: ArrayList<Post>){
        val database = FirebaseDatabase.getInstance().getReference("Post")
        val storage  = FirebaseStorage.getInstance().getReference("Post")
        database.child(idPost).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                if (photo!=defaultPhoto){
                    storage.child(idPost).delete().addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show()
                            remove(l[index])

                        }else{
                            Toast.makeText(context,"${task.exception}",Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show()
                    remove(l[index])
                }
            }else{
                Toast.makeText(context,"${it.exception}",Toast.LENGTH_LONG).show()
            }
        }
    }



}