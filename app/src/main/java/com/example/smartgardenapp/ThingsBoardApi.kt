package com.example.smartgardenapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.example.smartgardenapp.LoginResponse


interface ThingsBoardApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/plugins/rpc/oneway/{deviceId}")
    suspend fun sendRpcRequest(
        @Header("X-Authorization") token: String,
        @Path("deviceId") deviceId: String,
        @Body request: RpcRequest
    ): Response<Unit>
}