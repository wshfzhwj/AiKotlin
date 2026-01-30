package com.example.aikotlin.repository

import com.example.aikotlin.base.BaseRepository
import com.example.aikotlin.database.UserPreferenceDao
import com.example.aikotlin.model.LoginResponse
import com.example.aikotlin.model.UserPreference
import com.example.aikotlin.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class LoginRepository(
    private val apiService: ApiService,
    private val userPreferenceDao: UserPreferenceDao
) : BaseRepository() {

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = safeApiCall {
        delay(2000)
        if (email == "user@nexus.io" && password == "password") {
            val response = LoginResponse(
                token = "fake_jwt_token_123456",
                userId = "user_001",
                username = "Nexus User"
            )
            userPreferenceDao.insertPreference(UserPreference("auth_token", response.token))
            userPreferenceDao.insertPreference(UserPreference("user_id", response.userId))
            userPreferenceDao.insertPreference(UserPreference("username", response.username))
            response
        } else {
            throw Exception("Invalid email or password")
        }
    }

    suspend fun logout() {
        userPreferenceDao.deletePreference("auth_token")
        userPreferenceDao.deletePreference("user_id")
        userPreferenceDao.deletePreference("username")
    }
}
