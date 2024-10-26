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

        fun setToken(context: Context, tokenData: Token) {
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(TOKENKEY, tokenData.token)
            editor.apply()
        }

        fun getTokenData(context: Context): Token {
            val tokenData = Token()
            val sharedPreferences = context.getSharedPreferences(MyPREF, Context.MODE_PRIVATE)

            tokenData.token = sharedPreferences.getString(TOKENKEY, "") ?: ""

            return tokenData
        }
    }
}
