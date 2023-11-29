package com.example.museumexplore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.museumexplore.databinding.RecoverPasswordPageBinding
import com.example.museumexplore.databinding.RegisterPageBinding
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordActivity : AppCompatActivity() {
    private lateinit var binding: RecoverPasswordPageBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecoverPasswordPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}