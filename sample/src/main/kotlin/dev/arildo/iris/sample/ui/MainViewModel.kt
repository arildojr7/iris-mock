package dev.arildo.iris.sample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.sample.MockHandler
import dev.arildo.iris.sample.data.RetrofitInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(isLoading = true))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val apiService = RetrofitInitializer.apiService()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        viewModelScope.launch { fetchData() }
    }

    private suspend fun fetchData() = withContext(Dispatchers.IO) {
        _uiState.update { it.copy(isLoading = true) }

        val result = apiService.getUserProfile()
        val request = result.raw().request()
        val requestHeaders = request.extractHeaders()
        val responseHeaders = result.extractHeaders()
        val errorBody = result.errorBody()?.string()?.let(JsonParser::parseString)

        val uiState = MainUiState(
            isLoading = false,
            requestUrl = request.url().toString(),
            requestMethod = request.method(),
            requestHeaders = requestHeaders,
            responseHeaders = responseHeaders,
            responseBody = gson.toJson(result.body() ?: errorBody),
            responseCode = result.code().let { HttpCode.getEnum(it) }
        )
        delay(300)
        _uiState.emit(uiState)
    }

    fun onIrisMockChecked(checked: Boolean) {
        MockHandler.enableMocks = checked
        viewModelScope.launch { fetchData() }
    }

    private fun Response<*>.extractHeaders() =
        headers().names().associateWith { headers().values(it).joinToString(", ") }

    private fun Request.extractHeaders() =
        headers().names().associateWith { headers().values(it).joinToString(", ") }
}
