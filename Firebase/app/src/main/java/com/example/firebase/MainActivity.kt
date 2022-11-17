package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(){

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val db: FirebaseFirestore = Firebase.firestore //콜렉션
    val usersCollectionRef = db.collection("users")//users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Firebase.auth.currentUser == null) {//로그인 안된 상태
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()//메인액티비티 종료
        }

        val userEmail = Firebase.auth.currentUser?.email ?: "No user" // 현재 유저의 이메일 //getEmail()

        setupBottomNavigationonView()

        //binding.mainEmail.text = userEmail
        /* firestore 정보
        usersCollectionRef.document(userEmail).get()
            .addOnSuccessListener { // it: DocumentSnapshot
                binding.mainUserID.setText(it["userID"].toString())
                binding.mainEmail.setText(it["email"].toString())
                binding.mainName.setText(it["name"].toString())
            }
        */

        /* 로그아웃 기능
        binding.buttonSignout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(//로그아웃 후 로그인 액티비티로 이동
                Intent(this, LoginActivity::class.java)
            )
            finish()//메인액티비티 종료
        }
        */

    }

    private fun setupBottomNavigationonView() {
        binding.bottomNav.setOnItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, HomeFragment())
                        .commit()
                    true
                }
                R.id.action_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, SearchFragment())
                        .commit()
                    true
                }
                R.id.action_post -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, PostFragment())
                        .commit()
                    true
                }
                R.id.action_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, ProfileFragment())
                        .commit()
                    true
                }
                R.id.action_menu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, MenuFragment())
                        .commit()
                    true
                }
                else -> false
            }

        }
    }

    /* 콜렉션 도큐멘터리 관련 메모
    val db: FirebaseFirestore = Firebase.firestore //콜렉션
    val usersCollectionRef = db.collection("users") // users 콜렉션

    --> usersCollectionRef.document(ID) = 도큐먼트 레퍼런스 --> 도큐먼트 = uid(Firebase.auth.currentUser?.uid ?: "No User") 로그인한 유저의 uid와 연결

    db.collection("test")
        .document("ID1")
        .collection("inventory")
        .document("ID1").get().addOnSuccessListener {
        Log.d(TAG, "${it.id}, ${it["name"]}, ${it["quantity"]}")
        }
        둘이 동일
    db.document("/test/ID1/inventory/ID1").get().addOnSuccessListener {
        Log.d(TAG, "${it.id}, ${it["name"]}, ${it["quantity"]}")
    }
     */



    var lastTimeBackPressed : Long = 0
    override fun onBackPressed() {//뒤로 가기 버튼을 시간내로 두번 누르면 종료되는 backpressed
        if(System.currentTimeMillis() - lastTimeBackPressed >= 2000){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"한 번 더 이전 버튼을 누르면 종료합니다.",Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }



}



