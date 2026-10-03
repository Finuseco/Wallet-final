package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import com.example.data.local.NotificationEntity
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.WalletResponse
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBlue
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanLinkPink
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanOrange
import com.example.ui.theme.ToofanWhite
import com.example.ui.theme.ToofanYellow
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel
import com.example.util.PdfReceiptGenerator
import com.example.util.QrCodeGenerator
import com.example.util.TransactionTypeMapper
import com.example.ui.components.FloatingCapsuleBottomBar
import com.example.ui.components.QrScannerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()
    val session by dashboardViewModel.session.collectAsStateWithLifecycle()
    val transactions by dashboardViewModel.transactions.collectAsStateWithLifecycle()
    val notifications by dashboardViewModel.notifications.collectAsStateWithLifecycle()

    var showActionPlusDialog by remember { mutableStateOf(false) }
    var showMyQrCodeModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showQrScannerDialog by remember { mutableStateOf(false) }
    var qrScanTarget by remember { mutableStateOf("transfer") } // "transfer", "agent_deposit", "agent_withdraw", "agent_loan"
    var selectedContactForProfile by remember { mutableStateOf<com.example.data.model.PhoneContact?>(null) }

    var showServicesDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            FloatingCapsuleBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { dashboardViewModel.setSelectedTab(it) },
                onCentralActionClick = { showActionPlusDialog = true }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ToofanBgColor)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (uiState.selectedTab) {
                0 -> ToofanDashboardTab(
                    userProfile = userProfile,
                    transactions = transactions,
                    notifications = notifications,
                    walletResponse = uiState.walletResponse,
                    uiState = uiState,
                    dashboardViewModel = dashboardViewModel,
                    onOpenTransfer = { dashboardViewModel.openTransferDialog() },
                    onOpenServices = { showServicesDialog = true },
                    onOpenQr = { showQrDialog = true },
                    onLogout = { authViewModel.logout() },
                    onSettingsClick = { showSettingsModal = true },
                    onContactClick = { selectedContactForProfile = it },
                    onActionPlusClick = { showActionPlusDialog = true }
                )
                1 -> ToofanHistoryTab(
                    userProfile = userProfile,
                    notifications = notifications,
                    uiState = uiState,
                    transactions = transactions,
                    dashboardViewModel = dashboardViewModel
                )
                2 -> ToofanCardsAndSecurityTab(
                    uiState = uiState,
                    userProfile = userProfile,
                    notifications = notifications,
                    session = session,
                    dashboardViewModel = dashboardViewModel,
                    onLogout = { authViewModel.logout() }
                )
                3 -> ToofanProfileTab(
                    userProfile = userProfile,
                    notifications = notifications,
                    dashboardViewModel = dashboardViewModel,
                    onEditClick = { dashboardViewModel.openEditProfileDialog(userProfile) },
                    onNavigateSecurity = { dashboardViewModel.setSelectedTab(2) }
                )
            }
        }
    }

    // Quick Action Launcher Dialog (Triggered by bottom central "+")
    if (showActionPlusDialog) {
        ActionPlusModalDialog(
            userProfile = userProfile,
            onDismiss = { showActionPlusDialog = false },
            onSendScan = {
                showActionPlusDialog = false
                showQrScannerDialog = true
            },
            onReceiveQr = {
                showActionPlusDialog = false
                showQrDialog = true
            },
            onPayPos = {
                showActionPlusDialog = false
                showQrScannerDialog = true
            },
            onWithdraw = {
                showActionPlusDialog = false
                dashboardViewModel.openWithdrawalDialog()
            },
            onAgentDeposit = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentWithdraw = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentLoan = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentHistory = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 1)
            }
        )
    }

    // Settings Modal (Triggered by Top Bar Settings Gear)
    if (showSettingsModal) {
        CashPaySettingsModal(
            onDismiss = { showSettingsModal = false },
            userProfile = userProfile,
            onLogout = { authViewModel.logout() },
            onOpenActivateAgent = {
                showSettingsModal = false
                dashboardViewModel.openActivateAgentDialog()
            },
            onOpenForgotPin = {
                showSettingsModal = false
                dashboardViewModel.openForgotPinDialog(userProfile?.phone)
            }
        )
    }

    // Activate Agent Dialog
    ActivateAgentDialog(
        isOpen = uiState.isActivateAgentDialogOpen,
        plan = uiState.agentPlan,
        pin = uiState.agentPin,
        isLoading = uiState.isActivatingAgent,
        errorMessage = uiState.activateAgentError,
        successMessage = uiState.activateAgentSuccessMessage,
        onPlanChange = { dashboardViewModel.setAgentPlan(it) },
        onPinChange = { dashboardViewModel.setAgentPin(it) },
        onSubmit = { dashboardViewModel.submitActivateAgent(userProfile?.id ?: 1) },
        onDismiss = { dashboardViewModel.closeActivateAgentDialog() }
    )

    // Forgot PIN Dialog in Dashboard
    ForgotPinDialog(
        isOpen = uiState.isForgotPinDialogOpen,
        step = uiState.forgotPinStep,
        phone = uiState.forgotPinPhone,
        channel = uiState.forgotPinChannel,
        otp = uiState.forgotPinOtp,
        newPin = uiState.forgotPinNewPin,
        confirmPin = uiState.forgotPinConfirmPin,
        isLoading = uiState.isForgotPinLoading,
        errorMessage = uiState.forgotPinError,
        successMessage = uiState.forgotPinSuccessMessage,
        onPhoneChange = { dashboardViewModel.setForgotPinPhone(it) },
        onChannelChange = { dashboardViewModel.setForgotPinChannel(it) },
        onOtpChange = { dashboardViewModel.setForgotPinOtp(it) },
        onNewPinChange = { dashboardViewModel.setForgotPinNewPin(it) },
        onConfirmPinChange = { dashboardViewModel.setForgotPinConfirmPin(it) },
        onRequestSubmit = { dashboardViewModel.submitForgotPinRequest() },
        onVerifySubmit = { dashboardViewModel.submitForgotPinVerify() },
        onResetSubmit = { dashboardViewModel.submitForgotPinReset() },
        onDismiss = { dashboardViewModel.closeForgotPinDialog() }
    )

    // Complete Agent Services & Commissions Suite
    AgentServicesDialog(
        isOpen = uiState.isAgentServicesDialogOpen,
        uiState = uiState,
        userProfile = userProfile,
        onDismiss = { dashboardViewModel.closeAgentServicesScreen() },
        onOpenQrScanner = { target ->
            qrScanTarget = target
            showQrScannerDialog = true
        },
        onTabSelected = { dashboardViewModel.setAgentActiveTab(it) },
        onToggleAgentBalanceVisibility = { dashboardViewModel.toggleAgentBalanceVisibility() },
        onCentralActionClick = { showActionPlusDialog = true },
        onOpenSweepDialog = { dashboardViewModel.openSweepCommissionDialog(it) },
        onCloseSweepDialog = { dashboardViewModel.closeSweepCommissionDialog() },
        onSweepCurrencyChange = { dashboardViewModel.setSweepCurrency(it) },
        onSweepAmountChange = { dashboardViewModel.setSweepAmount(it) },
        onSweepIsAllChange = { dashboardViewModel.setSweepIsAll(it) },
        onSweepPinChange = { dashboardViewModel.setSweepPin(it) },
        onSubmitSweepCommission = { dashboardViewModel.submitSweepCommission(userProfile?.id ?: 1) },
        onDepositClientRefChange = { dashboardViewModel.setAgentDepositClientRef(it) },
        onSearchDepositClient = { dashboardViewModel.searchAndIdentifyClientForDeposit() },
        onConfirmDepositClient = { dashboardViewModel.confirmClientForDeposit() },
        onDepositAmountChange = { dashboardViewModel.setAgentDepositAmount(it) },
        onDepositCurrencyChange = { dashboardViewModel.setAgentDepositCurrency(it) },
        onSubmitDepositAmount = { dashboardViewModel.submitAmountForDeposit() },
        onDepositPinChange = { dashboardViewModel.setAgentDepositPin(it) },
        onSubmitDepositPin = { dashboardViewModel.submitPinForDeposit(userProfile?.id ?: 1) },
        onSubmitDeposit = { dashboardViewModel.submitAgentDeposit(userProfile?.id ?: 1) },
        onResetDeposit = { dashboardViewModel.resetAgentDeposit() },
        onWithdrawClientRefChange = { dashboardViewModel.setAgentWithdrawClientRef(it) },
        onWithdrawAmountChange = { dashboardViewModel.setAgentWithdrawAmount(it) },
        onWithdrawCurrencyChange = { dashboardViewModel.setAgentWithdrawCurrency(it) },
        onWithdrawChannelChange = { dashboardViewModel.setAgentWithdrawChannel(it) },
        onWithdrawClientOtpChange = { dashboardViewModel.setAgentWithdrawClientOtp(it) },
        onInitiateWithdraw = {
            when (uiState.agentWithdrawStep) {
                1 -> dashboardViewModel.searchAndIdentifyClientForWithdraw()
                2 -> dashboardViewModel.submitAmountForWithdraw()
                3 -> dashboardViewModel.initiateAgentWithdraw(userProfile?.id ?: 1)
            }
        },
        onSubmitWithdrawOtp = { dashboardViewModel.submitAgentWithdrawOtp(userProfile?.id ?: 1) },
        onResetWithdraw = { dashboardViewModel.resetAgentWithdraw() },
        onLoanClientRefChange = { dashboardViewModel.setAgentLoanClientRef(it) },
        onSearchLoanTarget = { dashboardViewModel.searchAgentLoanTarget() },
        onLoanAmountChange = { dashboardViewModel.setAgentLoanAmount(it) },
        onLoanCurrencyChange = { dashboardViewModel.setAgentLoanCurrency(it) },
        onLoanPinChange = { dashboardViewModel.setAgentLoanPin(it) },
        onSubmitLoanRepay = { dashboardViewModel.submitAgentLoanRepay(userProfile?.id ?: 1) },
        onResetLoanRepay = { dashboardViewModel.resetAgentLoanRepay() }
    )

    // Mini Public Profile Dialog
    selectedContactForProfile?.let { contact ->
        MiniPublicProfileDialog(
            contact = contact,
            onDismiss = { selectedContactForProfile = null },
            onSendMoney = {
                selectedContactForProfile = null
                if (contact.publicProfile != null) {
                    dashboardViewModel.triggerPrefilledTransfer(contact.name, contact.publicProfile)
                } else {
                    dashboardViewModel.onRecipientChanged(contact.name)
                    dashboardViewModel.openTransferDialog()
                }
            }
        )
    }

    // Services & Abonnements Dialog
    if (showServicesDialog) {
        ServicesDialog(
            onDismiss = { showServicesDialog = false },
            onServiceSelected = { service ->
                showServicesDialog = false
                dashboardViewModel.onRecipientChanged(service)
                dashboardViewModel.openTransferDialog()
            }
        )
    }

    // Loans / Crédit Dialog
    if (uiState.isLoansDialogOpen) {
        LoansDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeLoansDialog() }
        )
    }

    // Withdrawal Dialog
    if (uiState.isWithdrawalDialogOpen) {
        WithdrawalDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeWithdrawalDialog() }
        )
    }

    // Add Contact Dialog
    if (uiState.isAddContactDialogOpen) {
        AddContactDialog(
            uiState = uiState,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeAddContactDialog() }
        )
    }

    // Full Contacts Page Dialog
    if (uiState.isContactsPageOpen) {
        ContactsPageDialog(
            uiState = uiState,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeContactsPage() }
        )
    }

    // Transaction Detail Dialog
    uiState.selectedTransactionForDetail?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { dashboardViewModel.selectTransactionForDetail(null) }
        )
    }

    // All Transactions Full Page Dialog
    if (uiState.isAllTransactionsOpen) {
        AllTransactionsDialog(
            uiState = uiState,
            transactions = transactions,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeAllTransactionsPage() }
        )
    }

    // Notification Dialog
    if (uiState.isNotificationDialogOpen) {
        NotificationDialog(
            uiState = uiState,
            notifications = notifications,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeNotificationDialog() }
        )
    }

    // Edit Profile Dialog (with real CashPay PIN check)
    if (uiState.isEditProfileOpen) {
        ToofanEditProfileDialog(
            uiState = uiState,
            userProfile = userProfile,
            onDismiss = { dashboardViewModel.closeEditProfileDialog() },
            onFullNameChange = { dashboardViewModel.onEditFullNameChanged(it) },
            onCityChange = { dashboardViewModel.onEditCityChanged(it) },
            onProfessionChange = { dashboardViewModel.onEditProfessionChanged(it) },
            onAddressChange = { dashboardViewModel.onEditAddressChanged(it) },
            onPinChange = { dashboardViewModel.onEditPinChanged(it) },
            onSubmit = { userProfile?.phone?.let { dashboardViewModel.submitProfileUpdate(it) } }
        )
    }

    // Live Camera QR Scanner Dialog with instant automatic search
    if (showQrScannerDialog) {
        QrScannerDialog(
            onDismissRequest = { 
                showQrScannerDialog = false 
                qrScanTarget = "transfer"
            },
            onQrScanned = { result ->
                val cleaned = extractRecipientFromQr(result)
                when (qrScanTarget) {
                    "agent_withdraw" -> {
                        dashboardViewModel.setAgentWithdrawClientRef(cleaned)
                        dashboardViewModel.searchAndIdentifyClientForWithdraw()
                    }
                    "agent_deposit" -> {
                        dashboardViewModel.setAgentDepositClientRef(cleaned)
                        dashboardViewModel.searchAndIdentifyClientForDeposit()
                    }
                    "agent_loan" -> {
                        dashboardViewModel.setAgentLoanClientRef(cleaned)
                        dashboardViewModel.searchAgentLoanTarget()
                    }
                    else -> {
                        dashboardViewModel.onRecipientChanged(cleaned)
                        dashboardViewModel.openTransferDialog()
                        dashboardViewModel.searchTransferRecipient()
                    }
                }
                showQrScannerDialog = false
                qrScanTarget = "transfer"
            }
        )
    }

    // QR Code Dialog (Genuine ZXing scannable code + Scanner launch shortcut)
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = ToofanGreen)
                        Text("Mon QR Code CashPay", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val walletId = userProfile?.walletId ?: ""
                    val qrContent = "cashpay:$walletId?name=${userProfile?.fullName ?: ""}"
                    val qrBitmap = remember(qrContent) {
                        QrCodeGenerator.generateQrBitmap(qrContent, 280)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ToofanWhite,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(210.dp)
                            .padding(4.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code Réel",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ID: $walletId",
                            fontSize = 18.sp,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = ToofanGreen
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(walletId))
                                Toast.makeText(context, "Wallet ID copié : $walletId", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier Wallet ID",
                                tint = ToofanGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = userProfile?.fullName ?: "",
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ToofanMainDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scanner direct shortcut button requested by user
                    Button(
                        onClick = {
                            showQrDialog = false
                            showQrScannerDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E38)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ouvrir Scanner Caméra (Envoyer / Payer)",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                ToofanButton(
                    title = "Fermer",
                    onClick = { showQrDialog = false },
                    modifier = Modifier.width(100.dp)
                )
            }
        )
    }

    // Quick Transfer Dialog
    if (uiState.isQuickTransferOpen) {
        var pinInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { 
                dashboardViewModel.closeTransferDialog()
                dashboardViewModel.clearPrefilledTransfer()
            },
            shape = RoundedCornerShape(12.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = ToFocusBorderColor)
                    Text(
                        when (uiState.transferStep) {
                            1 -> "Nouveau Transfert"
                            2 -> "Confirmation & Frais"
                            3 -> "Saisir le Code PIN"
                            else -> "Transfert Réussi"
                        },
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.prefilledRecipient != null) {
                        // Display recipient confirmation card (Rule 12: Confirmation du correspondant)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ToofanGreen.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(1.5.dp, ToFocusBorderColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val photoUrl = uiState.prefilledRecipient!!.profilePhotoUrl ?: uiState.prefilledRecipient!!.profilePhoto
                                        val resolved = resolveAvatarUrl(photoUrl)
                                        if (resolved != null) {
                                            AsyncImage(
                                                model = resolved,
                                                contentDescription = "Avatar",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            )
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = ToFocusBorderColor)
                                        }
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = uiState.prefilledContactName ?: uiState.prefilledRecipient!!.fullName,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = ToofanMainDark
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Vérifié",
                                                tint = Color(0xFF00C48C),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = "ID Wallet : ${uiState.prefilledRecipient!!.walletId}",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 11.sp,
                                            color = ToofanBodyText
                                        )
                                        if (uiState.transferCurrency == "BTC" && uiState.prefilledRecipient!!.bitcoin?.available == true) {
                                            Text(
                                                text = "BTC : ${uiState.prefilledRecipient!!.bitcoin?.address?.take(12)}...",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 11.sp,
                                                color = ToFocusBorderColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                TextButton(
                                    onClick = { dashboardViewModel.clearPrefilledTransfer() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Changer", fontFamily = MulishFontFamily, fontSize = 11.sp, color = Color(0xFF0066FF))
                                }
                            }
                        }
                    }

                    if (uiState.transferSuccess) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Transfert validé avec succès !", fontFamily = MulishFontFamily, color = ToofanGreen, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Montant : ${uiState.transferAmount} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Frais : ${uiState.transferFee ?: 0.0} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Total débité : ${uiState.transferTotalDebit ?: 0.0} ${uiState.transferCurrency}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else if (uiState.transferStep == 2) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF7F9FB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Vérifiez les détails du transfert :", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Destinataire : ${uiState.transferRecipient}", fontSize = 13.sp)
                                Text("Montant : ${uiState.transferAmount} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Frais de réseau : ${uiState.transferFee ?: 1.0} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Total à débiter : ${uiState.transferTotalDebit ?: 0.0} ${uiState.transferCurrency}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ToofanGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it.filter { ch -> ch.isDigit() }.take(4) },
                            label = { Text("Code PIN CashPay (4 chiffres)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        // Currency Selector with live user balances
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val fiatMap = uiState.walletResponse?.balances?.fiat ?: emptyMap()
                            val natCode = uiState.walletResponse?.nationalCurrency?.code ?: "CDF"
                            val availableCurrencies = listOf("USD", "CDF", "EUR", "BTC")
                            availableCurrencies.forEach { curr ->
                                val selected = uiState.transferCurrency == curr
                                val balText = when (curr) {
                                    "USD" -> "${String.format(java.util.Locale.US, "%.1f", fiatMap["USD"] ?: 0.0)}$"
                                    "CDF" -> "${String.format(java.util.Locale.US, "%,.0f", fiatMap["CDF"] ?: (fiatMap[natCode] ?: 0.0))}F"
                                    "EUR" -> "${String.format(java.util.Locale.US, "%.1f", fiatMap["EUR"] ?: 0.0)}€"
                                    "BTC" -> "${String.format(java.util.Locale.US, "%.3f", uiState.walletResponse?.bitcoin?.balance ?: 0.0)}"
                                    else -> ""
                                }
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            dashboardViewModel.onCurrencyChanged(curr)
                                            // Auto-update recipient address if prefilledRecipient is available
                                            if (uiState.prefilledRecipient != null) {
                                                if (curr == "BTC" && uiState.prefilledRecipient!!.bitcoin?.available == true) {
                                                    dashboardViewModel.onRecipientChanged(uiState.prefilledRecipient!!.bitcoin!!.address ?: "")
                                                } else {
                                                    dashboardViewModel.onRecipientChanged(uiState.prefilledRecipient!!.walletId)
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) ToofanGreen else Color.White,
                                    border = BorderStroke(1.dp, if (selected) ToofanGreen else Color.LightGray)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(curr, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selected) Color.White else ToofanMainDark)
                                        Text(balText, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = if (selected) Color.White.copy(alpha = 0.9f) else Color.Gray)
                                    }
                                }
                            }
                        }

                        if (uiState.prefilledRecipient == null) {
                            // Dual Search Mode Selector (ID Wallet vs Phone)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isWallet = uiState.transferSearchMode == "wallet"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isWallet) Color(0xFF000E38) else Color.Transparent)
                                        .clickable { dashboardViewModel.setTransferSearchMode("wallet") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🆔 ID Wallet", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (isWallet) Color.White else Color(0xFF475569))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!isWallet) Color(0xFF000E38) else Color.Transparent)
                                        .clickable { dashboardViewModel.setTransferSearchMode("phone") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📱 N° Téléphone", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (!isWallet) Color.White else Color(0xFF475569))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = uiState.transferRecipient,
                                    onValueChange = { dashboardViewModel.onRecipientChanged(it) },
                                    label = {
                                        Text(if (uiState.transferSearchMode == "wallet") "Entrer l'ID Wallet (ex: WAL-XXX)" else "Entrer le numéro (ex: +243...)")
                                    },
                                    placeholder = {
                                        Text(if (uiState.transferSearchMode == "wallet") "WAL-123456" else "+243812345678")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { dashboardViewModel.searchTransferRecipient() },
                                    enabled = !uiState.isSearchingTransferRecipient && uiState.transferRecipient.isNotBlank(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    if (uiState.isSearchingTransferRecipient) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = "Vérifier", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        ToofanInputField(
                            value = uiState.transferAmount,
                            onValueChange = { dashboardViewModel.onAmountChanged(it) },
                            placeholder = "Montant à transférer"
                        )
                    }

                    if (uiState.transferError != null) {
                        Text(
                            text = uiState.transferError!!,
                            color = Color(0xFFFF4868),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            },
            confirmButton = {
                if (uiState.transferSuccess) {
                    ToofanButton(
                        title = "Terminer",
                        onClick = { 
                            dashboardViewModel.closeTransferDialog()
                            dashboardViewModel.clearPrefilledTransfer()
                        },
                        modifier = Modifier.width(100.dp)
                    )
                } else if (uiState.transferStep == 2) {
                    ToofanButton(
                        title = "Valider",
                        onClick = {
                            dashboardViewModel.confirmTransfer(pin = pinInput)
                        },
                        isLoading = uiState.isTransferLoading,
                        modifier = Modifier.width(100.dp)
                    )
                } else {
                    ToofanButton(
                        title = "Suivant",
                        onClick = { dashboardViewModel.previewTransfer() },
                        isLoading = uiState.isTransferLoading,
                        modifier = Modifier.width(100.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    dashboardViewModel.closeTransferDialog()
                    dashboardViewModel.clearPrefilledTransfer()
                }) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            }
        )
    }
}

@Composable
fun CashPayTopHeader(
    userProfile: UserProfileEntity?,
    unreadNotifCount: Int,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    height: androidx.compose.ui.unit.Dp = 240.dp,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000E38), // Official Brand Midnight Blue
                        Color(0xFF0A1C4D),
                        Color(0xFF16255C)
                    )
                )
            )
    ) {
        // Glowing ambient cosmic orbs
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(x = (-30).dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color(0xFFFF6600).copy(alpha = 0.20f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button if subpage + User Avatar + Greeting
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF00E5FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile?.profilePhotoUrl != null) {
                            AsyncImage(
                                model = userProfile.profilePhotoUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_patrick),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Bienvenue",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile?.firstName ?: "",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.badge_verified),
                                contentDescription = "Vérifié",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Right Action Bar: FR Language Indicator, Notification Bell, Settings Gear
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🇫🇷 FR",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onNotificationClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4868))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onSettingsClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Paramètres",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToofanDashboardTab(
    userProfile: UserProfileEntity?,
    transactions: List<TransactionEntity>,
    notifications: List<NotificationEntity>,
    walletResponse: WalletResponse?,
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onOpenTransfer: () -> Unit,
    onOpenServices: () -> Unit,
    onOpenQr: () -> Unit,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onContactClick: (com.example.data.model.PhoneContact) -> Unit = {},
    onActionPlusClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val natCode = walletResponse?.nationalCurrency?.code ?: "CDF"
    val natBal = walletResponse?.balances?.fiat?.get(natCode) ?: 0.0

    val usdBal = walletResponse?.balances?.fiat?.get("USD") ?: 0.0
    val eurBal = walletResponse?.balances?.fiat?.get("EUR") ?: 0.0
    val btcBal = walletResponse?.bitcoin?.balance ?: 0.0
    val btcAddress = walletResponse?.bitcoin?.address ?: ""

    val userWalletId = userProfile?.walletId ?: ""

    // Derive up to 3 recent correspondents dynamically from real transfers & state (NO MOCK DATA)
    val recentCorrespondents = remember(transactions, uiState.recentCorrespondents) {
        val list = mutableListOf<com.example.data.model.PhoneContact>()
        // 1. Add any manually interacted or searched contacts
        uiState.recentCorrespondents.forEach { c ->
            if (list.none { it.name.equals(c.name, ignoreCase = true) || (c.publicProfile?.walletId != null && it.publicProfile?.walletId == c.publicProfile?.walletId) }) {
                list.add(c)
            }
        }
        // 2. Extract unique correspondents from real transfer transactions
        transactions.filter { tx ->
            tx.type in listOf("TR", "TRANSFER", "TRANSFER_RECEIVED", "TRANSFER_SENT") ||
            tx.displayKind == "person" ||
            !tx.otherUserFullName.isNullOrBlank()
        }.forEach { tx ->
            val name = tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title }
            val walletId = if (tx.direction.lowercase() == "incoming") tx.senderWalletId else (tx.receiverWalletId ?: tx.recipient)
            val avatar = tx.otherUserProfilePhoto ?: tx.displayAvatar
            if (name.isNotBlank() && list.none { it.name.equals(name, ignoreCase = true) || (walletId != null && it.publicProfile?.walletId == walletId) }) {
                list.add(
                    com.example.data.model.PhoneContact(
                        name = name,
                        phone = "",
                        normalizedPhone = "",
                        isCashPayUser = true,
                        publicProfile = com.example.data.model.PublicProfileDto(
                            walletId = walletId ?: "",
                            fullName = name,
                            profilePhotoUrl = avatar,
                            profilePhoto = avatar
                        )
                    )
                )
            }
        }
        list.take(3)
    }

    // Filter transactions for Dashboard (limit 5)
    val filteredDashboardTx = remember(transactions, uiState.txDirectionFilter) {
        val filtered = transactions.filter { tx ->
            when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
        }
        filtered.take(5)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Bar Header with Overlapping Balance Cards Carousel
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                CashPayTopHeader(
                    userProfile = userProfile,
                    unreadNotifCount = unreadNotifCount,
                    onNotificationClick = { dashboardViewModel.openNotificationDialog() },
                    onSettingsClick = onSettingsClick
                )

                // Balance Cards mounted directly overlapping the header at top = 165.dp
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 165.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        when (page) {
                            0 -> {
                                // Card 1: Devise Nationale (CDF)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .height(180.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.linearGradient(listOf(Color(0xFF065F46), Color(0xFF047857), Color(0xFF10B981))))
                                            .padding(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .size(110.dp)
                                                .align(Alignment.BottomEnd)
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Solde Devise Nationale",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    IconButton(
                                                        onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                            contentDescription = "Afficher/Masquer",
                                                            tint = Color.White.copy(alpha = 0.85f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                val cdfCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == natCode.uppercase() }
                                                if (cdfCard != null) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Image(
                                                            painter = painterResource(id = if (cdfCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                            contentDescription = cdfCard.brand ?: "",
                                                            modifier = Modifier.height(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = ".... ${cdfCard.last4 ?: ""}",
                                                            fontFamily = MulishFontFamily,
                                                            fontSize = 12.sp,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = if (uiState.isBalanceVisible) "${String.format("%,.2f", natBal)} FC" else "•••••••• FC",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 24.sp,
                                                color = Color.White
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "N° de compte CP : $userWalletId",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )

                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(userWalletId))
                                                        Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copier",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // Card 2: Dollar Wallet (USD)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .height(180.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))))
                                            .padding(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .size(110.dp)
                                                .align(Alignment.BottomEnd)
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Solde Dollar USD",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    IconButton(
                                                        onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                            contentDescription = "Afficher/Masquer",
                                                            tint = Color.White.copy(alpha = 0.85f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                val usdCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == "USD" }
                                                if (usdCard != null) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Image(
                                                            painter = painterResource(id = if (usdCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                            contentDescription = usdCard.brand ?: "",
                                                            modifier = Modifier.height(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = ".... ${usdCard.last4 ?: ""}",
                                                            fontFamily = MulishFontFamily,
                                                            fontSize = 12.sp,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = if (uiState.isBalanceVisible) "$${String.format("%,.2f", usdBal)}" else "•••••••• $",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 30.sp,
                                                color = Color.White
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "N° de compte CP : $userWalletId",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )

                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(userWalletId))
                                                        Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copier",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                // Card 3: Euro Wallet (€)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .height(180.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF1E40AF))))
                                            .padding(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .size(110.dp)
                                                .align(Alignment.BottomEnd)
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Solde Euro EUR",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    IconButton(
                                                        onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                            contentDescription = "Afficher/Masquer",
                                                            tint = Color.White.copy(alpha = 0.85f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                val eurCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == "EUR" }
                                                if (eurCard != null) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Image(
                                                            painter = painterResource(id = if (eurCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                            contentDescription = eurCard.brand ?: "",
                                                            modifier = Modifier.height(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = ".... ${eurCard.last4 ?: ""}",
                                                            fontFamily = MulishFontFamily,
                                                            fontSize = 12.sp,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = if (uiState.isBalanceVisible) "€${String.format("%.2f", eurBal)}" else "•••••••• €",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 32.sp,
                                                color = Color.White
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "N° de compte CP : $userWalletId",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )

                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(userWalletId))
                                                        Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copier",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                // Card 4: Bitcoin Crypto Wallet (Mini Blockchain)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .height(180.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    elevation = CardDefaults.cardElevation(3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CurrencyBitcoin,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.08f),
                                            modifier = Modifier
                                                .size(120.dp)
                                                .align(Alignment.BottomEnd)
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Solde Bitcoin",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    IconButton(
                                                        onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                            contentDescription = "Afficher/Masquer",
                                                            tint = Color.White.copy(alpha = 0.85f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFF1E293B)
                                                ) {
                                                    Text(
                                                        text = "Mini Blockchain",
                                                        fontFamily = MulishFontFamily,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFFF59E0B),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CurrencyBitcoin,
                                                    contentDescription = "BTC",
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(26.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (uiState.isBalanceVisible) String.format("%.8f", btcBal) else "•••••••• BTC",
                                                    fontFamily = MulishFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    color = Color.White
                                                )
                                            }

                                            // Shortened BTC Address Script
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                val displayAddr = if (btcAddress.length > 18) {
                                                    "${btcAddress.take(8)}...${btcAddress.takeLast(6)}"
                                                } else btcAddress

                                                Text(
                                                    text = "Adresse BTC : $displayAddr",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 11.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )

                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(btcAddress))
                                                        Toast.makeText(context, "Adresse BTC copiée !", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copier BTC",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }

                                            // Quick Action Pills
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                BtcActionPill("Recevoir") { onOpenTransfer() }
                                                BtcActionPill("Envoyer") { onOpenTransfer() }
                                                BtcActionPill("Acheter") { onOpenTransfer() }
                                                BtcActionPill("Vendre") { onOpenTransfer() }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Carousel Indicator Dots (4 dots for the 4 balance cards)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { i ->
                            val isSelected = pagerState.currentPage == i
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) Color(0xFF00C48C) else Color(0xFFCBD5E1))
                                    .clickable {
                                        coroutineScope.launch { pagerState.animateScrollToPage(i) }
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Agent (if role agent), Retrait, Crédit, Transfer, Payment, Services
        val isAgentUser = userProfile?.role?.lowercase()?.trim() == "agent"

        if (isAgentUser) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { dashboardViewModel.openAgentServicesScreen() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF047857),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Espace Agent CashPay Agréé",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Commissions : ${if (uiState.isAgentBalanceVisible) "${String.format(java.util.Locale.US, "%.2f", uiState.agentCommissionUsd)} $ • ${String.format(java.util.Locale.US, "%,.0f", uiState.agentCommissionCdf)} FC" else "••••••••"}",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "Gérer",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ligne 1: 4 boutons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToofanActionSquare(
                        title = "Envoyer\nTransfert",
                        bgColor = Color(0xFF0066FF),
                        icon = Icons.Default.Send,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenTransfer
                    )
                    ToofanActionSquare(
                        title = "Retrait\nEspèces",
                        bgColor = Color(0xFFFF6600),
                        icon = Icons.Default.CallReceived,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.openWithdrawalDialog() }
                    )
                    ToofanActionSquare(
                        title = "Crédit\n& Prêts",
                        bgColor = Color(0xFF00B386),
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.openLoansDialog() }
                    )
                    ToofanActionSquare(
                        title = if (isAgentUser) "Espace\nAgent" else "Scanner\n& Payer",
                        bgColor = Color(0xFF000E38),
                        icon = if (isAgentUser) Icons.Default.SupportAgent else Icons.Default.QrCode,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isAgentUser) dashboardViewModel.openAgentServicesScreen()
                            else onOpenQr()
                        }
                    )
                }

                // Ligne 2: 4 boutons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToofanActionSquare(
                        title = "Dépôt\nRecharger",
                        bgColor = Color(0xFF10B981),
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isAgentUser) {
                                dashboardViewModel.openAgentServicesScreen(0)
                            } else {
                                onOpenTransfer()
                            }
                        }
                    )
                    ToofanActionSquare(
                        title = "Paiement\nFactures",
                        bgColor = Color(0xFF8B5CF6),
                        icon = Icons.Default.Receipt,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenServices
                    )
                    ToofanActionSquare(
                        title = "Mes\nCartes",
                        bgColor = Color(0xFFEC4899),
                        icon = Icons.Default.CreditCard,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.setSelectedTab(2) }
                    )
                    ToofanActionSquare(
                        title = "Voir\nPlus",
                        bgColor = Color(0xFF64748B),
                        icon = Icons.Default.FilterList,
                        modifier = Modifier.weight(1f),
                        onClick = onActionPlusClick
                    )
                }
            }
        }

        // Section Contacts Récents
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contacts récents",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Voir tout",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48),
                        modifier = Modifier.clickable { dashboardViewModel.openContactsPage() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val ringColors = listOf(Color(0xFF00C48C), Color(0xFFF59E0B), Color(0xFF3B82F6))

                    // Up to 3 recent correspondents from transfers
                    recentCorrespondents.take(3).forEachIndexed { index, correspondent ->
                        RecentCorrespondentItem(
                            contact = correspondent,
                            ringColor = ringColors.getOrElse(index) { Color(0xFF00C48C) },
                            onClick = { onContactClick(correspondent) }
                        )
                    }

                    // Button 4: "+ Ajouter"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable { dashboardViewModel.openAddContactDialog() }
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFF60A5FA).copy(alpha = 0.85f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ajouter",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Button 5: "Contacts"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable { dashboardViewModel.openContactsPage() }
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFF0F172A)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Contacts",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Contacts",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Section Transactions & Filter Capsule
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Voir tout",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48),
                        modifier = Modifier.clickable { dashboardViewModel.setSelectedTab(1) }
                    )
                }

                // Filter Capsule Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                            val selected = uiState.txDirectionFilter == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { dashboardViewModel.setTxDirectionFilter(code) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0xFF00C48C) else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dashboard Recent Transactions List (limit 5)
        items(filteredDashboardTx, key = { it.id }) { tx ->
            TransactionItemRow(
                tx = tx,
                onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
            )
        }

        // Official Partners & Payment Networks Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Réseaux & Partenaires Officiels",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToofanMainDark
                        )
                        Image(
                            painter = painterResource(id = R.drawable.badge_verified),
                            contentDescription = "Partenaires certifiés",
                            modifier = Modifier.height(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Mobile Money pris en charge",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ToofanBodyText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartnerLogoPill(drawableId = R.drawable.logo_mpesa, name = "M-Pesa")
                        PartnerLogoPill(drawableId = R.drawable.logo_orange_money, name = "Orange")
                        PartnerLogoPill(drawableId = R.drawable.logo_airtel_money, name = "Airtel")
                        PartnerLogoPill(drawableId = R.drawable.logo_mtn_money, name = "MTN")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Cartes & Transferts Internationaux",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ToofanBodyText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartnerLogoPill(drawableId = R.drawable.logo_visa, name = "Visa")
                        PartnerLogoPill(drawableId = R.drawable.logo_mastercard, name = "Mastercard")
                        PartnerLogoPill(drawableId = R.drawable.logo_paypal, name = "PayPal")
                        PartnerLogoPill(drawableId = R.drawable.logo_western_union, name = "Western Union")
                        PartnerLogoPill(drawableId = R.drawable.logo_moneygram, name = "MoneyGram")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = ToofanGreen.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ToofanGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Conformité de paiement sécurisé",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ToofanGreen
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.logo_pci_dss),
                                contentDescription = "PCI DSS Certified",
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BtcActionPill(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TransactionItemRow(
    tx: TransactionEntity,
    onClick: () -> Unit
) {
    val isCredit = tx.direction.lowercase() == "incoming"
    val typeInfo = TransactionTypeMapper.getTypeInfo(tx.type)
    val isPerson = tx.displayKind == "person" || (typeInfo.isPersonToPerson && !tx.otherUserFullName.isNullOrEmpty())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar for Person or Styled Service Icon for Service
                if (isPerson) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isCredit) Color(0xFF00C48C) else Color(0xFF3B82F6), CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarUrl = tx.displayAvatar ?: tx.otherUserProfilePhoto
                            if (!avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                val initials = (tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title })
                                    .take(2).uppercase()
                                Text(
                                    text = initials,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isCredit) Color(0xFF00C48C) else Color(0xFF3B82F6)
                                )
                            }
                        }
                        // Green status indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF00C48C))
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                } else {
                    // Service Icon (Airtel Money, Netflix, etc.)
                    val isOrangeService = tx.title.contains("Airtel", ignoreCase = true) || tx.title.contains("Orange", ignoreCase = true)
                    val serviceBgColor = if (isOrangeService) Color(0xFFF59E0B) else Color(0xFF0D9488)
                    Surface(
                        shape = CircleShape,
                        color = serviceBgColor,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = typeInfo.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    val rawName = tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title }
                    val heading = if (isPerson) {
                        if (isCredit) {
                            if (!rawName.startsWith("De", ignoreCase = true)) "De : $rawName" else rawName
                        } else {
                            if (!rawName.startsWith("À", ignoreCase = true) && !rawName.startsWith("Vous →")) "À : $rawName" else rawName
                        }
                    } else {
                        tx.displayName.ifBlank { tx.title }
                    }

                    Text(
                        text = heading,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    val typeText = if (isPerson) {
                        if (isCredit) "Transfert reçu • Crédit" else "Transfert envoyé • Débit"
                    } else {
                        TransactionTypeMapper.getDisplayTitle(tx.type, tx.direction)
                    }
                    Text(
                        text = typeText,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Amount, Fee & Date/Time on Right
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${if (isCredit) "+" else "-"} ${String.format("%,.2f", tx.amount)} ${tx.currency}",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isCredit) Color(0xFF00C48C) else Color(0xFFE11D48)
                )

                // Only show fees if outgoing (debit). Recipient does not pay/see sender's transaction fee.
                if (!isCredit && tx.fee > 0.0) {
                    Text(
                        text = "Frais : - ${String.format("%,.2f", tx.fee)} ${tx.currency}",
                        fontFamily = MulishFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFFE11D48).copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = tx.date,
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Détails",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TransactionDetailDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isCredit = transaction.direction.lowercase() == "incoming"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier
                        .height(32.dp)
                        .padding(bottom = 6.dp)
                )
                Text(
                    text = "Bordereau Officiel de Transaction",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToofanMainDark
                )
                Text(
                    text = "CashPay • Powered by FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Status & Amount Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCredit) Color(0xFFE8F5E9) else Color(0xFFFFECEF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isCredit) ToofanGreen else Color(0xFFFF4868),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCredit) "CRÉDIT REÇU / TRANSACTION ENTRÉE" else "DÉBIT EFFECTUÉ / TRANSFERT ENVOYÉ",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (isCredit) ToofanGreen else Color(0xFFFF4868),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${if (isCredit) "+" else "-"} ${String.format("%.2f", transaction.amount)} ${transaction.currency}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = ToofanMainDark
                        )

                        Text(
                            text = "Statut : ${transaction.status.uppercase()} • CERTIFIÉ",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCredit) ToofanGreen else Color(0xFF2E7D32),
                            fontSize = 11.sp
                        )
                    }
                }

                // Financial Breakdown (Montant, Frais, Total)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!isCredit) {
                            DetailRow("Montant Transféré :", "- ${String.format("%.2f", transaction.amount)} ${transaction.currency}", isBold = true)
                            if (transaction.fee > 0.0) {
                                DetailRow("Frais Réseau / Service :", "- ${String.format("%.2f", transaction.fee)} ${transaction.currency}")
                            }
                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                            DetailRow("Total Débité :", "- ${String.format("%.2f", transaction.amount + transaction.fee)} ${transaction.currency}", isBold = true, customColor = Color(0xFFE11D48))
                        } else {
                            DetailRow("Montant Reçu :", "+ ${String.format("%.2f", transaction.amount)} ${transaction.currency}", isBold = true, customColor = Color(0xFF00C48C))
                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                            DetailRow("Total Net Crédité :", "+ ${String.format("%.2f", transaction.amount)} ${transaction.currency}", isBold = true, customColor = Color(0xFF00C48C))
                        }
                    }
                }

                // Parties and Reference Details
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val typeInfo = TransactionTypeMapper.getTypeInfo(transaction.type)
                        DetailRow("Type Opération :", "${typeInfo.nameFr} (${transaction.type})")
                        DetailRow("Référence Transaction :", transaction.reference, isBold = true)
                        DetailRow("Intitulé / Libellé :", transaction.displayName.ifBlank { transaction.title })
                        val otherName = transaction.otherUserFullName ?: transaction.displayName.ifBlank { transaction.title }
                        if (isCredit) {
                            DetailRow("Expéditeur (Source) :", otherName, isBold = true)
                            if (!transaction.senderWalletId.isNullOrEmpty()) {
                                DetailRow("Portefeuille Émetteur :", transaction.senderWalletId)
                            }
                        } else {
                            DetailRow("Bénéficiaire (Cible) :", otherName, isBold = true)
                            if (!transaction.receiverWalletId.isNullOrEmpty() || transaction.recipient.isNotBlank()) {
                                DetailRow("Portefeuille Cible :", transaction.receiverWalletId ?: transaction.recipient)
                            }
                        }
                        DetailRow("Horodatage :", transaction.date)
                    }
                }

                // Genuine ZXing QR Code Display for Authentication
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val qrBitmap = remember(transaction.reference) {
                            QrCodeGenerator.generateQrBitmap("https://cashpay-all.com/tx/${transaction.reference}", 240)
                        }
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code Réel de Transaction",
                            modifier = Modifier
                                .size(130.dp)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scannez pour vérifier l'authenticité",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Télécharger / Partager Reçu PDF",
                onClick = {
                    PdfReceiptGenerator.generateAndShareReceiptPdf(context, transaction)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fermer", fontFamily = MulishFontFamily, color = ToofanBodyText, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false, customColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = MulishFontFamily)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = customColor ?: ToofanMainDark,
            fontFamily = MulishFontFamily
        )
    }
}

