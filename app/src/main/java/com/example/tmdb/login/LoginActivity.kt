package com.example.tmdb.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tmdb.R
import com.example.tmdb.main.MainActivity
import com.example.tmdb.register.RegisterActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.emailEditText
import kotlinx.android.synthetic.main.activity_login.passwordEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebase = FirebaseAuth.getInstance()
        if(firebase.currentUser != null){
            setCurrentUser(firebase.currentUser!!.uid)
            goToApp()
        }

        setLoginButtonListener()
        setAnonymousTextListener()
        setRegisterTextListener()
    }

    private fun setCurrentUser(userId: String){
        val sharedPreferences = getSharedPreferences("user", 0)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }

    private fun goToApp(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setLoginButtonListener(){
        loginButton.setOnClickListener {
            validateLogin()
        }
    }

    private fun validateLogin(){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(!areInputsEmpty(email, password)){
            loginButton.isEnabled = false
            firebase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    handleSuccessfulLogin(it)
                }
                .addOnFailureListener {
                    handleFailedLogin(it)
                }
        }
    }

    private fun areInputsEmpty(email: String, password: String): Boolean{
        return when {
            email.isEmpty() -> {
                emailEditText.error = "Email can't be empty"
                true
            }
            password.isEmpty() -> {
                passwordEditText.error = "Password can't be empty"
                true
            }

            else -> false
        }
    }

    private fun handleSuccessfulLogin(task: Task<AuthResult>){
        if(task.isSuccessful){
            setCurrentUser(firebase.currentUser!!.uid)
            goToApp()
        }
    }

    private fun handleFailedLogin(exception: Exception){
        loginButton.isEnabled = true
        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
    }

    private fun setAnonymousTextListener(){
        anonymouslyText.setOnClickListener {
            setCurrentUser("local")
            goToApp()
        }
    }

    private fun setRegisterTextListener() {
        noAccountText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}