package com.indigital.prakajakarta.data.pref

import android.content.Context
import com.indigital.prakajakarta.data.model.Token
import com.indigital.prakajakarta.data.model.pref.PostData

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

        fun setHouseOwner(context: Context, postData: PostData) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(HOUSEOWNERKEY, postData.houseOwner)
            editor.putFloat(LATKEY, postData.lat)
            editor.putFloat(LNGKEY, postData.lng)
            editor.putString(WORKKEY, postData.work)
            editor.putString(ADDRESSKEY, postData.address)
            editor.putString(AGEKEY, postData.age)
            editor.putString(SEXKEY, postData.sex)
            editor.apply()
        }

        fun setFirstAnswer(context: Context, postData: PostData) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(FIRSTANSWERKEY, postData.firstAnswer)
            editor.apply()
        }

        fun setSecondAnswer(context: Context, postData: PostData) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(SECONDANSWERKEY, postData.secondAnswer)
            editor.apply()
        }

        fun setToken(context: Context, tokenData: Token) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(TOKENKEY, tokenData.token)
            editor.apply()
        }

        fun getPostData(context: Context): PostData {
            val postData = PostData()
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)

            postData.houseOwner = sharedPreferences.getString(HOUSEOWNERKEY, "") ?: ""
            postData.lat = sharedPreferences.getFloat(LATKEY, 0.0f)
            postData.lng = sharedPreferences.getFloat(LNGKEY, 0.0f)
            postData.firstAnswer = sharedPreferences.getString(FIRSTANSWERKEY, "") ?: ""
            postData.secondAnswer = sharedPreferences.getString(SECONDANSWERKEY, "") ?: ""
            postData.work = sharedPreferences.getString(WORKKEY, "") ?: ""
            postData.address = sharedPreferences.getString(ADDRESSKEY, "") ?: ""
            postData.age = sharedPreferences.getString(AGEKEY, "") ?: ""
            postData.sex = sharedPreferences.getString(SEXKEY, "") ?: ""

            return postData
        }

        fun getTokenData(context: Context): Token {
            val tokenData = Token()
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)

            tokenData.token = sharedPreferences.getString(TOKENKEY, "") ?: ""

            return tokenData
        }
    }
}
