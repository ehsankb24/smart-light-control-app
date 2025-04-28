package com.sepiddynamics.samrt_light_control_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepiddynamics.samrt_light_control_app.ui.theme.SamrtlightcontrolappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamrtlightcontrolappTheme {
                val viewState by viewModel.viewState.collectAsStateWithLifecycle()
                MainLayout(viewState)
            }
        }
    }
}

@Composable
private fun MainLayout(
    viewState: MainViewState,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        when (viewState) {
            is MainViewState.Connected -> ConnectedLayout(
                modifier = modifier,
                state = viewState
            )

            is MainViewState.Failed -> FailedLayout(modifier, onRetry = viewState.retry)
            MainViewState.Loading -> LoadingLayout(modifier)
        }
    }
}

@Composable
private fun LoadingLayout(modifier: Modifier = Modifier) = Box(modifier = modifier.fillMaxSize()) {
    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
}

@Composable
private fun FailedLayout(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text("error")
    Button(onClick = onRetry) {
        Text("retry")
    }
}

@Composable
private fun ConnectedLayout(
    modifier: Modifier = Modifier,
    state: MainViewState.Connected
) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                state.stateTitle
            )
            Checkbox(
                checked = state.isOn,
                onCheckedChange = state.onCheckedChange
            )
        }
        Slider(
            value = state.progressValue,
            onValueChange = state.onValueChange,
            onValueChangeFinished = state.onValueChangeFinished,
            valueRange = 0f..100f,
            enabled = state.isOn
        )
        Text(text = state.progressLabel)
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadingPreview() {
    SamrtlightcontrolappTheme {
        MainLayout(
            viewState = MainViewState.Loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutFailedPreview() {
    SamrtlightcontrolappTheme {
        MainLayout(
            viewState = MainViewState.Failed {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutConnectPreview() {
    SamrtlightcontrolappTheme {
        MainLayout(
            viewState = MainViewState.Connected(
                isOn = false,
                progressValue = 10f,
                onValueChange = {},
                onCheckedChange = {},
                onValueChangeFinished = {}
            )
        )
    }
}