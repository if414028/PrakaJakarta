package com.indigital.prakajakarta.survey

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.camera.CameraActivity
import com.indigital.prakajakarta.data.model.pref.PostData
import com.indigital.prakajakarta.databinding.ActivitySecondQuestionBinding

class SecondQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondQuestionBinding

    private var answer = "1"
    private var postData = PostData()

    companion object {
        const val ARG_POST_DATA = "postData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second_question)

        getAdditionalData()
        setupLayout()
    }

    private fun getAdditionalData() {
        postData = intent.getParcelableExtra(ARG_POST_DATA)!!
    }

    private fun setupLayout() {
        binding.btnYes.setOnClickListener {
            answer = "1"
            binding.btnYes.setBackgroundResource(R.drawable.bg_enable_rounded)
            binding.txtYes.setTextColor(resources.getColor(R.color.colorWhite))

            binding.btnNo.setBackgroundResource(R.drawable.bg_disable_rounded)
            binding.txtNo.setTextColor(resources.getColor(R.color.colorText))
        }

        binding.btnNo.setOnClickListener {
            answer = "2"
            binding.btnYes.setBackgroundResource(R.drawable.bg_disable_rounded)
            binding.txtYes.setTextColor(resources.getColor(R.color.colorText))

            binding.btnNo.setBackgroundResource(R.drawable.bg_enable_rounded)
            binding.txtNo.setTextColor(resources.getColor(R.color.colorWhite))
        }

        binding.btnNext.setOnClickListener {
            submit()
        }

    }

    private fun submit() {
        postData.secondAnswer = answer

        val intent = Intent(applicationContext, CameraActivity::class.java)
        intent.putExtra(CameraActivity.ARG_POST_DATA, postData)
        startActivity(intent)
    }
}