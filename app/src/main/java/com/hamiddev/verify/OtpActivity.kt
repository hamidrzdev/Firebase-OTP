package com.hamiddev.verify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hamiddev.verify.databinding.ActivityOtpBinding


class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding

    private val mAuth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private lateinit var authCredential: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            authCredential = it.getString("Auth", "")
        }

        binding.verifyBtn.setOnClickListener {

            val otpCode = binding.otpTitle.text

            if (otpCode.isEmpty()) {
                binding.otpFormFeedback.visibility = View.VISIBLE
                binding.otpFormFeedback.text = "کد را وارد کنید"
                return@setOnClickListener
            }

            Toast.makeText(this,authCredential,Toast.LENGTH_SHORT).show()
            Toast.makeText(this,otpCode,Toast.LENGTH_SHORT).show()

            val credential = PhoneAuthProvider.getCredential(authCredential, otpCode.toString())
            signInWithPhoneCredential(credential)
        }

    }

    fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                goHome()
            }.addOnFailureListener {
                binding.otpFormFeedback.text = it.message
            }
    }

    fun goHome() {
        startActivity(Intent(this@OtpActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }

}