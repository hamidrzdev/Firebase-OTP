package com.hamiddev.verify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hamiddev.verify.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private val mAuth: FirebaseAuth by lazy {
        Firebase.auth
    }

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginFormFeedback.text = ""

        mAuth.setLanguageCode("fa")

        binding.generateBtn.setOnClickListener {
            val completePhone = "+${binding.countryCodeText.text}${binding.phoneNumberText.text}"

            if (!checkPhoneNumber() || !checkCountryCode())
                return@setOnClickListener


            val option = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(completePhone)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callBacks())
                .build()

            PhoneAuthProvider.verifyPhoneNumber(option)
        }

    }

    private fun callBacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Log.e(TAG, "onVerificationFailed: ${exception.message}")
            binding.loginFormFeedback.text = "احراز هویت با خطا روبرو شد. دوباره امتحان کنید";
            binding.loginFormFeedback.visibility = View.VISIBLE;
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            startActivity(Intent(this@LoginActivity, OtpActivity::class.java).apply {
                putExtra("Auth", p0)
            })
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    goHome()
                else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.loginFormFeedback.visibility = View.VISIBLE
                        binding.loginFormFeedback.text = "خطایی در احراز هویت رخ داد"
                    }
                }
            }
    }

    private fun checkPhoneNumber(): Boolean {
        if (binding.phoneNumberText.text.isEmpty() && binding.phoneNumberText.text.length != 10) {
            with(binding.loginFormFeedback) {
                visibility = View.VISIBLE
                text = "شماره وارد شده اشتباه میباشد"
            }
            return false
        }
        return true
    }

    private fun checkCountryCode(): Boolean {
        if (binding.countryCodeText.text.isEmpty() && binding.countryCodeText.text.length < 2) {
            with(binding.loginFormFeedback) {
                visibility = View.VISIBLE
                text = "کد وارد شده اشتباه میباشد"
            }
            return false
        }
        return true
    }

    private fun goHome() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }
}