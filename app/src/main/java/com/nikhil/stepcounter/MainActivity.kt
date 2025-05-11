package com.nikhil.stepcounter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.nikhil.stepcounter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var auth:FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnDontsignup.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            val email=binding.etemail2.text.toString()
            val password=binding.etpsswrd2.text.toString()
            auth.signInWithEmailAndPassword(
                email,
                password
            ).addOnSuccessListener {
                startActivity(Intent(this,HostActivity::class.java))
                Toast.makeText(this, "successfull", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()
            }

        }
    }
}