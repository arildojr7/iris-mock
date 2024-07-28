package dev.arildo.iris.sample.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.arildo.iris.sample.ui.theme.AppTheme
import dev.arildo.iris.sample.ui.theme.stringTextColor

@Composable
fun MultilineText(text: String) {
    Text(
        text = "\"\"\"\n$text\n\"\"\"",
        color = stringTextColor,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Preview
@Composable
private fun MultilineTextPreview() {
    AppTheme {
        MultilineText(text = "my text")
    }
}
