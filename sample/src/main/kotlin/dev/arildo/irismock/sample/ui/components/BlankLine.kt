package dev.arildo.irismock.sample.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BlankLine(lineCount: Int = 1) {
    Spacer(modifier = Modifier.size((lineCount * 18).dp))
}
