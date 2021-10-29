package com.example.cheaper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LandingActivity : AppCompatActivity() {

    // this stores the phone number of the user
    var number : String =""

    // create instance of firebase auth
    lateinit var auth: FirebaseAuth

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val tag = "Login log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        auth=FirebaseAuth.getInstance()
        //auth.useEmulator("localhost",9099)
        // start verification on click of the button
        findViewById<Button>(R.id.button_otp).setOnClickListener {
            login()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(tag , "onVerificationCompleted Success")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(tag , "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(tag,"onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
                //this@LandingActivity.enableUserManuallyInputCode()


                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase
                val intent = Intent(applicationContext,VerificacionActivity::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intent)
                finish()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d(tag,"Timeout, verification ID: $verificationId")
            }
        }
    }

    private fun login() {
        number = findViewById<EditText>(R.id.et_phone_number).text.trim().toString()

        // get the phone number from edit text and append the country cde with it
        if (number.isNotEmpty()){
            //number = "+50687204959"
            number = "+1 650-555-1234"
            val code = "123456"

            //auth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(number, code)
            //number = "$number"
            sendVerificationCode(number)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    // this method sends the verification code
    // and starts the callback of verification
    // which is implemented above in onCreate
    private fun sendVerificationCode(number: String) {
        Log.d(tag , "Phone number $number")
        // Force reCAPTCHA flow
        //auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true)


        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(3, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d(tag, "Auth started")
    }
}