@Composable
private fun ToofanHistoryTab(
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    uiState: DashboardUiState,
    transactions: List<TransactionEntity>,
    dashboardViewModel: DashboardViewModel
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    val filteredTx = remember(transactions, uiState.txDirectionFilter, uiState.txSearchQuery) {
        transactions.filter { tx ->
            val matchDir = when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
            val matchQuery = if (uiState.txSearchQuery.isBlank()) true else {
                tx.title.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.reference.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.displayName.contains(uiState.txSearchQuery, ignoreCase = true)
            }
            matchDir && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            CashPayTopHeader(
                userProfile = userProfile,
                unreadNotifCount = unreadNotifCount,
                onNotificationClick = { dashboardViewModel.openNotificationDialog() },
                onSettingsClick = { dashboardViewModel.setSelectedTab(2) },
                height = 140.dp
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Historique des Transactions",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )

                ToofanInputField(
                    value = uiState.txSearchQuery,
                    onValueChange = { dashboardViewModel.onSearchQueryChanged(it) },
                    placeholder = "Rechercher par nom, référence ou montant..."
                )

                // Direction Filters Capsule
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                            val selected = uiState.txDirectionFilter == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { dashboardViewModel.setTxDirectionFilter(code) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0xFF00C48C) else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        items(filteredTx, key = { it.id }) { tx ->
            TransactionItemRow(
                tx = tx,
                onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
            )
        }

        if (filteredTx.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune transaction trouvée",
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllTransactionsDialog(
    uiState: DashboardUiState,
    transactions: List<TransactionEntity>,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    var fromInput by remember { mutableStateOf(uiState.customFromDate) }
    var toInput by remember { mutableStateOf(uiState.customToDate) }

    val filteredTx = remember(transactions, uiState.txDirectionFilter, uiState.txSearchQuery) {
        transactions.filter { tx ->
            val matchDir = when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
            val matchQuery = if (uiState.txSearchQuery.isBlank()) true else {
                tx.title.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.reference.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.displayName.contains(uiState.txSearchQuery, ignoreCase = true)
            }
            matchDir && matchQuery
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ToofanBgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                color = ToofanWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = ToofanMainDark)
                    }
                    Text(
                        text = "Historique des Transactions",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ToofanMainDark
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input
                ToofanInputField(
                    value = uiState.txSearchQuery,
                    onValueChange = { dashboardViewModel.onSearchQueryChanged(it) },
                    placeholder = "Rechercher par nom, référence ou montant..."
                )

                // Direction Filters
                Text("Sens de transaction :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                        val selected = uiState.txDirectionFilter == code
                        Surface(
                            modifier = Modifier.clickable { dashboardViewModel.setTxDirectionFilter(code) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) ToofanGreen else Color.White,
                            border = BorderStroke(1.dp, if (selected) ToofanGreen else Color.LightGray)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else ToofanMainDark
                            )
                        }
                    }
                }

                // Date Filters
                Text("Période :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "Toutes", "TODAY" to "Aujourd'hui", "WEEK" to "Cette semaine", "MONTH" to "Ce mois", "CUSTOM" to "Date").forEach { (code, label) ->
                        val selected = uiState.txDateFilter == code
                        Surface(
                            modifier = Modifier.clickable { dashboardViewModel.setTxDateFilter(code) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) ToofanBlue else Color.White,
                            border = BorderStroke(1.dp, if (selected) ToofanBlue else Color.LightGray)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else ToofanBodyText
                            )
                        }
                    }
                }

                if (uiState.txDateFilter == "CUSTOM") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fromInput,
                            onValueChange = { fromInput = it },
                            label = { Text("Du (YYYY-MM-DD)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = toInput,
                            onValueChange = { toInput = it },
                            label = { Text("Au (YYYY-MM-DD)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        ToofanButton(
                            title = "Filtrer",
                            onClick = { dashboardViewModel.setCustomDates(fromInput, toInput) },
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
            ) {
                items(filteredTx, key = { it.id }) { tx ->
                    TransactionItemRow(
                        tx = tx,
                        onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartnerLogoPill(
    drawableId: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ToofanGrey1.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        color = ToofanWhite,
        shadowElevation = 0.5.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = name,
                modifier = Modifier.height(22.dp)
            )
        }
    }
}

@Composable
private fun ToofanServiceSquare(
    title: String,
    bgColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ToofanProfileTab(
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    dashboardViewModel: DashboardViewModel,
    onEditClick: () -> Unit,
    onNavigateSecurity: () -> Unit = {}
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            CashPayTopHeader(
                userProfile = userProfile,
                unreadNotifCount = unreadNotifCount,
                onNotificationClick = { dashboardViewModel.openNotificationDialog() },
                onSettingsClick = onNavigateSecurity,
                height = 140.dp
            )
        }

        item {
            // Profile Header Card with Avatar & Edit Action
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
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
                        if (userProfile?.profilePhotoUrl != null) {
                            AsyncImage(
                                model = userProfile.profilePhotoUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_patrick),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userProfile?.fullName ?: "",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ToofanMainDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Image(
                            painter = painterResource(id = R.drawable.badge_verified),
                            contentDescription = "Compte Certifié",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = userProfile?.profession ?: "—",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        color = ToofanBodyText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ToofanButton(
                        title = "Modifier les informations (PIN)",
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "edit_profile_button"
                    )
                }
            }
        }

        // Category 1: Identité & Contact
        item {
            ToofanProfileCategorySection(
                title = "Identité & Contact",
                icon = Icons.Default.Badge,
                items = listOf(
                    "Nom complet" to (userProfile?.fullName ?: "—"),
                    "Prénom" to (userProfile?.firstName ?: "—"),
                    "Nom" to (userProfile?.lastName ?: "—"),
                    "Post-nom" to (userProfile?.middleName ?: "—"),
                    "Genre" to (userProfile?.gender ?: "—"),
                    "État civil" to (userProfile?.maritalStatus ?: "—"),
                    "Date de naissance" to (userProfile?.birthDate ?: "—"),
                    "Lieu de naissance" to (userProfile?.birthPlace ?: "—"),
                    "Nationalité" to (userProfile?.nationality ?: "—"),
                    "Téléphone" to (userProfile?.phone ?: "—"),
                    "Email" to (userProfile?.email ?: "—")
                )
            )
        }

        // Category 2: Localisation & Résidence
        item {
            ToofanProfileCategorySection(
                title = "Localisation & Résidence",
                icon = Icons.Default.LocationOn,
                items = listOf(
                    "Pays" to (userProfile?.country ?: "—"),
                    "Province" to (userProfile?.province ?: "—"),
                    "Ville" to (userProfile?.city ?: "—"),
                    "Adresse" to (userProfile?.address ?: "—"),
                    "Agence" to (userProfile?.representative ?: "—")
                )
            )
        }

        // Category 3: Emploi & Finances
        item {
            ToofanProfileCategorySection(
                title = "Emploi & Finances",
                icon = Icons.Default.Work,
                items = listOf(
                    "Profession" to (userProfile?.profession ?: "—"),
                    "Activité" to (userProfile?.activityDescription ?: "—"),
                    "Revenu mensuel" to (userProfile?.incomePerMonth ?: "—"),
                    "Crypto actif" to (if (userProfile?.isCryptoActive == true) "Oui" else "Non")
                )
            )
        }

        // Category 4: Système & USSD
        item {
            ToofanProfileCategorySection(
                title = "Système & USSD",
                icon = Icons.Default.Shield,
                items = listOf(
                    "Wallet ID" to (userProfile?.walletId ?: "—"),
                    "Langue" to (userProfile?.language ?: "—"),
                    "Langue USSD" to (userProfile?.ussdLanguage ?: "—"),
                    "Canal de notif." to (userProfile?.notificationsChannel ?: "—"),
                    "Statut" to (userProfile?.status ?: "—"),
                    "KYC" to (userProfile?.verificationStatus ?: "—")
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ToofanProfileCategorySection(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ToofanWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ToofanGreen.copy(alpha = 0.12f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                    }
                }
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ToofanMainDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            items.forEachIndexed { index, (k, v) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = k, fontFamily = MulishFontFamily, fontSize = 13.sp, color = ToofanBodyText)
                    Text(text = v, fontFamily = MulishFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ToofanMainDark)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(color = ToofanGrey1.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ToofanSecurityTab(
    session: SessionEntity?,
    userProfile: UserProfileEntity?,
    onToggleBiometrics: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var isBiometricActive by remember { mutableStateOf(session?.biometricEnabled ?: true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sécurité & Accès",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ToofanMainDark
            )
        }

        // Biometrics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Déverrouillage biométrique", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ToofanMainDark)
                            Text("Empreinte & Reconnaissance faciale", fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
                        }
                    }

                    Switch(
                        checked = isBiometricActive,
                        onCheckedChange = {
                            isBiometricActive = it
                            onToggleBiometrics(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ToofanGreen
                        ),
                        modifier = Modifier.testTag("biometric_switch")
                    )
                }
            }
        }

        // PIN validation card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Code PIN CashPay", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ToofanMainDark)
                            Text("Requis pour transactions & modifications", fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ToofanButton(
                        title = "Tester modification profil avec PIN",
                        onClick = onEditProfile
                    )
                }
            }
        }

        // Logout Button
        item {
            Button(
                onClick = onLogout,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFECEF),
                    contentColor = Color(0xFFFF4868)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se déconnecter", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ToofanEditProfileDialog(
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onProfessionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = ToofanGreen)
                Text("Modifier le profil", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Appel PATCH /api/v1/auth/me avec vérification PIN",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = ToofanGreen
                )

                ToofanInputField(
                    value = uiState.editFullName,
                    onValueChange = onFullNameChange,
                    placeholder = "Nom complet"
                )

                ToofanInputField(
                    value = uiState.editCity,
                    onValueChange = onCityChange,
                    placeholder = "Ville / Commune"
                )

                ToofanInputField(
                    value = uiState.editProfession,
                    onValueChange = onProfessionChange,
                    placeholder = "Profession"
                )

                ToofanInputField(
                    value = uiState.editAddress,
                    onValueChange = onAddressChange,
                    placeholder = "Adresse"
                )

                ToofanInputField(
                    value = uiState.editPinConfirmation,
                    onValueChange = onPinChange,
                    placeholder = "Code PIN CashPay"
                )

                if (uiState.editErrorMessage != null) {
                    Text(
                        text = uiState.editErrorMessage,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFFFF4868)
                    )
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Valider",
                onClick = onSubmit,
                isLoading = uiState.isEditingLoading,
                modifier = Modifier.width(100.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
private fun NotificationDialog(
    uiState: DashboardUiState,
    notifications: List<NotificationEntity>,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val unreadCount = remember(notifications) { notifications.count { !it.read } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = ToofanGreen)
                    Text("Notifications ($unreadCount non lues)", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                if (unreadCount > 0) {
                    Text(
                        text = "Tout marquer lu",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ToofanGreen,
                        modifier = Modifier.clickable { dashboardViewModel.markAllNotificationsAsRead() }
                    )
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune notification pour le moment",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        color = ToofanBodyText
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        val (icon, tint) = when (notif.type.lowercase()) {
                            "new_message" -> Icons.Default.Chat to ToofanGreen
                            "missed_audio_call", "missed_video_call" -> Icons.Default.Phone to Color(0xFFFF4868)
                            "pos_sale" -> Icons.Default.ShoppingCart to ToofanBlue
                            "game_invite" -> Icons.Default.SportsEsports to ToofanOrange
                            "new_follower" -> Icons.Default.PersonAdd to Color(0xFF8B5CF6)
                            "meeting_invite" -> Icons.Default.Event to Color(0xFF14B8A6)
                            else -> Icons.Default.Notifications to ToofanBodyText
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!notif.read) ToofanGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (!notif.read) ToofanGreen.copy(alpha = 0.3f) else ToofanGrey1.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!notif.read) {
                                        dashboardViewModel.markNotificationAsRead(notif.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = tint.copy(alpha = 0.15f),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = if (!notif.read) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = ToofanMainDark
                                        )
                                        if (!notif.read) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(ToofanGreen)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.description,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = ToofanBodyText
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.timestamp,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Fermer",
                onClick = onDismiss,
                modifier = Modifier.width(100.dp)
            )
        }
    )
}

@Composable
fun ToofanCardsAndSecurityTab(
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    session: SessionEntity?,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }
    var isCardsTabActive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        CashPayTopHeader(
            userProfile = userProfile,
            unreadNotifCount = unreadNotifCount,
            onNotificationClick = { dashboardViewModel.openNotificationDialog() },
            onSettingsClick = { isCardsTabActive = false },
            height = 190.dp
        )

        // Top Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isCardsTabActive) ToofanGreen else Color.Transparent)
                    .clickable { isCardsTabActive = true },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = if (isCardsTabActive) Color.White else ToofanMainDark, modifier = Modifier.size(16.dp))
                    Text(
                        "Mes Cartes",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isCardsTabActive) Color.White else ToofanMainDark
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (!isCardsTabActive) ToofanGreen else Color.Transparent)
                    .clickable { isCardsTabActive = false },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = if (!isCardsTabActive) Color.White else ToofanMainDark, modifier = Modifier.size(16.dp))
                    Text(
                        "Sécurité & PIN",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!isCardsTabActive) Color.White else ToofanMainDark
                    )
                }
            }
        }

        if (isCardsTabActive) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Vos cartes de paiement",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ToofanMainDark
                            )
                            Text(
                                "Visa et Mastercard prêtes à l'emploi",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = ToofanBodyText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { dashboardViewModel.openBuyCardDialog() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                                Text("Commander", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ToofanGreen)
                            }
                        }
                    }
                }

                if (uiState.isLoadingCards) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ToofanGreen)
                        }
                    }
                } else if (uiState.userCards.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                            border = BorderStroke(1.5.dp, ToofanGrey1.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = ToofanGrey1, modifier = Modifier.size(48.dp))
                                Text(
                                    text = "Aucune carte active",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ToofanMainDark
                                )
                                Text(
                                    text = "Commandez votre carte Visa ou Mastercard virtuelle ou physique pour effectuer des paiements sécurisés partout dans le monde.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = ToofanBodyText,
                                    textAlign = TextAlign.Center
                                )
                                ToofanButton(
                                    title = "Commander une carte",
                                    onClick = { dashboardViewModel.openBuyCardDialog() },
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.userCards) { card ->
                        CashPayCardComponent(
                            card = card,
                            onFreeze = { dashboardViewModel.freezeCard(card.id ?: "") },
                            onUnfreeze = { dashboardViewModel.unfreezeCard(card.id ?: "") },
                            onManage = { dashboardViewModel.openManageCard(card) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            ToofanSecurityTab(
                session = session,
                userProfile = userProfile,
                onToggleBiometrics = { dashboardViewModel.toggleBiometrics(it) },
                onEditProfile = { dashboardViewModel.openEditProfileDialog(userProfile) },
                onLogout = onLogout
            )
        }
    }

    // Buy Card Dialog Flow (4 Steps)
    if (uiState.isBuyCardDialogOpen) {
        BuyCardDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeBuyCardDialog() }
        )
    }

    // Manage Card Settings Dialog
    uiState.selectedCardForManage?.let { card ->
        ManageCardDialog(
            card = card,
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeManageCard() }
        )
    }

    // Reveal Sensitive Card Details Challenge Modal
    if (uiState.isRevealDetailsDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            RevealCardDetailsDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeRevealDetails() }
            )
        }
    }

    // Source de Financement Modal
    if (uiState.isFundingDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            FundingSourceDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeFundingDialog() }
            )
        }
    }

    // PIN Update Modal
    if (uiState.isChangePinDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            ChangeCardPinDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeChangePin() }
            )
        }
    }

    // Withdrawal Dialog Modal
    if (uiState.isWithdrawalDialogOpen) {
        WithdrawalDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeWithdrawalDialog() }
        )
    }

    // Loans Dialog Modal
    if (uiState.isLoansDialogOpen) {
        LoansDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeLoansDialog() }
        )
    }
}

