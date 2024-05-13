package com.example.habittracker2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getViews()
        setListeners()
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getViews(){
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        createAccountButton = findViewById(R.id.create_account_tvbutton)
    }

    private fun setListeners(){
        loginButton.setOnClickListener{
            run{
                tryLogin()
            }
        }
        createAccountButton.setOnClickListener{
            run{
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun tryLogin(){
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener{
            run{
                Toast.makeText(this@MainActivity, "Signed in successfully", Toast.LENGTH_SHORT).show()
                loginButton.isEnabled = true
                val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                startActivity(intent)
            }
        }.addOnFailureListener{
            run{
                Toast.makeText(this@MainActivity, "Sign in failed", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
                loginButton.isEnabled = true
            }
        }
    }

}