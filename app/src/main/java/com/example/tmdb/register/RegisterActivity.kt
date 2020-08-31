package com.example.tmdb.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tmdb.R
import com.example.tmdb.main.MainActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuthentication: FirebaseAuth
    private lateinit var fireStoreDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuthentication = FirebaseAuth.getInstance()
        fireStoreDatabase = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener {
            submit()
        }
    }

    private fun submit(){
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if(!inputsEmpty(email, username, password) && doPasswordsMatch(password, confirmPassword)){
            signUpButton.isEnabled = false
            createUser(email, password)
        }
    }

    private fun inputsEmpty(email: String, username: String, password: String): Boolean{
        return when {
            email.isEmpty() -> {
                emailEditText.error = "Email can't be empty"
                true
            }
            username.isEmpty() -> {
                usernameEditText.error = "Username can't be empty"
                true
            }
            password.isEmpty() -> {
                passwordEditText.error = "Password can't be empty"
                true
            }

            else -> false
        }
    }

    private fun doPasswordsMatch(password: String, confirmPassword: String): Boolean{
        return if (password == confirmPassword){
            true
        } else{
            confirmPasswordEditText.error = "Passwords don't match"
            false
        }
    }

    private fun createUser(email: String, password: String){
        firebaseAuthentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                handleCompletedRegistration(it)
            }.addOnFailureListener {
                handleFailedRegistration(it)
            }
    }

    private fun handleCompletedRegistration(task: Task<AuthResult>){
        if(task.isSuccessful){
            setCurrentUser(firebaseAuthentication.currentUser!!.uid)
            insertUserDataInFirestore()
            goToApp()
        }
    }

    private fun setCurrentUser(userId: String){
        val sharedPreferences = getSharedPreferences("user", 0)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }


    private fun insertUserDataInFirestore(){
        val documentReference = fireStoreDatabase.collection("users").document(firebaseAuthentication.currentUser!!.uid)
        val user: HashMap<String, Any> = hashMapOf()
        user["username"] = usernameEditText.text.toString()
        documentReference.set(user)
    }

    private fun goToApp(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun handleFailedRegistration(exception: Exception){
        signUpButton.isEnabled = true
        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
    }
}