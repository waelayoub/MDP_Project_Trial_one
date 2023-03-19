package com.example.campusconnectapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.campusconnectapp.databinding.FragmentEventBinding

class EventFragment : Fragment() {

    private lateinit var binding: FragmentEventBinding
    private var textViewText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentEventBinding.inflate(inflater, container, false)
        if (textViewText != null) {
            binding.tvEvent.setText(textViewText)
        }
        return binding.root
    }

    fun setTextViewText(text: String) {
        binding.tvEvent.setText(text)
        textViewText=text
    }



}