package com.example.smartgardenapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.smartgardenapp.LoginResponse
import com.google.gson.JsonArray
import com.google.gson.JsonObject


interface ThingsBoardApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/plugins/rpc/oneway/{deviceId}")
    suspend fun sendRpcRequest(
        @Header("X-Authorization") token: String,
        @Path("deviceId") deviceId: String,
        @Body request: RpcRequest
    ): Response<Unit>

    @GET("/api/plugins/telemetry/DEVICE/{deviceId}/values/timeseries")
    suspend fun getTelemetryHistory(
        @Header("X-Authorization") token: String,
        @Path("deviceId") deviceId: String,
        @Query("keys") keys: String,
        @Query("startTs") startTs: Long,
        @Query("endTs") endTs: Long,
        @Query("limit") limit: Int = 100
    ): Response<JsonObject>
    
    // Lấy Shared Attributes (cấu hình)
    @GET("/api/plugins/telemetry/DEVICE/{deviceId}/values/attributes/SHARED_SCOPE")
    suspend fun getSharedAttributes(
        @Header("X-Authorization") token: String,
        @Path("deviceId") deviceId: String,
        @Query("keys") keys: String
    ): Response<JsonArray>

    // Cập nhật Shared Attributes
    @POST("/api/plugins/telemetry/DEVICE/{deviceId}/attributes/SHARED_SCOPE")
    suspend fun updateSharedAttributes(
        @Header("X-Authorization") token: String,
        @Path("deviceId") deviceId: String,
        @Body attributes: JsonObject
    ): Response<Unit>
}