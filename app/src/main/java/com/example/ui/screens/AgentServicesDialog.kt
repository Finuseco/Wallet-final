package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.data.model.AgentOperationRecord
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentServicesDialog(
    isOpen: Boolean,
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onOpenQrScanner: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onToggleAgentBalanceVisibility: () -> Unit,
    // Bascule Commission
    onOpenSweepDialog: (String) -> Unit,
    onCloseSweepDialog: () -> Unit,
    onSweepCurrencyChange: (String) -> Unit,
    onSweepAmountChange: (String) -> Unit,
    onSweepIsAllChange: (Boolean) -> Unit,
    onSweepPinChange: (String) -> Unit,
    onSubmitSweepCommission: () -> Unit,
    // Dépôt Client
    onDepositClientRefChange: (String) -> Unit,
    onDepositAmountChange: (String) -> Unit,
    onDepositCurrencyChange: (String) -> Unit,
    onDepositPinChange: (String) -> Unit,
    onSubmitDeposit: () -> Unit,
    onResetDeposit: () -> Unit,
    // Retrait Client (Demande & Confirmation OTP Client)
    onWithdrawClientRefChange: (String) -> Unit,
    onWithdrawAmountChange: (String) -> Unit,
    onWithdrawCurrencyChange: (String) -> Unit,
    onWithdrawChannelChange: (String) -> Unit,
    onWithdrawClientOtpChange: (String) -> Unit,
    onInitiateWithdraw: () -> Unit,
    onSubmitWithdrawOtp: () -> Unit,
    onResetWithdraw: () -> Unit,
    // Prêt Loan Me
    onLoanClientRefChange: (String) -> Unit,
    onSearchLoanTarget: () -> Unit,
    onLoanAmountChange: (String) -> Unit,
    onLoanCurrencyChange: (String) -> Unit,
    onLoanPinChange: (String) -> Unit,
    onSubmitLoanRepay: () -> Unit,
    onResetLoanRepay: () -> Unit
) {
    if (!isOpen) return

    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val isVisible = uiState.isAgentBalanceVisible

    // Active sub-modals for operational services
    var activeModal by remember { mutableStateOf<String?>(null) } // "deposit", "withdraw", "loan", "history"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        // Top bar row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Retour",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Espace Agent CashPay",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Agréé",
                                            tint = Color(0xFF00C48C),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "Portail Opérations Financières & Commissions",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onOpenQrScanner,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scanner QR Client",
                                    tint = Color(0xFF00C48C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Agent Profile Banner
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFF00C48C).copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = Color(0xFF00C48C),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = userProfile?.fullName ?: "Agent FINUSECO",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Wallet ID : ${userProfile?.walletId ?: ""}",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 11.sp,
                                            color = Color(0xFFCBD5E1)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF00C48C).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "AGENT ACTIF",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF00C48C),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC)),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Commission Cards Section (Clean Container)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Vos Soldes Commissions",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AgentCommissionCard(
                                currency = "USD",
                                amount = uiState.agentCommissionUsd,
                                isVisible = isVisible,
                                color = Color(0xFF0F172A),
                                onSweep = { onOpenSweepDialog("USD") }
                            )
                            AgentCommissionCard(
                                currency = "CDF",
                                amount = uiState.agentCommissionCdf,
                                isVisible = isVisible,
                                color = Color(0xFF312E81),
                                onSweep = { onOpenSweepDialog("CDF") }
                            )
                            AgentCommissionCard(
                                currency = "EUR",
                                amount = uiState.agentCommissionEur,
                                isVisible = isVisible,
                                color = Color(0xFF1E3A8A),
                                onSweep = { onOpenSweepDialog("EUR") }
                            )
                        }
                    }
                }

                // Info Note
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Basculez vos commissions vers votre compte principal instantanément.",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Section Action Shortcuts (Services Concernés)
                item {
                    Text(
                        text = "Services Opérationnels Agent",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AgentActionSquare(
                                title = "Dépôt\nClient",
                                subtitle = "Espèces → Wallet",
                                icon = Icons.Default.ArrowDownward,
                                color = Color(0xFF059669),
                                modifier = Modifier.weight(1f),
                                onClick = { activeModal = "deposit" }
                            )

                            AgentActionSquare(
                                title = "Retrait\nClient",
                                subtitle = "Confirmation par OTP",
                                icon = Icons.Default.ArrowUpward,
                                color = Color(0xFFD97706),
                                modifier = Modifier.weight(1f),
                                onClick = { activeModal = "withdraw" }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AgentActionSquare(
                                title = "Scanner\nWallet",
                                subtitle = "QR Code Client",
                                icon = Icons.Default.QrCodeScanner,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenQrScanner
                            )

                            AgentActionSquare(
                                title = "Prêt\nLoan Me",
                                subtitle = "Remboursement Client",
                                icon = Icons.Default.Payments,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f),
                                onClick = { activeModal = "loan" }
                            )
                        }

                        // Full width tile for History
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { activeModal = "history" },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 0.5.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFF0F172A).copy(alpha = 0.08f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Historique des Opérations Agent",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                Text(
                                    text = "${uiState.agentOperationsHistory.size} op.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // Modal: DÉPÔT CLIENT
    if (activeModal == "deposit") {
        AgentDepositModal(
            uiState = uiState,
            onClose = { activeModal = null },
            onClientRefChange = onDepositClientRefChange,
            onAmountChange = onDepositAmountChange,
            onCurrencyChange = onDepositCurrencyChange,
            onPinChange = onDepositPinChange,
            onSubmit = onSubmitDeposit,
            onReset = onResetDeposit,
            onOpenQrScanner = onOpenQrScanner
        )
    }

    // Modal: RETRAIT CLIENT (DEMANDE & OTP)
    if (activeModal == "withdraw") {
        AgentWithdrawModal(
            uiState = uiState,
            onClose = { activeModal = null },
            onClientRefChange = onWithdrawClientRefChange,
            onAmountChange = onWithdrawAmountChange,
            onCurrencyChange = onWithdrawCurrencyChange,
            onChannelChange = onWithdrawChannelChange,
            onClientOtpChange = onWithdrawClientOtpChange,
            onInitiate = onInitiateWithdraw,
            onSubmitOtp = onSubmitWithdrawOtp,
            onReset = onResetWithdraw,
            onOpenQrScanner = onOpenQrScanner
        )
    }

    // Modal: REMBOURSEMENT PRÊT LOAN ME
    if (activeModal == "loan") {
        AgentLoanModal(
            uiState = uiState,
            onClose = { activeModal = null },
            onClientRefChange = onLoanClientRefChange,
            onSearchTarget = onSearchLoanTarget,
            onAmountChange = onLoanAmountChange,
            onCurrencyChange = onLoanCurrencyChange,
            onPinChange = onLoanPinChange,
            onSubmit = onSubmitLoanRepay,
            onReset = onResetLoanRepay,
            onOpenQrScanner = onOpenQrScanner
        )
    }

    // Modal: HISTORIQUE DES OPÉRATIONS
    if (activeModal == "history") {
        AgentHistoryModal(
            history = uiState.agentOperationsHistory,
            onClose = { activeModal = null }
        )
    }

    // Modal Sheet: BASCULER COMMISSION VERS COMPTE PRINCIPAL
    if (uiState.isSweepCommissionDialogOpen) {
        SweepCommissionModalDialog(
            uiState = uiState,
            onClose = onCloseSweepDialog,
            onCurrencyChange = onSweepCurrencyChange,
            onAmountChange = onSweepAmountChange,
            onIsAllChange = onSweepIsAllChange,
            onPinChange = onSweepPinChange,
            onSubmit = onSubmitSweepCommission
        )
    }
}

