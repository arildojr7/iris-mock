package dev.arildo.irismock.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arildo.irismock.sample.ui.theme.AppTheme

@Composable
fun CodeBlock(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$title {",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
        )
        Column(modifier = Modifier.padding(start = 18.dp)) {
            content()
        }

        Text(
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            text = "}",
        )
    }
}

@Preview
@Composable
private fun CodeBlockPreview() {
    AppTheme {
        CodeBlock("myCustomBlock") {
            BlankLine()
        }
    }
}
