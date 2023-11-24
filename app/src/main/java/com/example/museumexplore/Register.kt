package com.example.museumexplore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.museumexplore.R
import com.example.museumexplore.databinding.RegisterPageBinding

class Register : AppCompatActivity() {
    private lateinit var binding: RegisterPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}