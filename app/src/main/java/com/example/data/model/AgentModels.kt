package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- AGENT DEPOSIT ---
@JsonClass(generateAdapter = true)
data class AgentDepositClientDto(
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "fullName") val fullName: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositDetailDto(
    @Json(name = "transactionId") val transactionId: Any? = null,
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "clientName") val clientName: String? = null,
    @Json(name = "depositAmount") val depositAmount: Double? = null,
    @Json(name = "depositedAmount") val depositedAmount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "agentFee") val agentFee: Double? = 0.0,
    @Json(name = "totalDebitAgent") val totalDebitAgent: Double? = null,
    @Json(name = "totalDebited") val totalDebited: Double? = null,
    @Json(name = "agentBalanceBefore") val agentBalanceBefore: Double? = null,
    @Json(name = "agentBalanceAfter") val agentBalanceAfter: Double? = null,
    @Json(name = "agentRemainingBalance") val agentRemainingBalance: Double? = null,
    @Json(name = "clientRemainingBalance") val clientRemainingBalance: Double? = null,
    @Json(name = "completedAt") val completedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositPreviewDto(
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "missingAmount") val missingAmount: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositRequest(
    @Json(name = "step") val step: String, // "identify", "confirm_client", "amount", "pin"
    @Json(name = "clientWalletId") val clientWalletId: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "pin") val pin: String? = null,
    @Json(name = "clientRef") val clientRef: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentDepositResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "step") val step: String? = null,
    @Json(name = "nextStep") val nextStep: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "operation") val operation: String? = null,
    @Json(name = "client") val client: AgentDepositClientDto? = null,
    @Json(name = "agentBalances") val agentBalances: Map<String, Double>? = null,
    @Json(name = "deposit") val deposit: AgentDepositDetailDto? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = null,
    @Json(name = "balance") val balance: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Any? = null,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "preview") val preview: AgentDepositPreviewDto? = null
)

// --- AGENT WITHDRAW ---
@JsonClass(generateAdapter = true)
data class AgentWithdrawRequest(
    @Json(name = "operation") val operation: String = "client_withdraw",
    @Json(name = "clientRef") val clientRef: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "authMethod") val authMethod: String = "pin", // "pin" or "otp"
    @Json(name = "authCode") val authCode: String, // 4-digit PIN or 6-digit OTP from client
    @Json(name = "agentPin") val agentPin: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentWithdrawResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = 0.0,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "error") val error: String? = null
)

// --- AGENT LOAN REPAYMENT ---
@JsonClass(generateAdapter = true)
data class AgentLoanRepaymentRequest(
    @Json(name = "clientRef") val clientRef: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class AgentLoanRepaymentResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "paid") val paid: Double? = 0.0,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "remaining") val remaining: Double? = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentLoanTargetResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "client") val client: AgentClientInfo? = null,
    @Json(name = "loan") val loan: AgentLoanInfo? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentClientInfo(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "walletId") val walletId: String? = null
)

@JsonClass(generateAdapter = true)
data class AgentLoanInfo(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "remainingBalance") val remainingBalance: Double? = 0.0,
    @Json(name = "amount") val amount: Double? = 0.0
)

// --- AGENT COMMISSION TRANSFER / BASCULE ---
@JsonClass(generateAdapter = true)
data class AgentCommissionTransferRequest(
    @Json(name = "currency") val currency: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class AgentCommissionTransferResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "operation") val operation: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "amount") val amount: Double? = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "remainingCommission") val remainingCommission: Double? = 0.0,
    @Json(name = "newMainBalance") val newMainBalance: Double? = 0.0,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// --- AGENT BALANCES RETRIEVAL ---
@JsonClass(generateAdapter = true)
data class AgentCommissionsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "balances") val balances: Map<String, Double>? = null,
    @Json(name = "commissionBalance") val commissionBalance: Map<String, Double>? = null,
    @Json(name = "error") val error: String? = null
)

// --- AGENT OPERATION RECORD ---
@JsonClass(generateAdapter = true)
data class AgentOperationRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String, // "DEPOSIT", "WITHDRAW", "LOAN_REPAY", "COMMISSION_SWEEP"
    val title: String,
    val clientRef: String? = null,
    val amount: Double,
    val currency: String,
    val commission: Double = 0.0,
    val reference: String? = null,
    val date: String = "",
    val status: String = "Complété"
)