@Composable
fun CashPayCardComponent(
    card: com.example.data.model.CardInfoDto,
    onFreeze: () -> Unit,
    onUnfreeze: () -> Unit,
    onManage: () -> Unit
) {
    val isVisa = card.brand?.lowercase() == "visa"
    val isFrozen = card.status?.lowercase() == "frozen" || card.status?.lowercase() == "inactive"
    val isPending = card.status?.lowercase() == "pending"

    // Card background linear gradient
    val brush = if (isVisa) {
        Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF3B82F6)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF311045), Color(0xFF701A75), Color(0xFFD946EF)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
                .padding(18.dp)
        ) {
            // Watermark icon with soft opacity
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header with CashPay brand text & Provider/Brand Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CASH PAY",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                    // Real Card Brand Logo Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isVisa) "VISA" else "MASTERCARD",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Masked PAN
                Text(
                    text = "••••  ••••  ••••  ${card.last4 ?: ""}",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                // Footer with Owner Name, Status badge & main action triggers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "PROPRIÉTAIRE DE LA CARTE",
                            fontFamily = MulishFontFamily,
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Patrick L.",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Action Buttons
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable {
                                if (isFrozen) onUnfreeze() else onFreeze()
                            }
                        ) {
                            Text(
                                text = if (isFrozen) "Dégeler" else "Geler",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ToFocusBorderColor, // Golden Orange accent
                            modifier = Modifier.clickable(onClick = onManage)
                        ) {
                            Text(
                                text = "Gérer",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Top-right Functional Status Indicator Dot
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 28.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isPending -> Color.Gray
                        isFrozen -> Color(0xFFFFB000)
                        else -> ToofanGreen
                    }
                ) {
                    Text(
                        text = card.status?.uppercase() ?: "",
                        fontFamily = MulishFontFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

val ToFocusBorderColor = Color(0xFFF59E0B)

@Composable
fun BuyCardDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Achat d'une Carte CashPay",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Étape ${uiState.purchaseStep}/5", fontFamily = MulishFontFamily, fontSize = 11.sp, color = ToofanGreen, fontWeight = FontWeight.Bold)
                    LinearProgressBar(step = uiState.purchaseStep, maxSteps = 5, modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
                }

                when (uiState.purchaseStep) {
                    1 -> {
                        Text("Choisissez votre Marque", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Les logos ci-dessous respectent les directives de marque Visa & Mastercard.", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Visa Box
                            BrandSelectBox(
                                title = "VISA",
                                isSelected = uiState.purchaseBrand == "visa",
                                logoRes = R.drawable.logo_visa,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseBrand("visa") }
                            )

                            // Mastercard Box
                            BrandSelectBox(
                                title = "Mastercard",
                                isSelected = uiState.purchaseBrand == "mastercard",
                                logoRes = R.drawable.logo_mastercard,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseBrand("mastercard") }
                            )
                        }
                    }
                    2 -> {
                        Text("Choisissez le type de carte", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            TypeSelectBox(
                                title = "💳 Carte virtuelle",
                                description = "Disponible immédiatement, utilisation en ligne sur vos sites préférés.",
                                priceText = if (uiState.purchaseBrand == "visa") "$ 5.00 (+ $ 1.00 frais)" else "$ 6.00 (+ $ 1.00 frais)",
                                isSelected = uiState.purchaseType == "virtuelle",
                                onClick = { viewModel.setPurchaseType("virtuelle") }
                            )

                            TypeSelectBox(
                                title = "💳 Carte physique",
                                description = "Carte livrée à votre domicile, retraits USSD + paiements en magasin.",
                                priceText = "$ 10.00 (+ $ 2.00 frais)",
                                isSelected = uiState.purchaseType == "physique",
                                onClick = { viewModel.setPurchaseType("physique") }
                            )
                        }
                    }
                    3 -> {
                        Text("Choisissez la devise", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("La devise de facturation de la carte.", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CurrencySelectPill(
                                symbol = "$",
                                code = "USD",
                                isSelected = uiState.purchaseCurrency == "USD",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseCurrency("USD") }
                            )

                            CurrencySelectPill(
                                symbol = "€",
                                code = "EUR",
                                isSelected = uiState.purchaseCurrency == "EUR",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseCurrency("EUR") }
                            )
                        }
                    }
                    4 -> {
                        Text("Récapitulatif de votre commande", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ToofanGreen.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                RecapRow("Marque :", uiState.purchaseBrand.uppercase())
                                RecapRow("Type :", uiState.purchaseType.replaceFirstChar { it.uppercase() })
                                RecapRow("Devise :", uiState.purchaseCurrency)
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)

                                val basePrice = if (uiState.purchaseType == "virtuelle") 5.0 else 10.0
                                val fee = if (uiState.purchaseType == "virtuelle") 1.0 else 2.0
                                val total = basePrice + fee

                                RecapRow("Prix de la carte :", "$ ${String.format("%.2f", basePrice)} ${uiState.purchaseCurrency}")
                                RecapRow("Frais de création :", "$ ${String.format("%.2f", fee)} ${uiState.purchaseCurrency}")
                                RecapRow("Total à débiter :", "$ ${String.format("%.2f", total)} ${uiState.purchaseCurrency}", isBold = true, customColor = ToofanGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Entrez votre code PIN CashPay pour valider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        ToofanInputField(
                            value = uiState.purchasePin,
                            onValueChange = { viewModel.setPurchasePin(it) },
                            placeholder = "Code PIN à 4 chiffres"
                        )

                        uiState.purchaseError?.let { err ->
                            Text(err, color = Color(0xFFFF4868), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    5 -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(54.dp))
                            Text("Achat réussi !", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ToofanMainDark)
                            Text(
                                "Votre nouvelle carte CashPay ${uiState.purchaseBrand.uppercase()} ${uiState.purchaseType} a été créée avec succès et est prête à être utilisée.",
                                fontSize = 12.sp,
                                color = ToofanBodyText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.purchaseStep == 5) {
                ToofanButton(
                    title = "Fermer",
                    onClick = onDismiss,
                    modifier = Modifier.width(110.dp)
                )
            } else {
                ToofanButton(
                    title = if (uiState.purchaseStep == 4) "Confirmer" else "Suivant",
                    onClick = {
                        if (uiState.purchaseStep == 4) {
                            viewModel.executeCardPurchase()
                        } else {
                            viewModel.nextPurchaseStep()
                        }
                    },
                    isLoading = uiState.isPurchaseLoading,
                    modifier = Modifier.width(110.dp)
                )
            }
        },
        dismissButton = {
            if (uiState.purchaseStep > 1 && uiState.purchaseStep < 5) {
                TextButton(onClick = { viewModel.prevPurchaseStep() }) {
                    Text("Précédent", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            } else if (uiState.purchaseStep < 5) {
                TextButton(onClick = onDismiss) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            }
        }
    )
}

@Composable
fun BrandSelectBox(
    title: String,
    isSelected: Boolean,
    logoRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = title,
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.Inside
            )
            RadioButton(selected = isSelected, onClick = onClick)
        }
    }
}

@Composable
fun TypeSelectBox(
    title: String,
    description: String,
    priceText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ToofanMainDark)
                Text(description, fontSize = 11.sp, color = ToofanBodyText)
                Text(priceText, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ToFocusBorderColor)
            }
        }
    }
}

