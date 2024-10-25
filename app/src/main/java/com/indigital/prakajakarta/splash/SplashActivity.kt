package com.indigital.prakajakarta.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.indigital.prakajakarta.MainActivity
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.Token
import com.indigital.prakajakarta.data.model.user.UserCheckLogin
import com.indigital.prakajakarta.data.pref.PrefManager
import com.indigital.prakajakarta.databinding.ActivitySplashBinding
import com.indigital.prakajakarta.login.LoginActivity
import com.indigital.prakajakarta.network.PrakaJakartaAPI
import com.indigital.prakajakarta.network.api.ApiCheckLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        Handler().postDelayed({
            checkLogin()
        }, 1000)

    }

    private fun checkLogin() {
        val tokenModel: Token = PrefManager.getTokenData(applicationContext)
        val token = PrefManager.getTokenData(applicationContext).token

        if (token.isNullOrEmpty()) {
            // Jika token null atau kosong, arahkan ke LoginActivity
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val splitToken = token.split("\\s+".toRegex()).toTypedArray()
            val tokenResult = splitToken.getOrNull(1)?.trim()

            if (tokenResult != null) {
                val service = PrakaJakartaAPI.createService(ApiCheckLogin::class.java)
                val call = service.checkLogin(tokenResult)
                call.enqueue(object : Callback<UserCheckLogin> {
                    override fun onResponse(
                        call: Call<UserCheckLogin>,
                        response: Response<UserCheckLogin>
                    ) {
                        val statusLogin = response.code().toString()
                        if (statusLogin == "200") {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Pastikan finish untuk mengakhiri activity ini
                        } else {
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()  // Tutup activity setelah diarahkan ke login
                        }
                    }

                    override fun onFailure(call: Call<UserCheckLogin>, t: Throwable) {
                        Toast.makeText(
                            this@SplashActivity,
                            "Failed to connect to the server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                // Jika tokenResult null, arahkan ke LoginActivity
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}