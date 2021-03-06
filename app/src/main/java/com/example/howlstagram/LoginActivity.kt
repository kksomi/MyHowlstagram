package com.example.howlstagram

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001 //request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener{
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener{
            //First step
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("782942169003-brsh51pdkgbd11mtp641462bk9llghbh.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }
    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){ //
                var account = result?.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{ //회원가입한 결과값 가져옴
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                } else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener{ //회원가입한 결과값 가져옴
            task ->
                if(task.isSuccessful){
                    //아이디가 생성되었을 때(회원가입)
                    moveMainPage(task.result?.user)
                } else if(task.exception?.message.isNullOrEmpty()) {
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    //로그인
                    signinEmail()
                }
        }
    }
    fun signinEmail(){ //로그인
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener{ //회원가입한 결과값 가져옴
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                } else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user: FirebaseUser?){ //파이어베이스 유저상태
        if(user != null){ //유저상태 존재
            startActivity(Intent( this,MainActivity::class.java)) //메인페이지로
        }
    }
}