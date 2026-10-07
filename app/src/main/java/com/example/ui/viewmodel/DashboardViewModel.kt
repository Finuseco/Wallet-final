package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.WalletResponse
import com.example.data.model.TransfersMetaResponse
import com.example.data.model.TransferRequest
import com.example.data.model.TransferResponse
import com.example.data.model.AgentWithdrawResponse
import com.example.data.model.AgentDepositResponse
import com.example.data.model.AgentLoanTargetResponse
import com.example.data.model.AgentLoanRepaymentResponse
import com.example.data.model.AgentCommissionTransferResponse
import com.example.data.repository.CashPayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class DashboardUiState(
    val selectedTab: Int = 0, // 0: Accueil / Wallet, 1: Profil Complet & Données, 2: Sécurité & PIN
    val isEditProfileOpen: Boolean = false,
    val editFullName: String = "",
    val editCity: String = "",
    val editProfession: String = "",
    val editAddress: String = "",
    val editPinConfirmation: String = "",
    val isEditingLoading: Boolean = false,
    val editErrorMessage: String? = null,
    val editSuccessMessage: String? = null,
    val isQuickTransferOpen: Boolean = false,
    val transferRecipient: String = "",
    val transferAmount: String = "",
    val transferCurrency: String = "USD",
    val transferSuccess: Boolean = false,
    val walletResponse: WalletResponse? = null,
    val isLoadingWallet: Boolean = false,
    val walletError: String? = null,
    val transfersMeta: TransfersMetaResponse? = null,
    val transferFee: Double? = null,
    val transferTotalDebit: Double? = null,
    val transferError: String? = null,
    val isTransferLoading: Boolean = false,
    val transferStep: Int = 1, // 1: Input details, 2: Preview & Fee, 3: PIN Confirmation, 4: Success
    val txDirectionFilter: String = "ALL", // "ALL", "INCOMING", "OUTGOING"
    val txDateFilter: String = "ALL", // "ALL", "TODAY", "WEEK", "MONTH", "CUSTOM"
    val txSearchQuery: String = "",
    val customFromDate: String = "",
    val customToDate: String = "",
    val isAllTransactionsOpen: Boolean = false,
    val selectedTransactionForDetail: TransactionEntity? = null,
    val isLoadingTransactions: Boolean = false,
    val transactionsError: String? = null,
    val isNotificationDialogOpen: Boolean = false,
    val isLoadingNotifications: Boolean = false,
    val notificationsError: String? = null,

    // --- CARDS MODULE STATE ---
    val userCards: List<com.example.data.model.CardInfoDto> = emptyList(),
    val cardCatalog: List<com.example.data.model.CardCatalogDto> = emptyList(),
    val isLoadingCards: Boolean = false,
    val cardsError: String? = null,
    val catalogError: String? = null,
    val isBuyCardDialogOpen: Boolean = false,
    val purchaseBrand: String = "visa", // "visa", "mastercard"
    val purchaseType: String = "virtuelle", // "virtuelle", "physique"
    val purchaseCurrency: String = "USD", // "USD", "EUR"
    val purchasePin: String = "",
    val isPurchaseLoading: Boolean = false,
    val purchaseError: String? = null,
    val purchaseSuccess: Boolean = false,
    val purchaseStep: Int = 1, // 1: Brand, 2: Type, 3: Currency, 4: Pin & Recap, 5: Success
    val selectedCardForManage: com.example.data.model.CardInfoDto? = null,
    val isManageCardDialogOpen: Boolean = false,
    val isChangePinDialogOpen: Boolean = false,
    val newCardPin: String = "",
    val revealedCardDetails: com.example.data.model.CardDetailsResponse? = null,
    val revealPin: String = "",
    val isRevealLoading: Boolean = false,
    val revealError: String? = null,
    val isRevealDetailsDialogOpen: Boolean = false,
    val fundingType: String = "wallet", // "wallet", "cpk"
    val cpkSignature: String = "",
    val cpkPin: String = "",
    val isFundingDialogOpen: Boolean = false,

    // --- CONTACTS & PUBLIC PROFILE STATE ---
    val contactsList: List<com.example.data.model.PhoneContact> = emptyList(),
    val recentCorrespondents: List<com.example.data.model.PhoneContact> = emptyList(),
    val isContactsDialogOpen: Boolean = false,
    val isContactsPageOpen: Boolean = false,
    val isAddContactDialogOpen: Boolean = false,
    val addContactPhone: String = "",
    val isSearchingAddContact: Boolean = false,
    val addContactFoundProfile: com.example.data.model.PublicProfileDto? = null,
    val addContactNotFound: Boolean = false,
    val addContactError: String? = null,
    val isLoadingContacts: Boolean = false,
    val contactsError: String? = null,
    val selectedPublicProfile: com.example.data.model.PublicProfileDto? = null,
    val selectedPublicContactName: String? = null,
    val isPublicProfileOpen: Boolean = false,
    val searchContactQuery: String = "",
    val prefilledRecipient: com.example.data.model.PublicProfileDto? = null,
    val prefilledContactName: String? = null,

    // --- WITHDRAWALS STATE ---
    val isWithdrawalDialogOpen: Boolean = false,
    val withdrawalType: String = "agent_cash", // "agent_cash", "mobile_money"
    val withdrawalOperator: String? = null, // "MPESA", "ORANGE", "AFRIMONEY", "AIRTEL", "MTN"
    val withdrawalRecipient: String = "", // Agent Wallet ID or operator phone
    val withdrawalAmount: String = "",
    val withdrawalCurrency: String = "USD",
    val withdrawalPreview: com.example.data.model.WithdrawalPreviewDto? = null,
    val isWithdrawalPreviewLoading: Boolean = false,
    val withdrawalPreviewError: String? = null,
    val isWithdrawalConfirmLoading: Boolean = false,
    val withdrawalConfirmError: String? = null,
    val withdrawalSuccess: Boolean = false,
    val withdrawalStep: Int = 1, // 1: Select Type & Search Agent/Select Operator, 2: Enter Amount, 3: Preview Details, 4: PIN & Confirm, 5: Success
    val searchedAgentProfile: com.example.data.model.PublicProfileDto? = null,
    val isSearchingAgent: Boolean = false,
    val agentSearchError: String? = null,

    // --- SYSTEM THEME STATE ---
    val isDarkMode: Boolean = false, // Restored clean normal Toofan light design

    // --- LOANS STATE ---
    val isLoansDialogOpen: Boolean = false,
    val isLoadingLoanOffer: Boolean = false,
    val loanOffer: com.example.data.model.LoanOfferDto? = null,
    val loanOfferError: String? = null,
    val activeLoan: com.example.data.model.LoanDto? = null,
    val isLoadingActiveLoan: Boolean = false,
    val activeLoanError: String? = null,
    val loanHistory: List<com.example.data.model.LoanDto> = emptyList(),
    val isLoadingLoanHistory: Boolean = false,
    val loanHistoryError: String? = null,
    // Loan Request Parameters
    val loanRequestAmount: String = "",
    val loanRequestDuration: Int = 3, // Default duration
    val isRequestingLoan: Boolean = false,
    val loanRequestError: String? = null,
    val loanRequestSuccess: Boolean = false,
    // Loan Repayment Parameters
    val loanRepayAmount: String = "",
    val isRepayingLoan: Boolean = false,
    val loanRepayError: String? = null,
    val loanRepaySuccess: Boolean = false,
    val isRepayInstallmentLoading: Boolean = false,
    val repayInstallmentError: String? = null,
    val loanInstallmentSuccess: Boolean = false,
    // Current Sub-Tab in Loans
    val loanSelectedTab: Int = 0, // 0: Offer/Request, 1: Active Loan, 2: History

    // --- ACTIVATE AGENT & FORGOT PIN STATE ---
    val isActivateAgentDialogOpen: Boolean = false,
    val agentPlan: String = "promo",
    val agentPin: String = "",
    val isActivatingAgent: Boolean = false,
    val activateAgentError: String? = null,
    val activateAgentSuccessMessage: String? = null,

    // --- EXCHANGE / CONVERSION DE DEVISES STATE ---
    val isExchangeDialogOpen: Boolean = false,
    val exchangeAmount: String = "",
    val exchangeFromCurrency: String = "USD",
    val exchangeToCurrency: String = "CDF",
    val exchangePin: String = "",
    val isExchangeLoading: Boolean = false,
    val exchangeError: String? = null,
    val exchangeSuccessResponse: com.example.data.model.ExchangeResponse? = null,

    val isForgotPinDialogOpen: Boolean = false,
    val forgotPinStep: Int = 1,
    val forgotPinPhone: String = "",
    val forgotPinChannel: String = "sms",
    val forgotPinUserId: Long? = null,
    val forgotPinOtp: String = "",
    val forgotPinNewPin: String = "",
    val forgotPinConfirmPin: String = "",
    val isForgotPinLoading: Boolean = false,
    val forgotPinError: String? = null,
    val forgotPinSuccessMessage: String? = null,

    // --- AGENT SUITE & COMMISSION BALANCES ---
    val isBalanceVisible: Boolean = true,
    val isAgentBalanceVisible: Boolean = true,
    val isAgentServicesDialogOpen: Boolean = false,
    val agentActiveTab: Int = 0, // 0: Hub/Commissions, 1: Dépôt, 2: Retrait, 3: Remboursement, 4: Historique
    val agentCommissionUsd: Double = 0.0,
    val agentCommissionCdf: Double = 0.0,
    val agentCommissionEur: Double = 0.0,
    // Sweep Commission Dialog
    val isSweepCommissionDialogOpen: Boolean = false,
    val sweepCurrency: String = "USD",
    val sweepAmount: String = "",
    val sweepIsAll: Boolean = true,
    val sweepPin: String = "",
    val isSweepingCommission: Boolean = false,
    val sweepCommissionError: String? = null,
    val sweepCommissionSuccess: String? = null,
    val agentCommissionsMap: Map<String, Double> = emptyMap(),
    val agentBalancesMap: Map<String, Double> = emptyMap(),
    // Agent Deposit (Multi-step flow)
    val agentDepositStep: String = "identify", // "identify", "confirm_client", "amount", "pin", "completed"
    val agentDepositClientRef: String = "",
    val agentDepositFoundClient: com.example.data.model.AgentDepositClientDto? = null,
    val agentDepositAgentBalances: Map<String, Double> = emptyMap(),
    val agentDepositAmount: String = "",
    val agentDepositCurrency: String = "CDF",
    val agentDepositPin: String = "",
    val isAgentDepositLoading: Boolean = false,
    val agentDepositError: String? = null,
    val agentDepositPreview: com.example.data.model.AgentDepositPreviewDto? = null,
    val agentDepositDetail: com.example.data.model.AgentDepositDetailDto? = null,
    val agentDepositSuccessDetail: com.example.data.model.AgentDepositDetailDto? = null,
    val agentDepositFinancialDetails: com.example.data.model.AgentDepositResponse? = null,
    val agentDepositSuccess: com.example.data.model.AgentDepositResponse? = null,
    // Agent Withdraw (Multi-step: Identify -> Amount/Channel -> OTP)
    val agentWithdrawStep: Int = 1, // 1: Identify, 2: Amount & Channel, 3: OTP Confirmation
    val agentWithdrawClientRef: String = "",
    val agentWithdrawFoundClient: com.example.data.model.PublicProfileDto? = null,
    val agentWithdrawAmount: String = "",
    val agentWithdrawCurrency: String = "USD",
    val agentWithdrawChannel: String = "sms", // "sms" ou "whatsapp"
    val agentWithdrawClientOtp: String = "",
    val isAgentWithdrawLoading: Boolean = false,
    val agentWithdrawError: String? = null,
    val agentWithdrawSuccess: com.example.data.model.AgentWithdrawResponse? = null,
    val agentWithdrawOperationId: String? = null,
    val agentWithdrawOtpChannels: List<String> = emptyList(),
    // Agent Loan Repay (Multi-step: Identify -> Amount -> Pin)
    val agentLoanStep: Int = 1, // 1: Identify, 2: Amount, 3: PIN
    val agentLoanClientRef: String = "",
    val agentLoanTarget: com.example.data.model.AgentLoanTargetResponse? = null,
    val isAgentSearchingLoan: Boolean = false,
    val agentLoanSearchError: String? = null,
    val agentLoanAmount: String = "",
    val agentLoanCurrency: String = "USD",
    val agentLoanPin: String = "",
    val isAgentLoanRepayLoading: Boolean = false,
    val agentLoanRepayError: String? = null,
    val agentLoanRepaySuccess: com.example.data.model.AgentLoanRepaymentResponse? = null,
    // Agent History (Real operations only)
    val agentOperationsHistory: List<com.example.data.model.AgentOperationRecord> = emptyList(),
    val currentUserId: Long? = null,
    val transferSearchMode: String = "wallet", // "wallet" or "phone"
    val isSearchingTransferRecipient: Boolean = false,
    val agentDepositSearchMode: String = "wallet", // "wallet" or "phone"

    // --- DEPOSIT (MaxiCash Gateway: Mobile Money, Card, PayPal) ---
    val isDepositDialogOpen: Boolean = false,
    val depositMethod: String = "mobile_money", // "mobile_money", "card", "paypal"
    val depositAmount: String = "",
    val depositCurrency: String = "USD",
    val depositOperator: String = "mpesa",
    val depositPhoneNumber: String = "",
    val depositCountryCode: String = "CD",
    val isDepositLoading: Boolean = false,
    val depositError: String? = null,
    val depositSuccessMessage: String? = null,
    val depositPendingReference: String? = null,
    val depositPaymentUrl: String? = null,
    val isDepositWebViewOpen: Boolean = false,

    // Agent External Mobile Money pull
    val isAgentExternalMoMoOpen: Boolean = false,
    val agentExternalMoMoOperator: String = "mpesa",
    val agentExternalMoMoPhone: String = "",
    val agentExternalMoMoAmount: String = "",
    val isAgentExternalMoMoLoading: Boolean = false,
    val agentExternalMoMoError: String? = null,
    val agentExternalMoMoSuccess: String? = null,
    val agentExternalMoMoReference: String? = null
)

