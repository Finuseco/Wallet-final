package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.CustomKeypad
import com.example.ui.components.PinDots
import com.example.ui.components.ToofanHeader
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanLinkPink
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun PinScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Full screen vivid fintech background image 100% visible
        Image(
            painter = painterResource(id = R.drawable.fintech_bg_person_1790609860029),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 1.0f
        )
        // Soft vignette so background photo remains 100% clear
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0x22070F22),
                            Color(0x330F1E3D),
                            Color(0x55050B17)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // CashPay Brand Logo prominently and cleanly lowered & centered
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo_white),
                contentDescription = "CashPay Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(50.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                    color = ToofanWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, ToofanGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!uiState.installedUserProfile?.profilePhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = uiState.installedUserProfile?.profilePhotoUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                val initials = (uiState.installedUserProfile?.fullName ?: uiState.rawPhone)
                                    .trim()
                                    .split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")
                                    .uppercase()
                                    .ifBlank { "CP" }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFF000E38),
                                                    Color(0xFF00E5FF)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val displayName = uiState.installedUserProfile?.fullName
                            ?: (if (uiState.rawPhone.isNotBlank()) uiState.rawPhone else "Mon Compte")

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = displayName,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = ToofanMainDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.badge_verified),
                                contentDescription = "Compte Vérifié",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Entrez votre code PIN CashPay",
                            fontFamily = MulishFontFamily,
                            fontSize = 14.sp,
                            color = ToofanBodyText,
                            modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
                        )

                        // 4 PIN Dots
                        PinDots(
                            pinLength = uiState.pinCode.length,
                            maxDigits = 4,
                            isError = uiState.errorMessage != null
                        )

                        if (uiState.isPinLoading) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ToofanGreen,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Vérification du code PIN...",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = ToofanGreen
                                )
                            }
                        }

                        // Error Message
                        AnimatedVisibility(
                            visible = uiState.errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFECEF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Erreur",
                                        tint = Color(0xFFFF4868),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFF4868)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // In-App Keypad
                        CustomKeypad(
                            onDigitClick = { digit ->
                                if (!uiState.isPinLoading) viewModel.onPinDigitEntered(digit)
                            },
                            onBackspaceClick = {
                                if (!uiState.isPinLoading) viewModel.onPinBackspace()
                            },
                            onBiometricClick = {
                                viewModel.onBiometricClicked()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Redirection / Shortcuts
                        Text(
                            text = "Code PIN oublié ?",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ToofanGreen,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openForgotPin() }
                                .padding(vertical = 6.dp)
                        )

                        Text(
                            text = "Ce n'est pas votre compte ? Changer de compte",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = ToofanLinkPink,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.logout() }
                                .padding(vertical = 4.dp)
                        )

                        Text(
                            text = "Abandonner et retour",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = ToofanBodyText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateBack() }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

        // Forgot PIN Dialog
        ForgotPinDialog(
            isOpen = uiState.isForgotPinOpen,
            step = uiState.forgotPinStep,
            phone = uiState.forgotPinPhone,
            channel = uiState.forgotPinChannel,
            otp = uiState.forgotPinOtp,
            newPin = uiState.forgotPinNewPin,
            confirmPin = uiState.forgotPinConfirmPin,
            isLoading = uiState.isForgotPinLoading,
            errorMessage = uiState.forgotPinError,
            successMessage = uiState.forgotPinSuccess,
            onPhoneChange = { viewModel.setForgotPinPhone(it) },
            onChannelChange = { viewModel.setForgotPinChannel(it) },
            onOtpChange = { viewModel.setForgotPinOtp(it) },
            onNewPinChange = { viewModel.setForgotPinNewPin(it) },
            onConfirmPinChange = { viewModel.setForgotPinConfirmPin(it) },
            onRequestSubmit = { viewModel.submitForgotPinRequest() },
            onVerifySubmit = { viewModel.submitForgotPinVerify() },
            onResetSubmit = { viewModel.submitForgotPinReset() },
            onDismiss = { viewModel.closeForgotPin() }
        )
    }
}