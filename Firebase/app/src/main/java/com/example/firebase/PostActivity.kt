package com.example.firebase

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.databinding.ActivityPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class PostActivity : AppCompatActivity() {
    
    var photoUri: Uri? = null
    var nameOfFile: String = ""

    lateinit var storage: FirebaseStorage
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        storage = Firebase.storage

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                // Suppose you have an ImageView that should contain the image:
                binding.postImage.setImageURI(imageUri)
                imageUri.also { photoUri = it }
            }
        }//신규 api 이미지 가져오기

        binding.postImage.setOnClickListener {
            getContent.launch("image/*")//이미지 뷰 클릭시 이미지 가져오기
            //photoUri에 ActivityResultContracts의 imageUri를 가져와야함
           // photoUri = getContent.
        }

        binding.postUploadButton.setOnClickListener {
            contentUpload()//
            upload(photoUri, nameOfFile)
            finish()
        }

        binding.postCancelButton.setOnClickListener {
            finish()//캔슬 버튼누르면 끝냄
        }
    }
    ////////////
    
//업로드 기능

    private fun contentUpload() {//storage에 사진 업로드
        //현재 시간을 String으로 만들기
        var timestamp = System.currentTimeMillis()//ms값 타임스탬프
        val fileName = "IMAGE_$timestamp.png"
        nameOfFile = fileName//전역변수에 파일이름 저장

        //서버 스토리지에 접근
        val storageRef = storage?.reference?.child("images")?.child(fileName)

        // 서버 스토리지에 파일 업로드
        storageRef?.putFile(photoUri!!).addOnCompleteListener {
            if (it.isSuccessful) {
            // upload success
                Toast.makeText(this, "upload success", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun upload(uri: Uri?, fileName: String) {//post의 내용들을 따로 firestore에 저장
        val db: FirebaseFirestore = Firebase.firestore
        val postCollectionRef = db.collection("post")//스토어 post콜렉션
        //contentDto 객체를 생성한다.
        val contentMap = hashMapOf(//포스팅 해시맵
            "imageUrl" to uri.toString(),
            "uid" to Firebase.auth?.currentUser?.uid,
            "userID" to Firebase.auth.currentUser?.email,
            "explain" to binding.postEditText.text.toString(),
            "timestamp" to System.currentTimeMillis().toLong(),
            "imageStorage" to fileName
        )
        postCollectionRef.document(fileName)//파일이름으로 구분, firestore에 포스트내용추가
            .set(contentMap)
        //액티비티 종료
        finish()
    }

//이미지 뷰를 클릭하면 사진을 업로드
//업로드 후 글내용 작성한 뒤, 업로드 버튼을 누르면
// firestore 컬렉션에 글내용과 이메일,uid,타임스탬프 등이 저장됨 ->이미지 이름별로 다큐멘터리 생성됨
// 스토리지에 images/하위에 이미지는 저장됨

}