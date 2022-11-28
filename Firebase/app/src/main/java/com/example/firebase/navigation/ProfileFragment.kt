package com.example.firebase.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firebase.R
import com.example.firebase.databinding.FragmentProfileBinding
import com.example.firebase.navigation.model.ContentDTO
import com.example.firebase.navigation.model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        //fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        fragmentView = binding!!.root
        currentUserUid = auth?.currentUser?.uid
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()

        //var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        //contentDTOs = ArrayList()
        //System.out.println("========================================querysnapshot size" + firestore?.collection("post"))

        /*var postInfo =  firestore?.collection("post")?.document(currentUserUid!!)
            firestore?.runTransaction { transaction ->
                var contentDTOs_data = transaction.get(postInfo!!).toObject(ContentDTO::class.java)
                System.out.println("========================================contentDTO " + contentDTOs_data?.explain)

                }*/

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

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
                //post.text = contentDTOs!![0].explain
                var postContent =StringBuilder()

                var size = contentDTOs.size
                System.out.println("=================================size " + size)

                for(i in 0..size-1)
                {

                    postContent.append(contentDTOs!![i].explain).append("\n")

                }
                post.text = postContent.toString()
                //notifyDataSetChanged()
                System.out.println("========================================adapter created")

                fragmentView?.account_btn_follow_signout?.setOnClickListener {
                    requestFollow()
                }
            }
                //getFollowerAndFollowing()
                return fragmentView

         }

        fun getFollowerAndFollowing() {
            firestore?.collection("users")?.document(uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                    if (followDTO?.followingCount != null) {
                        fragmentView?.account_tv_following_count?.text =
                            followDTO?.followingCount?.toString()
                    }
                    if (followDTO?.followerCount != null) {
                        fragmentView?.account_tv_follower_count?.text =
                            followDTO?.followerCount?.toString()
                        if (followDTO?.followers?.containsKey(currentUserUid!!)) {
                            fragmentView?.account_btn_follow_signout?.text =
                                getString(R.string.follow_cancel)
                            fragmentView?.account_btn_follow_signout?.background?.setColorFilter(
                                ContextCompat.getColor(requireActivity(), R.color.lightGray),
                                PorterDuff.Mode.MULTIPLY
                            )
                        } else {
                            fragmentView?.account_btn_follow_signout?.text =
                                getString(R.string.follow)
                            if (uid != currentUserUid) {
                                fragmentView?.account_btn_follow_signout?.background?.colorFilter =
                                    null
                            }
                        }
                    }
                }
        }

        fun requestFollow() {
            // 내 계정에 데이터 저장
            var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
            firestore?.runTransaction { transaction ->
                var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
                if (followDTO == null) {
                    followDTO = FollowDTO()
                    followDTO!!.followingCount = 1
                    followDTO!!.followers[uid!!] = true

                    transaction.set(tsDocFollowing, followDTO)
                    return@runTransaction
                }

                if (followDTO.followings.containsKey(uid)) {
                    // 내가 팔로우 한 상태일 때(언팔로우 버튼이 떠 있어야 함)
                    followDTO?.followingCount = followDTO.followingCount - 1
                    followDTO?.followers?.remove(uid)
                } else { // 내가 팔로우 하지 않은 상태(팔로우 버튼이 떠 있어야 함)
                    followDTO?.followingCount = followDTO.followingCount + 1
                    followDTO?.followers[uid!!] = true
                }
                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }
            // 상대방 계정에 데이터 저장
            var tsDocFollower = firestore?.collection("users")?.document(uid!!)
            firestore?.runTransaction { transaction ->
                var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
                if (followDTO == null) {
                    followDTO = FollowDTO()
                    followDTO!!.followerCount = 1
                    followDTO!!.followers[currentUserUid!!] = true

                    transaction.set(tsDocFollower, followDTO!!)
                    return@runTransaction
                }
                // 내가 상대방 계정에 팔로우를 했을 경우
                if (followDTO!!.followers.containsKey(currentUserUid)) {
                    followDTO!!.followerCount = followDTO!!.followerCount - 1
                    followDTO!!.followers.remove(currentUserUid)
                } else { // 상대방 계정을 팔로우 하지 않았을 경우
                    followDTO!!.followerCount = followDTO!!.followerCount + 1
                    followDTO!!.followers[currentUserUid!!] = true
                }
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }
        }
    }


// inner class UserFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
//
// var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
// init {
// contentDTOs = ArrayList()
// //System.out.println("========================================querysnapshot size" + firestore?.collection("post"))
//
// firestore?.collection ("post")?.whereEqualTo("uid", currentUserUid)?.addSnapshotListener{querySnapshot, firebaseFirestoreException ->
// contentDTOs.clear()
//
// System.out.println("========================================uid" + uid)
//
// if (querySnapshot == null) return@addSnapshotListener
// System.out.println("========================================querysnapshot size" + querySnapshot.size())
//
// for (snapshot in querySnapshot.documents!!) {
// contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
// System.out.println("=================================contentDTO added")
// }
// account_tv_post_count.text = contentDTOs.size.toString()
// notifyDataSetChanged()
// System.out.println("========================================adapter created")
//
// }
// }
// override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
// var width = resources.displayMetrics.widthPixels
// // var imageview = ImageView(p0.context)
// var textView = TextView(p0.context)
// // textView.layoutParams
// textView.layoutParams = LinearLayoutCompat.LayoutParams(width,width/3)
// return CustomViewHolder(textView)
// }
//
// inner class CustomViewHolder(var textview: TextView) : RecyclerView.ViewHolder(textview) {
//
// }
//
//
// override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
// var textview = (p0 as CustomViewHolder).textview
// p0.bind(contentDTOs[p1])
// }
//
// override fun getItemCount(): Int {
// System.out.println("========================================size? " + contentDTOs.size)
//
// return contentDTOs.size
// }
// fun bind(contentDTO: ContentDTO)
// {
// binding.post_userID.text = contentDTOs.
// }
// }
// }