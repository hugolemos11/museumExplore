package com.example.museumexplore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.museumexplore.databinding.LoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
    }
}