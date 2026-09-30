package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CountryDto
import com.example.data.model.ProfileInstallationDto
import com.example.data.model.UserProfileDto
import com.example.data.repository.CashPayRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMethod {
    PHONE, WALLET_ID
}

enum class AuthStep {
    IDENTIFICATION,
    REGISTER_FORM,
    OTP_CHANNEL_SELECT,
    OTP_VERIFICATION,
    PROFILE_INSTALLATION,
    PIN_ENTRY,
    COMPLETED
}

data class AuthUiState(
    val authMethod: AuthMethod = AuthMethod.PHONE,
    val selectedCountry: CountryDto = CountryDto("CD", "République démocratique du Congo", "+243"),
    val localPhone: String = "800001234",
    val walletId: String = "",
    val countries: List<CountryDto> = emptyList(),
    val isCountryPickerOpen: Boolean = false,
    val countrySearchQuery: String = "",
    val authStep: AuthStep = AuthStep.IDENTIFICATION,
    val userId: Long = 1,
    val channels: List<String> = listOf("sms", "whatsapp", "email"),
    val selectedChannel: String = "sms",
    val rawPhone: String = "",
    val rawEmail: String = "",
    val serverMaskedPhone: String? = null,
    val serverMaskedEmail: String? = null,
    val otpCode: String = "",
    val pinCode: String = "",
    val isOtpLoading: Boolean = false,
    val isPinLoading: Boolean = false,
    val isRegisterLoading: Boolean = false,
    val isInstallingProfile: Boolean = false,

    val installProgressPercent: Int = 0,
    val installStageText: String = "Initialisation du système...",
    val profileInstallation: ProfileInstallationDto? = null,
    val installedUserProfile: UserProfileDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // --- FORGOT PIN STATE ---
    val isForgotPinOpen: Boolean = false,
    val forgotPinStep: Int = 1,
    val forgotPinPhone: String = "",
    val forgotPinChannel: String = "sms",
    val forgotPinUserId: Long? = null,
    val forgotPinOtp: String = "",
    val forgotPinNewPin: String = "",
    val forgotPinConfirmPin: String = "",
    val isForgotPinLoading: Boolean = false,
    val forgotPinError: String? = null,
    val forgotPinSuccess: String? = null
) {
    val fullPhone: String
        get() = "${selectedCountry.dialCode}$localPhone"

    val isPhoneValid: Boolean
        get() = localPhone.length == 9

    val isWalletIdValid: Boolean
        get() = walletId.isNotBlank() && walletId.length >= 6

    val isIdentificationReady: Boolean
        get() = if (authMethod == AuthMethod.PHONE) isPhoneValid else isWalletIdValid

    val maskedPhone: String
        get() {
            if (!serverMaskedPhone.isNullOrBlank()) {
                val dial = selectedCountry.dialCode
                return "$dial $serverMaskedPhone"
            }
            val phoneToMask = if (rawPhone.isNotBlank()) rawPhone else fullPhone
            val digits = phoneToMask.filter { it.isDigit() }
            return if (digits.length >= 9) {
                val dial = if (phoneToMask.startsWith("+")) phoneToMask.substring(0, 4) else "+243"
                val first2 = digits.drop(3).take(2)
                val last2 = digits.takeLast(2)
                "$dial $first2 ••• ••$last2"
            } else {
                phoneToMask
            }
        }

    val maskedEmail: String
        get() {
            if (!serverMaskedEmail.isNullOrBlank()) return serverMaskedEmail
            val parts = rawEmail.split("@")
            return if (parts.size == 2) {
                val name = parts[0]
                val domain = parts[1]
                val visible = name.take(2)
                "$visible••••••@$domain"
            } else {
                rawEmail
            }
        }
}

