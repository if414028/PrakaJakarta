package com.indigital.prakajakarta.survey

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.pref.PostData
import com.indigital.prakajakarta.databinding.ActivityHouseOwnerInformationBinding

class HouseOwnerInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHouseOwnerInformationBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var sex: String = "PRIA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_owner_information)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermission()) {
            getLocation()
        } else {
            requestPermissions()
        }

        setupLayout()
    }

    private fun setupLayout() {
        binding.btnMale.setOnClickListener {
            sex = "PRIA"
            binding.btnMale.setBackgroundResource(R.drawable.bg_enable_rounded)
            binding.txtMale.setTextColor(resources.getColor(R.color.colorWhite))

            binding.btnFemale.setBackgroundResource(R.drawable.bg_disable_rounded)
            binding.txtFemale.setTextColor(resources.getColor(R.color.colorText))
        }

        binding.btnFemale.setOnClickListener {
            sex = "WANITA"
            binding.btnMale.setBackgroundResource(R.drawable.bg_disable_rounded)
            binding.txtMale.setTextColor(resources.getColor(R.color.colorText))

            binding.btnFemale.setBackgroundResource(R.drawable.bg_enable_rounded)
            binding.txtFemale.setTextColor(resources.getColor(R.color.colorWhite))

        }

        binding.etHouseOwner.addTextChangedListener { validateForm() }
        binding.etPekerjaan.addTextChangedListener { validateForm() }
        binding.etAlamat.addTextChangedListener { validateForm() }
        binding.etUsia.addTextChangedListener { validateForm() }

        binding.btnNext.setOnClickListener { submit() }

        binding.btnGetLocation.setOnClickListener {
            binding.isLoading = true
            getLocation()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (!checkLocationPermission()) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getLocation()
        } else {
            Toast.makeText(
                this,
                "Izin lokasi diperlukan untuk mendapatkan koordinat",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 100000
                fastestInterval = 50000
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Toast.makeText(
                        this@HouseOwnerInformationActivity,
                        "Berhasil mendapatkan lokasi",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.isLoading = false

                    for (location in locationResult.locations) {
                        lat = location.latitude
                        lng = location.longitude
                        binding.tvCoordinate.text = "Lat: $lat Lng: $lng"
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        binding.isLoading = false
                        Toast.makeText(
                            this@HouseOwnerInformationActivity,
                            "Lokasi tidak tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } else {
            binding.isLoading = false
            Toast.makeText(this, "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm() {
        val houseOwner = binding.etHouseOwner.text.toString()
        val pekerjaan = binding.etPekerjaan.text.toString()
        val alamat = binding.etAlamat.text.toString()
        val usia = binding.etUsia.text.toString()

        if (houseOwner.isEmpty() || pekerjaan.isEmpty() || alamat.isEmpty() || usia.isEmpty()) {
            binding.btnNext.isEnabled = false
        } else {
            binding.btnNext.isEnabled = true
        }
    }

    private fun submit() {
        if (lat != 0.0 && lng != 0.0) {
            val houseOwner = binding.etHouseOwner.text.toString()
            val work = binding.etPekerjaan.text.toString()
            val address = binding.etAlamat.text.toString()
            val age = binding.etUsia.text.toString()

            val postData = PostData()
            postData.houseOwner = houseOwner
            postData.lat = lat
            postData.lng = lng
            postData.work = work
            postData.address = address
            postData.age = age
            postData.sex = sex

            val intent = Intent(applicationContext, FirstQuestionActivity::class.java)
            intent.putExtra(FirstQuestionActivity.ARG_POST_DATA, postData)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Gagal mendapatkan lokasi, klik ulang tombol dapatkan lokasi",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}