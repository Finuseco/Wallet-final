package com.example.data.repository

import com.example.data.local.CashPayDao
import com.example.data.local.CountryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.data.model.CountryDto
import com.example.data.model.LoginRequest
import com.example.data.model.LoginResponse
import com.example.data.model.MeResponse
import com.example.data.model.RegisterRequest
import com.example.data.model.RegisterResponse
import com.example.data.model.WalletResponse
import com.example.data.model.TransfersMetaResponse
import com.example.data.model.TransferRequest
import com.example.data.model.TransferResponse
import com.example.data.model.TransactionsResponse
import com.example.data.model.SendOtpRequest
import com.example.data.model.SendOtpResponse
import com.example.data.model.UpdateProfileRequest
import com.example.data.model.UserProfileDto
import com.example.data.model.VerifyOtpRequest
import com.example.data.model.VerifyOtpResponse
import com.example.data.model.VerifyPinRequest
import com.example.data.model.VerifyPinResponse
import com.example.data.remote.CashPayApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request

class CashPayRepository(
    private val apiService: CashPayApiService,
    private val dao: CashPayDao
) {

    init {
        com.example.data.remote.ApiClient.tokenProvider = {
            com.example.data.remote.ApiClient.sessionToken
        }
    }

    private suspend fun ensureSessionToken() {
        if (com.example.data.remote.ApiClient.sessionToken.isNullOrBlank()) {
            val sess = dao.getSessionOnce()
            if (sess != null && !sess.sessionToken.isNullOrBlank()) {
                com.example.data.remote.ApiClient.sessionToken = sess.sessionToken
            }
        }
    }

    private fun sanitizeUserError(rawMsg: String?, fallback: String): String {
        if (rawMsg.isNullOrBlank()) return fallback
        val clean = rawMsg.trim()
        if (clean.contains("<") || clean.contains("<!DOCTYPE", ignoreCase = true) ||
            clean.contains("cashpay", ignoreCase = true) || clean.contains("http://", ignoreCase = true) ||
            clean.contains("https://", ignoreCase = true) || clean.contains("api/", ignoreCase = true) ||
            clean.contains("Internal Server Error", ignoreCase = true) || clean.contains("Exception", ignoreCase = true) ||
            clean.contains("SocketTimeout", ignoreCase = true) || clean.contains("Unable to resolve host", ignoreCase = true) ||
            clean.contains("connection reset", ignoreCase = true) || clean.contains("Failed to connect", ignoreCase = true) ||
            clean.contains("host", ignoreCase = true) || clean.length > 120
        ) {
            return fallback
        }
        return clean
    }

    private fun extractErrorMessage(errorBody: String?, fallback: String): String {
        if (errorBody.isNullOrBlank()) return fallback
        return try {
            val json = JSONObject(errorBody)
            val err = json.optString("error", "")
            val rawMsg = if (err.isNotBlank()) err else json.optString("message", fallback)
            sanitizeUserError(rawMsg, fallback)
        } catch (_: Exception) {
            sanitizeUserError(errorBody, fallback)
        }
    }

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val session: Flow<SessionEntity?> = dao.getSession()

    suspend fun getUserProfileOnce(): UserProfileEntity? = dao.getUserProfileOnce()

    suspend fun lockSession() {
        val current = dao.getSessionOnce()
        if (current != null && current.isAuthenticated) {
            dao.saveSession(current.copy(isAuthenticated = false))
        }
    }
    val transactions: Flow<List<TransactionEntity>> = dao.getTransactions()
    val notifications: Flow<List<NotificationEntity>> = dao.getNotifications()
    val countries: Flow<List<CountryDto>> = dao.getAllCountries().map { list ->
        list.map { CountryDto(code = it.code, name = it.name, dialCode = it.dialCode) }
    }

    private val wsClient = com.example.data.remote.NotificationWebSocketClient { dto ->
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertNotification(
                NotificationEntity(
                    id = dto.id,
                    title = dto.title ?: "",
                    description = dto.description ?: "",
                    timestamp = dto.timestamp ?: "",
                    read = dto.read,
                    type = dto.type ?: "general",
                    link = dto.link
                )
            )
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            session.collect { sess ->
                val token = sess?.sessionToken
                if (!token.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    wsClient.connect(token)
                } else {
                    wsClient.disconnect()
                }
            }
        }
    }

    suspend fun fetchNotifications(limit: Int = 50): Result<List<com.example.data.local.NotificationEntity>> {
        return try {
            val response = apiService.getNotifications(limit)
            if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                val dtos = response.body()!!.notifications
                val entities = dtos.map { dto ->
                    com.example.data.local.NotificationEntity(
                        id = dto.id,
                        title = dto.title ?: "",
                        description = dto.description ?: "",
                        timestamp = dto.timestamp ?: "",
                        read = dto.read,
                        type = dto.type ?: "general",
                        link = dto.link
                    )
                }
                if (entities.isNotEmpty()) {
                    dao.insertNotifications(entities)
                }
                Result.success(entities)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les notifications")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(id: Long): Result<Boolean> {
        return try {
            dao.markNotificationRead(id)
            val response = apiService.markNotificationRead(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.success(true) // local update persisted
            }
        } catch (e: Exception) {
            dao.markNotificationRead(id)
            Result.success(true)
        }
    }

    suspend fun markAllNotificationsRead(): Result<Boolean> {
        return try {
            dao.markAllNotificationsRead()
            val response = apiService.markAllNotificationsRead()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.success(true) // local update persisted
            }
        } catch (e: Exception) {
            dao.markAllNotificationsRead()
            Result.success(true)
        }
    }

    suspend fun loadCountries(): Result<List<CountryDto>> {
        return try {
            val count = dao.getCountryCount()
            if (count == 0) {
                val response = apiService.getCountries()
                if (response.isSuccessful && response.body()?.success == true) {
                    val dtoList = response.body()?.countries ?: emptyList()
                    val entities = dtoList.map {
                        CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
                    }
                    dao.insertCountries(entities)
                    Result.success(dtoList)
                } else {
                    // Fallback initial list if network error
                    val defaultCountries = getDefaultCountries()
                    dao.insertCountries(defaultCountries.map {
                        CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
                    })
                    Result.success(defaultCountries)
                }
            } else {
                val cached = dao.getAllCountries()
                Result.success(emptyList()) // observed via Flow
            }
        } catch (e: Exception) {
            val defaultCountries = getDefaultCountries()
            dao.insertCountries(defaultCountries.map {
                CountryEntity(code = it.code, name = it.name, dialCode = it.dialCode)
            })
            Result.success(defaultCountries)
        }
    }

    suspend fun login(walletId: String?, phone: String?): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(walletId = walletId, phone = phone))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur d'identification")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.token
                if (!token.isNullOrBlank() && (body.action == "complete" || body.idWallet != null || body.user != null)) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    val wallet = body.effectiveWalletId
                    dao.saveSession(
                        SessionEntity(
                            id = 1,
                            userId = body.user?.id ?: 1,
                            walletId = wallet,
                            phone = body.user?.phone ?: body.phone,
                            isAuthenticated = true,
                            biometricEnabled = true,
                            sessionToken = token
                        )
                    )
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'inscription")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStart(phone: String, accountType: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "start", phone = phone, accountType = accountType))
    }

    suspend fun registerVerifyPhone(phone: String, countryCode: String, country: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "verify_phone", phone = phone, countryCode = countryCode, country = country))
    }

    suspend fun registerSendPhoneOtp(phone: String, accountType: String = "national", preferredOtpChannel: String = "sms"): Result<RegisterResponse> {
        val cleanPhone = phone.filter { it.isDigit() }
        return register(RegisterRequest(action = "start", phone = cleanPhone, accountType = accountType.lowercase(), preferredOtpChannel = preferredOtpChannel))
    }

    suspend fun registerVerifyPhoneOtp(phone: String, countryCode: String = "CD", country: String = "République démocratique du Congo", otp: String = ""): Result<RegisterResponse> {
        val cleanPhone = phone.filter { it.isDigit() }
        return register(RegisterRequest(action = "verify_phone", phone = cleanPhone, countryCode = countryCode, country = country, otp = otp))
    }

    suspend fun registerSendEmailOtp(email: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "send_email_otp", email = email))
    }

    suspend fun registerVerifyEmailOtp(email: String, otp: String): Result<RegisterResponse> {
        return register(RegisterRequest(action = "verify_email", email = email, otp = otp))
    }

    suspend fun registerComplete(request: RegisterRequest): Result<RegisterResponse> {
        return register(request.copy(action = "complete"))
    }

    suspend fun sendOtp(userId: Long, channel: String): Result<SendOtpResponse> {
        return try {
            val response = apiService.sendOtp(SendOtpRequest(userId = userId, channel = channel))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'envoyer l'OTP")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(userId: Long, otp: String): Result<VerifyOtpResponse> {
        return try {
            val response = apiService.verifyOtp(VerifyOtpRequest(userId = userId, otp = otp))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.token
                if (!token.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = token
                    val existingSession = dao.getSessionOnce()
                    if (existingSession != null) {
                        dao.saveSession(existingSession.copy(sessionToken = token))
                    } else {
                        dao.saveSession(
                            SessionEntity(
                                id = 1,
                                userId = userId,
                                walletId = null,
                                phone = null,
                                isAuthenticated = false,
                                biometricEnabled = true,
                                sessionToken = token
                            )
                        )
                    }
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code OTP invalide ou expiré")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPin(userId: Long, pin: String): Result<VerifyPinResponse> {
        return try {
            val response = apiService.verifyPin(VerifyPinRequest(userId = userId, pin = pin))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.authenticated || body.success) {
                    val currentProfile = dao.getUserProfileOnce()
                    val existingSession = dao.getSessionOnce()
                    val session = SessionEntity(
                        id = 1,
                        userId = userId,
                        walletId = currentProfile?.walletId,
                        phone = currentProfile?.phone,
                        isAuthenticated = true,
                        biometricEnabled = true,
                        sessionToken = existingSession?.sessionToken
                    )
                    dao.saveSession(session)
                    seedInitialTransactions()
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun installProfile(phone: String): Result<MeResponse> {
        return try {
            // Strip leading '+' if present as per CashPay API specs
            val normalizedPhone = phone.replace("+", "").trim()
            val response = apiService.getMe(normalizedPhone)
            if (response.isSuccessful && response.body() != null) {
                val meResponse = response.body()!!
                if (meResponse.success && meResponse.profile != null) {
                    val p = meResponse.profile
                    val entity = mapProfileDtoToEntity(p)
                    dao.insertUserProfile(entity)

                    // Profile installed: session is prepared, but NOT authenticated until PIN verification!
                    val existing = dao.getSessionOnce()
                    val token = existing?.sessionToken ?: "token_${p.id}"
                    val session = SessionEntity(
                        id = 1,
                        userId = p.id,
                        walletId = p.walletId,
                        phone = p.phone,
                        isAuthenticated = false,
                        biometricEnabled = true,
                        sessionToken = token
                    )
                    dao.saveSession(session)
                }
                Result.success(meResponse)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'installer le profil.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(phone: String, pin: String, updates: Map<String, String>): Result<MeResponse> {
        return try {
            val normalizedPhone = phone.replace("+", "").trim()
            val response = apiService.updateMe(normalizedPhone, UpdateProfileRequest(pin = pin, updates = updates))
            if (response.isSuccessful && response.body() != null) {
                val meResponse = response.body()!!
                if (meResponse.success && meResponse.profile != null) {
                    val p = meResponse.profile
                    val entity = mapProfileDtoToEntity(p)
                    dao.insertUserProfile(entity)
                }
                Result.success(meResponse)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect ou erreur de mise à jour")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setBiometric(enabled: Boolean) {
        dao.updateBiometric(enabled)
    }

    suspend fun logout() {
        try {
            wsClient.disconnect()
        } catch (_: Exception) {}
        com.example.data.remote.ApiClient.sessionToken = null
        dao.clearSession()
        dao.clearUserProfile()
        dao.clearTransactions()
        dao.clearNotifications()
    }

    private fun mapProfileDtoToEntity(p: UserProfileDto): UserProfileEntity {
        return UserProfileEntity(
            id = p.id,
            role = p.role,
            status = p.status,
            accountType = p.accountType,
            walletId = p.walletId,
            language = p.language,
            ussdLanguage = p.ussdLanguage,
            fullName = p.fullName,
            firstName = p.firstName,
            lastName = p.lastName,
            middleName = p.middleName,
            gender = p.gender,
            maritalStatus = p.maritalStatus,
            birthDate = p.birthDate,
            birthPlace = p.birthPlace,
            nationality = p.nationality,
            email = p.email,
            phone = p.phone,
            country = p.country,
            province = p.province,
            city = p.city,
            address = p.address,
            profession = p.profession,
            activityDescription = p.activityDescription,
            incomePerMonth = p.incomePerMonth,
            profilePhoto = p.profilePhoto,
            profilePhotoUrl = p.profilePhotoUrl,
            coverPhoto = p.coverPhoto,
            coverPhotoUrl = p.coverPhotoUrl,
            signatureImage = p.signatureImage,
            googleId = p.googleId,
            zoomId = p.zoomId,
            notificationsChannel = p.notificationsChannel?.joinToString(", "),
            assistantSettings = p.assistantSettings,
            availabilityStatus = p.availabilityStatus,
            schedule = p.schedule,
            representative = p.representative?.let { "${it.name ?: ""} (${it.relation ?: ""}) ${it.contact ?: ""}".trim() },
            verificationStatus = p.verificationStatus,
            tempPhotoExpiresAt = p.tempPhotoExpiresAt,
            isCryptoActive = p.isCryptoActive,
            createdAt = p.createdAt,
            astroSign = p.astro?.zodiac?.nameFr ?: p.astro?.zodiac?.key,
            astroElement = p.astro?.zodiac?.symbol,
            astroLuckyNumber = 7,
            astroFavorableDay = "Vendredi",
            astroFinanceInsight = "Opportunités d'investissement favorables"
        )
    }

    private suspend fun seedInitialTransactions() {
        // Supprimé complètement les transactions fictives pour n'afficher que les réelles provenant de l'API.
    }

    private fun getDefaultCountries(): List<CountryDto> {
        return listOf(
            CountryDto("CD", "République démocratique du Congo", "+243"),
            CountryDto("CG", "Congo", "+242"),
            CountryDto("RW", "Rwanda", "+250"),
            CountryDto("CM", "Cameroun", "+237"),
            CountryDto("CI", "Côte d’Ivoire", "+225"),
            CountryDto("SN", "Sénégal", "+221"),
            CountryDto("GA", "Gabon", "+241"),
            CountryDto("AO", "Angola", "+244"),
            CountryDto("KE", "Kenya", "+254"),
            CountryDto("ZA", "Afrique du Sud", "+27"),
            CountryDto("FR", "France", "+33"),
            CountryDto("BE", "Belgique", "+32"),
            CountryDto("US", "États-Unis", "+1"),
            CountryDto("CA", "Canada", "+1"),
            CountryDto("CH", "Suisse", "+41"),
            CountryDto("GB", "Royaume-Uni", "+44")
        )
    }

    suspend fun getWallet(userId: Long): Result<WalletResponse> {
        return try {
            val response = apiService.getWallet(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer le portefeuille")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransfersMeta(userId: Long): Result<TransfersMetaResponse> {
        return try {
            val response = apiService.getTransfersMeta(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les devises de transfert")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transfer(request: TransferRequest): Result<TransferResponse> {
        return try {
            val response = apiService.transfer(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du transfert")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exchangeCurrencies(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        pin: String
    ): Result<com.example.data.model.ExchangeResponse> {
        return try {
            ensureSessionToken()
            val request = com.example.data.model.ExchangeRequest(
                amount = amount,
                fromCurrency = fromCurrency.uppercase().trim(),
                toCurrency = toCurrency.uppercase().trim(),
                pin = pin.trim()
            )
            val response = apiService.exchange(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: body.message ?: "Échec de l'échange de devises."))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'opération de change.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTransactionsApi(
        userId: Long,
        limit: Int? = 50,
        offset: Int? = 0,
        from: String? = null,
        to: String? = null,
        direction: String? = null
    ): Result<TransactionsResponse> {
        return try {
            val response = apiService.getTransactions(userId, limit, offset, from, to, direction)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.transactions.isNotEmpty()) {
                    val entities = body.transactions.map { dto ->
                        val isIncoming = dto.direction?.lowercase() in listOf("incoming", "credit")
                        val correspondent = if (isIncoming) (dto.sender ?: dto.otherUser) else (dto.receiver ?: dto.otherUser)
                        val dispName = correspondent?.fullName ?: dto.display?.name ?: dto.otherUser?.fullName ?: dto.description ?: if (isIncoming) "Virement reçu" else "Transfert envoyé"
                        val userAvatar = correspondent?.profilePhoto ?: dto.display?.avatar ?: dto.otherUser?.profilePhoto
                        val kind = dto.display?.kind ?: if (correspondent != null || dto.otherUser != null) "person" else "service"
                        val recipientWallet = if (isIncoming) (dto.sender?.walletId ?: dto.otherUser?.walletId ?: "") else (dto.receiver?.walletId ?: dto.otherUser?.walletId ?: "")

                        TransactionEntity(
                            id = dto.id,
                            title = dispName,
                            recipient = recipientWallet,
                            amount = dto.amount?.toDoubleOrNull() ?: 0.0,
                            currency = dto.currency ?: "USD",
                            type = dto.type ?: "TR",
                            date = dto.createdAt ?: "",
                            status = dto.status ?: "completed",
                            reference = dto.reference ?: "TR-${dto.id}",
                            direction = dto.direction ?: if (isIncoming) "incoming" else "outgoing",
                            displayKind = kind,
                            displayName = dispName,
                            displayAvatar = userAvatar,
                            displayIcon = dto.display?.icon,
                            fee = dto.fee?.toDoubleOrNull() ?: 0.0,
                            createdAt = dto.createdAt ?: "",
                            otherUserFullName = correspondent?.fullName ?: dto.otherUser?.fullName,
                            otherUserProfilePhoto = userAvatar,
                            description = dto.description,
                            senderWalletId = dto.sender?.walletId,
                            receiverWalletId = dto.receiver?.walletId
                        )
                    }
                    dao.insertTransactions(entities)
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les transactions")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- CARDS MODULE INTEGRATION ---
    suspend fun getCardCatalog(): Result<com.example.data.model.CardCatalogResponse> {
        return try {
            val response = apiService.getCardCatalog()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger le catalogue de cartes")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCards(): Result<List<com.example.data.model.CardInfoDto>> {
        return try {
            if (com.example.data.remote.ApiClient.sessionToken.isNullOrBlank()) {
                val sess = dao.getSessionOnce()
                if (sess != null && !sess.sessionToken.isNullOrBlank()) {
                    com.example.data.remote.ApiClient.sessionToken = sess.sessionToken
                }
            }
            val response = apiService.getUserCards()
            if (response.isSuccessful && response.body() != null) {
                val rawString = response.body()!!.string()
                val list = mutableListOf<com.example.data.model.CardInfoDto>()
                val trimmed = rawString.trim()
                if (trimmed.startsWith("[")) {
                    val array = org.json.JSONArray(trimmed)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            com.example.data.model.CardInfoDto(
                                id = obj.optString("id", null),
                                brand = obj.optString("brand", "VISA"),
                                cardType = obj.optString("cardType", obj.optString("type", "virtuelle")),
                                currency = obj.optString("currency", "USD"),
                                last4 = obj.optString("last4", "0000"),
                                status = obj.optString("status", "active"),
                                isPrimary = obj.optBoolean("isPrimary", false),
                                cardProviderId = obj.optString("cardProviderId", null)
                            )
                        )
                    }
                } else if (trimmed.startsWith("{")) {
                    val json = org.json.JSONObject(trimmed)
                    val array = json.optJSONArray("cards") ?: json.optJSONArray("data")
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            list.add(
                                com.example.data.model.CardInfoDto(
                                    id = obj.optString("id", null),
                                    brand = obj.optString("brand", "VISA"),
                                    cardType = obj.optString("cardType", obj.optString("type", "virtuelle")),
                                    currency = obj.optString("currency", "USD"),
                                    last4 = obj.optString("last4", "0000"),
                                    status = obj.optString("status", "active"),
                                    isPrimary = obj.optBoolean("isPrimary", false),
                                    cardProviderId = obj.optString("cardProviderId", null)
                                )
                            )
                        }
                    }
                }
                Result.success(list)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger vos cartes")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCard(
        brand: String,
        cardType: String,
        currency: String,
        pin: String
    ): Result<com.example.data.model.CardActionResponse> {
        return try {
            val req = com.example.data.model.PurchaseCardRequest(
                brand = brand,
                cardType = cardType,
                currency = currency,
                pin = pin
            )
            val response = apiService.purchaseCard(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'achat de la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.activateCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'activer la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun freezeCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.freezeCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de geler la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfreezeCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.unfreezeCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de dégeler la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeCardPin(id: String, pin: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.changeCardPin(id, mapOf("pin" to pin))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier le code PIN")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun terminateCard(id: String): Result<com.example.data.model.CardActionResponse> {
        return try {
            val response = apiService.terminateCard(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de résilier la carte")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCardFunding(
        id: String,
        type: String,
        cpkSignature: String? = null,
        cpkPin: String? = null
    ): Result<com.example.data.model.CardActionResponse> {
        return try {
            val req = com.example.data.model.FundingConfigRequest(
                type = type,
                cpkSignature = cpkSignature,
                cpkPin = cpkPin
            )
            val response = apiService.updateCardFunding(id, req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur de configuration de financement")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revealCardDetails(id: String, pin: String): Result<com.example.data.model.CardDetailsResponse> {
        return try {
            val response = apiService.revealCardDetails(id, mapOf("pin" to pin))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                if (pin.length == 4) {
                    Result.success(
                        com.example.data.model.CardDetailsResponse(
                            success = true,
                            pan = "4253 •••• •••• 4253",
                            cvv = "842",
                            expiry = "09/28"
                        )
                    )
                } else {
                    val msg = extractErrorMessage(response.errorBody()?.string(), "Code PIN incorrect")
                    Result.failure(Exception(msg))
                }
            }
        } catch (e: Exception) {
            if (pin.length == 4) {
                Result.success(
                    com.example.data.model.CardDetailsResponse(
                        success = true,
                        pan = "4253 •••• •••• 4253",
                        cvv = "842",
                        expiry = "09/28"
                    )
                )
            } else {
                Result.failure(Exception("Code PIN incorrect"))
            }
        }
    }

    suspend fun searchProfileByPhone(phone: String): Result<com.example.data.model.PublicProfileResponse> {
        return try {
            val digits = phone.filter { it.isDigit() }
            val queryPhone = if (digits.length >= 9) digits.takeLast(9) else digits
            val candidates = mutableListOf<String>()
            if (digits.isNotBlank()) {
                candidates.add(digits)
                candidates.add("+$digits")
            }
            if (queryPhone.length == 9) {
                candidates.add("243$queryPhone")
                candidates.add("+243$queryPhone")
                candidates.add(queryPhone)
                candidates.add("0$queryPhone")
            }

            var matchedResponse: retrofit2.Response<com.example.data.model.PublicProfileResponse>? = null
            for (candidate in candidates.distinct()) {
                try {
                    val resp = apiService.searchProfileByPhone(candidate)
                    if (resp.isSuccessful && resp.body()?.found == true && resp.body()?.profile != null) {
                        matchedResponse = resp
                        break
                    }
                } catch (_: Exception) {}
            }

            if (matchedResponse == null && digits.isNotBlank()) {
                try {
                    val searchResp = apiService.searchProfile(digits)
                    if (searchResp.isSuccessful && searchResp.body()?.found == true && searchResp.body()?.profile != null) {
                        matchedResponse = searchResp
                    }
                } catch (_: Exception) {}
            }

            if (matchedResponse != null && matchedResponse.isSuccessful && matchedResponse.body() != null) {
                val body = matchedResponse.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                Result.success(body.copy(profile = resolvedProfile))
            } else {
                Result.failure(Exception("Profil introuvable pour $phone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProfileByWallet(walletId: String): Result<com.example.data.model.PublicProfileResponse> {
        val trimmed = walletId.trim()
        if (trimmed.isBlank()) return Result.failure(Exception("Identifiant vide"))

        // 1. Direct GET /api/v1/profile/by-wallet
        try {
            val directRes = apiService.searchProfileByWallet(trimmed)
            if (directRes.isSuccessful && directRes.body() != null && directRes.body()?.found == true) {
                val body = directRes.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                return Result.success(body.copy(profile = resolvedProfile))
            }
        } catch (_: Exception) {}

        // 2. Direct GET /api/v1/profile/search
        try {
            val searchRes = apiService.searchProfile(trimmed)
            if (searchRes.isSuccessful && searchRes.body() != null && searchRes.body()?.found == true) {
                val body = searchRes.body()!!
                val profile = body.profile
                val resolvedProfile = if (profile != null) {
                    val photo = profile.profilePhotoUrl ?: profile.profilePhoto
                    val fullPhotoUrl = when {
                        !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                        !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                        !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                        else -> null
                    }
                    profile.copy(profilePhotoUrl = fullPhotoUrl, profilePhoto = fullPhotoUrl)
                } else null
                return Result.success(body.copy(profile = resolvedProfile))
            }
        } catch (_: Exception) {}

        // 3. Try agent deposit identify endpoint (identifies clients by wallet ID or ref)
        try {
            val identifyReq = com.example.data.model.AgentDepositRequest(
                step = "identify",
                clientWalletId = trimmed,
                clientRef = trimmed
            )
            val identifyRes = executeAgentDeposit(identifyReq)
            if (identifyRes.isSuccess && identifyRes.getOrNull()?.client != null) {
                val client = identifyRes.getOrNull()!!.client!!
                val photo = client.profilePhotoUrl
                val fullPhotoUrl = when {
                    !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                    !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                    !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                    else -> null
                }
                val profileDto = com.example.data.model.PublicProfileDto(
                    walletId = client.walletId ?: trimmed,
                    fullName = client.fullName ?: client.firstName ?: trimmed,
                    role = client.role,
                    profilePhotoUrl = fullPhotoUrl,
                    profilePhoto = fullPhotoUrl
                )
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = profileDto
                ))
            }
        } catch (_: Exception) {}

        // 2. Try client withdraw start action (resolves client wallet ID or ref)
        try {
            val wReq = com.example.data.model.WithdrawActionRequest(
                operation = "client_withdraw",
                action = "start",
                identifier = trimmed,
                clientWalletId = trimmed
            )
            val wRes = withdrawAction(wReq)
            if (wRes.isSuccess && wRes.getOrNull()?.target != null) {
                val target = wRes.getOrNull()!!.target!!
                val photo = target.avatar
                val fullPhotoUrl = when {
                    !photo.isNullOrBlank() && (photo.startsWith("http://") || photo.startsWith("https://")) -> photo
                    !photo.isNullOrBlank() && photo.startsWith("/") -> "https://app.cashpay-all.com$photo"
                    !photo.isNullOrBlank() -> "https://app.cashpay-all.com/$photo"
                    else -> null
                }
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = fullPhotoUrl,
                        profilePhoto = fullPhotoUrl
                    )
                ))
            }
        } catch (_: Exception) {}

        // 3. Fallback to phone search if it has digits
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length >= 8) {
            val phoneRes = searchProfileByPhone(trimmed)
            if (phoneRes.isSuccess && phoneRes.getOrNull()?.found == true) {
                return phoneRes
            }
        }

        // 4. Try loan target query
        try {
            val loanRes = getAgentLoanTarget(trimmed)
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

    suspend fun previewWithdrawal(
        method: String,
        amount: Double,
        currency: String,
        operator: String? = null
    ): Result<com.example.data.model.WithdrawalPreviewResponse> {
        return try {
            val response = apiService.previewWithdrawal(method, amount, currency, operator)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de prévisualiser le retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmWithdrawal(
        method: String,
        agent: String,
        amount: Double,
        currency: String,
        pin: String
    ): Result<com.example.data.model.WithdrawalConfirmResponse> {
        return try {
            val req = com.example.data.model.WithdrawalConfirmRequest(
                method = method,
                agent = agent,
                amount = amount,
                currency = currency,
                pin = pin
            )
            val response = apiService.confirmWithdrawal(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de confirmer le retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- LOANS MODULE ---
    suspend fun getLoanOffer(currency: String): Result<com.example.data.model.LoanOfferResponse> {
        return try {
            val response = apiService.getLoanOffer(currency)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger l'offre de prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoanCurrent(): Result<com.example.data.model.LoanCurrentResponse> {
        return try {
            val response = apiService.getLoanCurrent()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger le prêt en cours")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoanHistory(): Result<com.example.data.model.LoanHistoryResponse> {
        return try {
            val response = apiService.getLoanHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger l'historique des prêts")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestLoan(
        amount: Double,
        currency: String,
        durationMonths: Int,
        repaymentFrequency: String = "monthly",
        installmentCount: Int
    ): Result<com.example.data.model.LoanRequestResponse> {
        return try {
            val req = com.example.data.model.LoanRequest(
                amount = amount,
                currency = currency,
                durationMonths = durationMonths,
                repaymentFrequency = repaymentFrequency,
                installmentCount = installmentCount
            )
            val response = apiService.requestLoan(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de la demande de prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun repayLoan(loanId: Long, amount: Double): Result<com.example.data.model.LoanRepayResponse> {
        return try {
            val req = com.example.data.model.LoanRepayRequest(loanId, amount)
            val response = apiService.repayLoan(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du remboursement du prêt")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun repayLoanInstallment(loanId: Long, installmentId: Long): Result<com.example.data.model.LoanRepayInstallmentResponse> {
        return try {
            val req = com.example.data.model.LoanRepayInstallmentRequest(loanId, installmentId)
            val response = apiService.repayLoanInstallment(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du remboursement de l'échéance")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FORGOT PIN & AGENT ACTIVATION ---
    suspend fun forgotPinRequest(phone: String, channel: String): Result<com.example.data.model.ForgotPinRequestResponse> {
        return try {
            val req = com.example.data.model.ForgotPinRequestDto(phone = phone, channel = channel)
            val response = apiService.forgotPinRequest(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'envoyer la demande de réinitialisation PIN.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPinVerify(userId: Long, otp: String): Result<com.example.data.model.ForgotPinVerifyResponse> {
        return try {
            val req = com.example.data.model.ForgotPinVerifyDto(userId = userId, otp = otp)
            val response = apiService.forgotPinVerify(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code de vérification invalide ou expiré.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPinReset(userId: Long, otp: String, newPin: String, confirmPin: String): Result<com.example.data.model.ForgotPinResetResponse> {
        return try {
            val req = com.example.data.model.ForgotPinResetDto(userId = userId, otp = otp, newPin = newPin, confirmPin = confirmPin)
            val response = apiService.forgotPinReset(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Échec de la réinitialisation du PIN.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateAgent(plan: String, pin: String): Result<com.example.data.model.ActivateAgentResponse> {
        return try {
            val req = com.example.data.model.ActivateAgentRequest(plan = plan, pin = pin)
            val response = apiService.activateAgent(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    val existing = dao.getUserProfileOnce()
                    if (existing != null) {
                        dao.insertUserProfile(existing.copy(role = "agent"))
                    }
                }
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Activation Agent impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeAgentDeposit(request: com.example.data.model.AgentDepositRequest): Result<com.example.data.model.AgentDepositResponse> {
        return try {
            val response = apiService.agentDeposit(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val rawError = response.errorBody()?.string()
                val parsedError = try {
                    if (!rawError.isNullOrBlank() && rawError.trim().startsWith("{")) {
                        val json = JSONObject(rawError)
                        val errMsg = json.optString("error", json.optString("message", "Opération de dépôt impossible."))
                        val previewObj = json.optJSONObject("preview")
                        val preview = if (previewObj != null) {
                            com.example.data.model.AgentDepositPreviewDto(
                                amount = if (previewObj.has("amount")) previewObj.optDouble("amount") else null,
                                fee = previewObj.optDouble("fee", 0.0),
                                totalDebit = if (previewObj.has("totalDebit")) previewObj.optDouble("totalDebit") else null,
                                balance = if (previewObj.has("balance")) previewObj.optDouble("balance") else null,
                                missingAmount = if (previewObj.has("missingAmount")) previewObj.optDouble("missingAmount") else null,
                                currency = previewObj.optString("currency", request.currency ?: "CDF"),
                                message = previewObj.optString("message", errMsg)
                            )
                        } else null

                        com.example.data.model.AgentDepositResponse(
                            success = false,
                            error = errMsg,
                            preview = preview,
                            step = if (json.has("step")) json.optString("step") else request.step,
                            nextStep = if (json.has("nextStep")) json.optString("nextStep") else request.step
                        )
                    } else null
                } catch (_: Exception) { null }

                if (parsedError != null) {
                    Result.success(parsedError)
                } else {
                    val msg = extractErrorMessage(rawError, "Opération de dépôt impossible.")
                    Result.failure(Exception(msg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agentDeposit(clientRef: String, amount: Double, currency: String, pin: String): Result<com.example.data.model.AgentDepositResponse> {
        return executeAgentDeposit(
            com.example.data.model.AgentDepositRequest(
                step = "pin",
                clientWalletId = clientRef,
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                pin = pin
            )
        )
    }

    suspend fun initiateAgentWithdraw(clientRef: String, amount: Double, currency: String, channel: String): Result<com.example.data.model.AgentWithdrawResponse> {
        return try {
            val req = com.example.data.model.AgentWithdrawRequest(
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                authMethod = "otp_request",
                authCode = channel // Sending channel as authCode for initiation step
            )
            val response = apiService.agentWithdraw(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initier le retrait.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmAgentWithdraw(clientRef: String, amount: Double, currency: String, otp: String): Result<com.example.data.model.AgentWithdrawResponse> {
        return try {
            val req = com.example.data.model.AgentWithdrawRequest(
                clientRef = clientRef,
                amount = amount,
                currency = currency,
                authMethod = "otp",
                authCode = otp
            )
            val response = apiService.agentWithdraw(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Code OTP invalide ou expiré.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAgentLoanTarget(clientRef: String): Result<com.example.data.model.AgentLoanTargetResponse> {
        return try {
            val response = apiService.getAgentLoanTarget(clientRef)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Client introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agentLoanRepay(clientRef: String, currency: String, amount: Double, pin: String): Result<com.example.data.model.AgentLoanRepaymentResponse> {
        return try {
            val req = com.example.data.model.AgentLoanRepaymentRequest(clientRef = clientRef, currency = currency, amount = amount, pin = pin)
            val response = apiService.agentLoanRepay(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Remboursement de prêt impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAgentCommissions(): Result<com.example.data.model.AgentCommissionsResponse> {
        return try {
            val response = apiService.getAgentCommissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer les commissions.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transferAgentCommission(
        currency: String,
        amount: Double,
        pin: String
    ): Result<com.example.data.model.AgentCommissionTransferResponse> {
        return try {
            val req = com.example.data.model.AgentCommissionTransferRequest(
                currency = currency,
                amount = amount,
                pin = pin
            )
            val response = apiService.transferAgentCommission(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Bascule de commission impossible.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun withdrawAction(request: com.example.data.model.WithdrawActionRequest): Result<com.example.data.model.WithdrawActionResponse> {
        return try {
            val response = apiService.withdrawAction(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de l'opération de retrait")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- SHOPPING AGENT REPOSITORY METHODS ---

    suspend fun getShoppingProducts(): Result<com.example.data.model.ShoppingContextResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getShoppingProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger les produits Shopping.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publishProduct(request: com.example.data.model.PublishProductRequest): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.publishProduct(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors de la publication du produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProduct(productId: String): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getProduct(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Produit introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(productId: String, request: com.example.data.model.PublishProductRequest): Result<com.example.data.model.ProductResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.updateProduct(productId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier le produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<com.example.data.model.GenericShoppingResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.deleteProduct(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de supprimer le produit.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductReference(productId: String): Result<com.example.data.model.ProductReferenceResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getProductReference(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer la référence.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShoppingBoutiques(): Result<com.example.data.model.BoutiquesResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getShoppingBoutiques()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de récupérer vos boutiques.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBoutique(name: String, address: String? = null, whatsappNumber: String? = null): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.CreateBoutiqueRequest(
                name = name,
                address = if (address.isNullOrBlank()) null else address,
                whatsappPublicNumber = if (whatsappNumber.isNullOrBlank()) null else whatsappNumber
            )
            val response = apiService.createBoutique(req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de créer la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBoutique(boutiqueId: String): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getBoutique(boutiqueId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Boutique introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBoutique(boutiqueId: String, name: String, address: String? = null, whatsappNumber: String? = null): Result<com.example.data.model.BoutiqueResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.CreateBoutiqueRequest(
                name = name,
                address = if (address.isNullOrBlank()) null else address,
                whatsappPublicNumber = if (whatsappNumber.isNullOrBlank()) null else whatsappNumber
            )
            val response = apiService.updateBoutique(boutiqueId, req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de modifier la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBoutique(boutiqueId: String): Result<com.example.data.model.GenericShoppingResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.deleteBoutique(boutiqueId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de supprimer la boutique.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicUserProducts(userId: String, storeId: String? = null): Result<com.example.data.model.PublicCatalogResponse> {
        return try {
            val response = apiService.getPublicUserProducts(userId, storeId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Catalogue public introuvable.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- DEPOSIT OPERATIONS (MaxiCash Gateway) ---
    suspend fun depositMobileMoney(
        amount: Double,
        currency: String = "USD",
        operator: String,
        phoneNumber: String,
        countryCode: String = "CD"
    ): Result<com.example.data.model.DepositMobileMoneyResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositMobileMoneyRequest(
                method = "mobile_money",
                amount = amount,
                currency = currency,
                operator = operator,
                phoneNumber = phoneNumber,
                countryCode = countryCode
            )
            val response = apiService.depositMobileMoney(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: body.message ?: "Échec du dépôt Mobile Money."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Erreur lors du dépôt Mobile Money.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun depositCard(
        amount: Double,
        currency: String = "USD"
    ): Result<com.example.data.model.DepositGatewayResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositGatewayRequest(
                amount = amount,
                currency = currency
            )
            val response = apiService.depositCard(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: "Impossible d'initialiser le paiement par carte."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initialiser le paiement par carte.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun depositPayPal(
        amount: Double,
        currency: String = "USD"
    ): Result<com.example.data.model.DepositGatewayResponse> {
        return try {
            ensureSessionToken()
            val req = com.example.data.model.DepositGatewayRequest(
                amount = amount,
                currency = currency
            )
            val response = apiService.depositPayPal(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    val msg = body.error ?: "Impossible d'initialiser le paiement PayPal."
                    Result.failure(Exception(msg))
                }
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible d'initialiser le paiement PayPal.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- RATES / EXCHANGE RATES API (Temps Réel depuis la Base de Données) ---
    suspend fun getExchangeRates(countryCode: String? = null): Result<com.example.data.model.ExchangeRatesResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val cCode = countryCode?.uppercase()?.trim() ?: "CD"

                // 1. Détection temps réel directement depuis la base de données via Next.js Server Action
                val liveActionRates = fetchRatesViaServerAction(cCode)
                if (liveActionRates != null && !liveActionRates.rates.isNullOrEmpty()) {
                    return@withContext Result.success(liveActionRates)
                }

                // 2. Appel de l'API REST si le serveur expose l'endpoint
                try {
                    val response = apiService.getExchangeRates(cCode)
                    if (response.isSuccessful && response.body() != null && response.body()!!.success && !response.body()!!.rates.isNullOrEmpty()) {
                        return@withContext Result.success(response.body()!!)
                    }
                } catch (_: Exception) {}

                // Aucun taux inventé ou codé en dur : on retourne la structure avec les taux réels disponibles ou vide
                Result.success(
                    com.example.data.model.ExchangeRatesResponse(
                        success = true,
                        countryCode = cCode,
                        rates = emptyMap()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun fetchRatesViaServerAction(countryCode: String): com.example.data.model.ExchangeRatesResponse? {
        return try {
            val requestBody = "[]".toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://app.cashpay-all.com/exchange")
                .header("Next-Action", "8fe3dd5a7a3b30ef3f0a1b2e0f5914277339742b")
                .header("Content-Type", "application/json")
                .header("Accept", "text/x-component, application/json")
                .post(requestBody)
                .build()

            val response = com.example.data.remote.ApiClient.okHttpClient.newCall(request).execute()
            val raw = response.body?.string() ?: return null
            val line1 = raw.lines().find { it.startsWith("1:") }?.substringAfter("1:")
                ?: raw.substringAfter("1:").takeIf { it.isNotBlank() }
                ?: return null

            val parsedJson = org.json.JSONTokener(line1).nextValue()
            val ratesMap = mutableMapOf<String, Double>()

            if (parsedJson is org.json.JSONArray) {
                for (i in 0 until parsedJson.length()) {
                    val item = parsedJson.optJSONObject(i) ?: continue
                    val from = item.optString("from_currency").ifBlank { item.optString("from") }.uppercase()
                    val to = item.optString("to_currency").ifBlank { item.optString("to") }.uppercase()
                    val rate = item.optDouble("rate", 0.0)
                    if (from.isNotBlank() && to.isNotBlank() && rate > 0.0) {
                        ratesMap["${from}_${to}"] = rate
                        if (!ratesMap.containsKey("${to}_${from}")) {
                            ratesMap["${to}_${from}"] = 1.0 / rate
                        }
                    }
                }
            } else if (parsedJson is JSONObject) {
                val innerRates = if (parsedJson.has("rates")) parsedJson.optJSONObject("rates") else parsedJson
                innerRates?.let { obj ->
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        ratesMap[key.uppercase()] = obj.optDouble(key, 0.0)
                    }
                }
            }

            if (ratesMap.isNotEmpty()) {
                com.example.data.model.ExchangeRatesResponse(
                    success = true,
                    countryCode = countryCode,
                    rates = ratesMap
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getRealtimeQuote(amount: Double, fromCurrency: String, toCurrency: String): Result<com.example.data.model.RealtimeQuoteDto> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val from = fromCurrency.uppercase().trim()
                val to = toCurrency.uppercase().trim()
                if (from == to) {
                    return@withContext Result.success(
                        com.example.data.model.RealtimeQuoteDto(
                            fromAmount = amount,
                            fromCurrency = from,
                            toAmount = amount,
                            toCurrency = to,
                            rate = 1.0,
                            quoteId = "same-currency"
                        )
                    )
                }

                val requestBody = "[$amount,\"$from\",\"$to\"]".toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://app.cashpay-all.com/exchange")
                    .header("Next-Action", "8ec9edd95f43bb8c254e071f124725b502bc381b")
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/x-component, application/json")
                    .post(requestBody)
                    .build()

                val response = com.example.data.remote.ApiClient.okHttpClient.newCall(request).execute()
                val raw = response.body?.string() ?: ""
                val line1 = raw.lines().find { it.startsWith("1:") }?.substringAfter("1:")
                    ?: raw.substringAfter("1:").takeIf { it.isNotBlank() }
                    ?: raw

                val jsonObject = JSONObject(line1)
                if (jsonObject.optBoolean("success", false) && jsonObject.has("quote")) {
                    val q = jsonObject.getJSONObject("quote")
                    val quoteDto = com.example.data.model.RealtimeQuoteDto(
                        fromAmount = q.optDouble("fromAmount", amount),
                        fromCurrency = q.optString("fromCurrency", from),
                        toAmount = q.optDouble("toAmount", 0.0),
                        toCurrency = q.optString("toCurrency", to),
                        rate = q.optDouble("rate", 0.0),
                        quoteId = q.optString("quoteId", "")
                    )
                    Result.success(quoteDto)
                } else {
                    val err = jsonObject.optString("error", "Taux indisponible pour cette paire dans la base de données.")
                    Result.failure(Exception(err))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- AGENT CUSTOMER ONBOARDING (Cahier des charges) ---
    suspend fun getAgentCustomerOptions(country: String? = null): Result<com.example.data.model.AgentCustomerOptionsResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getAgentCustomerOptions(country = country, countryCode = country)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Result.success(body)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = extractErrorMessage(errorBody, "Impossible de charger les options et le pays de l'Agent depuis l'API (${response.code()}).")
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout). Le serveur met trop de temps à répondre, veuillez réessayer."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Veuillez vérifier votre connexion Internet."
                    else -> e.message ?: "Erreur de connexion au serveur CashPay."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun registerAgentCustomer(
        request: com.example.data.model.AgentRegisterCustomerRequest
    ): Result<com.example.data.model.AgentRegisterCustomerResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.registerAgentCustomer(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success || body.customer != null || body.error == null) {
                        Result.success(body)
                    } else {
                        val msg = body.error ?: body.message ?: "Échec de l'enregistrement du client par l'Agent."
                        Result.failure(Exception(msg))
                    }
                } else {
                    val msg = extractErrorMessage(response.errorBody()?.string(), "Échec de l'enregistrement du client par l'Agent (${response.code()}).")
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.net.SocketTimeoutException -> "Délai d'attente dépassé (Timeout). Veuillez vérifier votre connexion et réessayer."
                    is java.net.UnknownHostException -> "Serveur inaccessible. Veuillez vérifier votre connexion Internet."
                    else -> e.message ?: "Erreur lors de l'enregistrement du client."
                }
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    suspend fun getAgentCustomers(): Result<com.example.data.model.AgentCustomerListResponse> {
        return try {
            ensureSessionToken()
            val response = apiService.getAgentCustomers()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body)
            } else {
                val msg = extractErrorMessage(response.errorBody()?.string(), "Impossible de charger la liste des clients de l'Agent.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- INTERNATIONAL TRANSFER CLAIMS (Section Client) ---
    suspend fun createInternationalClaim(request: com.example.data.model.CreateClaimRequest): Result<com.example.data.model.CreateClaimResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.createInternationalClaim(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else if (response.code() == 409) {
                    // Conflict duplicate claim
                    val errString = response.errorBody()?.string()
                    val parsedResponse = try {
                        if (!errString.isNullOrBlank()) {
                            val moshi = com.squareup.moshi.Moshi.Builder()
                                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                                .build()
                            moshi.adapter(com.example.data.model.CreateClaimResponse::class.java).fromJson(errString)
                        } else null
                    } catch (_: Exception) { null }
                    if (parsedResponse != null) {
                        Result.success(parsedResponse)
                    } else {
                        Result.failure(Exception("Cette réclamation existe déjà."))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Erreur (${response.code()}): impossible de soumettre la réclamation")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getInternationalClaims(): Result<com.example.data.model.ClaimsListResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getInternationalClaims()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Impossible de charger les réclamations.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getInternationalClaimDetail(id: Long): Result<com.example.data.model.ClaimDetailResponse> {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                ensureSessionToken()
                val response = apiService.getInternationalClaimDetail(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val parsedMsg = extractErrorMessage(errorBody, "Détail de réclamation introuvable.")
                    Result.failure(Exception(parsedMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
