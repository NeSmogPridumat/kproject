package com.dteam.kproject.authorization.repository

import android.content.Context
import com.dteam.kproject.MainActivity
import com.dteam.kproject.data.AuthorizationData
import com.dteam.kproject.data.RegisterData
import com.dteam.kproject.data.UserIdResponse
import com.dteam.kproject.http.RetrofitHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class AuthRepository @Inject constructor() {

    //авторизация
    fun authorizationAsync(authorizationData: AuthorizationData): Deferred<UserIdResponse>
            = CoroutineScope(Dispatchers.IO).async{
        val response = RetrofitHelper.makeRetrofitService().auth(authorizationData)
        when {
            response.isSuccessful -> return@async response.body()!!
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    //регистрация
    fun registrationAsync(registerData: RegisterData): Deferred<UserIdResponse>
            = CoroutineScope(Dispatchers.IO).async{
        val response = RetrofitHelper.makeRetrofitService().register(registerData)
        when {
            response.isSuccessful -> return@async response.body()!!
            else -> {
                val jo = JSONObject(response.errorBody()!!.string())
                throw Throwable(jo.getString("msg"))
            }
        }
    }

    //сохранение id
    fun saveUserId(id: String, application: Context)
            = CoroutineScope(Dispatchers.Default).launch {
        val preference =
            application.getSharedPreferences(MainActivity.preferenceKey, Context.MODE_PRIVATE)
        preference.edit().putString("userId", id).apply()
    }
}