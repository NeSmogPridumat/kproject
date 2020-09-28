package com.dteam.kproject.http

import com.dteam.kproject.data.*
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @POST("/register")
    suspend fun register (
        @Body registerData: RegisterData
    ): Response<UserIdResponse>

    @GET("/get")
    suspend fun getTableTimes(
        @Query ("time") time: Long
    ):Response<Timetable>

    @POST("/set")
    suspend fun setTimes(
        @Body setTimesData: SetTimesData
    ):Response<Any>

    @POST("/auth")
    suspend fun auth(
        @Body authorizationData: AuthorizationData
    ):Response<UserIdResponse>

    @POST("/del")
    suspend fun delete(
        @Body setTimesData: SetTimesData
    ): Response<Any>

    @GET("/search")
    suspend fun search(
        @Query ("id") id: String,
        @Query ("time") time: Long
    ): Response<ArrayList<MyTimetable>>
}