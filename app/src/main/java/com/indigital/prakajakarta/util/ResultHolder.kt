package com.indigital.prakajakarta.util

import android.graphics.Bitmap

class ResultHolder {
    companion object {
        private var image: Bitmap? = null

        fun setImage(image: Bitmap?) {
            this.image = image
        }

        fun getImage(): Bitmap? {
            return image
        }
    }
}