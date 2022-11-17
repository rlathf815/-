package com.example.firebase.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase.LoginActivity
import com.example.firebase.MainActivity
import com.example.firebase.R
import com.example.firebase.databinding.FragmentMenuBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuFragment:Fragment(R.layout.fragment_menu) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMenuBinding.bind(view)

        val intent = Intent(activity, LoginActivity::class.java)

        binding.buttonSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(//로그아웃 후 로그인 액티비티로 이동
                intent
            )
            //finish()//메인액티비티 종료
        }

        //findNavController()
    }
}