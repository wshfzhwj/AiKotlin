package com.example.nexus.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Design System: Colors ---
object NexusColors {
    val Background = Color(0xFF0F172A)
    val Accent = Color(0xFF38BDF8)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val InputBg = Color(0xFF1E293B)
    val InputBorder = Color(0xFF334155)
}

// --- Design System: Typography (Approximation) ---
// In a real app, replace FontFamily.SansSerif with Font(R.font.outfit)
// and FontFamily.Monospace with Font(R.font.geist_mono)
object NexusTypography {
    val Logo = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        color = NexusColors.TextPrimary,
        letterSpacing = 2.sp
    )
    val SystemAccess = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = NexusColors.Accent,
        letterSpacing = 4.sp
    )
    val Label = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = NexusColors.TextSecondary,
        letterSpacing = 1.sp
    )
    val Input = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = NexusColors.TextPrimary
    )
    val ButtonText = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.Black,
        letterSpacing = 1.sp
    )
    val Footer = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        color = NexusColors.TextSecondary,
        letterSpacing = 1.sp
    )
}

@Composable
fun NexusLoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotCredentialsClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("user@nexus.io") }
    var password by remember { mutableStateOf("password") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusColors.Background)
    ) {
        // 1. Background Pattern: Concentric Circles (Digital Threshold)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 3
            val maxRadius = size.height * 1.2f
            
            // Draw multiple concentric circles with fading opacity
            for (r in 100 until 1200 step 150) {
                val opacity = (1f - r / 1200f).coerceIn(0f, 1f) * 0.15f
                drawCircle(
                    color = NexusColors.Accent.copy(alpha = opacity),
                    radius = r.toFloat(),
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // 2. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize() // Changed from fillMaxWidth() and removed verticalScroll()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(160.dp))

            // Logo Section
            Text(
                text = "NEXUS",
                style = NexusTypography.Logo
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SYSTEM ACCESS",
                style = NexusTypography.SystemAccess
            )

            Spacer(modifier = Modifier.weight(1f)) // Changed from fixed height

            // Form Section
            NexusInputField(
                label = "IDENTITY",
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            NexusInputField(
                label = "PASSPHRASE",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Action Button
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexusColors.Accent,
                    contentColor = Color.Black
                ),
                shape = CircleShape // Fully rounded
            ) {
                Text(
                    text = "AUTHENTICATE",
                    style = NexusTypography.ButtonText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onForgotCredentialsClick) {
                Text(
                    text = "Forgot credentials?",
                    style = NexusTypography.Label.copy(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Added to distribute space
        }

        // 3. Sticky Footer
        Text(
            text = "SECURE CONNECTION ENCRYPTED",
            style = NexusTypography.Footer,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Aligned to the bottom of the Box
                .padding(bottom = 48.dp)
        )
    }
}

@Composable
fun NexusInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = NexusTypography.Label,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = NexusTypography.Input,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            cursorBrush = SolidColor(NexusColors.Accent),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NexusColors.InputBg)
                        .border(1.dp, NexusColors.InputBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    
                    if (isPassword) {
                        // Simplified eye icon representation
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NexusColors.TextSecondary, CircleShape)
                        )
                    }
                }
            }
        )
    }
}

@Preview(widthDp = 412, heightDp = 915)
@Composable
fun NexusLoginScreenPreview() {
    MaterialTheme {
        NexusLoginScreen()
    }
}
