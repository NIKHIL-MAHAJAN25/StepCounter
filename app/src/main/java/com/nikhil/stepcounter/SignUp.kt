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
import com.google.firebase.firestore.firestore
import com.nikhil.stepcounter.databinding.ActivitySignUpBinding
import com.nikhil.stepcounter.dataclasses.User

class SignUp : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db=Firebase.firestore
    var collectioname="Users"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnAlrsignin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.btnSignup25.setOnClickListener {
            val email=binding.etupemail.text.toString()
            val password=binding.etuppsswrd.text.toString()
            val name=binding.etname.text.toString()
            val age=binding.etage.text.toString().toIntOrNull()
            val weight=binding.etweight.text.toString().toIntOrNull()
            val gender=when{
                binding.radioMale.isChecked->1
                binding.radioFemale.isChecked->2
                else->0
            }

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{
                val uid=auth.currentUser?.uid
                val user= User(authid = uid,name=name,age=age,gender=gender,weight=weight)
                if (uid != null) {
                    db.collection("Users").document(uid).set(user).addOnSuccessListener {
                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,HostActivity::class.java))
                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                }



            }.addOnFailureListener {
                Toast.makeText(this, "Signup failure", Toast.LENGTH_SHORT).show()
            }
        }
    }
}