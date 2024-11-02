package com.indigital.prakajakarta.survey

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.indigital.prakajakarta.MainActivity
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.post.create.CreatePostRequest
import com.indigital.prakajakarta.data.model.post.create.ImageUpload
import com.indigital.prakajakarta.data.model.post.create.PostDataResult
import com.indigital.prakajakarta.data.model.pref.PostData
import com.indigital.prakajakarta.data.pref.PrefManager
import com.indigital.prakajakarta.databinding.ActivityReviewSurveyBinding
import com.indigital.prakajakarta.login.LoginActivity
import com.indigital.prakajakarta.network.PrakaJakartaAPI
import com.indigital.prakajakarta.network.api.ApiCreatePost
import com.indigital.prakajakarta.network.api.ApiImageUpload
import com.indigital.prakajakarta.util.ResultHolder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReviewSurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewSurveyBinding

    private var postData = PostData()
    private var imageUrl: String? = null

    companion object {
        const val ARG_POST_DATA = "postData"
        const val ARG_SUCCESS = "success"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_survey)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        supportActionBar?.hide()

        getAdditionalData()
        setupLayout()
    }

    private fun getAdditionalData() {
        postData = intent.getParcelableExtra(SecondQuestionActivity.ARG_POST_DATA)!!
    }

    private fun setupLayout() {
        binding.model = postData
        binding.tvFirstAnswer.text = if (postData.firstAnswer == "1") "Ya" else "Tidak"
        binding.tvSecondAsnwer.text = if (postData.secondAnswer == "1") "Ya" else "Tidak"

        Glide.with(applicationContext)
            .asBitmap()
            .load(ResultHolder.getImage())
            .into(binding.ivSurvey)

        binding.btnSubmit.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        binding.progress.visibility = View.VISIBLE

        val image = ResultHolder.getImage()
        val file = image?.let { createTempFile(it) }

        val imageRequest = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val imageBody = MultipartBody.Part.createFormData("file", file?.name, imageRequest)

        val service = PrakaJakartaAPI.createService(ApiImageUpload::class.java)
        val call = service.uploadImage(imageBody)

        call.enqueue(object : Callback<ImageUpload> {
            override fun onResponse(call: Call<ImageUpload>, response: Response<ImageUpload>) {
                val code = response.code().toString()

                when (code) {
                    "200" -> {
                        imageUrl = response.body()?.result
                        createPost()
                    }

                    "401" -> {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    else -> {
                        Snackbar.make(
                            binding.root,
                            code + ": " + response.message(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
                binding.progress.visibility = View.GONE
                Snackbar.make(binding.root, "Gagal", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun createTempFile(bitmap: Bitmap): File {
        // Create a file in the external files directory with a unique name
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()}_image.jpg"
        )
        try {
            // Write bitmap data to file
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            FileOutputStream(file).use { fos ->
                fos.write(bitmapData)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun createPost() {
        val tokenModel = PrefManager.getTokenData(applicationContext)
        val token = tokenModel.token ?: ""

        val service = PrakaJakartaAPI.createService(ApiCreatePost::class.java)
        val request = CreatePostRequest(
            name = postData.houseOwner!!,
            address1 = postData.address!!,
            pekerjaan = postData.work!!,
            usia = postData.age!!,
            jenisKelamin = postData.sex!!,
            lat = postData.lat.toString(),
            lng = postData.lng.toString(),
            images = imageUrl!!,
            answer1 = postData.firstAnswer!!,
            answer2 = postData.secondAnswer!!
        )

        val call = service.createPost(
            token,
            request
        )

        call.enqueue(object : Callback<PostDataResult> {
            override fun onResponse(
                call: Call<PostDataResult>,
                response: Response<PostDataResult>
            ) {
                val code = response.code().toString()

                binding.progress.visibility = View.GONE
                when (code) {
                    "200" -> {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.putExtra(ARG_SUCCESS, true)
                        startActivity(intent)
                    }

                    "401" -> {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    "400" -> {
                        Snackbar.make(
                            binding.root,
                            "Tidak boleh ada kolom yang kosong, silahkan coba lagi",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        Snackbar.make(
                            binding.root,
                            code + ": " + response.message(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<PostDataResult>, t: Throwable) {
                binding.progress.visibility = View.GONE
                Snackbar.make(
                    binding.root,
                    "Anda tidak tersambung dengan internet",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }


}