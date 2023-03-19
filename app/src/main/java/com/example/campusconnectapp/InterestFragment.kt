package com.example.campusconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campusconnectapp.databinding.FragmentInterestBinding
import com.google.firebase.auth.FirebaseAuth


class InterestFragment : Fragment() {
    private lateinit var binding: FragmentInterestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInterestBinding.inflate(inflater, container, false)

        binding.SignOutButton.setOnClickListener {



            val auth = FirebaseAuth.getInstance()
            auth.signOut()

            val intent = Intent(requireContext(), SplashScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


            startActivity(intent)

        }
        return binding.root
    }


}