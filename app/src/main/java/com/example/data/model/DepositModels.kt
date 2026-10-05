package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DepositMobileMoneyRequest(
    @Json(name = "method") val method: String = "mobile_money",
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "operator") val operator: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "countryCode") val countryCode: String = "CD"
)

@JsonClass(generateAdapter = true)
data class DepositMobileMoneyResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class DepositGatewayRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "USD"
)

@JsonClass(generateAdapter = true)
data class DepositGatewayResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "paymentUrl") val paymentUrl: String? = null,
    @Json(name = "error") val error: String? = null
)

enum class MobileMoneyOperator(val code: String, val displayName: String) {
    MPESA("mpesa", "M-Pesa"),
    ORANGE_MONEY("orange_money", "Orange Money"),
    AIRTEL_MONEY("airtel_money", "Airtel Money"),
    AFRIMONEY("afrimoney", "Afrimoney"),
    MTN_MOMO("mtn_momo", "MTN MoMo"),
    WAVE("wave", "Wave"),
    MOOV_MONEY("moov_money", "Moov Money"),
    FREE_MONEY("free_money", "Free Money"),
    PAGA("paga", "Paga"),
    VODAFONE_CASH("vodafone_cash", "Vodafone Cash");

    companion object {
        fun getOperatorsForCountry(countryCode: String?): List<MobileMoneyOperator> {
            val code = countryCode?.uppercase()?.trim() ?: "CD"
            return when {
                code.contains("CD") || code == "243" -> listOf(MPESA, ORANGE_MONEY, AIRTEL_MONEY, AFRIMONEY)
                code.contains("CG") || code == "242" -> listOf(MTN_MOMO, AIRTEL_MONEY)
                code.contains("CI") || code == "225" -> listOf(ORANGE_MONEY, WAVE, MTN_MOMO, MOOV_MONEY)
                code.contains("SN") || code == "221" -> listOf(ORANGE_MONEY, WAVE, FREE_MONEY)
                code.contains("CM") || code == "237" -> listOf(MTN_MOMO, ORANGE_MONEY)
                code.contains("NG") || code == "234" -> listOf(MTN_MOMO, AIRTEL_MONEY, PAGA)
                code.contains("KE") || code == "254" -> listOf(MPESA, AIRTEL_MONEY)
                code.contains("GH") || code == "233" -> listOf(MTN_MOMO, VODAFONE_CASH)
                code.contains("UG") || code == "256" -> listOf(MTN_MOMO, AIRTEL_MONEY)
                code.contains("RW") || code == "250" -> listOf(MTN_MOMO, AIRTEL_MONEY)
                else -> listOf(MPESA, ORANGE_MONEY, AIRTEL_MONEY, AFRIMONEY, MTN_MOMO)
            }
        }
    }
}
