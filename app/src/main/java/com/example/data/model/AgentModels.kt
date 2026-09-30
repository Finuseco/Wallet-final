package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- AGENT DEPOSIT ---
@JsonClass(generateAdapter = true)
data class AgentDepositRequest(
    @Json(name = "clientRef") val clientRef: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class AgentDepositResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "totalDebit") val totalDebit: Double? = 0.0,
    @Json(name = "commission") val commission: Double? = 0.0,
    @Json(name = "error") val error: String? = null
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
    @Json(name = "pin") val pin: String,
    @Json(name = "transferAll") val transferAll: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AgentCommissionTransferResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "transferredAmount") val transferredAmount: Double? = 0.0,
    @Json(name = "currency") val currency: String? = "USD",
    @Json(name = "newCommissionBalance") val newCommissionBalance: Double? = 0.0,
    @Json(name = "newMainBalance") val newMainBalance: Double? = 0.0,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "error") val error: String? = null
)

// --- AGENT COMMISSION BALANCES ---
@JsonClass(generateAdapter = true)
data class AgentCommissionBalances(
    @Json(name = "usd") val usd: Double = 0.0,
    @Json(name = "cdf") val cdf: Double = 0.0,
    @Json(name = "eur") val eur: Double = 0.0
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