@Composable
fun CurrencySelectPill(
    symbol: String,
    code: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(symbol, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ToofanGreen else ToofanMainDark)
            Text(code, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ToofanBodyText)
        }
    }
}

@Composable
fun LinearProgressBar(step: Int, maxSteps: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(6.dp)
            .background(Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(step.toFloat() / maxSteps)
                .height(6.dp)
                .background(ToFocusBorderColor, RoundedCornerShape(3.dp))
        )
    }
}

@Composable
fun RecapRow(label: String, valText: String, isBold: Boolean = false, customColor: Color = ToofanMainDark) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
        Text(valText, fontFamily = MulishFontFamily, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold, fontSize = 12.sp, color = customColor)
    }
}

@Composable
fun ManageCardDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Gestion de la Carte",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mini Visual representation of card brand
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (card.brand?.lowercase() == "visa") Color(0xFF1E3A8A) else Color(0xFF311045),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${card.brand?.uppercase() ?: ""} •••• ${card.last4 ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(card.status?.uppercase() ?: "", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions List
                CardOptionItem(
                    title = "👁 Voir les détails de la carte",
                    description = "Afficher le numéro complet (PAN), CVV et date d'expiration.",
                    onClick = { viewModel.openRevealDetails() }
                )

                CardOptionItem(
                    title = "🔑 Modifier le PIN",
                    description = "Mettre à jour le code de sécurité physique de votre carte.",
                    onClick = { viewModel.openChangePin() }
                )

                CardOptionItem(
                    title = "💰 Source de financement",
                    description = "Associer cette carte à votre compte principal ou un Certificat CPK.",
                    onClick = { viewModel.openFundingDialog() }
                )

                CardOptionItem(
                    title = "🔒 Geler temporairement",
                    description = "Suspendre tous les paiements sur cette carte à tout moment.",
                    onClick = {
                        if (card.status?.lowercase() == "frozen") {
                            viewModel.unfreezeCard(card.id ?: "")
                        } else {
                            viewModel.freezeCard(card.id ?: "")
                        }
                    }
                )

                CardOptionItem(
                    title = "⚠ Résilier la carte",
                    description = "Désactiver et détruire définitivement cette carte de paiement.",
                    customColor = Color(0xFFFF4868),
                    onClick = { viewModel.terminateCard(card.id ?: "") }
                )
            }
        },
        confirmButton = {
            ToofanButton(title = "Fermer", onClick = onDismiss, modifier = Modifier.width(100.dp))
        }
    )
}

