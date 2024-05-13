package com.example.habittracker2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getViews()
        setListeners()
    }

    private fun getViews(){
        nameInput = findViewById(R.id.register_name_input)
        emailInput = findViewById(R.id.register_email_input)
        passwordInput = findViewById(R.id.register_password_input)
        registerButton = findViewById(R.id.register_button)
    }

    private fun setListeners(){
        registerButton.setOnClickListener{
            run{
                registerButton.isEnabled = false
                tryRegister()
            }
        }
    }

    private fun tryRegister(){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val name = nameInput.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                run{
                    val request = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    it.user!!.updateProfile(request)

                    val data = mapOf(
                        "email" to email,
                        "name" to name,
                        "age" to 21
                    )

                    db.collection("users")
                        .document(it.user!!.uid)
                        .set(data)
                        .addOnSuccessListener{
                            run{
                                Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            run{
                                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                                registerButton.isEnabled = true

                            }
                        }
                }
            }
            .addOnFailureListener {
                run{
                    Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                    registerButton.isEnabled = true

                }
            }
    }
}