package dev.arildo.irismock.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arildo.irismock.sample.R
import dev.arildo.irismock.sample.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    onIrisMockChecked: (checked: Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.iris_avatar),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("IrisMock")
                    }

                },
                actions = {
                    var checked by remember { mutableStateOf(true) }

                    Switch(
                        modifier = Modifier.padding(end = 8.dp),
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            onIrisMockChecked(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.background,
                            checkedBorderColor = MaterialTheme.colorScheme.background,
                            uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.background,
                            uncheckedBorderColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Preview
@Composable
private fun AppScaffoldPreview() {
    AppTheme {
        AppScaffold({}, {})
    }
}
