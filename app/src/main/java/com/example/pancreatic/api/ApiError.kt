package com.example.pancreatic.api

import retrofit2.HttpException

/** Prefer FastAPI / Retrofit error body text for user-visible messages. */
fun Throwable.userFacingMessage(): String {
    val http = this as? HttpException
        ?: return message?.takeIf { it.isNotBlank() } ?: "Request failed"

    val body = http.response()?.errorBody()?.use { it.string() }?.trim().orEmpty()
    if (body.isNotEmpty()) {
        return body
    }
    return "HTTP ${http.code()}"
}
