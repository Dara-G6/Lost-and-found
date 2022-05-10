package com.example.lostandfound


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.Fragment.DiscoverFragment
import com.example.lostandfound.Fragment.NewsFragment
import com.example.lostandfound.Fragment.ProfileFragment
import com.example.lostandfound.databinding.ActivityHomePageBinding


class HomePageActivity : AppCompatActivity() {
    //binding
    private lateinit var binding:ActivityHomePageBinding

    //fragment
    private  lateinit var newFragment : NewsFragment
    private lateinit var discoverFragment:DiscoverFragment
    private lateinit var profileFragment : ProfileFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newFragment = NewsFragment()
        discoverFragment = DiscoverFragment()
        profileFragment =ProfileFragment()
        binding.Menu.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.News->{
                    binding.TextHeader.visibility =View.VISIBLE
                    binding.TextHeader.setText(R.string.news)
                    supportFragmentManager.beginTransaction().replace(R.id.Fragment,newFragment).commit()
                }
                R.id.Discoveries->{
                    binding.TextHeader.visibility =View.GONE
                    binding.TextHeader.setText(R.string.search)
                    supportFragmentManager.beginTransaction().replace(R.id.Fragment,discoverFragment).commit()
                }
                R.id.User->{
                    binding.TextHeader.visibility =View.VISIBLE
                    binding.TextHeader.setText(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.Fragment,profileFragment).commit()
                }
            }

            true
        }
    }








}