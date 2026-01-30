package com.example.aikotlin.ui.login

// Please read docs/ai/skill.md and project-context.md before answering.

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aikotlin.MainActivity
import com.example.aikotlin.data.ViewModelFactory
import com.example.aikotlin.viewmodel.LoginUiEvent
import com.example.aikotlin.viewmodel.LoginUiState
import com.example.aikotlin.viewmodel.LoginViewModel

// 根据AGENTS.md，如果应用全局使用Hilt，Activity应该标记为AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides this) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                // Handle events
                LaunchedEffect(viewModel.uiEvent) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is LoginUiEvent.NavigateToHome -> {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                            is LoginUiEvent.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                        }
                    }
                }

                // 在这里应用您的主题。NexusLoginScreen本身不包含MaterialTheme，
                // 建议在Activity级别包裹，以确保Compose组件能正确获取主题属性。
                // 使用MaterialTheme是为了让Compose组件（如Surface）有默认的背景和文本颜色处理，
                // 也可以直接使用NexusColors和NexusTypography。
                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(
                        primary = NexusColors.Accent,
                        background = NexusColors.Background,
                        onBackground = NexusColors.TextPrimary
                    ),
                    typography = MaterialTheme.typography.copy(
                        displayLarge = NexusTypography.Logo,
                        bodyMedium = NexusTypography.Input
                    )
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        containerColor = NexusColors.Background
                    ) { padding ->
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.padding(padding),
                            color = NexusColors.Background
                        ) {
                            NexusLoginScreen(
                                onLoginClick = { email, password ->
                                    Log.d("LoginActivity", "Login attempt: Email=$email")
                                    viewModel.login(email, password)
                                },
                                onForgotCredentialsClick = {
                                    Log.d("LoginActivity", "Forgot credentials clicked.")
                                    // TODO: 导航到忘记密码页面
                                },
                                isLoading = uiState is LoginUiState.Loading
                            )
                        }
                    }
                }
            }
        }
    }
}
