package com.example.firebase.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase.R
import com.example.firebase.databinding.FragmentPostBinding

class PostFragment:Fragment(R.layout.fragment_post) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPostBinding.bind(view)


        //findNavController()
    }


}