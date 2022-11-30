package com.example.firebase.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebase.MainActivity
import com.example.firebase.R
import com.example.firebase.databinding.FragmentSearchBinding
import com.example.firebase.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment(R.layout.fragment_search){
    var fragmentView: View? = null

    var targetEmail: String?=null
    var auth : FirebaseAuth ?=null
    var firestore: FirebaseFirestore? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchBinding.bind(view)
        var userDto = UserDTO()
        val profileFragment =ProfileFragment()
        val bundle = Bundle()
        //var mainActivity: MainActivity? = null
        auth = FirebaseAuth.getInstance()
        fragmentView = binding!!.root
        firestore = FirebaseFirestore.getInstance()
        searchBtn.setOnClickListener {
            targetEmail = editText.text.toString()
            firestore?.collection("users")?.whereEqualTo("email", targetEmail)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null)
                        return@addSnapshotListener
                    if(querySnapshot.size()==0)
                        edittextcontent.text = "no result"

                    for (snapshot in querySnapshot.documents!!) {
                            userDto = snapshot.toObject((UserDTO::class.java))!!
                            edittextcontent.text = userDto.birth.toString()
                        System.out.println("===================="+ userDto.email.toString())
                        var mainActivity: MainActivity? = null
                        mainActivity!!.openProfileFragment(userDto.email.toString())
                        System.out.println("===================="+ userDto.email.toString())

                    }

                }

        }





            //findNavController()

    }
}