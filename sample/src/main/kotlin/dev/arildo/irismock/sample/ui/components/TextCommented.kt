package dev.arildo.irismock.sample.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.arildo.irismock.sample.ui.theme.AppTheme

@Composable
fun TextCommented(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Preview
@Composable
private fun TextCommentedPreview() {
    AppTheme {
        TextCommented(text = "my text")
    }
}
