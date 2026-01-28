package com.example.aikotlin.ui.login

// Please read docs/ai/skill.md and project-context.md before answering.
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nexus.ui.login.NexusLoginScreen
import com.example.nexus.ui.login.NexusColors // 导入颜色，确保主题一致
import com.example.nexus.ui.login.NexusTypography // 导入字体，确保主题一致
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

// 根据AGENTS.md，如果应用全局使用Hilt，Activity应该标记为AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 在这里应用您的主题。NexusLoginScreen本身不包含MaterialTheme，
            // 建议在Activity级别包裹，以确保Compose组件能正确获取主题属性。
            // 使用MaterialTheme是为了让Compose组件（如Surface）有默认的背景和文本颜色处理，
            // 也可以直接使用NexusColors和NexusTypography。
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    primary = NexusColors.Accent,
                    background = NexusColors.Background,
                    onBackground = NexusColors.TextPrimary
                    // 可以根据需要调整其他颜色
                ),
                typography = MaterialTheme.typography.copy(
                    // 这里可以映射NexusTypography到MaterialTheme的typography，
                    // 但由于NexusLoginScreen直接使用NexusTypography，这里可以简化。
                    // 仅供演示如何集成
                    displayLarge = NexusTypography.Logo,
                    bodyMedium = NexusTypography.Input
                )
            ) {
                // A surface container using the 'background' color from the theme
                Surface(color = NexusColors.Background) { // 使用NexusLoginScreen的背景色
                    NexusLoginScreen(
                        onLoginClick = { email, password ->
                            Log.d("LoginActivity", "Login attempt: Email=$email, Password=$password")
                            // TODO: 在这里处理登录逻辑，例如触发ViewModel的登录方法
                            // loginViewModel.login(email, password)
                        },
                        onForgotCredentialsClick = {
                            Log.d("LoginActivity", "Forgot credentials clicked.")
                            // TODO: 导航到忘记密码页面
                        }
                    )
                }
            }
        }
    }
}
