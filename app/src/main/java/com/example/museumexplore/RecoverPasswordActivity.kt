package com.example.museumexplore

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.museumexplore.databinding.RecoverPasswordPageBinding
import com.example.museumexplore.databinding.RegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverPasswordActivity : AppCompatActivity() {
    private lateinit var binding: RecoverPasswordPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecoverPasswordPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recoverPasswordButton.setOnClickListener {
            finish()
        }

        binding.editTextEmailAddress.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,  "Email Sent Successfully", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Email sent.")
                    } else {
                        Toast.makeText(this,  "Could Not Send Email", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}