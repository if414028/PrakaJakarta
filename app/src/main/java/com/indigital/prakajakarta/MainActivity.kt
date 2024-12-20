package com.indigital.prakajakarta

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.indigital.prakajakarta.data.model.Token
import com.indigital.prakajakarta.data.model.post.create.CreatePostRequest
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
import com.indigital.prakajakarta.util.SimpleRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SimpleRecyclerAdapter<Report>

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.colorWhite))

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Rekap Laporan"

        setupLayout()
        getReportList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.item1 -> {
                getReportList()
                return true
            }

            R.id.item2 -> {
                getFilteredReportList("Day")
                return true
            }

            R.id.item3 -> {
                getFilteredReportList("Week")
                return true
            }

            R.id.item4 -> {
                getFilteredReportList("Month")
                return true
            }

            R.id.item5 -> {
                val tokenObject = Token().apply { token = "" }
                PrefManager.setToken(applicationContext, tokenObject)
                finish()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false

    }

    private fun setupLayout() {
        setupRecyclerView()

        binding.btnCreateSurvey.setOnClickListener {
            createSurvey()
        }

        binding.swipeRefreshLayout.setOnRefreshListener { getReportList() }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            exitProcess(0)
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

    private fun createSurvey() {
        binding.progress.visibility = View.VISIBLE
        val tokenModel = PrefManager.getTokenData(applicationContext)
        val token = tokenModel.token ?: ""

        val service = PrakaJakartaAPI.createService(ApiPostList::class.java)
        val call = service.getPostFiltered(token, 30, "id", "DESC", "Day")
        call.enqueue(object : Callback<ReportDataResult> {
            override fun onResponse(
                call: Call<ReportDataResult>,
                response: Response<ReportDataResult>
            ) {
                val code = response.code().toString()

                when (code) {
                    "200" -> {
                        binding.progress.visibility = View.GONE
                        val result = response.body()
                        val data = result?.data

                        if (data != null) {
                            if (data.count < 20) {
                                val intent = Intent(
                                    applicationContext,
                                    HouseOwnerInformationActivity::class.java
                                )
                                startActivity(intent)
                            } else {
                                Snackbar.make(
                                    binding.root,
                                    "Anda hanya bisa membuat survei 20 kali sehari.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Data survei tidak tersedia.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

                    "401" -> {
                        binding.progress.visibility = View.GONE
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
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

            override fun onFailure(call: Call<ReportDataResult>, t: Throwable) {
                binding.progress.visibility = View.GONE
                Snackbar.make(
                    binding.root,
                    "Anda tidak terhubung dengan internet, Silahkan coba lagi",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getFilteredReportList(filter: String) {
        binding.progress.visibility = View.VISIBLE
        val tokenModel = PrefManager.getTokenData(applicationContext)
        val token = tokenModel.token ?: ""

        val service = PrakaJakartaAPI.createService(ApiPostList::class.java)
        val call = service.getPostFiltered(token, 30, "id", "DESC", filter)
        call.enqueue(object : Callback<ReportDataResult> {
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
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
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

            override fun onFailure(call: Call<ReportDataResult>, t: Throwable) {
                binding.progress.visibility = View.GONE
                Snackbar.make(
                    binding.root,
                    "Anda tidak terhubung dengan internet, Silahkan coba lagi",
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