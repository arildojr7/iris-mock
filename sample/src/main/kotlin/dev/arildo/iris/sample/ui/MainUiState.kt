package dev.arildo.iris.sample.ui

import dev.arildo.iris.mock.util.HttpCode

data class MainUiState(
    val isLoading: Boolean = false,
    val requestUrl: String = "",
    val requestHeaders: Map<String, String> = emptyMap(),
    val requestMethod: String? = null,
    val responseHeaders: Map<String, String> = emptyMap(),
    val responseBody: String? = null,
    val responseCode: HttpCode? = null,
)
