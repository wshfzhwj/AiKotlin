package com.example.aikotlin.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class EncryptionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val originalRequestBody = buffer.readUtf8()
            val encryptedRequestBody = EncryptionUtils.encrypt(originalRequestBody)
            val newRequestBody = okhttp3.RequestBody.create(requestBody.contentType(), encryptedRequestBody)
            request = request.newBuilder().method(request.method, newRequestBody).build()
        }

        val response = chain.proceed(request)
        val responseBody = response.body
        if (responseBody != null) {
            val encryptedResponseBody = responseBody.string()
            val decryptedResponseBody = EncryptionUtils.decrypt(encryptedResponseBody)
            val newResponseBody = decryptedResponseBody.toResponseBody(responseBody.contentType())
            return response.newBuilder().body(newResponseBody).build()
        }

        return response
    }
}