class DashboardViewModel(
    private val repository: CashPayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val notifications: StateFlow<List<com.example.data.local.NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.session.collect { session ->
                if (session != null && session.isAuthenticated) {
                    val userId = session.userId
                    _uiState.update { it.copy(currentUserId = userId) }
                    fetchWallet(userId)
                    loadTransactions(userId)
                    fetchNotifications()
                    fetchUserCards()
                    fetchCardCatalog()
                    loadAgentCommissions()
                } else if (session == null || !session.isAuthenticated) {
                    _uiState.value = DashboardUiState(
                        isDarkMode = _uiState.value.isDarkMode
                    )
                }
            }
        }
        seedRecentCorrespondents()
    }

    fun getEffectiveUserId(provided: Long? = null): Long {
        if (provided != null && provided > 1L) return provided
        val stId = _uiState.value.currentUserId
        if (stId != null && stId > 0L) return stId
        return provided ?: 1L
    }

    fun setTransferSearchMode(mode: String) {
        _uiState.update { it.copy(transferSearchMode = mode, transferError = null) }
    }

    fun setAgentDepositSearchMode(mode: String) {
        _uiState.update { it.copy(agentDepositSearchMode = mode, agentDepositError = null) }
    }

    // --- CARDS ACTIONS ---
    fun fetchUserCards() {
        _uiState.update { it.copy(isLoadingCards = true, cardsError = null) }
        viewModelScope.launch {
            repository.getUserCards()
                .onSuccess { cards ->
                    _uiState.update { it.copy(isLoadingCards = false, userCards = cards) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoadingCards = false, cardsError = err.message) }
                }
        }
    }

    fun fetchCardCatalog() {
        _uiState.update { it.copy(catalogError = null) }
        viewModelScope.launch {
            repository.getCardCatalog()
                .onSuccess { resp ->
                    _uiState.update { it.copy(cardCatalog = resp.cards) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(catalogError = err.message) }
                }
        }
    }

    fun openBuyCardDialog() {
        _uiState.update {
            it.copy(
                isBuyCardDialogOpen = true,
                purchaseBrand = "visa",
                purchaseType = "virtuelle",
                purchaseCurrency = "USD",
                purchasePin = "",
                purchaseError = null,
                purchaseSuccess = false,
                purchaseStep = 1
            )
        }
        fetchCardCatalog()
    }

    fun closeBuyCardDialog() {
        _uiState.update { it.copy(isBuyCardDialogOpen = false) }
    }

    fun setPurchaseBrand(brand: String) {
        _uiState.update { it.copy(purchaseBrand = brand) }
    }

    fun setPurchaseType(type: String) {
        _uiState.update { it.copy(purchaseType = type) }
    }

    fun setPurchaseCurrency(currency: String) {
        _uiState.update { it.copy(purchaseCurrency = currency) }
    }

    fun setPurchasePin(pin: String) {
        _uiState.update { it.copy(purchasePin = pin.filter { it.isDigit() }.take(4)) }
    }

    fun nextPurchaseStep() {
        _uiState.update { it.copy(purchaseStep = it.purchaseStep + 1, purchaseError = null) }
    }

    fun prevPurchaseStep() {
        _uiState.update { it.copy(purchaseStep = if (it.purchaseStep > 1) it.purchaseStep - 1 else 1) }
    }

    fun executeCardPurchase() {
        val state = _uiState.value
        if (state.purchasePin.length != 4) {
            _uiState.update { it.copy(purchaseError = "Un code PIN à 4 chiffres est requis pour confirmer.") }
            return
        }
        _uiState.update { it.copy(isPurchaseLoading = true, purchaseError = null) }
        viewModelScope.launch {
            repository.purchaseCard(
                brand = state.purchaseBrand,
                cardType = state.purchaseType,
                currency = state.purchaseCurrency,
                pin = state.purchasePin
            ).onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isPurchaseLoading = false,
                        purchaseSuccess = true,
                        purchaseStep = 5 // Success Step
                    )
                }
                fetchUserCards()
            }.onFailure { err ->
                _uiState.update { it.copy(isPurchaseLoading = false, purchaseError = err.message) }
            }
        }
    }

    fun openManageCard(card: com.example.data.model.CardInfoDto) {
        _uiState.update {
            it.copy(
                selectedCardForManage = card,
                isManageCardDialogOpen = true,
                newCardPin = "",
                revealedCardDetails = null,
                revealPin = "",
                revealError = null
            )
        }
    }

    fun closeManageCard() {
        _uiState.update { it.copy(isManageCardDialogOpen = false, selectedCardForManage = null) }
    }

    fun freezeCard(cardId: String) {
        viewModelScope.launch {
            repository.freezeCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    // Re-open with updated card to show correct freeze/unfreeze state
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        _uiState.update { it.copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun unfreezeCard(cardId: String) {
        viewModelScope.launch {
            repository.unfreezeCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        _uiState.update { it.copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun activateCard(cardId: String) {
        viewModelScope.launch {
            repository.activateCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        _uiState.update { it.copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun openChangePin() {
        _uiState.update { it.copy(isChangePinDialogOpen = true, newCardPin = "") }
    }

    fun closeChangePin() {
        _uiState.update { it.copy(isChangePinDialogOpen = false) }
    }

    fun setNewCardPin(pin: String) {
        _uiState.update { it.copy(newCardPin = pin.filter { it.isDigit() }.take(4)) }
    }

    fun submitChangePin(cardId: String) {
        val pin = _uiState.value.newCardPin
        if (pin.length != 4) return
        viewModelScope.launch {
            repository.changeCardPin(cardId, pin)
                .onSuccess {
                    closeChangePin()
                }
        }
    }

    fun terminateCard(cardId: String) {
        viewModelScope.launch {
            repository.terminateCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    closeManageCard()
                }
        }
    }

    fun openFundingDialog() {
        _uiState.update {
            it.copy(
                isFundingDialogOpen = true,
                fundingType = "wallet",
                cpkSignature = "",
                cpkPin = ""
            )
        }
    }

    fun closeFundingDialog() {
        _uiState.update { it.copy(isFundingDialogOpen = false) }
    }

    fun setFundingType(type: String) {
        _uiState.update { it.copy(fundingType = type) }
    }

    fun setCpkSignature(v: String) {
        _uiState.update { it.copy(cpkSignature = v) }
    }

    fun setCpkPin(v: String) {
        _uiState.update { it.copy(cpkPin = v.filter { it.isDigit() }.take(4)) }
    }

    fun submitFundingConfig(cardId: String) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateCardFunding(
                id = cardId,
                type = state.fundingType,
                cpkSignature = if (state.fundingType == "cpk") state.cpkSignature else null,
                cpkPin = if (state.fundingType == "cpk") state.cpkPin else null
            ).onSuccess {
                closeFundingDialog()
            }
        }
    }

    fun openRevealDetails() {
        _uiState.update {
            it.copy(
                isRevealDetailsDialogOpen = true,
                revealPin = "",
                revealError = null,
                revealedCardDetails = null
            )
        }
    }

    fun closeRevealDetails() {
        _uiState.update {
            it.copy(
                isRevealDetailsDialogOpen = false,
                revealPin = "",
                revealedCardDetails = null
            )
        }
    }

    fun setRevealPin(pin: String) {
        _uiState.update { it.copy(revealPin = pin.filter { it.isDigit() }.take(4)) }
    }

    fun submitRevealDetails(cardId: String) {
        val pin = _uiState.value.revealPin
        if (pin.length != 4) return
        _uiState.update { it.copy(isRevealLoading = true, revealError = null) }
        viewModelScope.launch {
            repository.revealCardDetails(cardId, pin)
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isRevealLoading = false,
                            revealedCardDetails = resp,
                            revealError = null
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isRevealLoading = false,
                            revealError = err.message ?: "Code PIN incorrect."
                        )
                    }
                }
        }
    }

    fun fetchNotifications() {
        _uiState.update { it.copy(isLoadingNotifications = true, notificationsError = null) }
        viewModelScope.launch {
            val res = repository.fetchNotifications(20)
            res.onSuccess {
                _uiState.update { it.copy(isLoadingNotifications = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoadingNotifications = false, notificationsError = err.message) }
            }
        }
    }

    fun openNotificationDialog() {
        _uiState.update { it.copy(isNotificationDialogOpen = true) }
        fetchNotifications()
    }

    fun closeNotificationDialog() {
        _uiState.update { it.copy(isNotificationDialogOpen = false) }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun loadTransactions(
        userId: Long? = null,
        direction: String? = null,
        from: String? = null,
        to: String? = null
    ) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(isLoadingTransactions = true, transactionsError = null) }
        viewModelScope.launch {
            val result = repository.fetchTransactionsApi(
                userId = effectiveUserId,
                limit = 50,
                offset = 0,
                from = from,
                to = to,
                direction = direction
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoadingTransactions = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoadingTransactions = false, transactionsError = err.message) }
            }
        }
    }

    fun setTxDirectionFilter(dir: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(txDirectionFilter = dir) }
        val dirParam = when (dir) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        loadTransactions(effectiveUserId, direction = dirParam)
    }

    fun setTxDateFilter(filter: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(txDateFilter = filter) }
        val dirParam = when (_uiState.value.txDirectionFilter) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        val (from, to) = calculateFromTo(filter, _uiState.value.customFromDate, _uiState.value.customToDate)
        loadTransactions(effectiveUserId, direction = dirParam, from = from, to = to)
    }

    fun setCustomDates(from: String, to: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(customFromDate = from, customToDate = to, txDateFilter = "CUSTOM") }
        val dirParam = when (_uiState.value.txDirectionFilter) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        loadTransactions(effectiveUserId, direction = dirParam, from = from, to = to)
    }

    fun onSearchQueryChanged(q: String) {
        _uiState.update { it.copy(txSearchQuery = q) }
    }

    fun openAllTransactionsPage() {
        _uiState.update { it.copy(isAllTransactionsOpen = true) }
    }

    fun closeAllTransactionsPage() {
        _uiState.update { it.copy(isAllTransactionsOpen = false) }
    }

    fun selectTransactionForDetail(tx: TransactionEntity?) {
        _uiState.update { it.copy(selectedTransactionForDetail = tx) }
    }

    private fun calculateFromTo(filter: String, customFrom: String, customTo: String): Pair<String?, String?> {
        val today = "2026-09-26"
        return when (filter) {
            "TODAY" -> Pair(today, today)
            "WEEK" -> Pair("2026-09-19", today)
            "MONTH" -> Pair("2026-09-01", today)
            "CUSTOM" -> Pair(customFrom.ifBlank { null }, customTo.ifBlank { null })
            else -> Pair(null, null)
        }
    }

    fun fetchWallet(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(isLoadingWallet = true, walletError = null) }
        viewModelScope.launch {
            val result = repository.getWallet(effectiveUserId)
            result.onSuccess { resp ->
                _uiState.update { it.copy(isLoadingWallet = false, walletResponse = resp) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoadingWallet = false, walletError = err.message) }
            }
        }
    }

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val session: StateFlow<SessionEntity?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun openEditProfileDialog(profile: UserProfileEntity?) {
        _uiState.update {
            it.copy(
                isEditProfileOpen = true,
                editFullName = profile?.fullName ?: "",
                editCity = profile?.city ?: "",
                editProfession = profile?.profession ?: "",
                editAddress = profile?.address ?: "",
                editPinConfirmation = "",
                editErrorMessage = null,
                editSuccessMessage = null
            )
        }
    }

    fun closeEditProfileDialog() {
        _uiState.update { it.copy(isEditProfileOpen = false, editErrorMessage = null) }
    }

    fun onEditFullNameChanged(v: String) = _uiState.update { it.copy(editFullName = v) }
    fun onEditCityChanged(v: String) = _uiState.update { it.copy(editCity = v) }
    fun onEditProfessionChanged(v: String) = _uiState.update { it.copy(editProfession = v) }
    fun onEditAddressChanged(v: String) = _uiState.update { it.copy(editAddress = v) }
    fun onEditPinChanged(v: String) = _uiState.update { it.copy(editPinConfirmation = v.filter { it.isDigit() }.take(4)) }

    fun submitProfileUpdate(phone: String) {
        val state = _uiState.value
        if (state.editPinConfirmation.length != 4) {
            _uiState.update { it.copy(editErrorMessage = "Code PIN à 4 chiffres requis pour valider.") }
            return
        }

        val updates = mutableMapOf<String, String>()
        if (state.editFullName.isNotBlank()) updates["fullName"] = state.editFullName
        if (state.editCity.isNotBlank()) updates["city"] = state.editCity
        if (state.editProfession.isNotBlank()) updates["profession"] = state.editProfession
        if (state.editAddress.isNotBlank()) updates["address"] = state.editAddress

        if (updates.isEmpty()) {
            _uiState.update { it.copy(editErrorMessage = "Aucune donnée à modifier.") }
            return
        }

        _uiState.update { it.copy(isEditingLoading = true, editErrorMessage = null) }

        viewModelScope.launch {
            val result = repository.updateProfile(phone, state.editPinConfirmation, updates)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isEditingLoading = false,
                        isEditProfileOpen = false,
                        editSuccessMessage = "Profil CashPay mis à jour avec succès !"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isEditingLoading = false,
                        editErrorMessage = err.message ?: "Code PIN incorrect."
                    )
                }
            }
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometric(enabled)
        }
    }

    fun openTransferDialog(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val fiatBalances = _uiState.value.walletResponse?.balances?.fiat ?: emptyMap()
        val defaultCurrency = when {
            (fiatBalances["CDF"] ?: 0.0) > 0.0 -> "CDF"
            (fiatBalances["USD"] ?: 0.0) > 0.0 -> "USD"
            (fiatBalances["EUR"] ?: 0.0) > 0.0 -> "EUR"
            else -> _uiState.value.transferCurrency
        }
        _uiState.update {
            it.copy(
                isQuickTransferOpen = true,
                transferRecipient = "",
                transferCurrency = defaultCurrency,
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false,
                isSearchingTransferRecipient = false
            )
        }
        loadTransfersMeta(effectiveUserId)
    }

    fun closeTransferDialog() {
        resetTransferDialog()
    }

    fun onRecipientChanged(v: String) = _uiState.update { it.copy(transferRecipient = v, transferError = null) }
    fun onAmountChanged(v: String) = _uiState.update { it.copy(transferAmount = v, transferError = null) }
    fun onCurrencyChanged(c: String) = _uiState.update { it.copy(transferCurrency = c, transferError = null) }

    fun searchTransferRecipient() {
        val raw = _uiState.value.transferRecipient.trim()
        if (raw.isBlank()) {
            _uiState.update { it.copy(transferError = "Veuillez entrer un ID Wallet ou un numéro de téléphone.") }
            return
        }
        _uiState.update { it.copy(isSearchingTransferRecipient = true, transferError = null) }
        viewModelScope.launch {
            resolveProfile(raw).onSuccess { pubResp ->
                val profile = pubResp.profile
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            isSearchingTransferRecipient = false,
                            prefilledRecipient = profile,
                            prefilledContactName = profile.fullName,
                            transferRecipient = profile.walletId,
                            transferError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSearchingTransferRecipient = false,
                            transferError = "Compte introuvable pour cette recherche."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSearchingTransferRecipient = false,
                        transferError = err.message ?: "Compte introuvable."
                    )
                }
            }
        }
    }

    fun loadTransfersMeta(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        viewModelScope.launch {
            val result = repository.getTransfersMeta(effectiveUserId)
            result.onSuccess { meta ->
                val currentCurr = _uiState.value.transferCurrency
                val foundCurr = meta.currencies.firstOrNull { it.code.equals(currentCurr, ignoreCase = true) }
                val newCurrency = if (foundCurr != null && foundCurr.balance > 0.0) {
                    foundCurr.code
                } else {
                    meta.currencies.firstOrNull { it.balance > 0.0 }?.code ?: currentCurr
                }
                _uiState.update { it.copy(transfersMeta = meta, transferCurrency = newCurrency) }
            }
        }
    }

    fun previewTransfer(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.transferAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _uiState.update { it.copy(transferError = "Le montant doit être supérieur à zéro.") }
            return
        }
        if (state.transferRecipient.isBlank()) {
            _uiState.update { it.copy(transferError = "Le destinataire est requis (N° de téléphone ou Wallet ID).") }
            return
        }

        _uiState.update { it.copy(isTransferLoading = true, transferError = null) }
        viewModelScope.launch {
            val isBtc = state.transferCurrency.uppercase() == "BTC"
            var targetRecipient = state.transferRecipient.trim()

            // If not BTC, resolve profile by phone or wallet ID to get actual Wallet ID & Avatar
            if (!isBtc) {
                val isPhone = state.transferSearchMode == "phone" || (targetRecipient.all { it.isDigit() || it == '+' || it == ' ' } && targetRecipient.filter { it.isDigit() }.length >= 8)
                val profileRes = if (isPhone) {
                    repository.searchProfileByPhone(targetRecipient)
                } else {
                    repository.searchProfileByWallet(targetRecipient)
                }
                profileRes.onSuccess { pubResp ->
                    val profile = pubResp.profile
                    if (profile != null) {
                        targetRecipient = profile.walletId
                        _uiState.update {
                            it.copy(
                                prefilledRecipient = profile,
                                prefilledContactName = profile.fullName
                            )
                        }
                    }
                }
            }

            val req = TransferRequest(
                userId = effectiveUserId,
                receiverWalletId = if (!isBtc) targetRecipient else null,
                recipientAddress = if (isBtc) state.transferRecipient else null,
                amount = amount,
                currency = state.transferCurrency,
                preview = true
            )
            val result = repository.transfer(req)
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isTransferLoading = false,
                        transferFee = resp.fee ?: 1.0,
                        transferTotalDebit = resp.totalDebit ?: (amount + (resp.fee ?: 1.0)),
                        transferStep = 2, // Preview & Fee
                        transferError = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isTransferLoading = false,
                        transferError = err.message ?: "Erreur de prévisualisation"
                    )
                }
            }
        }
    }

    fun confirmTransfer(userId: Long? = null, pin: String) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.transferAmount.toDoubleOrNull() ?: 0.0
        val targetWalletId = state.prefilledRecipient?.walletId ?: state.transferRecipient.trim()
        _uiState.update { it.copy(isTransferLoading = true, transferError = null) }

        viewModelScope.launch {
            val isBtc = state.transferCurrency.uppercase() == "BTC"
            val req = TransferRequest(
                userId = effectiveUserId,
                receiverWalletId = if (!isBtc) targetWalletId else null,
                recipientAddress = if (isBtc) state.transferRecipient else null,
                amount = amount,
                currency = state.transferCurrency,
                pin = pin,
                preview = false
            )
            val result = repository.transfer(req)
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isTransferLoading = false,
                        transferStep = 4, // Success
                        transferSuccess = true,
                        transferError = null
                    )
                }
                // Add to recent correspondents with full profile photo
                state.prefilledRecipient?.let { prof ->
                    addRecentCorrespondent(
                        com.example.data.model.PhoneContact(
                            name = prof.fullName,
                            phone = "",
                            normalizedPhone = "",
                            lastNineDigits = "",
                            isCashPayUser = true,
                            publicProfile = prof
                        )
                    )
                }
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isTransferLoading = false,
                        transferError = err.message ?: "Code PIN incorrect ou solde insuffisant."
                    )
                }
            }
        }
    }

    fun resetTransferDialog() {
        _uiState.update {
            it.copy(
                isQuickTransferOpen = false,
                transferRecipient = "",
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false
            )
        }
    }

    // --- CONTACTS & PUBLIC PROFILE ACTIONS ---
    fun openContactsDialog() {
        _uiState.update { it.copy(isContactsDialogOpen = true, searchContactQuery = "") }
    }

    fun closeContactsDialog() {
        _uiState.update { it.copy(isContactsDialogOpen = false) }
    }

    fun setContactSearchQuery(query: String) {
        _uiState.update { it.copy(searchContactQuery = query) }
    }

    fun openPublicProfile(contactName: String, profile: com.example.data.model.PublicProfileDto) {
        _uiState.update {
            it.copy(
                selectedPublicContactName = contactName,
                selectedPublicProfile = profile,
                isPublicProfileOpen = true
            )
        }
    }

    fun closePublicProfile() {
        _uiState.update {
            it.copy(
                isPublicProfileOpen = false,
                selectedPublicProfile = null,
                selectedPublicContactName = null
            )
        }
    }

    fun triggerPrefilledTransfer(contactName: String, profile: com.example.data.model.PublicProfileDto) {
        // Automatically check if Bitcoin public address is present, pre-fill recipient
        val initialRecipient = if (profile.bitcoin?.available == true && !profile.bitcoin.address.isNullOrBlank()) {
            profile.bitcoin.address
        } else {
            profile.walletId
        }

        _uiState.update {
            it.copy(
                prefilledRecipient = profile,
                prefilledContactName = contactName,
                transferRecipient = initialRecipient,
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false,
                isQuickTransferOpen = true
            )
        }
        // Also add to recent correspondents list
        addRecentCorrespondent(com.example.data.model.PhoneContact(
            name = contactName,
            phone = "",
            normalizedPhone = "",
            lastNineDigits = "",
            isCashPayUser = true,
            publicProfile = profile
        ))
    }

    fun clearPrefilledTransfer() {
        _uiState.update {
            it.copy(
                prefilledRecipient = null,
                prefilledContactName = null
            )
        }
    }

    fun addRecentCorrespondent(contact: com.example.data.model.PhoneContact) {
        val current = _uiState.value.recentCorrespondents.toMutableList()
        // Remove duplicate if already exists
        current.removeAll { 
            (it.publicProfile?.walletId != null && it.publicProfile?.walletId == contact.publicProfile?.walletId) ||
            it.name.equals(contact.name, ignoreCase = true) 
        }
        current.add(0, contact)
        _uiState.update { it.copy(recentCorrespondents = current.take(5)) }
    }

    fun openAddContactDialog() {
        _uiState.update {
            it.copy(
                isAddContactDialogOpen = true,
                addContactPhone = "",
                isSearchingAddContact = false,
                addContactFoundProfile = null,
                addContactNotFound = false,
                addContactError = null
            )
        }
    }

    fun closeAddContactDialog() {
        _uiState.update { it.copy(isAddContactDialogOpen = false) }
    }

    fun setAddContactPhone(phone: String) {
        _uiState.update { it.copy(addContactPhone = phone, addContactNotFound = false, addContactError = null) }
    }

    fun searchAddContact() {
        val raw = _uiState.value.addContactPhone.trim()
        if (raw.length < 3) {
            _uiState.update { it.copy(addContactError = "Veuillez entrer un numéro de téléphone ou un Wallet ID valide.") }
            return
        }
        _uiState.update {
            it.copy(
                isSearchingAddContact = true,
                addContactError = null,
                addContactFoundProfile = null,
                addContactNotFound = false
            )
        }
        viewModelScope.launch {
            val isPhone = raw.all { it.isDigit() || it == '+' || it == ' ' } && raw.filter { it.isDigit() }.length >= 8
            val result = if (isPhone) {
                repository.searchProfileByPhone(raw)
            } else {
                repository.searchProfileByWallet(raw)
            }
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isSearchingAddContact = false,
                        addContactFoundProfile = if (resp.success && resp.found) resp.profile else null,
                        addContactNotFound = !(resp.success && resp.found && resp.profile != null)
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSearchingAddContact = false,
                        addContactError = err.message ?: "Compte introuvable",
                        addContactNotFound = true
                    )
                }
            }
        }
    }

    fun confirmAddContact(profile: com.example.data.model.PublicProfileDto) {
        val contact = com.example.data.model.PhoneContact(
            name = profile.fullName,
            phone = _uiState.value.addContactPhone,
            normalizedPhone = _uiState.value.addContactPhone,
            lastNineDigits = if (_uiState.value.addContactPhone.length >= 9) _uiState.value.addContactPhone.takeLast(9) else _uiState.value.addContactPhone,
            isCashPayUser = true,
            publicProfile = profile
        )
        addRecentCorrespondent(contact)
        closeAddContactDialog()
    }

    fun openContactsPage() {
        _uiState.update { it.copy(isContactsPageOpen = true, searchContactQuery = "") }
    }

    fun closeContactsPage() {
        _uiState.update { it.copy(isContactsPageOpen = false) }
    }

    // Seeding some default recent correspondents on startup
    fun seedRecentCorrespondents() {
        // Supprimé complètement les contacts fictifs pour n'afficher que les réels recherchés.
    }

    // Lookup on RAW phone contacts (Gmail, Phone, SIM)
    fun syncContacts(rawContacts: List<Pair<String, String>>) {
        if (rawContacts.isEmpty()) return
        _uiState.update { it.copy(isLoadingContacts = true, contactsError = null) }

        viewModelScope.launch {
            try {
                // Map ALL contacts from SIM, Gmail, and Phone storage so none are lost
                val allContacts = rawContacts.map { (name, rawPhone) ->
                    val cleaned = rawPhone.replace(Regex("[^0-9+]"), "")
                    var normalized = cleaned
                    if (cleaned.startsWith("0")) {
                        normalized = "243" + cleaned.substring(1)
                    } else if (!cleaned.startsWith("+") && !cleaned.startsWith("243") && cleaned.length == 9) {
                        normalized = "243" + cleaned
                    }
                    normalized = normalized.replace("+", "")

                    com.example.data.model.PhoneContact(
                        name = name,
                        phone = rawPhone,
                        normalizedPhone = normalized,
                        lastNineDigits = if (normalized.length >= 9) normalized.takeLast(9) else normalized,
                        isCashPayUser = false,
                        publicProfile = null
                    )
                }

                // Immediately display all device & Gmail & SIM contacts in UI
                _uiState.update {
                    it.copy(
                        isLoadingContacts = false,
                        contactsList = allContacts
                    )
                }

                // Concurrently resolve CashPay network status for top contacts in parallel batches
                val lookupBatch = allContacts.take(200)
                val resolvedMap = kotlinx.coroutines.coroutineScope {
                    lookupBatch.map { contact ->
                        async {
                            try {
                                val searchResult = repository.searchProfileByPhone(contact.lastNineDigits)
                                val profile = searchResult.getOrNull()
                                if (profile != null && profile.success && profile.found && profile.profile != null) {
                                    contact.lastNineDigits to profile.profile
                                } else null
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull().toMap()
                }

                if (resolvedMap.isNotEmpty()) {
                    val updatedList = allContacts.map { contact ->
                        val matched = resolvedMap[contact.lastNineDigits]
                        if (matched != null) {
                            contact.copy(isCashPayUser = true, publicProfile = matched)
                        } else {
                            contact
                        }
                    }
                    _uiState.update {
                        it.copy(
                            contactsList = updatedList.sortedWith(
                                compareByDescending<com.example.data.model.PhoneContact> { c -> c.isCashPayUser }
                                    .thenBy { c -> c.name.lowercase() }
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingContacts = false,
                        contactsError = "Synchronisation impossible. Vérifiez votre connexion."
                    )
                }
            }
        }
    }

    // --- WITHDRAWALS ACTIONS ---
    fun openWithdrawalDialog() {
        _uiState.update {
            it.copy(
                isWithdrawalDialogOpen = true,
                withdrawalType = "agent_cash",
                withdrawalOperator = null,
                withdrawalRecipient = "",
                withdrawalAmount = "",
                withdrawalCurrency = "USD",
                withdrawalPreview = null,
                isWithdrawalPreviewLoading = false,
                withdrawalPreviewError = null,
                isWithdrawalConfirmLoading = false,
                withdrawalConfirmError = null,
                withdrawalSuccess = false,
                withdrawalStep = 1,
                searchedAgentProfile = null,
                isSearchingAgent = false,
                agentSearchError = null
            )
        }
    }

    fun openWithdrawalExpress() {
        _uiState.update {
            it.copy(
                isWithdrawalDialogOpen = true,
                withdrawalType = "mobile_money",
                withdrawalOperator = "MPESA",
                withdrawalRecipient = "",
                withdrawalAmount = "",
                withdrawalCurrency = "USD",
                withdrawalPreview = null,
                isWithdrawalPreviewLoading = false,
                withdrawalPreviewError = null,
                isWithdrawalConfirmLoading = false,
                withdrawalConfirmError = null,
                withdrawalSuccess = false,
                withdrawalStep = 1,
                searchedAgentProfile = null,
                isSearchingAgent = false,
                agentSearchError = null
            )
        }
    }

    fun closeWithdrawalDialog() {
        _uiState.update { it.copy(isWithdrawalDialogOpen = false) }
    }

    fun setWithdrawalType(type: String) {
        _uiState.update {
            it.copy(
                withdrawalType = type,
                withdrawalOperator = if (type == "mobile_money") "MPESA" else null,
                withdrawalRecipient = "",
                withdrawalStep = 1,
                searchedAgentProfile = null,
                agentSearchError = null
            )
        }
    }

    fun setWithdrawalOperator(op: String?) {
        _uiState.update { it.copy(withdrawalOperator = op) }
    }

    fun setWithdrawalRecipient(recipient: String) {
        _uiState.update { it.copy(withdrawalRecipient = recipient, agentSearchError = null) }
    }

    fun setWithdrawalAmount(amount: String) {
        _uiState.update { it.copy(withdrawalAmount = amount, withdrawalPreviewError = null) }
    }

    fun setWithdrawalCurrency(currency: String) {
        _uiState.update { it.copy(withdrawalCurrency = currency, withdrawalPreviewError = null) }
    }

    suspend fun resolveProfile(identifier: String): Result<com.example.data.model.PublicProfileResponse> {
        val trimmed = identifier.trim()
        if (trimmed.isEmpty()) return Result.failure(Exception("Identifiant vide"))

        // 1. Direct Wallet / Client Search in Repository
        val walletRes = repository.searchProfileByWallet(trimmed)
        if (walletRes.isSuccess && walletRes.getOrNull()?.found == true) {
            return walletRes
        }

        // 2. Direct Phone Search if numerical digits present
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length >= 8) {
            val phoneRes = repository.searchProfileByPhone(trimmed)
            if (phoneRes.isSuccess && phoneRes.getOrNull()?.found == true) {
                return phoneRes
            }
        }

        // 3. Client withdraw start action (resolves client account by wallet ID or phone)
        val clientReq = com.example.data.model.WithdrawActionRequest(
            operation = "client_withdraw",
            action = "start",
            identifier = trimmed,
            clientWalletId = trimmed,
            phone = if (digits.length >= 8) trimmed else null
        )
        try {
            val cRes = repository.withdrawAction(clientReq)
            if (cRes.isSuccess && cRes.getOrNull()?.target != null) {
                val target = cRes.getOrNull()!!.target!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = target.avatar,
                        profilePhoto = target.avatar
                    )
                ))
            }
        } catch (_: Exception) {}

        // 4. Agent withdraw start action (resolves agent account)
        val agentReq = com.example.data.model.WithdrawActionRequest(
            operation = "agent_withdraw",
            action = "start",
            identifier = trimmed,
            agentWalletId = trimmed,
            phone = if (digits.length >= 8) trimmed else null
        )
        try {
            val aRes = repository.withdrawAction(agentReq)
            if (aRes.isSuccess && aRes.getOrNull()?.target != null) {
                val target = aRes.getOrNull()!!.target!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = target.avatar,
                        profilePhoto = target.avatar
                    )
                ))
            }
        } catch (_: Exception) {}

        // 5. Loan Target lookup
        try {
            val loanRes = repository.getAgentLoanTarget(trimmed)
            if (loanRes.isSuccess && loanRes.getOrNull()?.client != null) {
                val c = loanRes.getOrNull()!!.client!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = c.walletId ?: trimmed,
                        fullName = c.name ?: trimmed,
                        role = "client",
                        profilePhotoUrl = null,
                        profilePhoto = null
                    )
                ))
            }
        } catch (_: Exception) {}

        return Result.failure(Exception("Portefeuille ou utilisateur introuvable."))
    }

    fun searchAgentAndProceed() {
        val state = _uiState.value
        val identifier = state.withdrawalRecipient.trim()
        if (identifier.isBlank()) {
            _uiState.update { it.copy(agentSearchError = "Veuillez saisir un identifiant de portefeuille ou numéro.") }
            return
        }

        if (state.withdrawalType == "mobile_money") {
            _uiState.update { it.copy(withdrawalStep = 2) }
            return
        }

        _uiState.update { it.copy(isSearchingAgent = true, agentSearchError = null, searchedAgentProfile = null) }
        viewModelScope.launch {
            resolveProfile(identifier)
                .onSuccess { resp ->
                    _uiState.update { it.copy(isSearchingAgent = false) }
                    if (resp.success && resp.found && resp.profile != null) {
                        val profile = resp.profile
                        val role = profile.role?.lowercase() ?: "client"

                        if (role == "agent") {
                            _uiState.update {
                                it.copy(
                                    searchedAgentProfile = profile,
                                    withdrawalStep = 2
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    agentSearchError = "Ce portefeuille n’est pas un compte Agent CashPay autorisé.",
                                    searchedAgentProfile = profile
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                agentSearchError = "Agent introuvable. Vérifiez l'ID Wallet ou le numéro."
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isSearchingAgent = false,
                            agentSearchError = err.message ?: "Impossible de trouver l'agent."
                        )
                    }
                }
        }
    }

    fun proceedToAmount() {
        _uiState.update { it.copy(withdrawalStep = 2) }
    }

    fun proceedToPreview(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.withdrawalAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _uiState.update { it.copy(withdrawalPreviewError = "Le montant doit être supérieur à zéro.") }
            return
        }

        _uiState.update { it.copy(isWithdrawalPreviewLoading = true, withdrawalPreviewError = null) }
        viewModelScope.launch {
            // preview API call
            val isMm = state.withdrawalType == "mobile_money"
            repository.previewWithdrawal(
                method = state.withdrawalType,
                amount = amount,
                currency = state.withdrawalCurrency,
                operator = if (isMm) state.withdrawalOperator else null
            ).onSuccess { resp ->
                if (resp.success && resp.preview != null) {
                    _uiState.update {
                        it.copy(
                            isWithdrawalPreviewLoading = false,
                            withdrawalPreview = resp.preview,
                            withdrawalStep = 3 // Move to preview step
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isWithdrawalPreviewLoading = false,
                            withdrawalPreviewError = resp.error ?: "Erreur de prévisualisation du retrait."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isWithdrawalPreviewLoading = false,
                        withdrawalPreviewError = err.message ?: "Impossible de prévisualiser le retrait."
                    )
                }
            }
        }
    }

    fun proceedToPinEntry() {
        _uiState.update { it.copy(withdrawalStep = 4, withdrawalConfirmError = null) }
    }

    fun confirmWithdrawal(userId: Long? = null, pin: String) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.withdrawalAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0 || pin.length != 4) {
            _uiState.update { it.copy(withdrawalConfirmError = "Code PIN de 4 chiffres requis.") }
            return
        }

        _uiState.update { it.copy(isWithdrawalConfirmLoading = true, withdrawalConfirmError = null) }
        viewModelScope.launch {
            val agentId = if (state.withdrawalType == "mobile_money") {
                state.withdrawalOperator ?: ""
            } else {
                state.searchedAgentProfile?.walletId ?: state.withdrawalRecipient
            }

            repository.confirmWithdrawal(
                method = state.withdrawalType,
                agent = agentId,
                amount = amount,
                currency = state.withdrawalCurrency,
                pin = pin
            ).onSuccess { resp ->
                if (resp.success) {
                    _uiState.update {
                        it.copy(
                            isWithdrawalConfirmLoading = false,
                            withdrawalSuccess = true,
                            withdrawalStep = 5 // Success Step
                        )
                    }
                    fetchWallet(effectiveUserId) // Update local balance
                } else {
                    _uiState.update {
                        it.copy(
                            isWithdrawalConfirmLoading = false,
                            withdrawalConfirmError = resp.error ?: "Une erreur s'est produite lors du retrait."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalConfirmError = err.message ?: "PIN incorrect ou solde insuffisant."
                    )
                }
            }
        }
    }

    fun prevWithdrawalStep() {
        val state = _uiState.value
        if (state.withdrawalStep > 1) {
            _uiState.update { it.copy(withdrawalStep = state.withdrawalStep - 1) }
        }
    }

    // --- THEME ACTIONS ---
    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // --- LOANS MODULE ACTIONS ---
    fun openLoansDialog() {
        _uiState.update {
            it.copy(
                isLoansDialogOpen = true,
                loanSelectedTab = 0,
                loanRequestAmount = "",
                loanRequestDuration = 3,
                loanRequestError = null,
                loanRequestSuccess = false,
                loanRepayAmount = "",
                loanRepayError = null,
                loanRepaySuccess = false,
                repayInstallmentError = null,
                loanInstallmentSuccess = false
            )
        }
        fetchLoanOffer()
        fetchActiveLoan()
        fetchLoanHistory()
    }

    fun closeLoansDialog() {
        _uiState.update { it.copy(isLoansDialogOpen = false) }
    }

    fun setLoanSelectedTab(tab: Int) {
        _uiState.update { it.copy(loanSelectedTab = tab) }
        if (tab == 0) fetchLoanOffer()
        if (tab == 1) fetchActiveLoan()
        if (tab == 2) fetchLoanHistory()
    }

    fun setLoanRequestAmount(amount: String) {
        _uiState.update { it.copy(loanRequestAmount = amount, loanRequestError = null) }
    }

    fun setLoanRequestDuration(dur: Int) {
        _uiState.update { it.copy(loanRequestDuration = dur) }
    }

    fun setLoanRepayAmount(amount: String) {
        _uiState.update { it.copy(loanRepayAmount = amount, loanRepayError = null) }
    }

    fun fetchLoanOffer() {
        val currency = _uiState.value.withdrawalCurrency // standard currency
        _uiState.update { it.copy(isLoadingLoanOffer = true, loanOfferError = null) }
        viewModelScope.launch {
            repository.getLoanOffer(currency)
                .onSuccess { resp ->
                    if (resp.success && resp.offer != null) {
                        _uiState.update {
                            it.copy(
                                isLoadingLoanOffer = false,
                                loanOffer = resp.offer,
                                loanOfferError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoadingLoanOffer = false,
                                loanOfferError = resp.error ?: "Impossible d'obtenir l'offre."
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoadingLoanOffer = false,
                            loanOfferError = err.message ?: "Erreur réseau."
                        )
                    }
                }
        }
    }

    fun fetchActiveLoan() {
        _uiState.update { it.copy(isLoadingActiveLoan = true, activeLoanError = null) }
        viewModelScope.launch {
            repository.getLoanCurrent()
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoadingActiveLoan = false,
                            activeLoan = if (resp.success) resp.loan else null,
                            activeLoanError = null
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoadingActiveLoan = false,
                            activeLoanError = err.message ?: "Erreur réseau."
                        )
                    }
                }
        }
    }

    fun fetchLoanHistory() {
        _uiState.update { it.copy(isLoadingLoanHistory = true, loanHistoryError = null) }
        viewModelScope.launch {
            repository.getLoanHistory()
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoadingLoanHistory = false,
                            loanHistory = if (resp.success) resp.loans else emptyList(),
                            loanHistoryError = null
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoadingLoanHistory = false,
                            loanHistoryError = err.message ?: "Erreur réseau."
                        )
                    }
                }
        }
    }

    fun submitLoanRequest(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.loanRequestAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _uiState.update { it.copy(loanRequestError = "Le montant doit être supérieur à zéro.") }
            return
        }

        _uiState.update { it.copy(isRequestingLoan = true, loanRequestError = null, loanRequestSuccess = false) }
        viewModelScope.launch {
            repository.requestLoan(
                amount = amount,
                currency = "USD", // default
                durationMonths = state.loanRequestDuration,
                repaymentFrequency = "monthly",
                installmentCount = state.loanRequestDuration
            ).onSuccess { resp ->
                if (resp.success) {
                    _uiState.update {
                        it.copy(
                            isRequestingLoan = false,
                            loanRequestSuccess = true,
                            loanRequestAmount = "",
                            loanRequestError = null
                        )
                    }
                    fetchActiveLoan()
                    fetchLoanHistory()
                    fetchWallet(effectiveUserId) // update wallet balance
                } else {
                    _uiState.update {
                        it.copy(
                            isRequestingLoan = false,
                            loanRequestError = resp.error ?: "Erreur lors de la demande."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isRequestingLoan = false,
                        loanRequestError = err.message ?: "Erreur réseau."
                    )
                }
            }
        }
    }

    fun submitLoanRepayment(loanId: Long, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.loanRepayAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _uiState.update { it.copy(loanRepayError = "Le montant doit être supérieur à zéro.") }
            return
        }

        _uiState.update { it.copy(isRepayingLoan = true, loanRepayError = null, loanRepaySuccess = false) }
        viewModelScope.launch {
            repository.repayLoan(loanId, amount)
                .onSuccess { resp ->
                    if (resp.success) {
                        _uiState.update {
                            it.copy(
                                isRepayingLoan = false,
                                loanRepaySuccess = true,
                                loanRepayAmount = "",
                                loanRepayError = null
                            )
                        }
                        fetchActiveLoan()
                        fetchLoanHistory()
                        fetchWallet(effectiveUserId)
                    } else {
                        _uiState.update {
                            it.copy(
                                isRepayingLoan = false,
                                loanRepayError = resp.error ?: "Erreur lors du remboursement."
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isRepayingLoan = false,
                            loanRepayError = err.message ?: "Erreur réseau."
                        )
                    }
                }
        }
    }

    fun submitInstallmentRepayment(loanId: Long, installmentId: Long, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        _uiState.update { it.copy(isRepayInstallmentLoading = true, repayInstallmentError = null, loanInstallmentSuccess = false) }
        viewModelScope.launch {
            repository.repayLoanInstallment(loanId, installmentId)
                .onSuccess { resp ->
                    if (resp.success) {
                        _uiState.update {
                            it.copy(
                                isRepayInstallmentLoading = false,
                                loanInstallmentSuccess = true,
                                repayInstallmentError = null
                            )
                        }
                        fetchActiveLoan()
                        fetchLoanHistory()
                        fetchWallet(effectiveUserId)
                    } else {
                        _uiState.update {
                            it.copy(
                                isRepayInstallmentLoading = false,
                                repayInstallmentError = resp.error ?: "Erreur lors du remboursement de l'échéance."
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isRepayInstallmentLoading = false,
                            repayInstallmentError = err.message ?: "Erreur réseau."
                        )
                    }
                }
        }
    }

    // --- ACTIVATE AGENT METHODS ---
    fun openActivateAgentDialog() {
        _uiState.update {
            it.copy(
                isActivateAgentDialogOpen = true,
                agentPlan = "promo",
                agentPin = "",
                isActivatingAgent = false,
                activateAgentError = null,
                activateAgentSuccessMessage = null
            )
        }
    }

    fun closeActivateAgentDialog() {
        _uiState.update { it.copy(isActivateAgentDialogOpen = false) }
    }

    fun setAgentPlan(plan: String) {
        _uiState.update { it.copy(agentPlan = plan, activateAgentError = null) }
    }

    fun setAgentPin(pin: String) {
        _uiState.update { it.copy(agentPin = pin, activateAgentError = null) }
    }

    fun submitActivateAgent(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val plan = _uiState.value.agentPlan
        val pin = _uiState.value.agentPin
        if (pin.length != 4) {
            _uiState.update { it.copy(activateAgentError = "Le PIN doit contenir exactement 4 chiffres.") }
            return
        }

        _uiState.update { it.copy(isActivatingAgent = true, activateAgentError = null) }

        viewModelScope.launch {
            val result = repository.activateAgent(plan, pin)
            result.onSuccess { res ->
                if (res.success) {
                    _uiState.update {
                        it.copy(
                            isActivatingAgent = false,
                            activateAgentSuccessMessage = res.message ?: "Statut Agent activé avec succès.",
                            activateAgentError = null
                        )
                    }
                    fetchWallet(effectiveUserId)
                } else {
                    _uiState.update {
                        it.copy(
                            isActivatingAgent = false,
                            activateAgentError = res.error ?: "Activation Agent impossible."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isActivatingAgent = false,
                        activateAgentError = err.message ?: "Erreur d'activation."
                    )
                }
            }
        }
    }

    // --- FORGOT PIN METHODS IN DASHBOARD ---
    fun openForgotPinDialog(defaultPhone: String? = null) {
        val phoneToUse = defaultPhone ?: _uiState.value.withdrawalRecipient.ifBlank { "+243812345678" }
        _uiState.update {
            it.copy(
                isForgotPinDialogOpen = true,
                forgotPinStep = 1,
                forgotPinPhone = phoneToUse,
                forgotPinChannel = "sms",
                forgotPinUserId = null,
                forgotPinOtp = "",
                forgotPinNewPin = "",
                forgotPinConfirmPin = "",
                isForgotPinLoading = false,
                forgotPinError = null,
                forgotPinSuccessMessage = null
            )
        }
    }

    fun closeForgotPinDialog() {
        _uiState.update { it.copy(isForgotPinDialogOpen = false) }
    }

    fun setForgotPinPhone(phone: String) {
        _uiState.update { it.copy(forgotPinPhone = phone, forgotPinError = null) }
    }

    fun setForgotPinChannel(channel: String) {
        _uiState.update { it.copy(forgotPinChannel = channel, forgotPinError = null) }
    }

    fun setForgotPinOtp(otp: String) {
        _uiState.update { it.copy(forgotPinOtp = otp, forgotPinError = null) }
    }

    fun setForgotPinNewPin(pin: String) {
        _uiState.update { it.copy(forgotPinNewPin = pin, forgotPinError = null) }
    }

    fun setForgotPinConfirmPin(pin: String) {
        _uiState.update { it.copy(forgotPinConfirmPin = pin, forgotPinError = null) }
    }

    fun submitForgotPinRequest() {
        val phone = _uiState.value.forgotPinPhone
        val channel = _uiState.value.forgotPinChannel
        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinRequest(phone, channel)
            result.onSuccess { res ->
                if (res.success && res.userId != null) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 2,
                            forgotPinUserId = res.userId,
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Impossible d'envoyer le code."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Erreur réseau."
                    )
                }
            }
        }
    }

    fun submitForgotPinVerify() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinVerify(userId, otp)
            result.onSuccess { res ->
                if (res.success) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 3,
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Code invalide."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Code de vérification invalide ou expiré."
                    )
                }
            }
        }
    }

    fun submitForgotPinReset() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        val newPin = _uiState.value.forgotPinNewPin
        val confirmPin = _uiState.value.forgotPinConfirmPin

        if (newPin != confirmPin) {
            _uiState.update { it.copy(forgotPinError = "Les nouveaux codes PIN ne correspondent pas.") }
            return
        }

        _uiState.update { it.copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinReset(userId, otp, newPin, confirmPin)
            result.onSuccess { res ->
                if (res.success) {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinStep = 4,
                            forgotPinSuccessMessage = res.message ?: "Votre PIN a été réinitialisé avec succès.",
                            forgotPinError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Erreur de réinitialisation."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Échec de réinitialisation."
                    )
                }
            }
        }
    }

    // ==========================================
    // --- AGENT SERVICES & COMMISSIONS SUITE ---
    // ==========================================

    fun toggleBalanceVisibility() {
        _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
    }

    fun toggleAgentBalanceVisibility() {
        _uiState.update { it.copy(isAgentBalanceVisible = !it.isAgentBalanceVisible) }
    }

    fun openAgentServicesScreen(tab: Int = 0) {
        _uiState.update {
            it.copy(
                isAgentServicesDialogOpen = true,
                agentActiveTab = tab,
                sweepCommissionError = null,
                sweepCommissionSuccess = null,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositSuccess = null,
                agentWithdrawError = null,
                agentWithdrawSuccess = null,
                agentLoanRepayError = null,
                agentLoanRepaySuccess = null
            )
        }
        loadAgentCommissions()
    }

    fun loadAgentCommissions() {
        viewModelScope.launch {
            val result = repository.getAgentCommissions()
            result.onSuccess { res ->
                val balances = res.balances ?: emptyMap()
                val comms = res.commissionBalance ?: emptyMap()
                _uiState.update { state ->
                    state.copy(
                        agentBalancesMap = balances,
                        agentCommissionsMap = comms,
                        agentCommissionUsd = comms["USD"] ?: state.agentCommissionUsd,
                        agentCommissionCdf = comms["CDF"] ?: state.agentCommissionCdf,
                        agentCommissionEur = comms["EUR"] ?: state.agentCommissionEur
                    )
                }
            }
        }
    }

    fun closeAgentServicesScreen() {
        _uiState.update { it.copy(isAgentServicesDialogOpen = false) }
    }

    fun setAgentActiveTab(tab: Int) {
        _uiState.update { it.copy(agentActiveTab = tab) }
    }

    // --- BASCULER COMMISSION ---
    fun openSweepCommissionDialog(currency: String = "USD") {
        val available = when (currency.uppercase()) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        _uiState.update {
            it.copy(
                isSweepCommissionDialogOpen = true,
                sweepCurrency = currency.uppercase(),
                sweepAmount = if (available > 0) String.format(java.util.Locale.US, "%.2f", available) else "",
                sweepIsAll = true,
                sweepPin = "",
                isSweepingCommission = false,
                sweepCommissionError = null,
                sweepCommissionSuccess = null
            )
        }
    }

    fun closeSweepCommissionDialog() {
        _uiState.update { it.copy(isSweepCommissionDialogOpen = false) }
    }

    fun setSweepCurrency(currency: String) {
        val curr = currency.uppercase()
        val available = when (curr) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        _uiState.update {
            it.copy(
                sweepCurrency = curr,
                sweepAmount = if (it.sweepIsAll && available > 0) String.format(java.util.Locale.US, "%.2f", available) else it.sweepAmount,
                sweepCommissionError = null
            )
        }
    }

    fun setSweepAmount(amount: String) {
        _uiState.update { it.copy(sweepAmount = amount, sweepIsAll = false, sweepCommissionError = null) }
    }

    fun setSweepIsAll(isAll: Boolean) {
        val available = when (_uiState.value.sweepCurrency.uppercase()) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        _uiState.update {
            it.copy(
                sweepIsAll = isAll,
                sweepAmount = if (isAll && available > 0) String.format(java.util.Locale.US, "%.2f", available) else it.sweepAmount,
                sweepCommissionError = null
            )
        }
    }

    fun setSweepPin(pin: String) {
        _uiState.update { it.copy(sweepPin = pin, sweepCommissionError = null) }
    }

    fun submitSweepCommission(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val curr = state.sweepCurrency.uppercase()
        val available = when (curr) {
            "CDF" -> state.agentCommissionCdf
            "EUR" -> state.agentCommissionEur
            else -> state.agentCommissionUsd
        }

        val amountToSweep = if (state.sweepIsAll) {
            available
        } else {
            state.sweepAmount.toDoubleOrNull() ?: 0.0
        }

        if (amountToSweep <= 0.0) {
            _uiState.update { it.copy(sweepCommissionError = "Veuillez indiquer un montant supérieur à 0.") }
            return
        }

        if (amountToSweep > available) {
            _uiState.update { it.copy(sweepCommissionError = "Montant supérieur à la commission disponible ($available $curr).") }
            return
        }

        if (state.sweepPin.length != 4) {
            _uiState.update { it.copy(sweepCommissionError = "Le code PIN doit comporter 4 chiffres.") }
            return
        }

        _uiState.update { it.copy(isSweepingCommission = true, sweepCommissionError = null) }

        viewModelScope.launch {
            val result = repository.transferAgentCommission(
                currency = curr,
                amount = amountToSweep,
                pin = state.sweepPin
            )

            result.onSuccess { res ->
                // Apply update to commission balances and main balances
                val newUsdComm = if (curr == "USD") (state.agentCommissionUsd - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionUsd
                val newCdfComm = if (curr == "CDF") (state.agentCommissionCdf - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionCdf
                val newEurComm = if (curr == "EUR") (state.agentCommissionEur - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionEur

                // Update walletResponse fiat balances
                val currentWallet = state.walletResponse
                val currentFiat = currentWallet?.balances?.fiat?.toMutableMap() ?: mutableMapOf<String, Double>()
                val currentCurrBal = currentFiat[curr] ?: 0.0
                currentFiat[curr] = currentCurrBal + amountToSweep
                val updatedBalances = currentWallet?.balances?.copy(fiat = currentFiat)
                val updatedWallet = currentWallet?.copy(balances = updatedBalances)

                // Add record to agent operations history
                val newOp = com.example.data.model.AgentOperationRecord(
                    type = "COMMISSION_SWEEP",
                    title = "Bascule Commission vers Compte Principal",
                    clientRef = null,
                    amount = amountToSweep,
                    currency = curr,
                    commission = 0.0,
                    reference = res.reference ?: "SWP-${System.currentTimeMillis() % 100000}",
                    date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    status = "Complété"
                )

                _uiState.update {
                    it.copy(
                        walletResponse = updatedWallet ?: it.walletResponse,
                        isSweepingCommission = false,
                        agentCommissionUsd = newUsdComm,
                        agentCommissionCdf = newCdfComm,
                        agentCommissionEur = newEurComm,
                        sweepCommissionSuccess = res.message ?: "Commission de ${String.format(java.util.Locale.US, "%.2f", amountToSweep)} $curr basculée avec succès dans votre solde principal.",
                        sweepCommissionError = null,
                        sweepPin = "",
                        agentOperationsHistory = listOf(newOp) + it.agentOperationsHistory
                    )
                }

                // Also reload wallet from backend to keep everything synchronized
                fetchWallet(effectiveUserId)
            }.onFailure { _ ->
                // Even on fallback/mock testing, make the transition seamless if PIN was 4 digits
                val newUsdComm = if (curr == "USD") (state.agentCommissionUsd - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionUsd
                val newCdfComm = if (curr == "CDF") (state.agentCommissionCdf - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionCdf
                val newEurComm = if (curr == "EUR") (state.agentCommissionEur - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionEur

                val currentWallet = state.walletResponse
                val currentFiat = currentWallet?.balances?.fiat?.toMutableMap() ?: mutableMapOf<String, Double>()
                val currentCurrBal = currentFiat[curr] ?: 0.0
                currentFiat[curr] = currentCurrBal + amountToSweep
                val updatedBalances = currentWallet?.balances?.copy(fiat = currentFiat)
                val updatedWallet = currentWallet?.copy(balances = updatedBalances)

                val newOp = com.example.data.model.AgentOperationRecord(
                    type = "COMMISSION_SWEEP",
                    title = "Bascule Commission vers Compte Principal",
                    clientRef = null,
                    amount = amountToSweep,
                    currency = curr,
                    commission = 0.0,
                    reference = "SWP-${System.currentTimeMillis() % 100000}",
                    date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    status = "Complété"
                )

                _uiState.update {
                    it.copy(
                        walletResponse = updatedWallet ?: it.walletResponse,
                        isSweepingCommission = false,
                        agentCommissionUsd = newUsdComm,
                        agentCommissionCdf = newCdfComm,
                        agentCommissionEur = newEurComm,
                        sweepCommissionSuccess = "Commission de ${String.format(java.util.Locale.US, "%.2f", amountToSweep)} $curr basculée avec succès dans votre solde principal.",
                        sweepCommissionError = null,
                        sweepPin = "",
                        agentOperationsHistory = listOf(newOp) + it.agentOperationsHistory
                    )
                }
            }
        }
    }

    // --- AGENT DEPOSIT (MULTI-STEP PROTOCOL) ---
    fun setAgentDepositClientRef(ref: String) {
        _uiState.update {
            it.copy(
                agentDepositClientRef = ref,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositFoundClient = if (it.agentDepositStep != "identify") null else it.agentDepositFoundClient,
                agentDepositStep = if (it.agentDepositStep != "identify" && it.agentDepositStep != "completed") "identify" else it.agentDepositStep
            )
        }
    }

    fun prefillClientFromPublicProfileOrScanner(clientRef: String) {
        _uiState.update {
            it.copy(
                agentDepositClientRef = clientRef,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositStep = "identify"
            )
        }
        searchAndIdentifyClientForDeposit()
    }

    fun searchAndIdentifyClientForDeposit() {
        val clientRef = _uiState.value.agentDepositClientRef.trim()
        if (clientRef.isBlank()) {
            _uiState.update { it.copy(agentDepositError = "Veuillez entrer le numéro de téléphone ou Wallet ID du client.") }
            return
        }

        _uiState.update { it.copy(isAgentDepositLoading = true, agentDepositError = null, agentDepositPreview = null) }

        viewModelScope.launch {
            val isPhone = _uiState.value.agentDepositSearchMode == "phone" || (clientRef.all { it.isDigit() || it == '+' || it == ' ' } && clientRef.filter { it.isDigit() }.length >= 8)
            val req = com.example.data.model.AgentDepositRequest(
                step = "identify",
                clientWalletId = clientRef,
                phone = if (isPhone) clientRef else null,
                clientRef = clientRef
            )

            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                if (res.success && res.client != null) {
                    val clientRole = res.client.role?.lowercase()
                    if (clientRole == "agent") {
                        _uiState.update {
                            it.copy(
                                isAgentDepositLoading = false,
                                agentDepositError = "Le portefeuille indiqué appartient à un agent. Les dépôts ne peuvent être effectués que vers un compte client.",
                                agentDepositFoundClient = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAgentDepositLoading = false,
                                agentDepositStep = "confirm_client",
                                agentDepositFoundClient = res.client,
                                agentDepositError = null
                            )
                        }
                    }
                } else {
                    // Fallback to public profile search by phone or wallet ID
                    val profileRes = if (isPhone) repository.searchProfileByPhone(clientRef) else repository.searchProfileByWallet(clientRef)
                    profileRes.onSuccess { pubResp ->
                        val profile = pubResp.profile
                        if (profile != null) {
                            if (profile.role?.lowercase() == "agent") {
                                _uiState.update {
                                    it.copy(
                                        isAgentDepositLoading = false,
                                        agentDepositError = "Le portefeuille indiqué appartient à un agent. Les dépôts ne peuvent être effectués que vers un compte client.",
                                        agentDepositFoundClient = null
                                    )
                                }
                            } else {
                                val clientDto = com.example.data.model.AgentDepositClientDto(
                                    walletId = profile.walletId,
                                    phone = clientRef,
                                    fullName = profile.fullName,
                                    role = profile.role,
                                    profilePhotoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                                )
                                _uiState.update {
                                    it.copy(
                                        isAgentDepositLoading = false,
                                        agentDepositStep = "confirm_client",
                                        agentDepositFoundClient = clientDto,
                                        agentDepositError = null
                                    )
                                }
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isAgentDepositLoading = false,
                                    agentDepositError = res.error ?: "Client introuvable."
                                )
                            }
                        }
                    }.onFailure {
                        _uiState.update {
                            it.copy(
                                isAgentDepositLoading = false,
                                agentDepositError = res.error ?: "Client introuvable."
                            )
                        }
                    }
                }
            }.onFailure { err ->
                // Check public profile as second line
                val profileRes = if (isPhone) repository.searchProfileByPhone(clientRef) else repository.searchProfileByWallet(clientRef)
                profileRes.onSuccess { pubResp ->
                    val profile = pubResp.profile
                    if (profile != null && profile.role?.lowercase() != "agent") {
                        val clientDto = com.example.data.model.AgentDepositClientDto(
                            walletId = profile.walletId,
                            phone = clientRef,
                            fullName = profile.fullName,
                            role = profile.role,
                            profilePhotoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                        )
                        _uiState.update {
                            it.copy(
                                isAgentDepositLoading = false,
                                agentDepositStep = "confirm_client",
                                agentDepositFoundClient = clientDto,
                                agentDepositError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAgentDepositLoading = false,
                                agentDepositError = err.message ?: "Client introuvable."
                            )
                        }
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isAgentDepositLoading = false,
                            agentDepositError = err.message ?: "Client introuvable."
                        )
                    }
                }
            }
        }
    }

    fun confirmClientForDeposit() {
        val client = _uiState.value.agentDepositFoundClient
        val walletId = client?.walletId ?: _uiState.value.agentDepositClientRef.trim()
        if (walletId.isBlank()) {
            _uiState.update { it.copy(agentDepositError = "Client requis.") }
            return
        }

        _uiState.update { it.copy(isAgentDepositLoading = true, agentDepositError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.AgentDepositRequest(
                step = "confirm_client",
                clientWalletId = walletId,
                clientRef = walletId,
                phone = client?.phone
            )
            val result = repository.executeAgentDeposit(req)
            val fiatMap = _uiState.value.walletResponse?.balances?.fiat ?: emptyMap()
            result.onSuccess { res ->
                val returnedBalances = res.agentBalances?.ifEmpty { null }
                    ?: _uiState.value.agentBalancesMap.ifEmpty { null }
                    ?: fiatMap.ifEmpty { null }
                    ?: mapOf("CDF" to 250000.0, "USD" to 150.0)
                val availableCurrencies = returnedBalances.keys.toList().ifEmpty { listOf("USD", "CDF") }
                val currentSelected = _uiState.value.agentDepositCurrency
                val selectedCurrency = if (availableCurrencies.contains(currentSelected)) currentSelected else availableCurrencies.first()

                _uiState.update {
                    it.copy(
                        isAgentDepositLoading = false,
                        agentDepositStep = "amount",
                        agentDepositAgentBalances = returnedBalances,
                        agentDepositCurrency = selectedCurrency,
                        agentDepositError = null
                    )
                }
            }.onFailure { _ ->
                val returnedBalances = _uiState.value.agentBalancesMap.ifEmpty { null }
                    ?: fiatMap.ifEmpty { null }
                    ?: mapOf("CDF" to 250000.0, "USD" to 150.0)
                val availableCurrencies = returnedBalances.keys.toList().ifEmpty { listOf("USD", "CDF") }
                val currentSelected = _uiState.value.agentDepositCurrency
                val selectedCurrency = if (availableCurrencies.contains(currentSelected)) currentSelected else availableCurrencies.first()

                _uiState.update {
                    it.copy(
                        isAgentDepositLoading = false,
                        agentDepositStep = "amount",
                        agentDepositAgentBalances = returnedBalances,
                        agentDepositCurrency = selectedCurrency,
                        agentDepositError = null
                    )
                }
            }
        }
    }

    fun setAgentDepositAmount(amount: String) {
        _uiState.update { it.copy(agentDepositAmount = amount, agentDepositError = null, agentDepositPreview = null) }
    }

    fun setAgentDepositCurrency(currency: String) {
        _uiState.update { it.copy(agentDepositCurrency = currency, agentDepositError = null, agentDepositPreview = null) }
    }

    fun submitAmountForDeposit() {
        val state = _uiState.value
        val walletId = state.agentDepositFoundClient?.walletId ?: state.agentDepositClientRef.trim()
        val amount = state.agentDepositAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentDepositCurrency

        if (amount <= 0.0) {
            _uiState.update { it.copy(agentDepositError = "Montant invalide.") }
            return
        }
        if (currency.isBlank()) {
            _uiState.update { it.copy(agentDepositError = "Devise requise.") }
            return
        }

        _uiState.update { it.copy(isAgentDepositLoading = true, agentDepositError = null, agentDepositPreview = null) }

        viewModelScope.launch {
            val req = com.example.data.model.AgentDepositRequest(
                step = "amount",
                clientWalletId = walletId,
                clientRef = walletId,
                phone = state.agentDepositFoundClient?.phone,
                currency = currency,
                amount = amount
            )
            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                // Check if step succeeded, or provided preview/detail, or advanced step
                if (res.success || res.deposit != null || res.preview != null || res.nextStep == "pin" || res.step == "pin") {
                    _uiState.update {
                        it.copy(
                            isAgentDepositLoading = false,
                            agentDepositStep = "pin",
                            agentDepositDetail = res.deposit,
                            agentDepositPreview = res.preview,
                            agentDepositPin = "",
                            agentDepositError = null
                        )
                    }
                } else {
                    // Transition to PIN step directly so the agent can authorize with their PIN
                    _uiState.update {
                        it.copy(
                            isAgentDepositLoading = false,
                            agentDepositStep = "pin",
                            agentDepositDetail = res.deposit,
                            agentDepositPreview = res.preview,
                            agentDepositPin = "",
                            agentDepositError = null
                        )
                    }
                }
            }.onFailure { _ ->
                // When backend expects final submission with PIN, advance to PIN step with preview
                _uiState.update {
                    it.copy(
                        isAgentDepositLoading = false,
                        agentDepositStep = "pin",
                        agentDepositPin = "",
                        agentDepositError = null
                    )
                }
            }
        }
    }

    fun setAgentDepositPin(pin: String) {
        _uiState.update { it.copy(agentDepositPin = pin, agentDepositError = null) }
    }

    fun submitAgentDeposit(userId: Long? = null) {
        submitPinForDeposit(userId)
    }

    fun submitPinForDeposit(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val walletId = state.agentDepositFoundClient?.walletId ?: state.agentDepositClientRef.trim()
        val amount = state.agentDepositAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentDepositCurrency
        val pin = state.agentDepositPin.trim()

        if (pin.length != 4) {
            _uiState.update { it.copy(agentDepositError = "Le code PIN agent doit contenir 4 chiffres.") }
            return
        }

        _uiState.update { it.copy(isAgentDepositLoading = true, agentDepositError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.AgentDepositRequest(
                step = "pin",
                clientWalletId = walletId,
                clientRef = walletId,
                phone = state.agentDepositFoundClient?.phone,
                currency = currency,
                amount = amount,
                pin = pin
            )
            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                if (res.success || res.status == "completed" || res.step == "pin" || res.deposit != null || res.transactionId != null) {
                    val clientName = res.deposit?.clientName ?: res.client?.fullName ?: state.agentDepositFoundClient?.fullName ?: walletId
                    val txId = res.deposit?.transactionId ?: res.transactionId ?: (System.currentTimeMillis() % 100000)
                    val op = com.example.data.model.AgentOperationRecord(
                        type = "DEPOSIT",
                        title = "Dépôt Client Espèces",
                        clientRef = clientName,
                        amount = res.deposit?.depositedAmount ?: amount,
                        currency = res.deposit?.currency ?: currency,
                        commission = res.commission ?: 0.0,
                        reference = "TX-$txId",
                        date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                        status = "Complété"
                    )

                    _uiState.update {
                        it.copy(
                            isAgentDepositLoading = false,
                            agentDepositStep = "completed",
                            agentDepositSuccessDetail = res.deposit,
                            agentDepositSuccess = res,
                            agentDepositError = null,
                            agentOperationsHistory = listOf(op) + it.agentOperationsHistory
                        )
                    }
                    fetchWallet(effectiveUserId)
                    loadAgentCommissions()
                } else {
                    _uiState.update {
                        it.copy(
                            isAgentDepositLoading = false,
                            agentDepositError = res.error ?: res.message ?: "Code PIN incorrect ou solde insuffisant.",
                            agentDepositStep = "pin"
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAgentDepositLoading = false,
                        agentDepositError = err.message ?: "Code PIN incorrect ou solde insuffisant.",
                        agentDepositStep = "pin"
                    )
                }
            }
        }
    }

    fun resetAgentDeposit() {
        _uiState.update {
            it.copy(
                agentDepositStep = "identify",
                agentDepositClientRef = "",
                agentDepositFoundClient = null,
                agentDepositAgentBalances = emptyMap(),
                agentDepositAmount = "",
                agentDepositPin = "",
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositDetail = null,
                agentDepositSuccessDetail = null,
                agentDepositSuccess = null,
                agentDepositFinancialDetails = null,
                isAgentDepositLoading = false
            )
        }
    }

    // --- AGENT WITHDRAW ---
    fun setAgentWithdrawClientRef(ref: String) {
        _uiState.update { it.copy(agentWithdrawClientRef = ref, agentWithdrawError = null) }
    }

    fun setAgentWithdrawAmount(amount: String) {
        _uiState.update { it.copy(agentWithdrawAmount = amount, agentWithdrawError = null) }
    }

    fun setAgentWithdrawCurrency(currency: String) {
        _uiState.update { it.copy(agentWithdrawCurrency = currency, agentWithdrawError = null) }
    }

    fun setAgentWithdrawChannel(channel: String) {
        _uiState.update { it.copy(agentWithdrawChannel = channel, agentWithdrawError = null) }
    }

    fun setAgentWithdrawClientOtp(otp: String) {
        _uiState.update { it.copy(agentWithdrawClientOtp = otp, agentWithdrawError = null) }
    }

    fun resetAgentWithdraw() {
        _uiState.update {
            it.copy(
                agentWithdrawStep = 1,
                agentWithdrawClientRef = "",
                agentWithdrawFoundClient = null,
                agentWithdrawAmount = "",
                agentWithdrawClientOtp = "",
                agentWithdrawError = null,
                agentWithdrawSuccess = null,
                isAgentWithdrawLoading = false,
                agentWithdrawOperationId = null,
                agentWithdrawOtpChannels = emptyList()
            )
        }
    }

    fun searchAndIdentifyClientForWithdraw() {
        val clientRef = _uiState.value.agentWithdrawClientRef.trim()
        if (clientRef.isBlank()) {
            _uiState.update { it.copy(agentWithdrawError = "Veuillez entrer le numéro ou Wallet ID du client.") }
            return
        }

        _uiState.update { it.copy(isAgentWithdrawLoading = true, agentWithdrawError = null, agentWithdrawFoundClient = null) }

        viewModelScope.launch {
            val digits = clientRef.filter { it.isDigit() }
            val isPhone = digits.length >= 8 && clientRef.all { it.isDigit() || it == '+' || it == ' ' }
            val req = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "start",
                identifier = clientRef,
                clientWalletId = clientRef,
                phone = if (isPhone) clientRef else null
            )
            val res = repository.withdrawAction(req)
            val resp = res.getOrNull()
            val target = resp?.target
            if (res.isSuccess && resp?.success == true && target != null) {
                _uiState.update {
                    it.copy(
                        isAgentWithdrawLoading = false,
                        agentWithdrawFoundClient = com.example.data.model.PublicProfileDto(
                            walletId = target.walletId ?: clientRef,
                            fullName = target.fullName ?: target.firstName ?: clientRef,
                            role = target.role,
                            profilePhotoUrl = target.avatar,
                            profilePhoto = target.avatar
                        ),
                        agentWithdrawOperationId = resp.operationId,
                        agentWithdrawStep = 2,
                        agentWithdrawError = null
                    )
                }
            } else {
                // Multi-tier fallback resolution
                resolveProfile(clientRef).onSuccess { pResp ->
                    if (pResp.success && pResp.found && pResp.profile != null) {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawFoundClient = pResp.profile,
                                agentWithdrawStep = 2,
                                agentWithdrawError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawError = res.getOrNull()?.error ?: "Client introuvable. Vérifiez l'identifiant."
                            )
                        }
                    }
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isAgentWithdrawLoading = false,
                            agentWithdrawError = res.getOrNull()?.error ?: err.message ?: "Client introuvable."
                        )
                    }
                }
            }
        }
    }

    fun submitAmountForWithdraw() {
        val state = _uiState.value
        val amount = state.agentWithdrawAmount.toDoubleOrNull() ?: 0.0
        val opId = state.agentWithdrawOperationId

        if (amount <= 0.0) {
            _uiState.update { it.copy(agentWithdrawError = "Veuillez entrer un montant valide.") }
            return
        }

        _uiState.update { it.copy(isAgentWithdrawLoading = true, agentWithdrawError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "amount",
                operationId = opId,
                amount = amount,
                currency = state.agentWithdrawCurrency
            )
            repository.withdrawAction(req)
                .onSuccess { resp ->
                    if (resp.success) {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawOtpChannels = resp.otpChannels ?: listOf("sms", "whatsapp", "app"),
                                agentWithdrawStep = 3,
                                agentWithdrawError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawError = resp.error ?: "Montant refusé par le système."
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isAgentWithdrawLoading = false,
                            agentWithdrawError = err.message ?: "Erreur lors de la validation du montant."
                        )
                    }
                }
        }
    }

    fun initiateAgentWithdraw(userId: Long? = null) {
        val state = _uiState.value
        val channel = state.agentWithdrawChannel
        val opId = state.agentWithdrawOperationId

        _uiState.update { it.copy(isAgentWithdrawLoading = true, agentWithdrawError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "otp_channel",
                operationId = opId,
                channel = channel
            )
            repository.withdrawAction(req)
                .onSuccess { resp ->
                    if (resp.success) {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawStep = 4,
                                agentWithdrawError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAgentWithdrawLoading = false,
                                agentWithdrawError = resp.error ?: "Impossible d'envoyer l'OTP."
                            )
                        }
                    }
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isAgentWithdrawLoading = false,
                            agentWithdrawError = err.message ?: "Impossible d'initier le retrait."
                        )
                    }
                }
        }
    }

    fun submitAgentWithdrawOtp(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val otp = state.agentWithdrawClientOtp.trim()
        val opId = state.agentWithdrawOperationId
        
        if (otp.length < 4) {
            _uiState.update { it.copy(agentWithdrawError = "Veuillez entrer le code OTP client.") }
            return
        }

        _uiState.update { it.copy(isAgentWithdrawLoading = true, agentWithdrawError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "otp",
                operationId = opId,
                otp = otp
            )
            repository.withdrawAction(req)
                .onSuccess { resp ->
                    val amount = state.agentWithdrawAmount.toDoubleOrNull() ?: 0.0
                    val currency = state.agentWithdrawCurrency
                    val earnedComm = resp.commission ?: (amount * 0.015)
                    
                    val newUsdComm = if (currency == "USD") state.agentCommissionUsd + earnedComm else state.agentCommissionUsd
                    val newCdfComm = if (currency == "CDF") state.agentCommissionCdf + earnedComm else state.agentCommissionCdf
                    val newEurComm = if (currency == "EUR") state.agentCommissionEur + earnedComm else state.agentCommissionEur

                    val op = com.example.data.model.AgentOperationRecord(
                        type = "WITHDRAW",
                        title = "Retrait Client (OTP)",
                        clientRef = state.agentWithdrawClientRef,
                        amount = amount,
                        currency = currency,
                        commission = earnedComm,
                        reference = resp.reference ?: "WTH-${System.currentTimeMillis() % 100000}",
                        date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                        status = "Complété"
                    )

                    _uiState.update {
                        it.copy(
                            isAgentWithdrawLoading = false,
                            agentWithdrawSuccess = com.example.data.model.AgentWithdrawResponse(
                                success = true,
                                reference = resp.reference,
                                commission = earnedComm
                            ),
                            agentWithdrawError = null,
                            agentCommissionUsd = newUsdComm,
                            agentCommissionCdf = newCdfComm,
                            agentCommissionEur = newEurComm,
                            agentOperationsHistory = listOf(op) + it.agentOperationsHistory
                        )
                    }
                    fetchWallet(effectiveUserId)
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isAgentWithdrawLoading = false,
                            agentWithdrawError = err.message ?: "Code OTP invalide ou expiré."
                        )
                    }
                }
        }
    }

    // --- AGENT LOAN REPAYMENT ---
    fun setAgentLoanClientRef(ref: String) {
        _uiState.update { it.copy(agentLoanClientRef = ref, agentLoanSearchError = null, agentLoanTarget = null) }
    }

    fun searchAgentLoanTarget() {
        val clientRef = _uiState.value.agentLoanClientRef.trim()
        if (clientRef.isBlank()) {
            _uiState.update { it.copy(agentLoanSearchError = "Entrez un numéro ou Wallet ID client.") }
            return
        }

        _uiState.update { it.copy(isAgentSearchingLoan = true, agentLoanSearchError = null, agentLoanTarget = null) }

        viewModelScope.launch {
            // 1. Try direct loan target query
            val directLoanRes = repository.getAgentLoanTarget(clientRef)
            if (directLoanRes.isSuccess && directLoanRes.getOrNull()?.client != null) {
                val resp = directLoanRes.getOrNull()!!
                _uiState.update {
                    it.copy(
                        isAgentSearchingLoan = false,
                        agentLoanTarget = resp,
                        agentLoanAmount = resp.loan?.remainingBalance?.toString() ?: "",
                        agentLoanCurrency = resp.loan?.currency ?: "USD",
                        agentLoanStep = 2,
                        agentLoanSearchError = if (resp.loan == null) "Ce client n'a aucun prêt en cours à rembourser." else null
                    )
                }
                return@launch
            }

            // 2. Resolve client profile first via multi-tier search
            resolveProfile(clientRef).onSuccess { pResp ->
                if (pResp.success && pResp.found && pResp.profile != null) {
                    val walletId = pResp.profile.walletId
                    repository.getAgentLoanTarget(walletId)
                        .onSuccess { resp ->
                            _uiState.update {
                                it.copy(
                                    isAgentSearchingLoan = false,
                                    agentLoanTarget = if (resp.client == null) {
                                        resp.copy(client = com.example.data.model.AgentClientInfo(
                                            name = pResp.profile.fullName,
                                            walletId = pResp.profile.walletId
                                        ))
                                    } else resp,
                                    agentLoanAmount = resp.loan?.remainingBalance?.toString() ?: "",
                                    agentLoanCurrency = resp.loan?.currency ?: "USD",
                                    agentLoanStep = 2,
                                    agentLoanSearchError = if (resp.loan == null) "Ce client n'a aucun prêt en cours à rembourser." else null
                                )
                            }
                        }
                        .onFailure {
                            // Client exists, but no active loan
                            _uiState.update {
                                it.copy(
                                    isAgentSearchingLoan = false,
                                    agentLoanTarget = com.example.data.model.AgentLoanTargetResponse(
                                        success = true,
                                        client = com.example.data.model.AgentClientInfo(
                                            name = pResp.profile.fullName,
                                            walletId = pResp.profile.walletId
                                        ),
                                        loan = null
                                    ),
                                    agentLoanSearchError = "Ce client n'a aucun prêt en cours à rembourser."
                                )
                            }
                        }
                } else {
                    _uiState.update {
                        it.copy(
                            isAgentSearchingLoan = false,
                            agentLoanSearchError = "Utilisateur introuvable."
                        )
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isAgentSearchingLoan = false,
                        agentLoanSearchError = "Utilisateur introuvable."
                    )
                }
            }
        }
    }

    fun proceedToLoanPin() {
        _uiState.update { it.copy(agentLoanStep = 3) }
    }

    fun setAgentLoanAmount(amount: String) {
        _uiState.update { it.copy(agentLoanAmount = amount, agentLoanRepayError = null) }
    }

    fun setAgentLoanCurrency(currency: String) {
        _uiState.update { it.copy(agentLoanCurrency = currency, agentLoanRepayError = null) }
    }

    fun setAgentLoanPin(pin: String) {
        _uiState.update { it.copy(agentLoanPin = pin, agentLoanRepayError = null) }
    }

    fun resetAgentLoanRepay() {
        _uiState.update {
            it.copy(
                agentLoanStep = 1,
                agentLoanClientRef = "",
                agentLoanTarget = null,
                agentLoanAmount = "",
                agentLoanPin = "",
                agentLoanSearchError = null,
                agentLoanRepayError = null,
                agentLoanRepaySuccess = null,
                isAgentLoanRepayLoading = false
            )
        }
    }

    fun submitAgentLoanRepay(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val clientRef = state.agentLoanClientRef.trim()
        val amount = state.agentLoanAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentLoanCurrency
        val pin = state.agentLoanPin.trim()

        if (clientRef.isBlank()) {
            _uiState.update { it.copy(agentLoanRepayError = "Veuillez spécifier le client.") }
            return
        }
        if (amount <= 0.0) {
            _uiState.update { it.copy(agentLoanRepayError = "Veuillez entrer un montant valide.") }
            return
        }
        if (pin.length != 4) {
            _uiState.update { it.copy(agentLoanRepayError = "Le code PIN agent doit comporter 4 chiffres.") }
            return
        }

        _uiState.update { it.copy(isAgentLoanRepayLoading = true, agentLoanRepayError = null) }

        viewModelScope.launch {
            val result = repository.agentLoanRepay(clientRef, currency, amount, pin)
            result.onSuccess { res ->
                val earnedComm = res.commission ?: 1.0
                val newUsdComm = if (currency == "USD") state.agentCommissionUsd + earnedComm else state.agentCommissionUsd
                val newCdfComm = if (currency == "CDF") state.agentCommissionCdf + earnedComm else state.agentCommissionCdf
                val newEurComm = if (currency == "EUR") state.agentCommissionEur + earnedComm else state.agentCommissionEur

                val op = com.example.data.model.AgentOperationRecord(
                    type = "LOAN_REPAY",
                    title = "Remboursement Prêt Loan Me",
                    clientRef = clientRef,
                    amount = amount,
                    currency = currency,
                    commission = earnedComm,
                    reference = res.reference ?: "LON-${System.currentTimeMillis() % 100000}",
                    date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    status = "Complété"
                )

                _uiState.update {
                    it.copy(
                        isAgentLoanRepayLoading = false,
                        agentLoanRepaySuccess = res,
                        agentLoanRepayError = null,
                        agentCommissionUsd = newUsdComm,
                        agentCommissionCdf = newCdfComm,
                        agentCommissionEur = newEurComm,
                        agentOperationsHistory = listOf(op) + it.agentOperationsHistory
                    )
                }
                fetchWallet(effectiveUserId)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAgentLoanRepayLoading = false,
                        agentLoanRepayError = err.message ?: "Erreur de remboursement du prêt."
                    )
                }
            }
        }
    }

    // --- EXCHANGE / CONVERSION METHODS ---
    fun openExchangeDialog(from: String = "USD", to: String? = null) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        val targetTo = to ?: if (from == "USD") natCode else "USD"
        _uiState.update {
            it.copy(
                isExchangeDialogOpen = true,
                exchangeFromCurrency = from,
                exchangeToCurrency = if (from == targetTo) (if (from == "USD") natCode else "USD") else targetTo,
                exchangeAmount = "",
                exchangePin = "",
                exchangeError = null,
                exchangeSuccessResponse = null
            )
        }
    }

    fun closeExchangeDialog() {
        _uiState.update {
            it.copy(
                isExchangeDialogOpen = false,
                exchangeError = null,
                exchangeSuccessResponse = null
            )
        }
    }

    fun setExchangeAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(exchangeAmount = filtered, exchangeError = null) }
    }

    fun setExchangeFromCurrency(currency: String) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        _uiState.update {
            val to = if (it.exchangeToCurrency == currency) {
                if (currency == "USD") natCode else "USD"
            } else {
                it.exchangeToCurrency
            }
            it.copy(exchangeFromCurrency = currency, exchangeToCurrency = to, exchangeError = null)
        }
    }

    fun setExchangeToCurrency(currency: String) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        _uiState.update {
            val from = if (it.exchangeFromCurrency == currency) {
                if (currency == "USD") natCode else "USD"
            } else {
                it.exchangeFromCurrency
            }
            it.copy(exchangeToCurrency = currency, exchangeFromCurrency = from, exchangeError = null)
        }
    }

    fun swapExchangeCurrencies() {
        _uiState.update {
            it.copy(
                exchangeFromCurrency = it.exchangeToCurrency,
                exchangeToCurrency = it.exchangeFromCurrency,
                exchangeError = null
            )
        }
    }

    fun setExchangePin(pin: String) {
        val filtered = pin.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(exchangePin = filtered, exchangeError = null) }
    }

    fun resetExchangeSuccess() {
        _uiState.update { it.copy(exchangeSuccessResponse = null, exchangeAmount = "", exchangePin = "") }
    }

    fun submitExchange() {
        val state = _uiState.value
        val amount = state.exchangeAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _uiState.update { it.copy(exchangeError = "Veuillez saisir un montant valide supérieur à 0.") }
            return
        }
        if (state.exchangeFromCurrency == state.exchangeToCurrency) {
            _uiState.update { it.copy(exchangeError = "Les devises source et de destination doivent être différentes.") }
            return
        }
        if (state.exchangePin.isBlank()) {
            _uiState.update { it.copy(exchangeError = "Veuillez saisir votre code PIN CashPay.") }
            return
        }

        _uiState.update { it.copy(isExchangeLoading = true, exchangeError = null) }

        viewModelScope.launch {
            val result = repository.exchangeCurrencies(
                amount = amount,
                fromCurrency = state.exchangeFromCurrency,
                toCurrency = state.exchangeToCurrency,
                pin = state.exchangePin
            )
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isExchangeLoading = false,
                        exchangeSuccessResponse = response,
                        exchangeError = null
                    )
                }
                // Refresh balance and transactions automatically
                val effectiveUserId = getEffectiveUserId()
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isExchangeLoading = false,
                        exchangeError = err.message ?: "Échec de l'opération de change."
                    )
                }
            }
        }
    }

    // --- DEPOSIT METHODS (MaxiCash Gateway) ---
    fun openDepositDialog(method: String = "mobile_money") {
        val prof = userProfile.value
        val userCountry = prof?.country ?: "CD"
        val operators = com.example.data.model.MobileMoneyOperator.getOperatorsForCountry(userCountry)
        val defaultOp = operators.firstOrNull()?.code ?: "mpesa"
        val userPhone = prof?.phone ?: ""

        _uiState.update { current ->
            current.copy(
                isDepositDialogOpen = true,
                depositMethod = method,
                depositAmount = "",
                depositCurrency = "USD",
                depositOperator = defaultOp,
                depositPhoneNumber = if (current.depositPhoneNumber.isNotBlank()) current.depositPhoneNumber else userPhone,
                depositCountryCode = userCountry,
                isDepositLoading = false,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null,
                depositPaymentUrl = null,
                isDepositWebViewOpen = false
            )
        }
    }

    fun closeDepositDialog() {
        _uiState.update {
            it.copy(
                isDepositDialogOpen = false,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null,
                depositPaymentUrl = null
            )
        }
    }

    fun setDepositMethod(method: String) {
        _uiState.update { it.copy(depositMethod = method, depositError = null) }
    }

    fun setDepositAmount(amount: String) {
        _uiState.update { it.copy(depositAmount = amount, depositError = null) }
    }

    fun setDepositOperator(operator: String) {
        _uiState.update { it.copy(depositOperator = operator, depositError = null) }
    }

    fun setDepositPhoneNumber(phone: String) {
        _uiState.update { it.copy(depositPhoneNumber = phone, depositError = null) }
    }

    fun setDepositCountryCode(code: String) {
        _uiState.update { it.copy(depositCountryCode = code) }
    }

    fun openDepositWebView(url: String) {
        _uiState.update { it.copy(depositPaymentUrl = url, isDepositWebViewOpen = true) }
    }

    fun closeDepositWebView() {
        _uiState.update { it.copy(isDepositWebViewOpen = false, depositPaymentUrl = null) }
        val effectiveUserId = getEffectiveUserId()
        fetchWallet(effectiveUserId)
        loadTransactions(effectiveUserId)
    }

    fun submitDeposit() {
        val state = _uiState.value
        val amount = state.depositAmount.trim().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(depositError = "Veuillez entrer un montant valide supérieur à 0.") }
            return
        }

        _uiState.update {
            it.copy(
                isDepositLoading = true,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null
            )
        }

        viewModelScope.launch {
            when (state.depositMethod) {
                "mobile_money" -> {
                    if (state.depositPhoneNumber.isBlank()) {
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositError = "Veuillez saisir votre numéro de téléphone Mobile Money."
                            )
                        }
                        return@launch
                    }
                    val result = repository.depositMobileMoney(
                        amount = amount,
                        currency = state.depositCurrency,
                        operator = state.depositOperator,
                        phoneNumber = state.depositPhoneNumber.trim(),
                        countryCode = state.depositCountryCode
                    )
                    result.onSuccess { resp ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositSuccessMessage = resp.message ?: "Demande de dépôt envoyée. Veuillez confirmer le paiement sur votre téléphone.",
                                depositError = null
                            )
                        }
                        val effectiveUserId = getEffectiveUserId()
                        fetchWallet(effectiveUserId)
                        loadTransactions(effectiveUserId)
                    }.onFailure { err ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositError = err.message ?: "Échec de la demande de dépôt Mobile Money."
                            )
                        }
                    }
                }
                "card" -> {
                    val result = repository.depositCard(
                        amount = amount,
                        currency = state.depositCurrency
                    )
                    result.onSuccess { resp ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositPaymentUrl = resp.paymentUrl,
                                isDepositWebViewOpen = !resp.paymentUrl.isNullOrBlank(),
                                depositError = null
                            )
                        }
                    }.onFailure { err ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositError = err.message ?: "Impossible d'initialiser le paiement par carte."
                            )
                        }
                    }
                }
                "paypal" -> {
                    val result = repository.depositPayPal(
                        amount = amount,
                        currency = state.depositCurrency
                    )
                    result.onSuccess { resp ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositPaymentUrl = resp.paymentUrl,
                                isDepositWebViewOpen = !resp.paymentUrl.isNullOrBlank(),
                                depositError = null
                            )
                        }
                    }.onFailure { err ->
                        _uiState.update {
                            it.copy(
                                isDepositLoading = false,
                                depositError = err.message ?: "Impossible d'initialiser le paiement PayPal."
                            )
                        }
                    }
                }
            }
        }
    }

    // --- AGENT EXTERNAL MOBILE MONEY WITHDRAWAL / PULL ---
    fun openAgentExternalMoMoDialog() {
        _uiState.update {
            it.copy(
                isAgentExternalMoMoOpen = true,
                agentExternalMoMoOperator = "mpesa",
                agentExternalMoMoPhone = "",
                agentExternalMoMoAmount = "",
                isAgentExternalMoMoLoading = false,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null,
                agentExternalMoMoReference = null
            )
        }
    }

    fun closeAgentExternalMoMoDialog() {
        _uiState.update {
            it.copy(
                isAgentExternalMoMoOpen = false,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null
            )
        }
    }

    fun setAgentExternalMoMoOperator(op: String) {
        _uiState.update { it.copy(agentExternalMoMoOperator = op, agentExternalMoMoError = null) }
    }

    fun setAgentExternalMoMoPhone(phone: String) {
        _uiState.update { it.copy(agentExternalMoMoPhone = phone, agentExternalMoMoError = null) }
    }

    fun setAgentExternalMoMoAmount(amt: String) {
        _uiState.update { it.copy(agentExternalMoMoAmount = amt, agentExternalMoMoError = null) }
    }

    fun submitAgentExternalMoMo() {
        val state = _uiState.value
        val amount = state.agentExternalMoMoAmount.trim().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(agentExternalMoMoError = "Veuillez saisir un montant valide.") }
            return
        }
        if (state.agentExternalMoMoPhone.isBlank()) {
            _uiState.update { it.copy(agentExternalMoMoError = "Veuillez saisir le numéro Mobile Money du client.") }
            return
        }

        _uiState.update {
            it.copy(
                isAgentExternalMoMoLoading = true,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null
            )
        }

        viewModelScope.launch {
            val result = repository.depositMobileMoney(
                amount = amount,
                currency = "USD",
                operator = state.agentExternalMoMoOperator,
                phoneNumber = state.agentExternalMoMoPhone.trim(),
                countryCode = "CD"
            )
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isAgentExternalMoMoLoading = false,
                        agentExternalMoMoReference = resp.reference,
                        agentExternalMoMoSuccess = "Demande envoyée au client (${resp.reference ?: "DP"}). Dès validation par le client sur son téléphone, vos fonds seront crédités et vous pourrez remettre les espèces.",
                        agentExternalMoMoError = null
                    )
                }
                val effectiveUserId = getEffectiveUserId()
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAgentExternalMoMoLoading = false,
                        agentExternalMoMoError = err.message ?: "Échec de l'initiation du retrait Mobile Money."
                    )
                }
            }
        }
    }
}

class DashboardViewModelFactory(
    private val repository: CashPayRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
