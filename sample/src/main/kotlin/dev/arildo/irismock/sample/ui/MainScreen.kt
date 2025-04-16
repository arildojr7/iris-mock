package dev.arildo.irismock.sample.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arildo.irismock.util.HttpCode
import dev.arildo.irismock.sample.ui.components.AppScaffold
import dev.arildo.irismock.sample.ui.components.BlankLine
import dev.arildo.irismock.sample.ui.components.CodeBlock
import dev.arildo.irismock.sample.ui.components.MultilineText
import dev.arildo.irismock.sample.ui.components.TextAttribution
import dev.arildo.irismock.sample.ui.components.TextCommented
import dev.arildo.irismock.sample.ui.theme.AppTheme

@Composable
fun MainScreen(uiState: MainUiState, onIrisMockChecked: (checked: Boolean) -> Unit) {
    AppScaffold(onIrisMockChecked = onIrisMockChecked) { innerPadding ->

        AnimatedVisibility(visible = !uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 20.dp,
                        end = 20.dp,
                    )
            ) {
                BlankLine()
                CodeBlock(title = "request") {
                    TextAttribution("url", uiState.requestUrl)
                    TextAttribution("method", uiState.requestMethod.orEmpty())

                    BlankLine()

                    TextCommented("// headers")
                    uiState.requestHeaders.forEach {
                        TextAttribution(it.key, it.value)
                    }
                }

                BlankLine(2)
                CodeBlock(title = "response") {
                    TextAttribution(
                        key = "status",
                        value = "${uiState.responseCode?.name}/${uiState.responseCode?.code}"
                    )

                    BlankLine()
                    TextCommented("// headers")

                    uiState.responseHeaders.forEach {
                        TextAttribution(it.key, it.value)
                    }

                    BlankLine()
                    TextCommented("// response body")

                    MultilineText(uiState.responseBody.orEmpty())
                }
            }
        }

        AnimatedVisibility(visible = uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

    }
}


@Preview
@Composable
private fun MainScreenPreview() {
    val uiState = MainUiState(
        isLoading = false,
        requestMethod = "GET",
        requestHeaders = mapOf("Header1" to "value1", "Header2" to "value2", "Header3" to "value4"),
        responseHeaders = mapOf("Header1" to "value1", "Header2" to "value2"),
        responseBody = "{\"data\" : \"preview!\"}",
        responseCode = HttpCode.OK
    )
    AppTheme {
        MainScreen(uiState) {}
    }
}
