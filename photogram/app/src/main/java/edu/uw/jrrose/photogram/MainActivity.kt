package edu.uw.jrrose.photogram

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val RC_SIGN_IN = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.login_btn -> {
//                Toast.makeText(this, "logged in", Toast.LENGTH_LONG).show()
                login()
                true
            }
            R.id.logout_btn -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun login() {
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    invalidateOptionsMenu()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "$user logged in", Toast.LENGTH_LONG).show()
                invalidateOptionsMenu()
            } else {
                if (response == null) {
                    Log.v(TAG, "User cancelled sign-in")
                } else {
                    Log.v(TAG, response.error.toString())
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        val btn = menu!!.findItem(R.id.login_btn)
        if (FirebaseAuth.getInstance().currentUser != null) {
//            btn.title = "logout"
//            val logoutUser = menu.findItem(R.id.logout_btn)
//            logoutUser.isVisible = true
            menu!!.removeItem(R.id.login_btn)
//            btn.setOnMenuItemClickListener {
//                logout()
//                true
//            }
        } else {
//            btn.title = "login"
            menu!!.removeItem(R.id.logout_btn)
        }
        return super.onPrepareOptionsMenu(menu)
    }
}