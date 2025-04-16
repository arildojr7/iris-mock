package dev.arildo.irismock.sample.ui

import dev.arildo.irismock.util.HttpCode

data class MainUiState(
    val isLoading: Boolean = false,
    val requestUrl: String = "",
    val requestHeaders: Map<String, String> = emptyMap(),
    val requestMethod: String? = null,
    val responseHeaders: Map<String, String> = emptyMap(),
    val responseBody: String? = null,
    val responseCode: HttpCode? = null,
)
