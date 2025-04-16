package dev.arildo.irismock.sample.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.arildo.irismock.sample.ui.theme.AppTheme

@Composable
fun TextAttribution(key: String, value: String) {
    Row {
        Text(
            text = key,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = " = $value",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,

            )
    }
}

@Preview
@Composable
private fun TextAttributionPreview() {
    AppTheme {
        TextAttribution("key", "value")
    }
}