@Composable
fun CardOptionItem(
    title: String,
    description: String,
    customColor: Color = ToofanMainDark,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = ToofanGrey1.copy(alpha = 0.15f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = customColor)
            Text(description, fontSize = 11.sp, color = ToofanBodyText)
        }
    }
}

@Composable
fun RevealCardDetailsDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Détails Sensibles de la Carte", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.revealedCardDetails == null) {
                    Text("Veuillez saisir votre PIN CashPay à 4 chiffres pour révéler les détails.", fontSize = 12.sp)
                    ToofanInputField(
                        value = uiState.revealPin,
                        onValueChange = { viewModel.setRevealPin(it) },
                        placeholder = "PIN à 4 chiffres"
                    )

                    uiState.revealError?.let { err ->
                        Text(err, color = Color(0xFFFF4868), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val details = uiState.revealedCardDetails
                    Text("Détails révélés de la carte sécurisée :", fontSize = 11.sp, color = ToofanGreen, fontWeight = FontWeight.Bold)

                    RevealDetailRow("Numéro de carte (PAN) :", details.pan ?: "4111 1111 1111 ${card.last4}")
                    RevealDetailRow("Code CVV :", details.cvv ?: "123")
                    RevealDetailRow("Date d'expiration :", details.expiry ?: "12/30")
                }
            }
        },
        confirmButton = {
            if (uiState.revealedCardDetails == null) {
                ToofanButton(
                    title = "Révéler",
                    onClick = { viewModel.submitRevealDetails(card.id ?: "") },
                    isLoading = uiState.isRevealLoading,
                    modifier = Modifier.width(100.dp)
                )
            } else {
                ToofanButton(
                    title = "Fermer",
                    onClick = onDismiss,
                    modifier = Modifier.width(100.dp)
                )
            }
        },
        dismissButton = {
            if (uiState.revealedCardDetails == null) {
                TextButton(onClick = onDismiss) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            }
        }
    )
}

