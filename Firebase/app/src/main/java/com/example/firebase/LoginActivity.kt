package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {// 로그인버튼 "Login"
            val userEmail = binding.email.text.toString()
            val password = binding.password.text.toString()
            doLogin(userEmail, password)
        }
        binding.signup.setOnClickListener {//텍스트 "sing up"
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
            finish()
        }
    }


    private fun doLogin(userEmail: String, password: String) {
// … 다음 슬라이드에서 …
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    var lastTimeBackPressed : Long = 0
    override fun onBackPressed() {
        if (Firebase.auth.currentUser == null) {//로그인 안된 상태일때, 그냥 어플 종료
            if(System.currentTimeMillis() - lastTimeBackPressed >= 2000){
                lastTimeBackPressed = System.currentTimeMillis()
                Toast.makeText(this,"한 번 더 이전 버튼을 누르면 종료합니다.",Toast.LENGTH_LONG).show()
            } else {
                finish()
            }
        }else{
        startActivity(Intent(this, MainActivity::class.java))//추후 PBL페이지 2번
        finish()
        }
    }
}
