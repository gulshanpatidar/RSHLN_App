package com.example.rshlnapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.ActivityAuthenticationBinding
import com.example.rshlnapp.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.UserDataReader
import com.google.firebase.ktx.Firebase
import okhttp3.internal.Util
import java.util.concurrent.TimeUnit


class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    var number: String = ""
    lateinit var auth: FirebaseAuth
    private val TAG = "AuthenticationActivity"

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        auth = Firebase.auth

        binding.sendOtpButton.setOnClickListener {
            binding.phoneNumberLabelInAuthentication.visibility = View.GONE
            binding.phoneNumberInAuthentication.visibility = View.GONE
            binding.sendOtpButton.visibility = View.GONE
            binding.enterOtpAuthenticationLabel.visibility = View.VISIBLE
            binding.enterOtpAuthentication.visibility = View.VISIBLE
            binding.loginButtonAuthentication.visibility = View.VISIBLE
            binding.resendOtpButtonAuthentication.isEnabled = false
            binding.resendOtpButtonAuthentication.visibility = View.VISIBLE
            binding.resendOtpTimer.visibility = View.VISIBLE
            login()
        }

        binding.resendOtpButtonAuthentication.setOnClickListener {
            resendVerificationCode(number,resendToken)
            binding.resendOtpButtonAuthentication.isEnabled = false
            startCountDown()
        }

        binding.loginButtonAuthentication.setOnClickListener {
            val otp = binding.enterOtpAuthentication.text.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
                Log.d(TAG, "onVerificationCompleted Success")
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG, "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase
//                val intent = Intent(applicationContext,OtpActivity::class.java)
//                intent.putExtra("storedVerificationId",storedVerificationId)
//                startActivity(intent)
//                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        number = binding.phoneNumberInAuthentication.text.trim().toString()

        // get the phone number from edit text and append the country cde with it
        if (number.isNotEmpty()){
            number = "+91$number"
            sendVerificationCode(number)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
        startCountDown()
    }

    // this method sends the verification code
    // and starts the callback of verification
    // which is implemented above in onCreate
    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("GFG" , "Auth started")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    val createdAt = firebaseUser?.metadata?.creationTimestamp
                    val lastSignIn = firebaseUser?.metadata?.lastSignInTimestamp
                    if (createdAt==lastSignIn){
                        //this is a new user
                        val user = User(userId = auth.currentUser!!.uid,username = "Demo User",mobileNumber = number)
                        UserDao().addUser(user)
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        //it is an existing user
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            callbacks,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

    private fun startCountDown(){
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOtpTimer.setText("Resend OTP after: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.resendOtpTimer.setText("")
                binding.resendOtpButtonAuthentication.isEnabled = true
            }
        }.start()
    }

    override fun onBackPressed() {
        if (binding.enterOtpAuthentication.visibility==View.VISIBLE){
            //hide the current views
            binding.enterOtpAuthentication.visibility = View.GONE
            binding.enterOtpAuthenticationLabel.visibility = View.GONE
            binding.loginButtonAuthentication.visibility = View.GONE
            binding.resendOtpTimer.visibility = View.GONE
            binding.resendOtpButtonAuthentication.visibility =  View.GONE
            //show the previous views
            binding.phoneNumberLabelInAuthentication.visibility = View.VISIBLE
            binding.phoneNumberInAuthentication.visibility = View.VISIBLE
            binding.sendOtpButton.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

}