package com.example.campusconnectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.campusconnectapp.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Home_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val eventFragment = EventFragment()
    private val interestFragment = InterestFragment()
    private val settingsFragment = SettingsFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCurrentFragment(eventFragment)



        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_events -> setCurrentFragment(eventFragment)
                R.id.item_interest -> setCurrentFragment(interestFragment)
                R.id.item_settings -> setCurrentFragment(settingsFragment)
            }
            true
        }



        val intent = intent
        var myInfo:String? = intent.getStringExtra("name").toString()
        myInfo+="\n"
        myInfo+= intent.getStringExtra("id").toString()
        myInfo+="\n"
        myInfo+= intent.getStringExtra("mail").toString()

        GlobalScope.launch(Dispatchers.Main) {
            eventFragment.setTextViewText(myInfo!!)
        }




    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}