// ====================================================
// --- AGENT COMMISSION CARD ---
// ====================================================

@Composable
private fun AgentCommissionCard(
    currency: String,
    amount: Double,
    isVisible: Boolean,
    color: Color,
    onSweep: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Commission $currency",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = if (isVisible) "${String.format(java.util.Locale.US, "%,.2f", amount)} $currency" else "•••••• $currency",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Surface(
                onClick = onSweep,
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Basculer",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AgentActionSquare(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontFamily = MulishFontFamily,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

// ====================================================
// --- MODAL: DÉPÔT CLIENT ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentDepositModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dépôt d'argent pour un client",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (uiState.agentDepositSuccess != null) {
                val res = uiState.agentDepositSuccess
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dépôt client réussi !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = res.message ?: "Le compte client a été crédité.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF047857),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Réf : ${res.reference ?: "DEP-CONFIRMED"}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Nouveau Dépôt", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentDepositClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro ou Wallet ID client") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = Color(0xFF00C48C)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USD", "CDF", "EUR").forEach { curr ->
                        val selected = uiState.agentDepositCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) Color(0xFF059669) else Color(0xFFE2E8F0),
                            modifier = Modifier.clickable { onCurrencyChange(curr) }
                        ) {
                            Text(
                                text = curr,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.agentDepositAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant reçu en espèces") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.agentDepositPin,
                    onValueChange = { if (it.length <= 4) onPinChange(it) },
                    label = { Text("Code PIN Agent (4 chiffres)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.agentDepositError != null) {
                    Text(
                        text = uiState.agentDepositError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isAgentDepositLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    if (uiState.isAgentDepositLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirmer le Dépôt", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: RETRAIT CLIENT (DEMANDE & OTP CLIENT) ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentWithdrawModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onChannelChange: (String) -> Unit,
    onClientOtpChange: (String) -> Unit,
    onInitiate: () -> Unit,
    onSubmitOtp: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Retrait demandé par un client",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (uiState.agentWithdrawSuccess != null) {
                val res = uiState.agentWithdrawSuccess
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Retrait validé avec succès !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Vous pouvez remettre les espèces au client.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Réf : ${res.reference ?: "WTH-CONFIRMED"}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Nouveau Retrait", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (uiState.agentWithdrawStep == 1) {
                // Étape 1 : Initialisation de la demande
                Text(
                    text = "Initiez le retrait pour le client. Il devra vous fournir l'OTP reçu pour confirmer.",
                    fontFamily = MulishFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentWithdrawClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro ou Wallet ID client") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD97706),
                            focusedLabelColor = Color(0xFFD97706)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = Color(0xFF00C48C)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USD", "CDF", "EUR").forEach { curr ->
                        val selected = uiState.agentWithdrawCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) Color(0xFFD97706) else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { onCurrencyChange(curr) }
                        ) {
                            Text(
                                text = curr,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.agentWithdrawAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant à retirer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
                        focusedLabelColor = Color(0xFFD97706)
                    )
                )

                // Canal de transmission de l'OTP au client
                Text(
                    text = "Envoyer l'OTP de confirmation via :",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("sms" to "SMS", "whatsapp" to "WhatsApp", "app" to "App").forEach { (code, label) ->
                        val selected = uiState.agentWithdrawChannel == code
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onChannelChange(code) }
                        ) {
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selected) Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onInitiate,
                    enabled = !uiState.isAgentWithdrawLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Initier le Retrait (Envoi OTP)", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Étape 2 : Saisie de l'OTP client
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Demande en attente d'OTP",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Le client (${uiState.agentWithdrawClientRef}) doit vous fournir le code reçu par ${uiState.agentWithdrawChannel.uppercase()}.",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.agentWithdrawClientOtp,
                    onValueChange = onClientOtpChange,
                    label = { Text("Code OTP du Client") },
                    placeholder = { Text("Saisissez les 6 chiffres") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
                        focusedLabelColor = Color(0xFFD97706)
                    )
                )

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onSubmitOtp,
                    enabled = !uiState.isAgentWithdrawLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirmer le Retrait avec l'OTP", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: PRÊT LOAN ME ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentLoanModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onSearchTarget: () -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remboursement Prêt Loan Me",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (uiState.agentLoanRepaySuccess != null) {
                val res = uiState.agentLoanRepaySuccess
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prêt remboursé avec succès !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF5B21B6)
                        )
                        Text(
                            text = res.message ?: "Le prêt du client a été remboursé.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF6D28D9)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Nouveau Remboursement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentLoanClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro ou Wallet ID client") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSearchTarget,
                        enabled = !uiState.isAgentSearchingLoan,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF7C3AED), RoundedCornerShape(12.dp))
                    ) {
                        if (uiState.isAgentSearchingLoan) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher", tint = Color.White)
                        }
                    }
                }

                if (uiState.agentLoanSearchError != null) {
                    Text(
                        text = uiState.agentLoanSearchError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                val target = uiState.agentLoanTarget
                if (target?.loan != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3E8FF)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Client : ${target.client?.name ?: "Client CashPay"}",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF4C1D95)
                            )
                            Text(
                                text = "Solde restant : ${String.format(java.util.Locale.US, "%.2f", target.loan.remainingBalance ?: 0.0)} ${target.loan.currency}",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.agentLoanAmount,
                        onValueChange = onAmountChange,
                        label = { Text("Montant à rembourser (${uiState.agentLoanCurrency})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.agentLoanPin,
                        onValueChange = { if (it.length <= 4) onPinChange(it) },
                        label = { Text("Code PIN Agent (4 chiffres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (uiState.agentLoanRepayError != null) {
                        Text(
                            text = uiState.agentLoanRepayError,
                            fontFamily = MulishFontFamily,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onSubmit,
                        enabled = !uiState.isAgentLoanRepayLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        if (uiState.isAgentLoanRepayLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Confirmer le Remboursement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: HISTORIQUE AGENT ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentHistoryModal(
    history: List<AgentOperationRecord>,
    onClose: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historique des Opérations Agent",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune opération agent enregistrée",
                            fontFamily = MulishFontFamily,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history, key = { it.id }) { op ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = op.title,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = if (!op.clientRef.isNullOrBlank()) "${op.clientRef} • ${op.date}" else op.date,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%,.2f", op.amount)} ${op.currency}",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    if (op.commission > 0.0) {
                                        Text(
                                            text = "+${String.format(java.util.Locale.US, "%.2f", op.commission)} comm.",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color(0xFF059669)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: BASCULER COMMISSION VERS COMPTE PRINCIPAL ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SweepCommissionModalDialog(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onIsAllChange: (Boolean) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val available = when (uiState.sweepCurrency.uppercase()) {
        "CDF" -> uiState.agentCommissionCdf
        "EUR" -> uiState.agentCommissionEur
        else -> uiState.agentCommissionUsd
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00C48C).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = Color(0xFF00C48C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Basculer vers Compte Principal",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Text(
                text = "Sélectionnez la devise de la commission",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("USD", "CDF", "EUR").forEach { curr ->
                    val selected = uiState.sweepCurrency == curr
                    val currAvailable = when (curr) {
                        "CDF" -> uiState.agentCommissionCdf
                        "EUR" -> uiState.agentCommissionEur
                        else -> uiState.agentCommissionUsd
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCurrencyChange(curr) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = curr,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selected) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", currAvailable)}",
                                fontFamily = MulishFontFamily,
                                fontSize = 10.sp,
                                color = if (selected) Color(0xFF00C48C) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Commission disponible :",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF166534)
                    )
                    Text(
                        text = "${String.format(java.util.Locale.US, "%,.2f", available)} ${uiState.sweepCurrency}",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF15803D)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (uiState.sweepIsAll) Color(0xFF00C48C) else Color(0xFFE2E8F0),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onIsAllChange(true) }
                ) {
                    Text(
                        text = "Tout basculer (100%)",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (uiState.sweepIsAll) Color.White else Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!uiState.sweepIsAll) Color(0xFF00C48C) else Color(0xFFE2E8F0),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onIsAllChange(false) }
                ) {
                    Text(
                        text = "Montant personnalisé",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (!uiState.sweepIsAll) Color.White else Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            if (!uiState.sweepIsAll) {
                OutlinedTextField(
                    value = uiState.sweepAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant à basculer (${uiState.sweepCurrency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Text(
                text = "✓ Déplacement instantané de la commission vers votre solde principal ${uiState.sweepCurrency} correspondant, sans conversion de devise.",
                fontFamily = MulishFontFamily,
                fontSize = 11.sp,
                color = Color(0xFF059669)
            )

            OutlinedTextField(
                value = uiState.sweepPin,
                onValueChange = { if (it.length <= 4) onPinChange(it) },
                label = { Text("Confirmez avec votre code PIN (4 chiffres)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (uiState.sweepCommissionError != null) {
                Text(
                    text = uiState.sweepCommissionError,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFFDC2626),
                    fontSize = 12.sp
                )
            }

            if (uiState.sweepCommissionSuccess != null) {
                Text(
                    text = uiState.sweepCommissionSuccess,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onSubmit,
                enabled = !uiState.isSweepingCommission && available > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C48C))
            ) {
                if (uiState.isSweepingCommission) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "Confirmer la Bascule",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
