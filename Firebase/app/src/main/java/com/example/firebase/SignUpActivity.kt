package com.example.firebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebase.databinding.ActivityPostBinding
import com.example.firebase.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SignUpActivity : AppCompatActivity() {

    var photoUri: Uri? = null
    var nameOfFile: String = ""
    var varEmail: String = ""

    lateinit var storage: FirebaseStorage
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        storage = Firebase.storage

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                // Suppose you have an ImageView that should contain the image:
                binding.signupImage.setImageURI(imageUri)
                imageUri.also { photoUri = it }
            }
        }//신규 api 이미지 가져오기

        binding.signupImage.setOnClickListener {
            getContent.launch("image/*")//이미지 뷰 클릭시 이미지 가져오기
            //photoUri에 ActivityResultContracts의 imageUri를 가져와야함
            // photoUri = getContent.
        }//이미지뷰 누르면 프로필사진 설정 crop기능도 있다는데 추가할 의향있음.

        binding.create.setOnClickListener {// 회원가입화S면 생성 "create account"
            val db: FirebaseFirestore = Firebase.firestore //콜렉션
            val usersCollectionRef = db.collection("users")//users

            val name = binding.name.text.toString()
            val birth = binding.birth.text.toString()
            val email = binding.signUpEmail.text.toString() //email, 중복구분, 컬렉션-도큐먼트 구분
            val password = binding.signUpPW.text.toString()
            val confirmPW = binding.confirmPW.text.toString()//비밀번호 체크
            val userID = binding.userID.text.toString()// 유저 아이디 [닉네임개념]
            //val uid = Firebase.auth.currentUser?.uid //id생성전엔 uid를 불러올 수 없었음. 컬렉션은 email로 구분.

            doSignUp(email, password)//아이디 생성 // 비밀번호 확인 기능추가 // 중복체크 추가
            contentUpload()
            //스토어(profile/) 업로드 //파일이름으로 구분 = users콜렉션 이메일 도큐먼트의 imageStorage 필드로 구분.

            val usersMap = hashMapOf(//회원가입 해시맵
                "profileUri" to photoUri.toString(),
                "name" to name,
                "birth" to birth,
                "email" to email,
                "password" to password,
                "userID" to userID,
                "imageStorage" to nameOfFile
            )
            usersCollectionRef
                .document(email)//이메일로 구분
                .set(usersMap)

            finish()
        }

        binding.signupImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.signIn.setOnClickListener{
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }

    //회원가입 기능
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


    //프로필 사진 업로드 기능
    private fun contentUpload() {//storage에 프로필 사진 업로드
        //현재 시간을 String으로 만들기
        var timestamp = System.currentTimeMillis()//ms값 타임스탬프
        val fileName = "PROFILE_$timestamp.png" // 이름 다음 주의
        nameOfFile = fileName//전역변수에 파일이름 저장

        //서버 스토리지에 접근
        val storageRef = storage?.reference?.child("profile")?.child(nameOfFile)

        // 서버 스토리지에 파일 업로드
        storageRef?.putFile(photoUri!!).addOnCompleteListener {
            if (it.isSuccessful) {
                // upload success
                Toast.makeText(this, "sign up success", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}