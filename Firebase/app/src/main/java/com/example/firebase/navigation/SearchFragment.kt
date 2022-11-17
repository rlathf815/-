package com.example.firebase.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase.R
import com.example.firebase.databinding.FragmentSearchBinding


class SearchFragment : Fragment(R.layout.fragment_search){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchBinding.bind(view)

        //findNavController()

    }
}