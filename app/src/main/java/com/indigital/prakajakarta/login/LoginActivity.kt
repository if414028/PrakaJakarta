package com.indigital.prakajakarta.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.indigital.prakajakarta.MainActivity
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.Token
import com.indigital.prakajakarta.data.model.user.UserDataResult
import com.indigital.prakajakarta.data.pref.PrefManager
import com.indigital.prakajakarta.databinding.ActivityLoginBinding
import com.indigital.prakajakarta.network.PrakaJakartaAPI
import com.indigital.prakajakarta.network.api.ApiLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val CAMERA_PERMISSION_CODE = 100
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setupLayout()
        requestPermissions()
    }

    private fun setupLayout() {
        setPasswordTextVisibility()
        binding.btnLogin.setOnClickListener { doLogin() }
    }

    private fun setPasswordTextVisibility() {
        binding.imgShowPassword.setOnClickListener {
            val inputType = binding.etPassword.inputType
            if (inputType == 129) {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT
                binding.imgShowPassword.setImageResource(R.drawable.ic_seen)
            } else {
                binding.etPassword.inputType = 129
                binding.imgShowPassword.setImageResource(R.drawable.ic_blind)
            }
            binding.etPassword.setSelection(binding.etPassword.length())
        }
    }

    private fun doLogin() {
        binding.progress.visibility = View.VISIBLE
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        val service = PrakaJakartaAPI.createService(ApiLogin::class.java)
        val call = service.login(username, password)

        call.enqueue(object : Callback<UserDataResult> {
            override fun onResponse(
                call: Call<UserDataResult>,
                response: Response<UserDataResult>
            ) {
                val code = response.code().toString()

                when (code) {
                    "200" -> {
                        val tokenData = response.headers()["authorization"]
                        val splited = tokenData?.split("\\s+".toRegex())?.toTypedArray()
                        val token = "Bearer ${splited?.get(1)?.trim()}"

                        val tokenObject = Token()
                        tokenObject.token = token

                        PrefManager.setToken(applicationContext, tokenObject)

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        binding.progress.visibility = View.GONE
                    }

                    "401" -> {
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Username atau password salah, silahkan coba lagi",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    "400" -> {
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Username atau password tidak boleh kosong, silahkan coba lagi.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Sedang ada gangguan server, silahkan coba lagi.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserDataResult>, t: Throwable) {
                binding.progress.visibility = View.GONE
                Snackbar.make(
                    binding.root,
                    "Anda tidak tersambung dengan internet",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { permission ->
            when (permission.key) {
                Manifest.permission.CAMERA -> {
                    if (permission.value) {
                        Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
                    }
                }

                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (permission.value) {
                        Toast.makeText(this, "Izin lokasi diberikan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}