class AuthViewModel(
    private val repository: CashPayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentSession: StateFlow<SessionEntity?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUserProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadCountries()
        viewModelScope.launch {
            repository.countries.collect { list ->
                if (list.isNotEmpty()) {
                    _uiState.update { it.copy(countries = list) }
                }
            }
        }
        viewModelScope.launch {
            repository.session.collect { session ->
                if (session != null && !session.sessionToken.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = session.sessionToken
                    _uiState.update {
                        it.copy(
                            userId = session.userId,
                            walletId = session.walletId ?: it.walletId,
                            rawPhone = session.phone ?: it.rawPhone,
                            authStep = AuthStep.COMPLETED
                        )
                    }
                }
            }
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            repository.loadCountries()
        }
    }

    fun setAuthMethod(method: AuthMethod) {
        _uiState.update { it.copy(authMethod = method, errorMessage = null) }
    }

    fun setCountryPickerOpen(open: Boolean) {
        _uiState.update { it.copy(isCountryPickerOpen = open, countrySearchQuery = "") }
    }

    fun setCountrySearchQuery(query: String) {
        _uiState.update { it.copy(countrySearchQuery = query) }
    }

    fun selectCountry(country: CountryDto) {
        _uiState.update {
            it.copy(
                selectedCountry = country,
                isCountryPickerOpen = false,
                countrySearchQuery = "",
                errorMessage = null
            )
        }
    }

    fun onLocalPhoneChanged(input: String) {
        val filtered = input.filter { it.isDigit() }.take(9)
        _uiState.update { it.copy(localPhone = filtered, errorMessage = null) }
    }

    fun onWalletIdChanged(input: String) {
        val uppercase = input.uppercase().filter { it.isLetterOrDigit() }.take(16)
        _uiState.update { it.copy(walletId = uppercase, errorMessage = null) }
    }

    fun submitIdentification() {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val phone = if (state.authMethod == AuthMethod.PHONE) state.fullPhone else null
            val wallet = if (state.authMethod == AuthMethod.WALLET_ID) state.walletId else null

            val result = repository.login(walletId = wallet, phone = phone)
            result.onSuccess { response ->
                val resolvedPhone = response.phone ?: state.fullPhone
                val channels = response.effectiveChannels
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        userId = response.userId ?: 1,
                        rawPhone = resolvedPhone,
                        serverMaskedPhone = response.maskedPhone,
                        serverMaskedEmail = response.maskedEmail,
                        rawEmail = response.email ?: (response.maskedEmail ?: it.rawEmail),
                        walletId = response.walletId ?: it.walletId,
                        channels = channels,
                        selectedChannel = channels.firstOrNull() ?: "sms",
                        authStep = AuthStep.OTP_CHANNEL_SELECT,
                        successMessage = response.message
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        errorMessage = err.message ?: "Erreur d'identification"
                    )
                }
            }
        }
    }

    fun selectOtpChannel(channel: String) {
        _uiState.update { it.copy(selectedChannel = channel, errorMessage = null) }
    }

    fun requestOtpSend() {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.sendOtp(state.userId, state.selectedChannel)
            result.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        authStep = AuthStep.OTP_VERIFICATION,
                        otpCode = "",
                        errorMessage = null,
                        successMessage = resp.message
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        errorMessage = err.message ?: "Impossible d'envoyer l'OTP"
                    )
                }
            }
        }
    }

    fun submitOtpVerification() {
        val state = _uiState.value
        if (state.otpCode.length == 6) {
            verifyOtpAutomatically(state.otpCode)
        } else {
            _uiState.update { it.copy(errorMessage = "Veuillez saisir le code à 6 chiffres.") }
        }
    }

    fun onOtpChanged(newOtp: String) {
        val filtered = newOtp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = filtered, errorMessage = null) }

        if (filtered.length == 6) {
            verifyOtpAutomatically(filtered)
        }
    }

    private fun verifyOtpAutomatically(otp: String) {
        val state = _uiState.value
        _uiState.update { it.copy(isOtpLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.verifyOtp(state.userId, otp)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        authStep = AuthStep.PROFILE_INSTALLATION,
                        errorMessage = null
                    )
                }
                startSystemProfileInstallation()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isOtpLoading = false,
                        otpCode = "",
                        errorMessage = err.message ?: "Code OTP incorrect."
                    )
                }
            }
        }
    }

    private fun startSystemProfileInstallation() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                authStep = AuthStep.PROFILE_INSTALLATION,
                isInstallingProfile = true,
                installProgressPercent = 1,
                installStageText = "Connexion au serveur sécurisé CashPay..."
            )
        }

        viewModelScope.launch {
            val phoneToQuery = if (state.rawPhone.isNotBlank()) state.rawPhone else state.fullPhone

            // Asynchronous OS-like gradual installation progression: 1% -> 100%
            for (p in 2..30) {
                delay(30)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Noyau CashPay • Résolution du compte ${state.maskedPhone}..."
                    )
                }
            }

            // Real API Call GET /api/v1/auth/me?phone=...
            val result = repository.installProfile(phoneToQuery)

            for (p in 31..65) {
                delay(25)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Chiffrement AES-256 • Synchronisation des certificats de paiement..."
                    )
                }
            }

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        profileInstallation = response.profileInstallation,
                        installedUserProfile = response.profile
                    )
                }
            }

            for (p in 66..99) {
                delay(20)
                _uiState.update {
                    it.copy(
                        installProgressPercent = p,
                        installStageText = "Finalisation de l'environnement • Module USSD & Profil Astrologique..."
                    )
                }
            }

            delay(100)
            _uiState.update {
                it.copy(
                    installProgressPercent = 100,
                    installStageText = "Profil installé • Confirmation par code PIN requise.",
                    isInstallingProfile = false
                )
            }

            delay(400)
            // Move to PIN Confirmation step
            _uiState.update {
                it.copy(authStep = AuthStep.PIN_ENTRY)
            }
        }
    }

    fun onPinDigitEntered(digit: String) {
        val current = _uiState.value.pinCode
        if (current.length < 4) {
            val updated = current + digit
            _uiState.update { it.copy(pinCode = updated, errorMessage = null) }

            if (updated.length == 4) {
                verifyPinAutomatically(updated)
            }
        }
    }

    fun onPinBackspace() {
        val current = _uiState.value.pinCode
        if (current.isNotEmpty()) {
            _uiState.update { it.copy(pinCode = current.dropLast(1), errorMessage = null) }
        }
    }

    private fun verifyPinAutomatically(pin: String) {
        val state = _uiState.value
        _uiState.update { it.copy(isPinLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.verifyPin(state.userId, pin)
            result.onSuccess { resp ->
                if (resp.authenticated || resp.success) {
                    _uiState.update {
                        it.copy(
                            isPinLoading = false,
                            authStep = AuthStep.COMPLETED,
                            pinCode = "",
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isPinLoading = false,
                            pinCode = "",
                            errorMessage = resp.error ?: "Code PIN incorrect."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isPinLoading = false,
                        pinCode = "",
                        errorMessage = err.message ?: "Code PIN incorrect."
                    )
                }
            }
        }
    }

    fun onBiometricClicked() {
        _uiState.update {
            it.copy(
                errorMessage = "Veuillez saisir votre code PIN CashPay pour synchroniser la sécurité biométrique."
            )
        }
    }

    fun goToRegister() {
        _uiState.update { it.copy(authStep = AuthStep.REGISTER_FORM, errorMessage = null) }
    }

    fun navigateBack() {
        val current = _uiState.value.authStep
        val previous = when (current) {
            AuthStep.IDENTIFICATION -> AuthStep.IDENTIFICATION
            AuthStep.REGISTER_FORM -> AuthStep.IDENTIFICATION
            AuthStep.OTP_CHANNEL_SELECT -> AuthStep.IDENTIFICATION
            AuthStep.OTP_VERIFICATION -> AuthStep.OTP_CHANNEL_SELECT
            AuthStep.PROFILE_INSTALLATION -> AuthStep.OTP_VERIFICATION
            AuthStep.PIN_ENTRY -> AuthStep.OTP_VERIFICATION
            AuthStep.COMPLETED -> AuthStep.COMPLETED
        }
        _uiState.update {
            it.copy(
                authStep = previous,
                errorMessage = null,
                pinCode = "",
                otpCode = ""
            )
        }
    }

    fun onRawEmailChanged(email: String) {
        _uiState.update { it.copy(rawEmail = email) }
    }

    fun resetToLogin() {
        _uiState.update {
            AuthUiState(
                countries = it.countries,
                selectedCountry = it.selectedCountry
            )
        }
    }

    fun submitRegistration(
        accountType: String,
        firstName: String,
        middleName: String,
        lastName: String,
        gender: String,
        birthDate: String,
        birthPlace: String,
        nationality: String,
        province: String,
        city: String,
        address: String,
        profession: String,
        activityDescription: String,
        incomePerMonth: String,
        idType: String,
        idNumber: String,
        companyName: String,
        idFrontBase64: String?,
        idBackBase64: String?,
        profilePhotoBase64: String?,
        signatureBase64: String?
    ) {
        val state = _uiState.value
        _uiState.update { it.copy(isRegisterLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val req = com.example.data.model.RegisterRequest(
                action = "submit_registration",
                phone = state.fullPhone,
                countryCode = state.selectedCountry.code,
                accountType = accountType,
                email = state.rawEmail.ifBlank { null },
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                birthDate = birthDate,
                birthPlace = birthPlace,
                gender = gender,
                nationality = nationality,
                country = state.selectedCountry.name,
                province = province,
                city = city,
                address = address,
                profession = profession,
                activityDescription = activityDescription,
                incomePerMonth = incomePerMonth.toDoubleOrNull(),
                idType = idType,
                idNumber = idNumber,
                idFrontImage = idFrontBase64,
                idBackImage = idBackBase64,
                profilePhoto = profilePhotoBase64,
                signatureImage = signatureBase64,
                companyName = companyName.ifBlank { null }
            )

            val res = repository.register(req)
            res.onSuccess { resp ->
                _uiState.update {
                    it.copy(
                        isRegisterLoading = false,
                        authStep = AuthStep.OTP_CHANNEL_SELECT,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isRegisterLoading = false,
                        errorMessage = err.message ?: "Erreur d'inscription"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update {
                AuthUiState(
                    countries = it.countries,
                    selectedCountry = it.selectedCountry
                )
            }
        }
    }

    // --- FORGOT PIN METHODS ---
    fun openForgotPin() {
        val initialPhone = _uiState.value.fullPhone
        _uiState.update {
            it.copy(
                isForgotPinOpen = true,
                forgotPinStep = 1,
                forgotPinPhone = initialPhone,
                forgotPinChannel = "sms",
                forgotPinUserId = null,
                forgotPinOtp = "",
                forgotPinNewPin = "",
                forgotPinConfirmPin = "",
                isForgotPinLoading = false,
                forgotPinError = null,
                forgotPinSuccess = null
            )
        }
    }

    fun closeForgotPin() {
        _uiState.update { it.copy(isForgotPinOpen = false) }
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
                            forgotPinSuccess = res.message ?: "Votre PIN a été réinitialisé avec succès.",
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
}

class AuthViewModelFactory(
    private val repository: CashPayRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
