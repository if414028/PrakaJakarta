package com.indigital.prakajakarta

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.indigital.prakajakarta.data.model.post.list.Report
import com.indigital.prakajakarta.data.model.post.list.ReportDataResult
import com.indigital.prakajakarta.data.pref.PrefManager
import com.indigital.prakajakarta.databinding.ActivityMainBinding
import com.indigital.prakajakarta.databinding.ItemReportBinding
import com.indigital.prakajakarta.login.LoginActivity
import com.indigital.prakajakarta.network.PrakaJakartaAPI
import com.indigital.prakajakarta.network.api.ApiPostList
import com.indigital.prakajakarta.survey.DetailSurveyActivity
import com.indigital.prakajakarta.survey.HouseOwnerInformationActivity
import com.indigital.prakajakarta.survey.ReviewSurveyActivity
import com.indigital.prakajakarta.ui.theme.PrakaJakartaTheme
import com.indigital.prakajakarta.util.SimpleRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SimpleRecyclerAdapter<Report>

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        setupLayout()
        getReportList()
    }

    private fun setupLayout() {
        setupRecyclerView()

        binding.btnCreateSurvey.setOnClickListener {
            val intent = Intent(applicationContext, HouseOwnerInformationActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener { getReportList() }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            exitProcess(0) // use exitProcess from kotlin.system package to terminate the app
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun getReportList() {
        binding.progress.visibility = View.VISIBLE
        val tokenModel = PrefManager.getTokenData(applicationContext)
        val token = tokenModel.token

        val service = PrakaJakartaAPI.createService(ApiPostList::class.java)
        val call = token?.let { service.getPostList(it, 30, "id", "DESC") }
        call?.enqueue(object : Callback<ReportDataResult> {
            override fun onResponse(
                call: Call<ReportDataResult>,
                response: Response<ReportDataResult>
            ) {
                val code = response.code().toString()

                when (code) {
                    "200" -> {
                        binding.progress.visibility = View.GONE
                        val result = response.body()

                        if (result?.data?.rows.isNullOrEmpty()) {
                            binding.lyEmptyData.visibility = View.VISIBLE
                            binding.rvSurvey.visibility = View.GONE
                        } else {
                            binding.lyEmptyData.visibility = View.GONE
                            binding.rvSurvey.visibility = View.VISIBLE
                            adapter.mainData = result?.data?.rows ?: listOf()
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    "401" -> {
                        binding.progress.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    "400" -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Username atau password tidak boleh kosong, silahkan coba lagi.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Sedang ada gangguan server, silahkan coba lagi.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ReportDataResult>, t: Throwable) {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progress.visibility = View.GONE
                Snackbar.make(
                    binding.root,
                    "Sedang ada gangguan server, silahkan coba lagi.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvSurvey.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = SimpleRecyclerAdapter(
            arrayListOf(),
            R.layout.item_report
        ) { holder, item ->
            val itemBinding: ItemReportBinding = holder?.layoutBinding as ItemReportBinding

            val dateResult = item?.createdAt?.length?.coerceAtMost(10)
                ?.let { item.createdAt?.substring(0, it) }


            itemBinding.tvItemHouseOwner.text = item?.name ?: ""
            itemBinding.tvItemDate.text = dateResult

            itemBinding.root.setOnClickListener {
                val intent = Intent(applicationContext, DetailSurveyActivity::class.java)
                intent.putExtra(DetailSurveyActivity.ARG_SURVEY_ID, item.id)
                startActivity(intent)
            }
        }
        binding.rvSurvey.adapter = adapter
    }

}