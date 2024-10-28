package com.indigital.prakajakarta.survey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.post.detail.ReportDataDetailResult
import com.indigital.prakajakarta.data.pref.PrefManager
import com.indigital.prakajakarta.databinding.ActivityDetailSurveyBinding
import com.indigital.prakajakarta.network.PrakaJakartaAPI
import com.indigital.prakajakarta.network.api.ApiPostDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailSurveyActivity : AppCompatActivity() {

    companion object {
        const val ARG_SURVEY_ID = "surveyId"
    }

    private lateinit var binding: ActivityDetailSurveyBinding

    private var surveyId = ""
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_survey)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        getAdditionalData()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { map ->
            googleMap = map
            getPostDetail()
        }
    }

    private fun getAdditionalData() {
        surveyId = intent.getStringExtra(ARG_SURVEY_ID).toString()
    }

    private fun getPostDetail() {
        val tokenModel = PrefManager.getTokenData(applicationContext)
        val token = tokenModel.token
        val service = PrakaJakartaAPI.createService(ApiPostDetail::class.java)
        val call = token?.let { service.getPostDetail(it, surveyId) }

        call?.enqueue(object : Callback<ReportDataDetailResult> {
            override fun onResponse(
                call: Call<ReportDataDetailResult>,
                response: Response<ReportDataDetailResult>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        binding.tvAddress1.text = data.address1
                        binding.tvAddress2.text = data.address2
                        binding.tvHouseOwnerMap.text = data.name
                        binding.tvAnswer1.text = if (data.answer1 == "1") "Ya" else "Tidak"
                        binding.tvAnswer2.text = if (data.answer2 == "1") "Ya" else "Tidak"
                        val lat = data.lat?.toDoubleOrNull()
                        val lng = data.lng?.toDoubleOrNull()

                        if (lat != null && lng != null) {
                            val markerPosition = LatLng(lat, lng)
                            googleMap.addMarker(
                                MarkerOptions().position(markerPosition).title(data.name)
                            )
                            val padding = 400
                            googleMap.setPadding(0, 0, 0, padding)

                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    markerPosition,
                                    15f,
                                )
                            )
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Koordinat tidak valid",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Snackbar.make(binding.root, "Error: ${response.code()}", Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ReportDataDetailResult>, t: Throwable) {
                Snackbar.make(
                    binding.root,
                    "Gagal mengambil data: ${t.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}