package com.indigital.prakajakarta.data.pref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.indigital.prakajakarta.data.model.Token
import com.indigital.prakajakarta.data.model.pref.PostData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrefManager(private val context: Context) {

    companion object {
        private const val MyPREF = "MyPref"
        private const val HOUSEOWNERKEY = "HouseOwnerKey"
        private const val FIRSTANSWERKEY = "FirstAnswerKey"
        private const val SECONDANSWERKEY = "SecondAnswerKey"
        private const val LATKEY = "LatKey"
        private const val LNGKEY = "LngKey"
        private const val WORKKEY = "WorkKey"
        private const val ADDRESSKEY = "AddressKey"
        private const val AGEKEY = "AgeKey"
        private const val SEXKEY = "SexKey"
        private const val TOKENKEY = "TokenKey"

        fun setToken(context: Context, tokenData: Token) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(TOKENKEY, tokenData.token)
            editor.apply()
        }

        fun getTokenData(context: Context): Token {
            val tokenData = Token()
            val sharedPreferences = context.getSharedPreferences(MyPREF, MODE_PRIVATE)

            tokenData.token = sharedPreferences.getString(TOKENKEY, "") ?: ""

            return tokenData
        }

        fun getTodaySurveyCount(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences("SurveyPrefs", MODE_PRIVATE)
            val today = getCurrentDate()
            val savedDate = sharedPreferences.getString("lastSurveyDate", "")

            return if (today == savedDate) {
                sharedPreferences.getInt("surveyCount", 0)
            } else {
                0 // Reset jika tanggalnya sudah berbeda
            }
        }

        // Fungsi untuk menyimpan jumlah survei yang baru dibuat
        fun saveSurveyCount(context: Context, count: Int) {
            val sharedPreferences = context.getSharedPreferences("SurveyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("surveyCount", count)
            editor.putString("lastSurveyDate", getCurrentDate()) // Simpan tanggal terbaru
            editor.apply()
        }

        private fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date())
        }
    }
}
