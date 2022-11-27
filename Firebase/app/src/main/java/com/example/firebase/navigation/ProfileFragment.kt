package com.example.firebase.navigation

import android.app.DownloadManager.Request
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.support.v4.*
import androidx.recyclerview.widget.*
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firebase.R
import com.example.firebase.MainActivity
import com.example.firebase.databinding.ActivityMainBinding

import com.google.firebase.firestore.FirebaseFirestore
import com.example.firebase.databinding.FragmentProfileBinding
import com.example.firebase.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.support.v4.*


class ProfileFragment: Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth : FirebaseAuth ?=null
    var currentUserUid: String? = null
    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        System.out.println("========================================view created")
        //System.out.println("========================================dto size? " + getItemCount())
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        //fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        fragmentView= binding!!.root
        currentUserUid = auth?.currentUser?.uid
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()

        //System.out.println("========================================account_recyclerview found? " + account_recyclerview)

        binding?.apply {
            this.accountRecyclerview.layoutManager = GridLayoutManager(requireActivity(),3)
            this.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        }
        // account_recyclerview?.adapter=UserFragmentRecyclerViewAdapter()
        // account_recyclerview?.layoutManager = GridLayoutManager(requireActivity(),3)
        return fragmentView
    }
    inner class UserFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            contentDTOs = ArrayList()
            //System.out.println("========================================querysnapshot size" + firestore?.collection("post"))

            firestore?.collection ("post")?.whereEqualTo("uid", currentUserUid)?.addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()

                System.out.println("========================================uid" + uid)

                if (querySnapshot == null) return@addSnapshotListener
                System.out.println("========================================querysnapshot size" + querySnapshot.size())

                for (snapshot in querySnapshot.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    System.out.println("=================================contentDTO added")
                }
                account_tv_post_count.text = contentDTOs.size.toString()
                notifyDataSetChanged()
                System.out.println("========================================adapter created")

            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Intent.ACTION_OPEN_DOCUMENT

            Glide.with(p0.itemView.context)
                .load(contentDTOs[p1].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageview)
        }

        override fun getItemCount(): Int {
            System.out.println("========================================size? " + contentDTOs.size)

            return contentDTOs.size
        }

    }
}