@Composable
fun RevealDetailRow(label: String, value: String) {
    val clipboardManager = LocalClipboardManager.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = ToofanGrey1.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(label, fontSize = 10.sp, color = ToofanBodyText)
                Text(value, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ToofanMainDark)
            }

            IconButton(onClick = { clipboardManager.setText(AnnotatedString(value)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ToofanGreen, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun ChangeCardPinDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modifier le Code PIN", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Saisissez le nouveau code PIN de 4 chiffres pour votre carte.", fontSize = 12.sp)
                ToofanInputField(
                    value = uiState.newCardPin,
                    onValueChange = { viewModel.setNewCardPin(it) },
                    placeholder = "Nouveau PIN"
                )
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Enregistrer",
                onClick = { viewModel.submitChangePin(card.id ?: "") },
                enabled = uiState.newCardPin.length == 4,
                modifier = Modifier.width(110.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
fun FundingSourceDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Source de Financement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FundingTypePill(
                    title = "◉ Wallet CashPay",
                    description = "Le solde de votre portefeuille finance la carte.",
                    isSelected = uiState.fundingType == "wallet",
                    onClick = { viewModel.setFundingType("wallet") }
                )

                FundingTypePill(
                    title = "◉ Certificat CPK",
                    description = "Utiliser un certificat CPK associé.",
                    isSelected = uiState.fundingType == "cpk",
                    onClick = { viewModel.setFundingType("cpk") }
                )

                if (uiState.fundingType == "cpk") {
                    Spacer(modifier = Modifier.height(4.dp))
                    ToofanInputField(
                        value = uiState.cpkSignature,
                        onValueChange = { viewModel.setCpkSignature(it) },
                        placeholder = "Signature du certificat CPK"
                    )
                    ToofanInputField(
                        value = uiState.cpkPin,
                        onValueChange = { viewModel.setCpkPin(it) },
                        placeholder = "PIN CPK"
                    )
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Associer",
                onClick = { viewModel.submitFundingConfig(card.id ?: "") },
                modifier = Modifier.width(100.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
fun FundingTypePill(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) ToofanGreen else ToofanMainDark)
            Text(description, fontSize = 11.sp, color = ToofanBodyText)
        }
    }
}

fun resolveAvatarUrl(raw: String?): Any? {
    if (raw.isNullOrBlank()) return null
    val trimmed = raw.trim()
    if (trimmed.startsWith("drawable:")) return trimmed
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
    if (trimmed.startsWith("data:image")) {
        return try {
            val base64Data = trimmed.substringAfter("base64,")
            val decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (_: Exception) { trimmed }
    }
    val clean = trimmed.removePrefix("/")
    return "https://app.cashpay-all.com/$clean"
}

@Composable
fun ToofanActionSquare(
    title: String,
    bgColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                lineHeight = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecentCorrespondentItem(
    contact: com.example.data.model.PhoneContact,
    ringColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular Avatar with Ring Border
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(2.dp, ringColor, CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val rawPhoto = contact.publicProfile?.profilePhotoUrl ?: contact.publicProfile?.profilePhoto
                val resolved = resolveAvatarUrl(rawPhoto)
                when {
                    resolved is String && resolved.startsWith("drawable:") -> {
                        val res = when (resolved) {
                            "drawable:avatar_jean" -> R.drawable.avatar_jean
                            "drawable:avatar_marie" -> R.drawable.avatar_marie
                            "drawable:avatar_koffi" -> R.drawable.avatar_koffi
                            else -> R.drawable.avatar_jean
                        }
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    resolved != null -> {
                        AsyncImage(
                            model = resolved,
                            contentDescription = contact.name,
                            placeholder = painterResource(id = R.drawable.avatar_jean),
                            error = painterResource(id = R.drawable.avatar_jean),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    contact.name.contains("Jean", ignoreCase = true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_jean),
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    contact.name.contains("Marie", ignoreCase = true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_marie),
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    contact.name.contains("Koffi", ignoreCase = true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_koffi),
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    else -> {
                        val initials = contact.name.trim().split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2).joinToString("").uppercase()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(ringColor.copy(alpha = 0.85f), ringColor))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (initials.isNotBlank()) initials else "CP",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Green status indicator at bottom right
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF00C48C))
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = contact.name,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = Color(0xFF0F172A),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
fun AddContactDialog(
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0066FF))
                Text(
                    text = "Ajouter un contact",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Rechercher par Numéro de téléphone ou Wallet ID :",
                    fontSize = 13.sp,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF64748B)
                )

                ToofanInputField(
                    value = uiState.addContactPhone,
                    onValueChange = { dashboardViewModel.setAddContactPhone(it) },
                    placeholder = "Ex: +243899... ou WAL-XXXX"
                )

                if (uiState.isSearchingAddContact) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00C48C),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (uiState.addContactError != null) {
                    Text(
                        text = uiState.addContactError!!,
                        color = Color(0xFFFF4868),
                        fontSize = 12.sp,
                        fontFamily = MulishFontFamily
                    )
                }

                if (uiState.addContactNotFound) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ce numéro n'est pas encore associé à un compte CashPay actif.",
                            modifier = Modifier.padding(10.dp),
                            color = Color(0xFFBE123C),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                uiState.addContactFoundProfile?.let { profile ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.5.dp, Color(0xFF00C48C), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val photoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                                if (!photoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00C48C))
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.fullName,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Wallet ID : ${profile.walletId}",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00C48C),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Compte CashPay Actif",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00C48C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.addContactFoundProfile != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToofanButton(
                        title = "Envoyer",
                        onClick = {
                            val prof = uiState.addContactFoundProfile!!
                            dashboardViewModel.confirmAddContact(prof)
                            dashboardViewModel.triggerPrefilledTransfer(prof.fullName, prof)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ToofanButton(
                        title = "Enregistrer",
                        onClick = {
                            dashboardViewModel.confirmAddContact(uiState.addContactFoundProfile!!)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                ToofanButton(
                    title = "Rechercher",
                    onClick = { dashboardViewModel.searchAddContact() },
                    isLoading = uiState.isSearchingAddContact,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun ContactsPageDialog(
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasContactPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactPermission = isGranted
        if (isGranted) {
            readAndSyncPhoneContacts(context, dashboardViewModel)
        }
    }

    LaunchedEffect(hasContactPermission) {
        if (hasContactPermission && uiState.contactsList.isEmpty()) {
            readAndSyncPhoneContacts(context, dashboardViewModel)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color(0xFF0F172A))
                    }
                    Text(
                        text = "Contacts",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Search Input
                ToofanInputField(
                    value = uiState.searchContactQuery,
                    onValueChange = { dashboardViewModel.setContactSearchQuery(it) },
                    placeholder = "Rechercher par nom ou numéro..."
                )

                if (!hasContactPermission) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00C48C).copy(alpha = 0.12f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF00C48C),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Accès aux contacts",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "CashPay souhaite accéder à vos contacts afin de trouver vos correspondants et vous permettre d'envoyer de l'argent plus facilement.",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF64748B)
                            )
                            ToofanButton(
                                title = "Autoriser l'accès aux contacts",
                                onClick = {
                                    permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else if (uiState.isLoadingContacts) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF00C48C))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Synchronisation des contacts CashPay...",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    val filtered = remember(uiState.contactsList, uiState.searchContactQuery) {
                        if (uiState.searchContactQuery.isBlank()) uiState.contactsList else {
                            uiState.contactsList.filter {
                                it.name.contains(uiState.searchContactQuery, ignoreCase = true) ||
                                        it.phone.contains(uiState.searchContactQuery)
                            }
                        }
                    }

                    val cashPayContacts = filtered.filter { it.isCashPayUser }
                    val otherContacts = filtered.filter { !it.isCashPayUser }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        if (cashPayContacts.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Contacts CashPay (${cashPayContacts.size})",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF00C48C),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            items(cashPayContacts) { contact ->
                                ContactItemCard(
                                    contact = contact,
                                    onSendClick = {
                                        onDismiss()
                                        if (contact.publicProfile != null) {
                                            dashboardViewModel.triggerPrefilledTransfer(contact.name, contact.publicProfile)
                                        } else {
                                            dashboardViewModel.onRecipientChanged(contact.name)
                                            dashboardViewModel.openTransferDialog()
                                        }
                                    }
                                )
                            }
                        }

                        if (otherContacts.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Autres contacts (${otherContacts.size})",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )
                            }
                            items(otherContacts) { contact ->
                                OtherContactItemCard(contact = contact)
                            }
                        }

                        if (filtered.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aucun contact trouvé",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 14.sp,
                                        color = Color(0xFF94A3B8)
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

@Composable
private fun ContactItemCard(
    contact: com.example.data.model.PhoneContact,
    onSendClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .border(1.5.dp, Color(0xFF00C48C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val photoUrl = contact.publicProfile?.profilePhotoUrl ?: contact.publicProfile?.profilePhoto
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00C48C),
                            fontSize = 14.sp
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Vérifié",
                            tint = Color(0xFF00C48C),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = "ID : ${contact.publicProfile?.walletId ?: contact.phone}",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF00C48C),
                modifier = Modifier.clickable(onClick = onSendClick)
            ) {
                Text(
                    text = "Envoyer",
                    color = Color.White,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun OtherContactItemCard(
    contact: com.example.data.model.PhoneContact
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = contact.name,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = contact.phone,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Invitation CashPay envoyée à ${contact.name}", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "Inviter",
                    color = Color(0xFF2563EB),
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun ServicesDialog(
    onDismiss: () -> Unit,
    onServiceSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF8B5CF6))
                Text(
                    text = "Services & Abonnements",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Choisissez un service ou un abonnement à régler :",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                val services = listOf(
                    Triple("Mobile Money", "M-Pesa, Orange Money, Airtel, MTN", Color(0xFFF59E0B)),
                    Triple("Abonnements TV", "Canal+, Startimes, EasyTV", Color(0xFF2563EB)),
                    Triple("Factures Publiques", "SNEL (Électricité), REGIDESO (Eau)", Color(0xFF00C48C)),
                    Triple("Services Digitaux", "Netflix, Spotify, Apple Music, PlayStation", Color(0xFF8B5CF6))
                )

                services.forEach { (name, desc, color) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onServiceSelected(name) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = color.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPlusModalDialog(
    userProfile: com.example.data.local.UserProfileEntity?,
    onDismiss: () -> Unit,
    onSendScan: () -> Unit,
    onReceiveQr: () -> Unit,
    onPayPos: () -> Unit,
    onWithdraw: () -> Unit,
    // Agent actions
    onAgentDeposit: () -> Unit = {},
    onAgentWithdraw: () -> Unit = {},
    onAgentLoan: () -> Unit = {},
    onAgentHistory: () -> Unit = {}
) {
    val isAgent = userProfile?.role?.lowercase()?.trim() == "agent"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFF5B37).copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFFF5B37),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = if (isAgent) "Menu Agent CashPay" else "Actions Rapides CashPay",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isAgent) "Services opérationnels pour agents agréés :" else "Choisissez l'opération que vous souhaitez effectuer :",
                    fontSize = 12.sp,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF64748B)
                )

                if (isAgent) {
                    // Agent Actions
                    ActionPlusTile(
                        title = "Dépôt Espèces",
                        subtitle = "Effectuer un dépôt sur le compte d'un client",
                        icon = Icons.Default.ArrowDownward,
                        iconBg = Color(0xFF059669),
                        onClick = onAgentDeposit
                    )
                    ActionPlusTile(
                        title = "Retrait Espèces",
                        subtitle = "Initier un retrait pour un client (OTP)",
                        icon = Icons.Default.ArrowUpward,
                        iconBg = Color(0xFFFF6600),
                        onClick = onAgentWithdraw
                    )
                    ActionPlusTile(
                        title = "Prêt Loan Me",
                        subtitle = "Rembourser le prêt d'un client",
                        icon = Icons.Default.Payments,
                        iconBg = Color(0xFF7C3AED),
                        onClick = onAgentLoan
                    )
                    ActionPlusTile(
                        title = "Historique Agent",
                        subtitle = "Voir mes dernières commissions",
                        icon = Icons.Default.Receipt,
                        iconBg = Color(0xFF0066FF),
                        onClick = onAgentHistory
                    )
                } else {
                    // Standard Client Actions
                    ActionPlusTile(
                        title = "Envoyer de l'argent",
                        subtitle = "Scanner un QR Code ou entrer un Wallet ID / N°",
                        icon = Icons.Default.Send,
                        iconBg = Color(0xFF00C48C),
                        onClick = onSendScan
                    )
                    ActionPlusTile(
                        title = "Recevoir des fonds",
                        subtitle = "Afficher mon QR Code & mon Wallet ID",
                        icon = Icons.Default.QrCode,
                        iconBg = Color(0xFF55ACEE),
                        onClick = onReceiveQr
                    )
                    ActionPlusTile(
                        title = "Paiement Commerçant / POS",
                        subtitle = "Scanner le QR Code d'un point de vente",
                        icon = Icons.Default.ShoppingCart,
                        iconBg = Color(0xFFFF8A71),
                        onClick = onPayPos
                    )
                    ActionPlusTile(
                        title = "Retrait Agent CashPay",
                        subtitle = "Retirer du cash auprès d'un agent agréé",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBg = Color(0xFF7C3AED),
                        onClick = onWithdraw
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun CashPaySettingsModal(
    onDismiss: () -> Unit,
    userProfile: UserProfileEntity?,
    onLogout: () -> Unit,
    onOpenActivateAgent: () -> Unit,
    onOpenForgotPin: () -> Unit
) {
    var selectedLang by remember { mutableStateOf("Français (RDC)") }
    var isBiometricEnabled by remember { mutableStateOf(true) }
    val isAgentUser = userProfile?.role?.lowercase() == "agent"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ToofanGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = "Paramètres de l'Application",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- SECTION 1: COMPTE & SERVICES ---
                item {
                    Text(
                        text = "COMPTE & SERVICES",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ToofanGreen,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = (userProfile?.fullName?.take(1) ?: "").uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    val profileName = userProfile?.fullName ?: ""
                                    Text(
                                        text = profileName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Wallet ID: ${userProfile?.walletId ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                // Removed redundant AGENT/CLIENT label per user feedback
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            // Devenir Agent Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenActivateAgent() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = if (isAgentUser) "Gérer mon statut Agent" else "Devenir Agent CashPay",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = "Souscription aux formules Agent (Promo / Normal)",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // --- SECTION 2: SÉCURITÉ & PIN ---
                item {
                    Text(
                        text = "SÉCURITÉ & AUTHENTIFICATION",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Forgot PIN Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenForgotPin() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFF5B37), modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = "PIN Oublié / Réinitialiser le PIN",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = "Réinitialisation par SMS ou WhatsApp OTP",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }

                            // Biometrics Switch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                    Column {
                                        Text(
                                            text = "Authentification Biométrique",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ToofanMainDark
                                        )
                                        Text(
                                            text = "Empreinte / Reconnaissance faciale",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                                Switch(
                                    checked = isBiometricEnabled,
                                    onCheckedChange = { isBiometricEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ToofanGreen)
                                )
                            }
                        }
                    }
                }

                // --- SECTION 3: PRÉFÉRENCES SYSTÈME ---
                item {
                    Text(
                        text = "PRÉFÉRENCES SYSTÈME",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Langue de l'application :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ToofanMainDark,
                        fontFamily = MulishFontFamily
                    )
                    val languages = listOf("Français (RDC)", "English", "Swahili", "Lingala")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        languages.take(2).forEach { lang ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedLang = lang },
                                color = if (selectedLang == lang) ToofanGreen.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (selectedLang == lang) ToofanGreen else Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = lang,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedLang == lang) ToofanGreen else ToofanMainDark
                                )
                            }
                        }
                    }
                }

                // --- SECTION 4: DÉCONNEXION ---
                item {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4868).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Se déconnecter",
                            color = Color(0xFFFF4868),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun MiniPublicProfileDialog(
    contact: com.example.data.model.PhoneContact,
    onDismiss: () -> Unit,
    onSendMoney: () -> Unit
) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val profile = contact.publicProfile

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = ToofanGreen)
                Text(
                    text = "Profil Public Correspondant",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Large Avatar
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(2.dp, ToofanGreen),
                    modifier = Modifier.size(68.dp)
                ) {
                    val photoUrl = profile?.profilePhotoUrl ?: profile?.profilePhoto
                    if (!photoUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = photoUrl,
                            contentDescription = contact.name,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = contact.name.take(2).uppercase(),
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = ToofanGreen
                            )
                        }
                    }
                }

                Text(
                    text = profile?.fullName ?: contact.name,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )

                if (!profile?.profession.isNullOrBlank()) {
                    Text(
                        text = profile.profession,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Wallet ID Pill
                val walletId = profile?.walletId ?: contact.phone
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ID Wallet :", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(walletId, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ToofanGreen)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(walletId))
                                Toast.makeText(context, "ID copié : $walletId", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ToofanGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // BTC Address if available
                profile?.bitcoin?.address?.let { btcAddr ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Adresse Bitcoin :", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(btcAddr, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFFF59E0B), maxLines = 1)
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(btcAddr))
                                    Toast.makeText(context, "Adresse BTC copiée !", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Activity / Location
                if (!profile?.city.isNullOrBlank() || !profile?.country.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Text(
                            text = listOfNotNull(profile.city, profile.country).joinToString(", "),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action: Envoyer de l'argent
                Button(
                    onClick = onSendMoney,
                    colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Envoyer de l'argent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
private fun ActionPlusTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun readAndSyncPhoneContacts(context: Context, dashboardViewModel: DashboardViewModel) {
    try {
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        val rawList = mutableListOf<Pair<String, String>>()
        cursor?.use {
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = if (nameIdx != -1) it.getString(nameIdx) ?: "Contact" else "Contact"
                val num = if (numIdx != -1) it.getString(numIdx) ?: "" else ""
                if (num.isNotBlank() && rawList.none { pair -> pair.second == num }) {
                    rawList.add(name to num)
                }
            }
        }
        dashboardViewModel.syncContacts(rawList)
    } catch (e: Exception) {
        // Handled gracefully
    }
}

private fun extractRecipientFromQr(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
        try {
            val json = org.json.JSONObject(trimmed)
            val wallet = json.optString("walletId", json.optString("wallet", json.optString("receiverWalletId", json.optString("clientWalletId", ""))))
            if (wallet.isNotBlank()) return wallet
            val phone = json.optString("phone", json.optString("phoneNumber", ""))
            if (phone.isNotBlank()) return phone
            val target = json.optString("target", json.optString("recipient", ""))
            if (target.isNotBlank()) return target
        } catch (_: Exception) {}
    }
    var clean = trimmed
    if (clean.startsWith("cashpay://", ignoreCase = true)) clean = clean.substring(10)
    else if (clean.startsWith("cashpay:", ignoreCase = true)) clean = clean.substring(8)
    else if (clean.startsWith("bitcoin:", ignoreCase = true)) clean = clean.substring(8)

    if (clean.contains("wallet=")) {
        clean = clean.substringAfter("wallet=").substringBefore("&")
    } else if (clean.contains("receiver=")) {
        clean = clean.substringAfter("receiver=").substringBefore("&")
    } else if (clean.contains("?")) {
        clean = clean.substringBefore("?")
    }
    return clean.trim()
}

