package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite

@Composable
fun InstallationGuideScreen(
    onFinished: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var permissionsRequested by remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsRequested = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070F22),
                        Color(0xFF0F1E3D),
                        Color(0xFF050B17)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Logo & Step indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo_white),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier.height(38.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Guide d'Installation & Astuces",
                    fontFamily = MulishFontFamily,
                    fontSize = 13.sp,
                    color = ToofanGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .width(if (i == stepIndex) 28.dp else 10.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (i == stepIndex) ToofanGreen else Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            // Interactive Step Content
            AnimatedContent(
                targetState = stepIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "step_content"
            ) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (step) {
                            0 -> {
                                // Step 1: Login Guide
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(ToofanGreen.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = ToofanGreen,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Astuce 1 : Connexion Facile",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Entrez votre numéro de téléphone (RDC +243 ou international) ou votre Wallet ID CashPay pour vous connecter instantanément en toute simplicité.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            1 -> {
                                // Step 2: OTP & Profile Setup
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(46.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Astuce 2 : Validation OTP & Profil",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Recevez votre code OTP par SMS ou WhatsApp en temps réel. Lors de la première installation, votre profil et votre clé de sécurité PIN sont configurés en quelques secondes.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            2 -> {
                                // Step 3: Fast Payments & Permissions
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(ToofanGreen.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = ToofanGreen,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Astuce 3 : Scanner & Payez Partout",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Scannez des QR codes en magasin, envoyez de l'argent vers vos contacts, et gérez vos devises CDF, USD et Bitcoin sans tracas.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Permission button
                                Button(
                                    onClick = {
                                        val perms = mutableListOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.READ_CONTACTS
                                        )
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        permissionsLauncher.launch(perms.toTypedArray())
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (permissionsRequested) ToofanGreen else Color(0xFF223457)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = if (permissionsRequested) Icons.Default.Check else Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (permissionsRequested) "Autorisations accordées" else "Autoriser Caméra & Contacts",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (stepIndex < 2) {
                            stepIndex++
                        } else {
                            onFinished()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = if (stepIndex < 2) "Suivant / Next" else "Confirmer & Accéder à CashPay",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                if (stepIndex < 2) {
                    TextButton(onClick = onFinished) {
                        Text(
                            text = "Passer le guide",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "CashPay • Powered by FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
