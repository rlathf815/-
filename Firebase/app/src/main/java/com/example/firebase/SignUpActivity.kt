package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebase.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.create.setOnClickListener {// 회원가입화면 생성 "create account"
            val db: FirebaseFirestore = Firebase.firestore //콜렉션
            val usersCollectionRef = db.collection("users")//users

            val name = binding.name.text.toString()
            val birth = binding.birth.text.toString()
            val email = binding.signUpEmail.text.toString() //email, 중복구분, 컬렉션-도큐먼트 구분
            val password = binding.signUpPW.text.toString()
            val confirmPW = binding.confirmPW.text.toString()//비밀번호 체크
            val userID = binding.userID.text.toString()// 유저 아이디 [닉네임개념]

            //val uid = Firebase.auth.currentUser?.uid //id생성전엔 uid를 불러올 수 없었음. 컬렉션은 userID로 구분.

            val usersMap = hashMapOf(//회원가입 해시맵
                "name" to name,
                "birth" to birth,
                "email" to email,
                "password" to password,
                "userID" to userID
            )

            doSignUp(email, password)//아이디 생성 후, 개인정보를 firestore // 비밀번호 확인 기능추가 // 중복체크 추가

                usersCollectionRef.document(email)//이메일로 구분
                    .set(usersMap)

        }

        binding.signIn.setOnClickListener{
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }


    private fun doSignUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword( email , password )
            .addOnCompleteListener(this){
                if (it.isSuccessful) {
                    Log.w("SignUpActivity", "createUserWithEmail", it.exception)
                    Toast.makeText(this, "Authentication success.", Toast.LENGTH_SHORT).show()
                    try {
                        it.result
                    } catch (e: Exception) { // 임시 이메일 중복 // toast가 안뜸
                        e.printStackTrace()
                        Toast.makeText(this, "이미 있는 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w("SignUpActivity", "createUserWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}