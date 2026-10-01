package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MulishFontFamily

// Official Brand Colors
private val BrandBlueMidnight = Color(0xFF000E38)
private val BrandBlueDark = Color(0xFF0A1C4D)
private val BrandOrange = Color(0xFFFF6600)
private val BrandOrangeDark = Color(0xFFE65C00)
private val BrandOrangeLight = Color(0xFFFF8533)
private val BrandBgGray = Color(0xFFF4F6F9)
private val BrandCardDark = Color(0xFF051336)
private val BrandGreen = Color(0xFF00C853)

data class GuideStepData(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val badges: List<String>,
    val description: String,
    val highlights: List<Pair<String, String>>,
    val actionText: String? = null
)

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

    val steps = remember {
        listOf(
            GuideStepData(
                title = "Solde Multi-Devises en Temps Réel",
                subtitle = "Portefeuille Numérique Intelligent",
                imageRes = R.drawable.img_guide_balances,
                badges = listOf("USD ($)", "CDF (FC)", "Temps Réel ⚡"),
                description = "Consultez vos balances en direct avec mise à jour instantanée à chaque transaction. Basculez en un clic entre Franc Congolais et Dollar US au meilleur taux officiel garanti.",
                highlights = listOf(
                    "Actualisation dynamique" to "Le solde réagit immédiatement à chaque crédit et débit.",
                    "Gestion multi-comptes" to "Gardez vos avoirs en USD et CDF sous le même portefeuille sécurisé.",
                    "Visibilité & Confidentialité" to "Masquez ou affichez votre solde d'un simple geste."
                )
            ),
            GuideStepData(
                title = "Transferts & Envois Instantanés",
                subtitle = "Envoyez sans frontières à 0% de tracas",
                imageRes = R.drawable.img_guide_transfers,
                badges = listOf("P2P Gratuit", "Vers Téléphone", "Wallet ID"),
                description = "Envoyez de l'argent instantanément à vos proches par numéro de téléphone ou Wallet ID. Recevez des notifications en temps réel et partagez vos reçus certifiés.",
                highlights = listOf(
                    "Recherche intelligente" to "Reconnaissance automatique des bénéficiaires et contacts.",
                    "Confirmation sécurisée" to "Aperçu clair des montants et frais avant chaque validation.",
                    "Historique complet" to "Suivez toutes vos entrées et sorties avec reçus téléchargeables."
                )
            ),
            GuideStepData(
                title = "Sécurité Maximale & Biométrie",
                subtitle = "Protection Bancaire de Niveau Supérieur",
                imageRes = R.drawable.img_guide_security,
                badges = listOf("Empreinte Digitale", "PIN à 4 chiffres", "Chiffrement AES"),
                description = "Vos fonds et vos données sont protégés par le chiffrement le plus robuste. Déverrouillez l'application avec votre empreinte ou votre code PIN sans devoir ressaisir vos identifiants.",
                highlights = listOf(
                    "Déverrouillage biométrique" to "Accès instantané et sécurisé via le lecteur d'empreinte.",
                    "Isolation des sessions" to "La déconnexion efface intégralement vos clés et caches locaux.",
                    "Code PIN confidentiel" to "Chaque transfert sensible requiert votre validation par PIN."
                )
            ),
            GuideStepData(
                title = "Espace Agent & Paiements QR",
                subtitle = "Dépôts, Retraits & Cash-in / Cash-out",
                imageRes = R.drawable.img_guide_agent_qr,
                badges = listOf("Dépôt Immédiat", "Scan & Pay", "Points Agréés"),
                description = "Effectuez des dépôts et retraits d'espèces auprès de nos agents partenaires ou payez chez vos commerçants d'un simple scan de QR Code.",
                highlights = listOf(
                    "Dépôt sans frais client" to "Créditez votre compte CashPay chez n'importe quel agent agréé.",
                    "Scanner QR Code" to "Paiement ultra-rapide sans contact en magasin.",
                    "Espace professionnel" to "Outils dédiés pour les agents avec suivi des commissions en direct."
                ),
                actionText = "Autoriser Caméra & Contacts"
            )
        )
    }

    val currentStep = steps[stepIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrandBlueMidnight,
                                BrandBlueDark
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cashpay_logo_white),
                        contentDescription = "CashPay Logo",
                        modifier = Modifier.height(34.dp)
                    )

                    Surface(
                        color = BrandOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandOrange.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Étape ${stepIndex + 1}/${steps.size}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandOrangeLight,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step progress bar indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in steps.indices) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        i == stepIndex -> Brush.horizontalGradient(listOf(BrandOrange, BrandOrangeLight))
                                        i < stepIndex -> Brush.horizontalGradient(listOf(BrandOrange.copy(alpha = 0.7f), BrandOrange))
                                        else -> Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.2f)))
                                    }
                                )
                                .clickable { stepIndex = i }
                        )
                    }
                }
            }

            // Animated Main Content Area
            AnimatedContent(
                targetState = stepIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(300))
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(300))
                    }
                },
                label = "step_content_anim",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) { targetIndex ->
                val step = steps[targetIndex]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // High quality Step Illustration Card with official brand overlay
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = step.imageRes),
                                    contentDescription = step.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Gradient tint on image bottom
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    BrandBlueMidnight.copy(alpha = 0.85f)
                                                ),
                                                startY = 100f
                                            )
                                        )
                                )

                                // Category badge on image
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    step.badges.forEach { badge ->
                                        Surface(
                                            color = BrandOrange.copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = badge,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title & Subtitle
                        Text(
                            text = step.subtitle.uppercase(),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = BrandOrange,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = step.title,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = BrandBlueMidnight,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = step.description,
                            fontFamily = MulishFontFamily,
                            fontSize = 13.5.sp,
                            color = Color(0xFF4A5568),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Highlights Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            step.highlights.forEach { (boldPart, descPart) ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 3.dp)
                                            .size(16.dp)
                                            .background(BrandOrange.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = BrandOrange,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                    Text(
                                        text = androidx.compose.ui.text.buildAnnotatedString {
                                            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = BrandBlueMidnight))
                                            append(boldPart)
                                            append(" : ")
                                            pop()
                                            pushStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF4A5568)))
                                            append(descPart)
                                            pop()
                                        },
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.5.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        // Specific action for Step 4 (Permissions)
                        if (targetIndex == 3) {
                            Spacer(modifier = Modifier.height(12.dp))
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
                                    containerColor = if (permissionsRequested) BrandGreen else BrandBlueDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("guide_permission_btn")
                            ) {
                                Icon(
                                    imageVector = if (permissionsRequested) Icons.Default.Check else Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (permissionsRequested) "Autorisations accordées avec succès" else "Activer Caméra & Scanner QR",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (stepIndex > 0) {
                        Button(
                            onClick = { stepIndex-- },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = BrandBlueMidnight
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.35f)
                                .height(50.dp)
                                .testTag("guide_prev_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Précédent",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Next / Finish Button with Gradient
                    Button(
                        onClick = {
                            if (stepIndex < steps.size - 1) {
                                stepIndex++
                            } else {
                                onFinished()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandOrange
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(if (stepIndex > 0) 0.65f else 1f)
                            .height(50.dp)
                            .testTag("guide_next_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (stepIndex < steps.size - 1) "Suivant" else "Commencer avec CashPay",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (stepIndex < steps.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (stepIndex < steps.size - 1) {
                    TextButton(
                        onClick = onFinished,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag("guide_skip_btn")
                    ) {
                        Text(
                            text = "Passer le guide et continuer",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "CashPay • Propulsé par FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
