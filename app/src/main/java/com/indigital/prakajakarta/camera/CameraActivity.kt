package com.indigital.prakajakarta.camera

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.indigital.prakajakarta.R
import com.indigital.prakajakarta.data.model.pref.PostData
import com.indigital.prakajakarta.databinding.ActivityCameraBinding
import com.indigital.prakajakarta.survey.ReviewSurveyActivity
import com.indigital.prakajakarta.survey.SecondQuestionActivity
import com.indigital.prakajakarta.survey.SecondQuestionActivity.Companion
import com.indigital.prakajakarta.util.ResultHolder
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Grid
import com.otaliastudios.cameraview.controls.Mode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraView: CameraView

    private var postData = PostData()

    companion object {
        const val ARG_CAMERA_RESULT = "argCameraResult"
        const val ARG_POST_DATA = "postData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)

        getAdditionalData()

        cameraView = binding.camera
        cameraView.setLifecycleOwner(this)
        cameraView.mode = Mode.PICTURE
        cameraView.snapshotMaxHeight = 500
        cameraView.snapshotMaxWidth = 500
        cameraView.grid = Grid.DRAW_3X3
        cameraView.gridColor = Color.WHITE
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)

                val imageData = result.data

                try {
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    ResultHolder.setImage(bitmap)
                    val intent = Intent(applicationContext, ReviewSurveyActivity::class.java)
                    intent.putExtra(ReviewSurveyActivity.ARG_POST_DATA, postData)
                    startActivity(intent)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })

        //setup listener for take photo
        binding.btnImageCapture.setOnClickListener {
            cameraView.takePictureSnapshot()
        }

        binding.btnRotateCamera.setOnClickListener {
            cameraView.toggleFacing()
        }
    }

    private fun getAdditionalData() {
        postData = intent.getParcelableExtra(ARG_POST_DATA)!!
    }
}