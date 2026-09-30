package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoanOfferDto(
    @Json(name = "eligible") val eligible: Boolean = false,
    @Json(name = "reason") val reason: String? = null,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "guaranteeRate") val guaranteeRate: Double? = 0.0,
    @Json(name = "interestRate") val interestRate: Double? = 0.0,
    @Json(name = "durations") val durations: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LoanOfferResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "offer") val offer: LoanOfferDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepaymentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "date") val date: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanInstallmentDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "number") val number: Int = 0,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "dueDate") val dueDate: String? = null,
    @Json(name = "status") val status: String? = null // "pending", "paid", "overdue"
)

@JsonClass(generateAdapter = true)
data class LoanDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "principal_amount") val principalAmount: Double? = null,
    @Json(name = "total_repayment") val totalRepayment: Double? = null,
    @Json(name = "repaid_amount") val repaidAmount: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "interest_rate") val interestRate: Double? = 0.0,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    @Json(name = "remaining_balance") val remainingBalance: Double = 0.0,
    @Json(name = "monthly_payment") val monthlyPayment: Double? = null,
    @Json(name = "repayment_frequency") val repaymentFrequency: String? = null,
    @Json(name = "installment_count") val installmentCount: Int? = null,
    @Json(name = "installment_amount") val installmentAmount: Double? = null,
    @Json(name = "status") val status: String? = null, // "requested", "approved", "repaid", "rejected"
    @Json(name = "repayments") val repayments: List<LoanRepaymentDto> = emptyList(),
    @Json(name = "schedule") val schedule: List<LoanInstallmentDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LoanCurrentResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "loan") val loan: LoanDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanHistoryResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "loans") val loans: List<LoanDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "durationMonths") val durationMonths: Int,
    @Json(name = "repaymentFrequency") val repaymentFrequency: String = "monthly",
    @Json(name = "installmentCount") val installmentCount: Int
)

@JsonClass(generateAdapter = true)
data class LoanRequestResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayRequest(
    @Json(name = "loanId") val loanId: Long,
    @Json(name = "amount") val amount: Double
)

@JsonClass(generateAdapter = true)
data class LoanRepayResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "paid") val paid: Double? = null,
    @Json(name = "remaining") val remaining: Double? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentRequest(
    @Json(name = "loanId") val loanId: Long,
    @Json(name = "installmentId") val installmentId: Long
)

@JsonClass(generateAdapter = true)
data class LoanRepayInstallmentResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "paid") val paid: Double? = null,
    @Json(name = "remaining") val remaining: Double? = null,
    @Json(name = "installmentNumber") val installmentNumber: Int? = null,
    @Json(name = "error") val error: String? = null
)
